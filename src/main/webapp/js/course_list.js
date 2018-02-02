$(function(){
    var student_id = GetQueryString("id");

    // $.ajax({
    //     type: "GET",
    //     url: "/api/v1/Student?studentId="+student_id,
    //     dataType: "json",
    //     success: function(data){
    //         var course = data;
    //         var course_html = "";
    //         for(var i=0; i<course.length; i++){
    //             course_html += showCourse(course[i].id, (i+1), course[i].childName, course[i].course_name, course[i].time, course[i].address);
    //         }
    //         $("#subscribe-course").prepend(course_html);
    //     },
    //     error: function(){
    //         msg_alert("alert", "错误，请稍后重试");
    //     }
    // });

    var data = {
        "studentId": student_id,
    };
    $.ajax({
        type: "POST",
        url: "/api/v1/Student",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function(data){
            var course = data;
            var course_html = "";
            for(var i=0; i<course.length; i++){
                course_html += showCourse(course[i].id, (i+1), course[i].childName, course[i].course_name, course[i].time, course[i].address);
            }
            $("#subscribe-course").prepend(course_html);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    function showCourse(id, num, student_name, course_name, time, address) {
        var html = '<div class="course-info" data-id="' + id + '">' +
            '<div class="num">'+ num + '</div>' +
            '<div class="student-name">'+ student_name + '</div>' +
            '<div class="course-name"><i></i>'+ course_name + '</div>' +
            '<div class="time"><i></i>'+ time + '</div>' +
            '<div class="address"><i></i>'+ address + '</div>' +
            '</div>';
        return html;
    }

    $("#course-tab").delegate("li", "click", function(){
        var type = $(this).attr("data-type");
        $(this).siblings("li").removeClass("active");
        $(this).addClass("active");
        $(".content").hide();
        $("#"+type).show();
        return false;
    });
});