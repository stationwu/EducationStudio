
$(function(){
    //判断用户有没有注册

    ///////////////等待赋值
    var is_register = true;
    ////////////////

    //如果没有注册
    if(is_register == false){
        msg_alert("confirm_one_btn", "请先完成注册并添加学员~");
        return false;
    }

    //已注册，则获取当前学员的课程列表

    ///////////等待赋值
    var id = 1;
    var child = {
        "id": id;
    };
    ///////////////

    $.ajax({
        type: "GET",
        /////////////获取课程列表url
        // url: "/api/v1/Customer/Detail",
        //////////////
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(child),
        success: function(data){
            /////////////
            var courses = data;
            //////////////
            console.log(courses);
            if(!courses.length){
                msg_alert("confirm_one_btn", "请先添加课程~");
                return false;
            }
            //显示课程列表收缩状态
            var html = '<div id="course-title" class="close"><i></i>课程：' + courses[0].name + '(剩余' + courses[0].count + '节)<div class="icon"></div></div><ul id="course-list" style="display: none;">';
            if(courses[0].type != "experience"){
                html += '<li class="active"><em class="name">' + courses[0].name + '</em>(剩余<em class="count">' + courses[0].count + '</em>节)</li>';
            }else{
                html += '<li class="active" data-type="experience">' + courses[0].name + '(预约体验课)</li>';
            }

            for(var i=1; i<courses.length; i++){
                if(courses[i].type != "experience"){
                    html += '<li><em class="name">' + courses[i].name + '</em>(剩余<em class="count">' + courses[i].count + '</em>节)</li>';
                }else{
                    html += '<li data-type="experience">' + courses[i].name + '(预约体验课)</li>';
                }
            }
            html += '</ul>';
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    $("#course-subscribe").delegate("#course-title", "click", function(){
        $(this).toogleClass("close");
        $("#course-list").toggle();
        return false;
    });

    $("#course-subscribe").delegate("li", "click", function(){
        var type = $(this).attr("data-type");
        if(type == "experience"){
            //跳转到预约体验课页面
        }
        var count = $(this).children(".count");
        var name = $(this).children(".name");
        //////////////////拿到学生姓名
        var student_name = "lina";
        var mobile = "18800000000";
        //////////////
        if(count == 0){
            msg_alert("confirm_one_btn", student_name + "的" + name + "课程已用完，请联系老师(" + mobile + ")购买课程~");
        }else{
            $("#course-title").hide();
            $("#course-list").hide();


    }
        return false;
    });

});