$(function(){
    var student_id = GetQueryString("id");
    var course = [
        {
            "id":1,
            "student_name":"张一白",
            "course_name":"《绘本课》",
            "time":"2017.12.20 09:00～10:00",
            "address":"淮海中路1487弄"
        },
        {
            "id":2,
            "student_name":"张二白",
            "course_name":"《绘本课》",
            "time":"2017.12.21 09:00～10:00",
            "address":"淮海中路1487弄"
        },
        {
            "id":3,
            "student_name":"张一白",
            "course_name":"《绘本课》",
            "time":"2017.12.22 09:00～10:00",
            "address":"淮海中路1487弄"
        }
    ];
    var course_html = "";
    for(var i=0; i<course.length; i++){
        course_html += showCourse(course[i].id, (i+1), course[i].student_name, course[i].course_name, course[i].time, course[i].address);
    }
    $("#subscribe-course").append(course_html);

    $.ajax({
        type: "GET",
        url: "/api/v1/Student?studentId="+student_id,
        dataType: "json",
        success: function(data){
            ///////等待赋值
            var course = data;
            //////////
            console.log(course);
            // var course_html = "";
            // for(var i=0; i<course.length; i++){
            //     course_html += showCourse(course[i].id, (i+1), course[i].student_name, course[i].course_name, course[i].time, course[i].address);
            // }
            // $("#subscribe-course").append(course_html);
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