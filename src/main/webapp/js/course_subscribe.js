var strDate = null;
var strTime = null;
$(function(){
    var student_id = GetQueryString("id");
    $.ajax({
        type: "GET",
        url: "/api/v1/CourseCategory?studentId="+student_id,
        dataType: "json",
        success: function(data){
            console.log(data);
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
            $("#time-list").children("li").removeClass("active");
            $("#calendar").children(".calendar-date").children(".item").removeClass("active");
        }else{
            $(this).addClass("close");
        }
        return false;
    });

    $("#course-list").delegate("li", "click", function(){
        var id = $(this).attr("data-id");
        var type = $(this).attr("data-type");
        if(type == "demo"){
            console.log("跳转到预约体验课页面");
            return false;
        }
        var count = $(this).children(".count").html();
        var name = $(this).children(".name").html();
        console.log(name,count,id);



        if(count == "0"){
            //////////////////拿到学生姓名和老师手机
            var data = {
                "studentId": student_id,
            };
            $.ajax({
                type: "POST",
                url: "/api/v1/Student",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function(data){
                    console.log(data);
                    var student_name = data.childName;
                    var mobile = data.hardcode;
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
                type: "GET",
                url: "/api/v1/AvailableCourse?courseCategoryId="+id,
                dataType: "json",
                success: function(data){
                    console.log("某一类型课程",data);
                    //可选的日期加active
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
        strTime = $(this).children(".time").html();
        if(!active){
            return false;
        }
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
        var data = {
            "studentId": student_id,
            //////////假数据待赋值
            "courseId": "1"
        };
        $.ajax({
            type: "POST",
            url: "/api/v1/AllBeforeCourse/book",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function(data){
                console.log("预约返回值",data);
                //根据返回值不同显示弹窗
                //成功：继续预约、查看预约课程带id
                //失败
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });
        return false;
    });

});

