
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
            var sex = $(this).children(".thumbnail.active").hasClass("boy")?"boy":"girl";

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
                name: name,
                birth: birth,
                sex: sex
            });
        });
        if(!children.length){
            return false;
        }
        //调用添加学员接口
        console.log(children);
        // $.ajax({
        //     type: "POST",
        //     url: "/verify",
        //     contentType: "application/json",
        //     data: JSON.stringify(oSMSObject),
        //     success: function(data){
        //         msg_alert("alert", "发送成功");
        //         $(this).removeClass("active");
        //         timer($('#verifyCodeBtn'));
        //         $('#wxVerifyCode').val(data);
        //     },
        //     error: function(){
        //         msg_alert("alert", "错误，请稍后重试");
        //     }
        // });

        return false;
    });

    $("#signup-form-btn").click(function(){
        var mobileNumber = $("#mobileNumber").val();
        var flag = validate(mobileNumber, "mobile");
        if(!flag){
            // layer.open({
            //     content:"请您填写正确的手机号",
            //     btn: ["确定"],
            //     className: "popup"
            // });
            msg_alert("confirm_one_btn", "请您填写正确的手机号");
        }
        var captcha = $("#captcha").val();


        // var openCode = $("#wxOpenCode").val();
        var openCode = 1234;
        var verifyCodeId = $("#wxVerifyCode").val();
        var verifyCode = $("#captcha").val();
        var customer = {
            "openCode": openCode,
            "mobilePhone": mobileNumber,
            "verifyCode": verifyCode,
            "verifyCodeId": verifyCodeId,
            "children": []
        };

        // $.ajax({
            // url: '/api/v1/Customer/SignUp',
            // contentType: 'application/json',
            // type: 'POST',
            // data: JSON.stringify(customer),
        // }).done(function (response) {
        //     window.location.href = "/user/center";
        // });

        $.ajax({
            type: "POST",
            url: "/api/v1/Customer/SignUp",
            contentType: "application/json",
            data: JSON.stringify(customer),
            success: function(data){
                window.location.href = "/user/center";
            },
            error: function(){
                // layer.open({
                //     content: "错误，请稍后重试",
                //     skin: "msg",
                //     time: 2
                // });
                msg_alert("alert", "错误，请稍后重试");
            }
        });

        return false;
    });

});