var strDate;
var strTime;
$(function(){

    var courses = [
        {
            "id":1,
            "name":"绘本课",
            "count":"30",
            "type":"normal"
        },{
            "id":2,
            "name":"铅笔课",
            "count":"20",
            "type":"normal"
        },{
            "id":3,
            "name":"油画棒",
            "count":"0",
            "type":"normal"
        },{
            "id":4,
            "name":"马克笔",
            "count":"30",
            "type":"experience"
        },{
            "id":5,
            "name":"彩色铅笔",
            "count":"30",
            "type":"experience"
        }
    ];

    if(!courses.length){
        msg_alert("confirm_one_btn", "请先添加课程~");
        return false;
    }
    //显示课程列表收缩状态
    var html = '<div id="course-title" class="close"><div class="icon"></div>课程：<span class="name">' + courses[0].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[0].count + '</span>节)'+
        '<i></i></div><ul id="course-list" style="display: none;">';
    if(courses[0].type != "experience"){
        html += '<li class="active" data-id="' + courses[0].id + '"><span class="name">' + courses[0].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[0].count + '</span>节)</li>';
    }else{
        html += '<li class="active" data-id="' + courses[0].id + '" data-type="experience">' + courses[0].name + '&nbsp;&nbsp;(预约体验课)</li>';
    }

    for(var i=1; i<courses.length; i++){
        if(courses[i].type != "experience"){
            html += '<li data-id="' + courses[i].id + '"><span class="name">' + courses[i].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[i].count + '</span>节)</li>';
        }else{
            html += '<li data-id="' + courses[i].id + '" data-type="experience">' + courses[i].name + '&nbsp;&nbsp;(预约体验课)</li>';
        }
    }
    html += '</ul>';

    $("#course-subscribe").prepend(html);



    var id = GetQueryString("id");

    $.ajax({
        type: "GET",
        url: "/api/v1/CourseCategory?studentId="+id,
        dataType: "json",
        success: function(data){
            console.log(data);
            /////////////等待赋值
            // var courses = ;
            // //////////////
            // console.log(courses);
            // if(!courses.length){
            //     msg_alert("confirm_one_btn", "请先添加课程~");
            //     return false;
            // }
            // //显示课程列表收缩状态
            // var html = '<div id="course-title" class="close"><i></i>课程：<span class="name">' + courses[0].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[0].count + '</span>节)'+
            //     '<div class="icon"></div></div><ul id="course-list" style="display: none;">';
            // if(courses[0].type != "experience"){
            //     html += '<li class="active"><span class="name">' + courses[0].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[0].count + '</span>节)</li>';
            // }else{
            //     html += '<li class="active" data-type="experience">' + courses[0].name + '&nbsp;&nbsp;(预约体验课)</li>';
            // }
            //
            // for(var i=1; i<courses.length; i++){
            //     if(courses[i].type != "experience"){
            //         html += '<li><span class="name">' + courses[i].name + '</span>&nbsp;&nbsp;(剩余<span class="count">' + courses[i].count + '</span>节)</li>';
            //     }else{
            //         html += '<li data-type="experience">' + courses[i].name + '&nbsp;&nbsp;(预约体验课)</li>';
            //     }
            // }
            // html += '</ul>';
            //
            // $("#course-subscribe").prepend(html);
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
        }else{
            $(this).addClass("close");

        }
        return false;
    });

    $("#course-list").delegate("li", "click", function(){
        var id = $(this).attr("data-id");
        var type = $(this).attr("data-type");
        if(type == "experience"){
            console.log("跳转到预约体验课页面");
            return false;
        }
        var count = $(this).children(".count").html();
        var name = $(this).children(".name").html();
        console.log(name,count,id);
        //////////////////拿到学生姓名和老师手机
        var student_name = "lina";
        var mobile = "18800000000";
        //////////////
        if(count == "0"){
            msg_alert("confirm_one_btn", student_name + "的" + name + "课程已用完，请联系老师(" + mobile + ")购买课程~");
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
        $("#subscribe-time").html("<em></em>预约时间:" + strDate + "&nbsp;&nbsp;" + strTime).show();
        return false;
    });

    $("#subscribe-btn").click(function(){
        var data = {
            "studentId": id,
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

