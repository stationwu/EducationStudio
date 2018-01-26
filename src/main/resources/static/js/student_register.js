
$(function(){
    var children = [];
    var num = 1;
    $("#add-student-form").delegate(".thumbnail", "click", function(){
        $(this).siblings(".thumbnail").removeClass("active");
        $(this).addClass("active");
        return false;
    });
    $("#add-student-form-btn").click(function(){
        num++;
        var data = $(".student-info").eq(0).clone().attr("data-id",num);
        data.children("input").val("");
        data.children(".thumbnail").removeClass("active");
        data.children(".thumbnail").eq(0).addClass("active");
        $("#add-student-form-btn").before(data);
        return false;
    });
    $("#add-student-btn").click(function(){
        children = [];
        $("#add-student-form .student-info").each(function (i) {
            var name = $(this).children("input[name='name']").val();
            var birth = $(this).children("input[name='birth']").val();
            var sex = $(this).children(".thumbnail.active").hasClass("boy")?"BOY":"GIRL";

            var flag = validate(name, "name");
            if(!flag){
                msg_alert("confirm_one_btn", "第"+ (i+1) +"个学员姓名格式不正确");
                return false;
            }
            var flag = validate(birth, "date");
            if(!flag){
                msg_alert("confirm_one_btn", "第"+ (i+1) +"个学员生日格式不正确");
                return false;
            }
            children.push({
                childName: name,
                birthday: birth,
                gender: sex
            });
        });
        if(!children.length){
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
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });

        return false;
    });


});