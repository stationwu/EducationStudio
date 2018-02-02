package com.edu.controller;

import com.edu.dao.CustomerRepository;
import com.edu.dao.StudentRepository;
import com.edu.domain.Customer;
import com.edu.domain.Student;
import com.edu.domain.dto.ChildContainer;
import com.edu.utils.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserCenterController {
	
	@Autowired
	private CustomerRepository repository;
	@Autowired
	private StudentRepository studentRepository;

	public final static String USER_CENTER_PATH = "/user/center";
	
	public final static String USER_SIGNIN_PATH = "/user/signin";

    public final static String STUDENT_NEW_PATH = "/user/student/new";
    
    public final static String STUDENT_LIST_PATH = "/user/student/list";
    
    public final static String STUDENT_SEARCH_PATH = "/user/student/search";
    
    public final static String STUDENT_UPLOAD_PATH = "/user/student/upload";
    
    public final static String COURSE_BOOK_PATH = "/user/course/book";
    
    public final static String COURSE_LIST_PATH = "/user/course/list";

	public final static String SESSION_OPENID_KEY = "openCode";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(USER_CENTER_PATH)
	private String gotoUserHomeOrSignUp(HttpSession session, Model model) {
		
		String openId = (String)session.getAttribute(SESSION_OPENID_KEY);
		
        logger.debug(">>> Your OPENID is: " + openId);

	    String view = null;

        if(repository.isCustomerAlreadyRegistered(openId)) {
            logger.debug(">>> You've already registered");
            logger.debug(">>> Redirecting to user's home page");

            Customer customer = repository.findOneByOpenCode(openId);
            model.addAttribute("customer", customer);
            view = "user_info";
        } else {
            logger.debug(">>> You're not registered");
            logger.debug(">>> Redirecting to the signup page");

            Customer customer = new Customer();
            customer.setOpenCode(openId);

            model.addAttribute("customer", customer);
            model.addAttribute("openCode", openId);
            view = "user_signup";
        }

        return view;
    }
	
	@PostMapping(USER_CENTER_PATH)
	@ResponseBody
	private String editCustomerInfo(@RequestParam(value = "mobilePhone")String mobilePhone,
			@RequestParam(value = "address")String address ,HttpSession session) {
		String openId = (String)session.getAttribute(SESSION_OPENID_KEY);
		Customer customer = repository.findOneByOpenCode(openId);
		customer.setAddress(address);
		customer.setMobilePhone(mobilePhone);
		repository.save(customer);
		return "修改成功";
    }
	
	@GetMapping(USER_SIGNIN_PATH)
	private String signIn(HttpSession session, Model model) {
		return "redirect:" + USER_CENTER_PATH;
    }
    
	@GetMapping("user/session")
	@ResponseBody
	public String createSession(HttpSession session) {
		String openId = "123456";
        session.setAttribute(Constant.SESSION_OPENID_KEY, openId);
		
		return "true";
	}

    @GetMapping(STUDENT_NEW_PATH)
    public String createStudent() {
        return "student_register"; // serve the page
    }
    
    @GetMapping(STUDENT_LIST_PATH)
    public String listStudent() {
        return "student_list"; // serve the page
    }
    
    @GetMapping(COURSE_BOOK_PATH)
    public String bookCourse() {
        return "course_subscribe"; // serve the page
    }
    
    @GetMapping(COURSE_LIST_PATH)
    public String courseListForStudent() {
        return "course_list"; // serve the page
    }
    
    @GetMapping(STUDENT_SEARCH_PATH)
    public String searchStudent() {
        return "student_search"; // serve the page
    }
    
    @GetMapping(STUDENT_UPLOAD_PATH)
    public String signAndUploadStudent(@RequestParam(value="studentId") String id, HttpSession session, Model model) {
		Student student = studentRepository.findOne(id);
    	model.addAttribute("studentContainer", new ChildContainer(student));
        return "student_upload"; // serve the page
    }
}