var courses = null;
var strDate = null;
var strTime = null;
var course_category_id = null;
var demo_course = false;
var course_id = null;
var available_date = {};

function show_subscribe_panel() {
    $("#subscribe").show();
    $.ajax({
        type: "POST",
        url: "/api/v1/AvailableCourse?courseCategoryId="+course_category_id,
        dataType: "json",
        success: function(data){
            var course = data;
            if(!demo_course){
                for(var i=0; i<course.length; i++){
                    var date = course[i].date.split("-").join("");
                    var timeFrom = course[i].timeFrom.split(":")[0];
                    var id = course[i].id;
                    if(available_date[date] == undefined){
                        available_date[date] = {};
                    }
                    available_date[date][id] = {
                        "timeFrom": timeFrom,
                        "maxSeat": course[i].maxSeat,
                        "bookedSeat": course[i].bookedSeat
                    };
                }
            }else{
                for(var i=0; i<course.length; i++){
                    var date = course[i].date.split("-").join("");
                    var timeFrom = course[i].timeFrom.split(":")[0];
                    var id = course[i].id;
                    if(available_date[date] == undefined){
                        available_date[date] = {};
                    }
                    available_date[date][id] = {
                        "timeFrom": timeFrom,
                        "maxSeat": course[i].maxSeat,
                        "bookedSeat": course[i].bookedSeat
                    };
                }
            }

            //可选的日期加active
            $.each(available_date, function(key,value){
                $(".calendar-date .item[data='"+key+"']").addClass("active");
            });

        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });
}

$(function(){
    $("body").css("height","auto");
    var student_id = GetQueryString("id");
    $.ajax({
        type: "GET",
        url: "/api/v1/CourseCategory?studentId="+student_id,
        dataType: "json",
        success: function(data){
            courses = data;
            if(!courses.length){
                msg_alert("confirm_one_btn", "请先添加课程~");
                return false;
            }
            //显示课程列表收缩状态
            var html = '<div id="course-title" class="close"><div class="icon"></div>课程：<span class="info"><span class="name">' + courses[0].courseName + '</span> (剩余<span class="count">' + courses[0].leftPeriod + '</span>节)</span>'+
                '<i></i></div><ul id="course-list" style="display: none;">';

            if(courses[0].demoCourse != true){
                html += '<li class="active" data-id="' + courses[0].id + '"><span class="name">' + courses[0].courseName + '</span> (剩余<span class="count">' + courses[0].leftPeriod + '</span>节)</li>';
            }else{
                html += '<li class="active" data-id="' + courses[0].id + '" data-type="demo">' + courses[0].courseName + ' (预约体验课)</li>';
            }

            for(var i=1; i<courses.length; i++){
                if(courses[i].demoCourse != true){
                    html += '<li data-id="' + courses[i].id + '"><span class="name">' + courses[i].courseName + '</span> (剩余<span class="count">' + courses[i].leftPeriod + '</span>节)</li>';
                }else{
                    html += '<li data-id="' + courses[i].id + '" data-type="demo">' + courses[i].courseName + ' (预约体验课)</li>';
                }
            }
            html += '</ul>';

            $("#course-subscribe").prepend(html);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    $("#course-subscribe").delegate("#course-title", "click", function(){
        $("#course-list").toggle();
        if($(this).hasClass("close")){
            $(this).removeClass("close");
            $("#subscribe-time").html("").hide();
            $("#time-list").children("li.active").removeClass("active");
            $("#time-list").children("li.select").removeClass("select").children(".subscribe-btn").html("预约");
            $("#time-list").children("li").removeAttr("data-id data-maxseat data-bookedseat");
            $("#calendar").children(".calendar-date").children(".item").removeClass("active select");
            $("#subscribe").hide();
            $("#demo-course").hide().children(".course-image").children("img").attr("src","");
            strDate = null;
            strTime = null;
            course_category_id = null;
            demo_course = false;
            course_id = null;
            available_date = {};
        }else{
            $(this).addClass("close");
        }
        return false;
    });

    $("#course-subscribe").delegate("#course-list li", "click", function(){
        course_category_id = $(this).attr("data-id");
        var count = $(this).children(".count").html();
        var name = $(this).children(".name").html();
        if($(this).attr("data-type") == "demo"){
            demo_course = true;
        }

        if(demo_course == false && count == "0"){
            $.ajax({
                type: "POST",
                url: "/api/v1/Student?studentId="+student_id,
                dataType: "json",
                success: function(data){
                    var student_name = data.childName;
                    var mobile = "13818668959";
                    msg_alert("confirm_one_btn", student_name + "的" + name + "课程已用完，请联系老师(" + mobile + ")购买课程~");
                    return false;
                },
                error: function(){
                    msg_alert("alert", "错误，请稍后重试");
                }
            });
            return false;
        }
        $("#course-title").addClass("close");

        $("#course-title").children(".info").html($(this).html());
        $("#course-list").hide();

        if(demo_course){
            var img_url = "";
            $.each(courses, function(key,value){
                if(value.id == course_category_id){
                    if(value.images[0] && value.images[0].imageUrl){
                        img_url = value.images[0].imageUrl;
                    }
                }
            });
            if(img_url != ""){
                $("#demo-course").show().children(".course-image").children("img").attr("src",img_url);
            }else{
                $("#demo-course").show().children(".course-image").children("img").attr("src","/images/test.png");
            }
        }else{
            show_subscribe_panel();
        }
        return false;
    });

    $("#demo-subscribe-btn").click(function(){
        $("#demo-course").hide();
        show_subscribe_panel();
    });

    $("#time-list").delegate("li", "click", function(){
        var active = $(this).hasClass("active");
        if(!active){
            return false;
        }
        strTime = $(this).children(".time").html();
        course_id = $(this).attr("data-id");
        $(this).siblings(".select").removeClass("select").children(".subscribe-btn").html("预约");
        $(this).addClass("select").children(".subscribe-btn").html("<i></i>");
        $("#subscribe-time").html("<em></em>预约时间:" + strDate + " " + strTime).show();
        return false;
    });

    $("#subscribe-btn").click(function(){
        if(strDate == null || strTime == null){
            msg_alert("confirm_one_btn", "请先选择预约时间");
            return false;
        }
        if(!demo_course){
            var booked_seat = $("#time-list").children("li[data-id='"+course_id+"']").attr("data-bookedSeat");
            var max_seat = $("#time-list").children("li[data-id='"+course_id+"']").attr("data-maxSeat");
            if(booked_seat >= max_seat){
                var content = "<div id='before-book-popup'><div class='content'>您预约的该时段已有10人已预约，可能会出现拥挤或等候现象！</div><div class='btn-panel'><span class='btn1'>确认预约</span><span class='btn2'>预约其他时段</span></div></div>";
                layer.open({
                    content:content,
                    className: "popup-2-btn",
                    success: function(elem){
                        $(elem).delegate(".btn1", "click", function(){
                            layer.closeAll();
                            $.ajax({
                                type: "POST",
                                url: "/api/v1/AvailableCourse/book?studentId="+student_id+"&courseId="+course_id,
                                dataType: "json",
                                success: function(data){
                                    var content = "<div id='book-popup'><div class='content'><div class='title'><i></i>预约成功~</div><div>预约成功短信已发送到您预留的手机，请注意查收</div></div><div class='btn-panel'><span class='btn1'>确认</span><span class='btn2'>预约其他课</span></div></div>";
                                    layer.open({
                                        content:content,
                                        className: "popup-2-btn",
                                        success: function(elem){
                                            $(elem).delegate(".btn1", "click", function(){
                                                layer.closeAll();
                                                $("#course-title").click();
                                                $("#course-title").click();
                                                location.href = "/user/course/list";
                                            });
                                            $(elem).delegate(".btn2", "click", function(){
                                                layer.closeAll();
                                                $("#course-title").click();
                                            });
                                        }
                                    });
                                    return false;
                                },
                                error: function(XMLHttpRequest, textStatus, errorThrown){
                                    if(XMLHttpRequest.responseText){
                                        var response = JSON.parse(XMLHttpRequest.responseText);
                                        if(response.message){
                                            msg_alert("alert", response.message);
                                        }else{
                                            msg_alert("alert", "错误，请稍后重试");
                                        }
                                    }else{
                                        msg_alert("alert", "错误，请稍后重试");
                                    }
                                }
                            });
                            return false;
                        });
                        $(elem).delegate(".btn2", "click", function(){
                            layer.closeAll();
                        });
                    }
                });
                return false;
            }

            $.ajax({
                type: "POST",
                url: "/api/v1/AvailableCourse/book?studentId="+student_id+"&courseId="+course_id,
                dataType: "json",
                success: function(data){
                    var content = "<div id='book-popup'><div class='content'><div class='title'><i></i>预约成功~</div><div>预约成功短信已发送到您预留的手机，请注意查收</div></div><div class='btn-panel'><span class='btn1'>确认</span><span class='btn2'>预约其他课</span></div></div>";
                    layer.open({
                        content:content,
                        className: "popup-2-btn",
                        success: function(elem){
                            $(elem).delegate(".btn1", "click", function(){
                                layer.closeAll();
                                $("#course-title").click();
                                $("#course-title").click();
                                location.href = "/user/course/list";
                            });
                            $(elem).delegate(".btn2", "click", function(){
                                layer.closeAll();
                                $("#course-title").click();
                            });
                        }
                    });

                    return false;
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    if(XMLHttpRequest.responseText){
                        var response = JSON.parse(XMLHttpRequest.responseText);
                        if(response.message){
                            msg_alert("alert", response.message);
                        }else{
                            msg_alert("alert", "错误，请稍后重试");
                        }
                    }else{
                        msg_alert("alert", "错误，请稍后重试");
                    }
                }
            });
        }else{
            var course_price;
            var course_name;
            $.each(courses,function(key,value){
                if(value["id"] == course_category_id){
                    course_price = value["price"];
                    course_name = value["courseName"];
                }
            });
            var date = strDate.split("-");
            var course_date = date[0]+"年"+date[1]+"月"+date[2]+"日";

            var content = "<div id='before-demo-popup'><div class='content'><div class='title'>确认预约</div><div>确认以"+course_price+"元的体验价格预约"+course_date+course_name+"</div></div><div class='btn-panel'><span class='btn1'>取消</span><span class='btn2'>去支付</span></div></div>";
            layer.open({
                content:content,
                className: "popup-2-btn",
                success: function(elem){
                    $(elem).delegate(".btn1", "click", function(){
                        layer.closeAll();
                    });
                    $(elem).delegate(".btn2", "click", function(){
                        //发送订单
                        var data = {
                            "studentId": student_id,
                            "courseId": course_id
                        };
                        $.ajax({
                            type: "POST",
                            url: "/api/v1/requestQuote",
                            contentType: "application/json",
                            dataType: "json",
                            data: JSON.stringify(data),
                            success: function(data){
                                var order_id = data["id"];
                                $.ajax({
                                    type: "POST",
                                    url: "/api/v1/pay?orderId="+order_id,
                                    dataType: "json",
                                    success: function(response){
                                        if (response.success) {
                                            var body = {
                                                url : window.location.href
                                            };

                                            $.ajax({
                                                url: '/api/v1/JsApiSignature',
                                                contentType: 'application/json',
                                                accept: 'application/json',
                                                type: 'POST',
                                                data: JSON.stringify(body),
                                            }).done(function (signature) {
                                                wx.config({
                                                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                                                    appId: signature.appId, // 必填，公众号的唯一标识
                                                    timestamp: signature.timestamp, // 必填，生成签名的时间戳
                                                    nonceStr: signature.nonceStr, // 必填，生成签名的随机串
                                                    signature: signature.signature, // 必填，签名，见附录1
                                                    jsApiList: [  // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                                                        'chooseWXPay'
                                                    ]
                                                });

                                                //上方的config检测通过后，会执行ready方法
                                                wx.ready(function(){
                                                    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
                                                    console.log("JSSDK config信息验证成功");

                                                    wx.chooseWXPay({
                                                        timestamp: response.timestamp,
                                                        nonceStr: response.nonceStr,
                                                        package: response.package,
                                                        signType: response.signType,
                                                        paySign: response.paySign,
                                                        success: function(res) {
                                                            // window.location.href = "/paymentResult/" + order.id;
                                                            layer.closeAll();
                                                            var content = "<div id='demo-popup'><div class='content'><div class='title'><i></i>预约成功~</div><div>预约成功短信已发送到您预留的手机，请注意查收</div></div><div class='btn-panel'><span class='btn1'>确认</span><span class='btn2'>预约其他课</span></div></div>";
                                                            layer.open({
                                                                content:content,
                                                                className: "popup-2-btn",
                                                                success: function(elem){
                                                                    $(elem).delegate(".btn1", "click", function(){
                                                                        layer.closeAll();
                                                                        $("#course-title").click();
                                                                        $("#course-title").click();
                                                                        location.href = "/user/course/list";
                                                                    });
                                                                    $(elem).delegate(".btn2", "click", function(){
                                                                        layer.closeAll();
                                                                        $("#course-title").click();
                                                                    });
                                                                }
                                                            });
                                                        },
                                                        //该complete回调函数，相当于try{}catch(){}异常捕捉中的finally，无论支付成功与否，都会执行complete回调函数。即使wx.error执行了，也会执行该回调函数.
                                                        complete : function(res) {
                                                            //  /!*注意：res对象的errMsg属性名称，是没有下划线的，与WeixinJSBridge支付里面的err_msg是不一样的。而且，值也是不同的。*!/
                                                            if (res.errMsg == "chooseWXPay:ok") {
                                                                //window.location.href = data[0].sendUrl;
                                                            } else if (res.errMsg == "chooseWXPay:cancel") {
                                                                msg_alert("alert", "你手动取消支付");
                                                            } else if (res.errMsg == "chooseWXPay:fail") {
                                                                msg_alert("alert", "支付失败");
                                                            } else if (res.errMsg == "config:invalid signature") {
                                                                msg_alert("alert", "支付签名验证错误，请检查签名正确与否 or 支付授权目录正确与否等");
                                                            }
                                                        }
                                                    });
                                                });

                                                wx.error(function(res) {
                                                    if (res.errMsg == "config:invalid url domain") {
                                                        msg_alert("alert", "微信支付(测试)授权目录设置有误");
                                                    } else {
                                                        msg_alert("alert", "检测出问题:" + res.errMsg);
                                                    }
                                                });
                                            });

                                        } else {
                                            msg_alert("alert", response.message);
                                            window.location.href = "/paymentResult/" + order.id;
                                        }
                                        return false;
                                    },
                                    error: function(){
                                        msg_alert("alert", "错误，请稍后重试");
                                    }
                                });
                                return false;
                            },
                            error: function(){
                                msg_alert("alert", "错误，请稍后重试");
                            }
                        });
                    });
                }
            });
        }

        return false;
    });
});