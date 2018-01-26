
$(function(){
    $.ajax({
        type: "GET",
        url: "/api/v1/Customer/Detail",
        dataType: "json",
        // contentType: "application/json",
        // data: JSON.stringify(children),
        success: function(data){
            var children = data;
            console.log(children);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });


});