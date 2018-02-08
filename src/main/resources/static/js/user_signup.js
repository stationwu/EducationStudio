
$(function(){
    var wait = 60;
    function timer(o) {
        if (wait == 0) {
            o.addClass("active");
            o.text("获取验证码");
            wait = 60;
            return;
        } else {
            o.removeClass("active");
            o.text(wait+"s后重发");
            wait--;
        }
        setTimeout(function() {
            timer(o);
        },1000);
    };
    function validate(value, type){
        var reg = "";
        switch(type){
            case "mobile":
                reg = /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/;
                break;
            default:
                return false;
        }

        return reg.test(value);
    }


    $("#verifyCodeBtn").click(function(){
        if(!$(this).hasClass("active")){
            return false;
        }
        var mobileNumber = $("#mobileNumber").val();
        var flag = validate(mobileNumber, "mobile");
        if(!flag){
            msg_alert("confirm_one_btn", "请您填写正确的手机号");
        }else{
            //调用发送验证码接口
            var oSMSObject = {
                mobile: mobileNumber,
                type: "R"
            };

            $.ajax({
                type: "POST",
                url: "/verify",
                contentType: "application/json",
                data: JSON.stringify(oSMSObject),
                success: function(data){
                    msg_alert("alert", "发送成功");
                    $(this).removeClass("active");
                    timer($('#verifyCodeBtn'));
                    $('#wxVerifyCode').val(data);
                },
                error: function(){
                    msg_alert("alert", "错误，请稍后重试");
                }
            });

        }
        return false;
    });

    $("#signup-form-btn").click(function(){
        var mobileNumber = $("#mobileNumber").val();
        var flag = validate(mobileNumber, "mobile");
        if(!flag){
            msg_alert("confirm_one_btn", "请您填写正确的手机号");
            return false;
        }

        var verifyCode = $("#captcha").val();
        var flag = validate(verifyCode, "verifyCode");
        if(!flag){
            msg_alert("confirm_one_btn", "请您填写正确格式的验证码");
            return false;
        }

        // var captcha = $("#captcha").val();
        var verifyCodeId = $("#wxVerifyCode").val();

        var customer = {
            "mobilePhone": mobileNumber,
            "verifyCode": verifyCode,
            "verifyCodeId": verifyCodeId,
            "children": []
        };

        $.ajax({
            type: "POST",
            url: "/api/v1/Customer/SignUp",
            contentType: "application/json",
            data: JSON.stringify(customer),
            success: function(data){
                window.location.href = "/user/student/new";
            },
            error: function(){
                msg_alert("alert", "错误，请稍后重试");
            }
        });

        return false;
    });

});