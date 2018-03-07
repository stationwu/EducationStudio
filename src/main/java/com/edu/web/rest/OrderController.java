package com.edu.web.rest;

import com.edu.config.WechatPayProperties;
import com.edu.dao.*;
import com.edu.domain.*;
import com.edu.domain.dto.LessonBookingInfo;
import com.edu.domain.dto.OrderContainer;
import com.edu.errorhandler.RequestDeniedException;
import com.edu.utils.Constant;
import com.edu.utils.URLUtil;
import com.edu.utils.WebUtils;
import com.edu.utils.WxTimeStampUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyCoupon;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayBaseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.edu.utils.Constant.CONTACT_PHONE_NUMBER;
import static com.edu.utils.Constant.RESPONSE_ERROR_MESSAGE_HEADER;
import static com.edu.utils.WxPayConstants.DEVICE_WEB;
import static com.edu.utils.WxPayConstants.FEE_TYPE_CNY;
import static com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse.fail;
import static com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse.success;

@Controller
public class OrderController {
    public static final String PATH = "/api/v1/Order";
    public static final String REQUEST_QUOTE_FOR_LESSON = "/api/v1/requestQuote";
    public static final String PAYMENT_NOTIFY_PATH = "/api/v1/pay/notify";
    public static final String PAY_PATH = "/api/v1/Order/{id}/pay";
    public static final String PAY_WITH_PARAMETER_PATH = "/api/v1/pay";
    public static final String CANCEL_PATH = "/api/v1/Order/{id}/cancelPay";
    public static final String REQ_REFUND_PATH = "/api/v1/Order/{id}/requestRefund";
    public static final String REFUND_PATH = "/manager/api/v1/order/{id}/refund";
    public static final String REFUND_NOTIFY_PATH = "/api/v1/refund/notify";
    public static final String PAY_RESULT_VIEW = "/paymentResult/{orderId}";

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private RefundRepository refundRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseProductRepository courseProductRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private WxPayService wxPayService;
    @Autowired private WxMpService wxMpService;
    @Autowired private WebUtils webUtils;
    @Autowired private WxTimeStampUtil wxTimeStampUtil;
    @Autowired private WechatPayProperties wxPayProperties;

    public static final DateTimeFormatter lessonDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String DEMO_LESSON_TEMPLATE_ID = "DWBwEF9LLzwlaO70QWE1SbMmR5DmLZ4akzwgyXqty8Y";
    private static final String OTHER_GOODS_TEMPLATE_ID = "nBGFhvqZlCsute7SqnatA-Iis5iJaVQZZDNHe4OMEP0";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Lock paymentNofificationLock = new ReentrantLock();

    @ResponseBody
    @RequestMapping(PATH)
    public List<OrderContainer> listOrders(HttpSession session) {
        String openId = (String) session.getAttribute(Constant.SESSION_OPENID_KEY);

        Customer customer = customerRepository.findOneByOpenCode(openId);

        if (customer == null) {
            throw new RequestDeniedException("You're not allowed to access the requested resource");
        }

        return customer.getOrders().stream()
                .map(x -> new OrderContainer(x))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @ResponseBody
    @RequestMapping(value = REQUEST_QUOTE_FOR_LESSON, method = RequestMethod.POST)
    public ResponseEntity<?> requestQuote(@RequestBody LessonBookingInfo bookingInfo, HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        String openId = (String)session.getAttribute(Constant.SESSION_OPENID_KEY);

        Customer customer = customerRepository.findOneByOpenCode(openId);

        Course bookedLesson = courseRepository.findOne(bookingInfo.getCourseId());
        if (bookedLesson == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, String.format("课程（%d）未预约", bookingInfo.getCourseId()));
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CourseCategory courseCategory = bookedLesson.getCourseCategory();
        if (courseCategory == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, String.format("预约课程（%d）的课程信息无效", bookingInfo.getCourseId()));
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Student student = studentRepository.findOne(bookingInfo.getStudentId());
        if (student == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, String.format("学生（%s）不存在", bookingInfo.getStudentId()));
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!customer.getStudents().contains(student)) { // Not my child, cannot pay for him
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, String.format("不能购买课程，学生（%s）不是您的孩子", bookingInfo.getStudentId()));
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int quantity = 1; // Always 1 lesson per order...

        // Please regard it as orderItem (without quantity though), while productCategory could be
        // regarded as the product master
        CourseProduct courseProduct = new CourseProduct();

        courseProduct.setCourseCategory(courseCategory);

        courseProduct.setStudent(student);
        courseProduct.setStartFrom(LocalDateTime.parse(bookedLesson.getDate() + " " + bookedLesson.getTimeFrom(), lessonDateTimeFormat));
        courseProduct.setEndAt(LocalDateTime.parse(bookedLesson.getDate() + " " + bookedLesson.getTimeTo(), lessonDateTimeFormat));
        courseProduct.setQuantity(quantity);
        courseProduct.setSubTotalAmount(courseCategory.getPrice().multiply(BigDecimal.valueOf(quantity))); // No discount so far
        courseProduct.setAddress(addressRepository.findFirstByOrderByIdAsc());
        courseProduct = courseProductRepository.save(courseProduct);

        Order order = new Order();
        order.addCourseProduct(courseProduct);
        order.setTotalAmount(courseProduct.getSubTotalAmount());
        order.setDate(LocalDate.now().toString());
        order.setCustomer(customer);
        order.setStatus(Order.Status.CREATED);
        order = orderRepository.save(order);

        return new ResponseEntity<>(new OrderContainer(order), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = PAY_WITH_PARAMETER_PATH, method = RequestMethod.POST)
    public PayResult payWithParameter(@RequestParam("orderId") String orderId, HttpServletRequest request) {
        String openCode = (String) request.getSession().getAttribute(Constant.SESSION_OPENID_KEY);
        String spbillCreateIp = webUtils.getClientIp(request);
        String paymentNotifyUrl = URLUtil.getHostUrl(request) + PAYMENT_NOTIFY_PATH;

        return doPay(openCode, orderId, spbillCreateIp, paymentNotifyUrl);
    }

    @ResponseBody
    @RequestMapping(value = PAY_PATH, method = RequestMethod.POST)
    public PayResult pay(@PathVariable("id") String orderId, HttpServletRequest request) {
        String openCode = (String) request.getSession().getAttribute(Constant.SESSION_OPENID_KEY);
        String spbillCreateIp = webUtils.getClientIp(request);
        String paymentNotifyUrl = URLUtil.getHostUrl(request) + PAYMENT_NOTIFY_PATH;

        return doPay(openCode, orderId, spbillCreateIp, paymentNotifyUrl);
    }

    private PayResult doPay(String openCode, String orderId, String spbillCreateIp, String paymentNotifyUrl) {
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            return PayResult.fail( "订单号（" + orderId + "）不存在" );
        }
        if (order.getStatus() != Order.Status.CREATED && order.getStatus() != Order.Status.NOTPAY) {
            return PayResult.fail( "订单（" + orderId + "）已关闭，无法继续支付" );
        }

        WxPayUnifiedOrderRequest payRequest = WxPayUnifiedOrderRequest.newBuilder()
                //.appid()
                .outTradeNo(order.getId())
                .openid(openCode)
                .body(buildBody(order))
                .spbillCreateIp(spbillCreateIp)
                .timeStart(wxTimeStampUtil.getCurrentTimeStamp())
                .timeExpire(wxTimeStampUtil.builder().now().afterMinutes(wxPayProperties.getExpiryInMinutes()).build())
                .notifyURL(paymentNotifyUrl)
                .totalFee(WxPayBaseRequest.yuanToFee(order.getTotalAmount().toString())) // 198.99 -> "198.99" -> 19899
                .build();

        WxPayMpOrderResult payResult = null;
        try {
            payResult = wxPayService.createOrder(payRequest);
        } catch (WxPayException e) {
            return PayResult.fail("创建预付单失败, 原因:{" + e.getMessage() + "}");
        }

        Payment payment;
        if (order.getPayment() != null) {
            payment = order.getPayment();
        } else {
            payment = new Payment();
        }

        payment.setTimeStart(payRequest.getTimeStart());
        payment.setSpBillCreateIp(payRequest.getSpbillCreateIp());
        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        order.setStatus(Order.Status.NOTPAY);
        orderRepository.save(order);

        return PayResult.ok(payResult);
    }

    @ResponseBody
    @RequestMapping(value = PAYMENT_NOTIFY_PATH, method = RequestMethod.POST)
    public String onPaymentNotify(@RequestBody String xmlData) {
        logger.debug(">>> 收到了支付通知 <<<");

        WxPayOrderNotifyResult result = null;

        try {
            result = wxPayService.parseOrderNotifyResult(xmlData);
        } catch (WxPayException e) {
            return WxPayNotifyResponse.fail("无效请求 " + e.getMessage());
        }

        // 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
        // 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。
        // 在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
        paymentNofificationLock.lock();

        Order order = null; // Make it visible to template message

        try {
            String orderId = result.getOutTradeNo();
            order = orderRepository.findOne(orderId);

            if (order == null) {
                return fail("订单号（" + orderId + "）不存在");
            }

            logger.debug("订单（%s）收到了支付通知", order.getId());

            if (order.getStatus() == Order.Status.PAID) {
                return success("重复通知，支付完成");
            }
            if (order.getStatus() != Order.Status.NOTPAY) {
                return fail("订单（" + orderId + "）状态错误（" + order.getStatusText() + "），无法继续支付");
            }

            Payment payment = order.getPayment();

            // Important -->
            payment.setOrder(order);
            payment.setTransactionId(result.getTransactionId());
            payment.setOpenId(result.getOpenid());
            // <--
            // Not so important -->
            if (null != result.getDeviceInfo()) { payment.setDeviceInfo(result.getDeviceInfo()); }
            if (null != result.getFeeType()) { payment.setFeeType(result.getFeeType()); }
            if (null != result.getTradeType()) { payment.setTradeType(result.getTradeType()); }
            if (null != result.getAttach()) { payment.setAttach(result.getAttach()); }
            if (null != result.getBankType()) { payment.setBankType(result.getBankType()); }
            if (null != result.getTimeEnd()) { payment.setTimeEnd(result.getTimeEnd()); }
            if (null != result.getIsSubscribe()) { payment.setIsSubsribe(result.getIsSubscribe()); }
            if (null != result.getCashFee()) { payment.setCashFee(result.getCashFee()); }
            if (null != result.getCashFeeType()) { payment.setCashFeeType(result.getCashFeeType()); }
            if (null != result.getTotalFee()) { payment.setTotalFee(result.getTotalFee()); }
            if (null != result.getSettlementTotalFee()) { payment.setSettlementTotalFee(result.getSettlementTotalFee()); }
            if (null != result.getCouponFee()) { payment.setCouponFee(result.getCouponFee()); }
            if (null != result.getCouponCount()) { payment.setCouponCount(result.getCouponCount()); }

            int index = 0;
            for (WxPayOrderNotifyCoupon couponUsed : result.getCouponList()) {
                Coupon coupon = new Coupon();
                coupon.setCouponIndex(++index);
                if (null != couponUsed.getCouponFee()) { coupon.setCouponFee(couponUsed.getCouponFee()); }
                if (null != couponUsed.getCouponId()) { coupon.setCouponId(couponUsed.getCouponId()); }
                if (null != couponUsed.getCouponType()) { coupon.setCouponType(couponUsed.getCouponType()); }
                coupon.setPayment(payment);
                coupon = couponRepository.save(coupon);
                payment.addCoupon(coupon);
            }
            // <--

            paymentRepository.save(payment);

            order.setStatus(Order.Status.PAID);
            orderRepository.save(order);
        } finally {
            paymentNofificationLock.unlock();
        }

        // 支付成功，发送模板消息
        List<CourseProduct> courseProducts = order.getCourseProductsMap().keySet().stream().collect(Collectors.toList());
        if (courseProducts.size() > 0) { // Should be always true because you won't place an empty order
            CourseProduct courseProduct = courseProducts.get(0);
            if (courseProduct.getCourseCategory().isDemoCourse()) {
                WxMpTemplateMsgService templateMsgService = wxMpService.getTemplateMsgService();
                WxMpTemplateMessage templateMessage = buildTemplateMessageForDemoLesson(
                        order.getCustomer().getOpenCode(),
                        order.getId(),
                        courseProduct.getCourseCategory().getCourseName(),
                        courseProduct.getStartFrom(),
                        courseProduct.getEndAt(),
                        courseProduct.getAddressText(),
                        CONTACT_PHONE_NUMBER);
                try {
                    templateMsgService.sendTemplateMsg(templateMessage);
                } catch (WxErrorException ex) {
                    logger.info("订单（%s）已完成支付，但我们无法发送消息通知客户，原因：%s", order.getId(), ex.getMessage());
                }
            } else {
                // TODO: 添加对其他商品的支持
            }
        }

        return success("支付完成");
    }

    @RequestMapping(value = CANCEL_PATH, method = RequestMethod.POST)
    public ResponseEntity<?> cancelPay(@PathVariable("id") String orderId) {
        HttpHeaders headers = new HttpHeaders();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单号（" + orderId + "）不存在");
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        if (order.getStatus() != Order.Status.CREATED && order.getStatus() != Order.Status.NOTPAY) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单（" + orderId + "）已关闭，无法取消");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);

        WxPayOrderCloseResult result;

        try {
            result = wxPayService.closeOrder(orderId);
        } catch (WxPayException e) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "取消付款时发生了错误");
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = REQ_REFUND_PATH, method = RequestMethod.POST)
    public ResponseEntity<?> requestRefund(@PathVariable("id") String orderId) {
        HttpHeaders headers = new HttpHeaders();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单号（" + orderId + "）不存在");
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        if (order.getStatus() != Order.Status.PAID) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单（" + orderId + "）未支付，无法申请退款");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        order.setStatus(Order.Status.REFUND_REQUESTED);
        // orderRepository.save(order); // not supported yet

        headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "尚不支持退款");
        return new ResponseEntity<>(headers, HttpStatus.NOT_IMPLEMENTED);
    }

    @Transactional
    @RequestMapping(value = REFUND_PATH, method = RequestMethod.POST)
    public ResponseEntity<?> refund(@PathVariable("id") String orderId) {
        HttpHeaders headers = new HttpHeaders();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单号（" + orderId + "）不存在");
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        if (order.getStatus() != Order.Status.REFUND_REQUESTED) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单（" + orderId + "）未申请退款");
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "订单（" + orderId + "）尚未付款");
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        // Lock entry to prevent two people approve refund at same time
        paymentRepository.findOneForUpdate(payment.getId());

        Refund refund;
        if (payment.getRefund() != null) { // Already requested once
            refund = payment.getRefund();
        } else {
            refund = new Refund();
            refund.setPayment(payment);
            refund.setCashFee(payment.getCashFee());
            refund.setCashFeeType(payment.getCashFeeType());
            refund = refundRepository.save(refund);
        }

        WxPayRefundRequest refundRequest = WxPayRefundRequest.newBuilder()
                .outTradeNo(String.valueOf(order.getId()))
                .outRefundNo(String.valueOf(refund.getId()))
                .refundFee(WxPayBaseRequest.yuanToFee(String.valueOf(order.getTotalAmount())))
                .refundFeeType(FEE_TYPE_CNY)
                // .transactionId(order.getPayment().getTransactionId()) // 与out_trade_no二选一
                .totalFee(WxPayBaseRequest.yuanToFee(String.valueOf(order.getTotalAmount())))
                .deviceInfo(DEVICE_WEB)
                .refundDesc("商户订单（" + order.getId() + "）微信订单号（" + order.getPayment().getTransactionId() + "）请求退款")
                .build();

        WxPayRefundResult result;

        try {
            result = wxPayService.refund(refundRequest);
        } catch (WxPayException e) {
            headers.add(RESPONSE_ERROR_MESSAGE_HEADER, "退款失败, 原因:{" + e.getMessage() + "}");
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        payment.setRefund(refund);
        paymentRepository.save(payment);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = REFUND_NOTIFY_PATH, method = RequestMethod.POST)
    public String onRefundNotify(@RequestBody String xmlData) {
        WxPayRefundNotifyResult result = null;

        try {
            result = wxPayService.parseRefundNotifyResult(xmlData);
        } catch (WxPayException e) {
            return WxPayNotifyResponse.fail("无效请求 " + e.getMessage());
        }

        WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();
        String orderId = reqInfo.getOutTradeNo();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            return fail("订单号（" + orderId + "）不存在");
        }
        if (order.getStatus() != Order.Status.NOTPAY) {
            return fail("订单（" + orderId + "）状态错误（" + order.getStatusText() + "），无法继续支付");
        }

        Payment payment = order.getPayment();
        Refund refund = payment.getRefund();
        refund.setTotalFee(reqInfo.getTotalFee());
        refund.setSettlementRefundFee(reqInfo.getSettlementRefundFee());
        refund.setSettlementTotalFee(reqInfo.getSettlementTotalFee());
        refund.setRefundId(reqInfo.getRefundId());
        refund.setRefundFee(reqInfo.getRefundFee());

        refundRepository.save(refund);

        order.setStatus(Order.Status.REFUND);

        return success("退款完成");
    }

    @RequestMapping(value = PAY_RESULT_VIEW, method = RequestMethod.GET)
    public ModelAndView onSuccessfulPay(@PathVariable("orderId") String orderId) {
        ModelAndView mav = new ModelAndView();
        Order order = orderRepository.findOne(orderId);

        if (order == null) {
            mav.addObject("orderId", orderId);
            mav.addObject("errorMessage", "订单 #" + orderId + " 不存在");
            mav.addObject("hintMessage", "您可以到“我的订单”中检查您的订单。如果您已经支付，请联系客服人员。");
            mav.setViewName("payment_error");
            return mav;
        }
        if (order.getStatus() != Order.Status.PAID) {
            mav.addObject("orderId", orderId);
            mav.addObject("errorMessage", "订单 #" + orderId + " 支付未成功");
            mav.addObject("hintMessage", "您可以到“我的订单”中继续支付。如果您已经支付，请联系客服人员。");
            mav.setViewName("payment_error");
            return mav;
        }

        mav.addObject("orderId", orderId);
        mav.addObject("detailMessage", "订单 #" + orderId);
        mav.setViewName("payment_done");
        return mav;
    }


    private String buildBody(Order order) {
        List<String> list = new LinkedList<>();

        if (order.getCourseProductsMap() != null && !order.getCourseProductsMap().isEmpty()) {
            for (Map.Entry<CourseProduct, Integer> entry : order.getCourseProductsMap().entrySet()) {
                list.add(entry.getKey().getCourseCategory().getCourseName() + " x " + entry.getValue());
            }
            String template = "课程：%s 数量：%d 节 上课时间 %s - %s";
            for (CourseProduct courseProduct : order.getCourseProductsMap().keySet()) {
                list.add(String.format(template,
                        courseProduct.getCourseCategory().getCourseName(),
                        courseProduct.getQuantity(),
                        courseProduct.getStartFrom().format(lessonDateTimeFormat),
                        courseProduct.getEndAt().format(lessonDateTimeFormat)));
            }
        }

        String body = String.join(", ", list);
        if (body.length() > 128) { // max length is 128 by Weixin service
            return body.substring(0, 126) + "等";
        } else {
            return body;
        }
    }

    private WxMpTemplateMessage buildTemplateMessageForDemoLesson(String sendToOpenId,
                                                                  String orderId,
                                                                  String courseName,
                                                                  LocalDateTime startFrom,
                                                                  LocalDateTime endAt,
                                                                  String address,
                                                                  String contactPhoneNumber) {

        List<WxMpTemplateData> templateData = new ArrayList<>();

        templateData.add(new WxMpTemplateData("first", "你成功购买了以下课程"));
        templateData.add(new WxMpTemplateData("keyword1", orderId));
        templateData.add(new WxMpTemplateData("keyword2", courseName));
        templateData.add(new WxMpTemplateData("keyword3", String.format("%s - %s", startFrom.format(lessonDateTimeFormat), endAt.format(lessonDateTimeFormat))));
        templateData.add(new WxMpTemplateData("keyword4", address));
        templateData.add(new WxMpTemplateData("keyword5", contactPhoneNumber));
        templateData.add(new WxMpTemplateData("remark", "请准时前来上课。如有疑问，请通过联系电话与我们联系。谢谢！"));

        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .templateId(DEMO_LESSON_TEMPLATE_ID)
                .toUser(sendToOpenId)
                .data(templateData)
                .build();
        return templateMessage;
    }

    /**
     * In order to response the JS client with a JSON object
     * instead of an XML string
     */
    public static class PayResult {
        private String timestamp;
        private String nonceStr;
        private String packageValue; // can't name it 'package' as it's keyword
        private String signType;
        private String paySign;
        private String message; // In case error happens
        private boolean success;

        private PayResult(WxPayMpOrderResult wxResult) {
            this.timestamp = wxResult.getTimeStamp();
            this.nonceStr = wxResult.getNonceStr();
            this.packageValue = wxResult.getPackageValue();
            this.signType = wxResult.getSignType();
            this.paySign = wxResult.getPaySign();
            this.success = true;
        }

        private PayResult(String message) {
            this.message = message;
            this.success = false;
        }

        public static PayResult ok(WxPayMpOrderResult wxResult) {
            return new PayResult(wxResult);
        }

        public static PayResult fail(String message) {
            return new PayResult(message);
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getNonceStr() {
            return nonceStr;
        }

        public String getPackage() {
            return packageValue;
        }

        public String getSignType() {
            return signType;
        }

        public String getPaySign() {
            return paySign;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
