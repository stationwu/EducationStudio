
$(function(){
    var width = document.documentElement.clientWidth;
    var height = document.documentElement.clientHeight;
    var btn_height = document.getElementById("signup-form-btn").offsetHeight;
    var content_height = height - btn_height;
    $(".content").height(content_height);

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
        //var flag = validate(verifyCode, "verifyCode");
        //if(!flag){
        //    msg_alert("confirm_one_btn", "请您填写正确格式的验证码");
        //    return false;
        //}

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