
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
            // layer.open({
            //     content:"请您填写正确的手机号",
            //     btn: ["确定"],
            //     className: "popup" //这样你就可以在css里面控制该弹层的风格了
            // });
            msg_alert("confirm_one_btn", "请您填写正确的手机号");
        }else{
            //调用发送验证码接口
            var oSMSObject = {
                mobile: mobileNumber,
                type: "R"
            };

            // $.ajax({
                // url: '/verify',
                // contentType: 'application/json',
                // type: 'POST',
                // data: JSON.stringify(oSMSObject),
            // }).done(function (response) {
                // layer.open({
                //     content: "发送成功",
                //     skin: "msg",
                //     time: 2
                // });
                // $(this).removeClass("active");
                // timer($('#verifyCodeBtn'));
                // $('#wxVerifyCode').val(response);
            // });

            $.ajax({
                type: "POST",
                url: "/verify",
                contentType: "application/json",
                data: JSON.stringify(oSMSObject),
                success: function(data){
                    // layer.open({
                    //     content: "发送成功",
                    //     skin: "msg",
                    //     time: 2
                    // });
                    msg_alert("alert", "发送成功");
                    $(this).removeClass("active");
                    timer($('#verifyCodeBtn'));
                    $('#wxVerifyCode').val(data);
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

        }
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