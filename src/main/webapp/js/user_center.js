$(function(){
    var id = GetQueryString("id");
    $.ajax({
        type: "GET",
        url: "/api/v1/Student",
        dataType: "json",
        success: function(data){
            var painting_count = 0;
            var course_count = 0;
            for(var i=0; i<data.length; i++){
                painting_count += data[i].paints.length;
            }
            $("#paintings").children("span.count").html(painting_count);

        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });
    $.ajax({
        type: "GET",
        url: "/api/v1/Customer/Detail",
        dataType: "json",
        success: function(data){
            $(".mobile").html(data.mobilePhone);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });
    $("#courses").click(function () {
        location.href = "/user/course/list";
        return false;
    });
    $("#paintings").click(function () {
        location.href = "/user/paint/list";
        return false;
    });
});