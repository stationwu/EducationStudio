var strDate = null;
var strTime = null;
var course_category_id = null;
var course_id = null;
var available_date = {};
$(function(){
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
            var html = '<div id="course-title" class="close"><div class="icon"></div>课程：<span class="name">' + courses[0].courseName + '</span> (剩余<span class="count">' + courses[0].leftPeriod + '</span>节)'+
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
            $("#subscribe").hide();
            strDate = null;
            strTime = null;
            course_category_id = null;
            course_id = null;
            available_date = {};
            $("#time-list").children("li").removeClass("active");
            $("#calendar").children(".calendar-date").children(".item").removeClass("active");
        }else{
            $(this).addClass("close");
        }
        return false;
    });

    $("#course-subscribe").delegate("#course-list li", "click", function(){
        var course_category_id = $(this).attr("data-id");
        var type = $(this).attr("data-type");
        if(type == "demo"){
            console.log("跳转到预约体验课页面");
            return false;
        }
        var count = $(this).children(".count").html();
        var name = $(this).children(".name").html();
        console.log(name,count,course_category_id);

        if(count == "0"){
            $.ajax({
                type: "POST",
                url: "/api/v1/Student?studentId="+student_id,
                dataType: "json",
                success: function(data){
                    var student_name = data.childName;
                    var mobile = "18800000000";
                    msg_alert("confirm_one_btn", student_name + "的" + name + "课程已用完，请联系老师(" + mobile + ")购买课程~");
                },
                error: function(){
                    msg_alert("alert", "错误，请稍后重试");
                }
            });

        }else{
            $("#course-title").addClass("close");
            $("#course-title").children(".name").html(name);
            $("#course-title").children(".count").html(count);
            $("#course-list").hide();
            $("#subscribe").show();

            $.ajax({
                type: "POST",
                url: "/api/v1/AvailableCourse?courseCategoryId="+course_category_id,
                dataType: "json",
                success: function(data){
                    console.log("某一类型课程",data);
                    var course = data;
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
                    console.log(available_date);

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
        return false;
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
        var booked_seat = $("#time-list").children("li[data-id='"+course_id+"']").attr("data-bookedSeat");
        var max_seat = $("#time-list").children("li[data-id='"+course_id+"']").attr("data-maxSeat");
        if(booked_seat >= max_seat){
            var content = "<div id='before-book-popup'><div class='content'>您预约的该时段已有10人已预约，可能会出现拥挤或等候现象！</div><div class='btn-panel'><span class='btn1'>确认预约</span><span class='btn2'>预约其他时段</span></div></div>";
            layer.open({
                content:content,
                className: "popup-2-btn"
            });
            return false;
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/AvailableCourse/book?studentId="+student_id+"&courseId="+course_id,
            dataType: "json",
            success: function(data){
                console.log("预约返回值",data);
                //根据返回值不同显示弹窗
                //成功：继续预约、查看预约课程带id
                // var content = "<div class='title'><i></i>预约成功~</div><div class='content'>预约成功短信已发送到您预留的手机，请注意查收</div>";
                // layer.open({
                //     content:content,
                //     btn: ["预约其他课", "确认"],
                //     className: "popup",
                //     yes: function(index, layero){
                //         layer.close(index);
                //         $("#course-title").click();
                //         return false;
                //     },
                //     btn2: function(index, layero){
                //         location.href = "/user/course/list?id="+student_id;
                //         return false;
                //     },
                // });

                var content = "<div id='book-popup'><div class='content'><div class='title'><i></i>预约成功~</div><div>预约成功短信已发送到您预留的手机，请注意查收</div></div><div class='btn-panel'><span class='btn1'>确认</span><span class='btn2'>预约其他课</span></div></div>";
                layer.open({
                    content:content,
                    className: "popup-2-btn"
                });

                return false;
                //失败
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });
        return false;
    });
    $("#before-book-popup").delegate(".btn1", "click", function(){
        layer.closeAll();
        $.ajax({
            type: "POST",
            url: "/api/v1/AvailableCourse/book?studentId="+student_id+"&courseId="+course_id,
            dataType: "json",
            success: function(data){
                var content = "<div id='book-popup'><div class='content'><div class='title'><i></i>预约成功~</div><div>预约成功短信已发送到您预留的手机，请注意查收</div></div><div class='btn-panel'><span class='btn1'>确认</span><span class='btn2'>预约其他课</span></div></div>";
                layer.open({
                    content:content,
                    className: "popup-2-btn"
                });
                return false;
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });
        return false;
    });
    $("#before-book-popup").delegate(".btn2", "click", function(){
        layer.closeAll();
    });
    $("#book-popup").delegate(".btn1", "click", function(){
        location.href = "/user/course/list?id="+student_id;
    });
    $("#book-popup").delegate(".btn2", "click", function(){
        layer.closeAll();
        $("#course-title").click();
    });
});

