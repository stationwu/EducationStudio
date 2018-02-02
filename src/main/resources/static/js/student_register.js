
$(function(){
    var children = [];
    var num = 1;
    $("#add-student-form").delegate(".thumbnail", "click", function(){
        $(this).siblings(".thumbnail").removeClass("active");
        $(this).addClass("active");
        return false;
    });
    $("#add-student-form").delegate("input[name='birth']", "click", function(){
        $(this).removeAttr("placeholder");
        $(this).css("padding-left","0.66667rem");
    });
    $("#add-student-form-btn").click(function(){
        num++;
        var data = $(".student-info").eq(0).clone().attr("data-id",num);
        data.children("input").val("");
        data.children(".thumbnail").removeClass("active");
        data.children(".thumbnail").eq(0).addClass("active");
        data.children("input[name='birth']").attr("placeholder","请选择上课学员的生日");
        data.children("input[name='birth']").removeAttr("style");
        $("#add-student-form-btn").before(data);
        return false;
    });
    $("#add-student-btn").click(function(){
        children = [];
        var flag = true;
        $("#add-student-form .student-info").each(function (i) {
            var name = $(this).children("input[name='name']").val();
            var birth = $(this).children("input[name='birth']").val();
            var sex = $(this).children(".thumbnail.active").hasClass("boy")?"BOY":"GIRL";

            var vali = validate(name, "name");
            if(!vali){
                flag = false;
                msg_alert("confirm_one_btn", "第"+ (i+1) +"个学员姓名格式不正确");
                return false;
            }
            var vali = validate(birth, "date");
            if(!vali){
                flag = false;
                msg_alert("confirm_one_btn", "第"+ (i+1) +"个学员生日格式不正确");
                return false;
            }
            children.push({
                childName: name,
                birthday: birth,
                gender: sex
            });
        });
        if(!children.length || !flag){
            return false;
        }
        //调用添加学员接口
        // console.log(children);
        $.ajax({
            type: "POST",
            url: "/api/v1/Customer/AddChild",
            contentType: "application/json",
            data: JSON.stringify(children),
            success: function(data){
                msg_alert("alert", "添加成功");
                //跳转到什么页面？
                window.location.href = "/user/student/list";
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });

        return false;
    });


});