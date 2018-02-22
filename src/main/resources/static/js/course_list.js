var student_data;
$(function(){
    // var student_id = GetQueryString("id");
    var student_id;
    var course_id;

    $.ajax({
        type: "GET",
        url: "/api/v1/Student",
        dataType: "json",
        success: function(data){
            var course_html = "";
            var student_html = "";
            var num = 1;
            student_data = data;
            if(student_data.length){
                // student_html += '<div id="student-title" class="title" data-id="' + student_data[0].id + '">学员:' + student_data[0].childName + '<i></i></div>'+
                //                 '<ul id="student-list" class="list">';
                $("#student-title").attr("data-id", student_data[0].id).html("学员:" + student_data[0].childName + "<i></i>");
            }

            for(var i=0; i<student_data.length; i++){
                var courses = student_data[i].reversedCourses;
                var childName = student_data[i].childName;
                for(var j=0; j<courses.length; j++){
                    course_html += showCourse(courses[j].id, (num++), childName, courses[j].courseName, (courses[j].date+" "+courses[j].timeFrom+"~"+courses[j].timeTo), courses[j].address);
                }

                student_html += '<li data-id="' + student_data[i].id + '">' + student_data[i].childName + '</li>';
            }
            $("#student-list").html(student_html);
            student_html = "";
            // student_html += '</ul>';

            if(student_data[0].courseCategories.length){
                $("#course-search-btn").addClass("active");
                // student_html += '<div id="course-title" class="title" data-id="' + student_data[0].courseCategories[0].id + '">课程:' + student_data[0].courseCategories[0].courseName + '<i></i></div>'+
                //                 '<ul id="course-list" class="list">';
                $("#course-title").attr("data-id", student_data[0].courseCategories[0].id).html("课程:" + student_data[0].courseCategories[0].courseName + "<i></i>");
            }
            for(var i=0; i<student_data[0].courseCategories.length; i++){
                console.log(student_data[0].courseCategories[i]);
                student_html += '<li data-id="' + student_data[0].courseCategories[i].id + '">' + student_data[0].courseCategories[i].courseName + '</li>';
            }
            // student_html += '</ul>';

            $("#subscribe-course").prepend(course_html);
            $("#course-list").html(student_html);
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
    $("#course-info").delegate("#student-title", "click", function(){
       $(this).next(".list").toggle();
        $("#course-list").hide();
        $("#detail").hide();
    });
    $("#course-info").delegate("#course-title", "click", function(){
        $(this).next(".list").toggle();
        $("#detail").hide();
    });
    $("#student-list").delegate("li", "click", function(){
        student_id = $(this).attr("data-id");
        // console.log(student_id);
        $("#student-title").attr("data-id",student_id).html("学员:" + $(this).html() + "<i></i>");
        $("#course-title").removeAttr("data-id");
        $.each(student_data, function(key,value){
            console.log(value.id)
            if(value.id == student_id){
                var courses = value.courseCategories;
                var student_html = '';
                $("#course-title").hide();
                $("#course-search-btn").removeClass("active");
                if(courses.length){
                    $("#course-title").show();
                    $("#course-title").attr("data-id",courses[0].id).html("课程:" + courses[0].courseName + "<i></i>");
                    $("#course-search-btn").addClass("active");
                }

                for(var i=0; i<courses.length; i++){
                    student_html += '<li data-id="' + courses[i].id + '">' + courses[i].courseName + '</li>';
                }
                $("#course-list").html(student_html).hide();
                return false;
            }
        });
        $("#student-list").hide();

        // $("#course-list").show();
    });

    $("#course-list").delegate("li", "click", function(){
        var course_id = $(this).attr("data-id");
        // console.log(student_id);
        $("#course-title").attr("data-id",course_id).html("课程:" + $(this).html() + "<i></i>");
        $("#course-list").hide();
    });

    $("#course-search-btn").click(function(){
        if(!$(this).hasClass("active")){
            return false;
        }
        student_id = $("#student-title").attr("data-id");
        course_id = $("#course-title").attr("data-id");
        var courses, course, name;
        $.each(student_data, function(key,value){
            if(value.id == student_id){
                courses = value.courseCategories;
                name = value.childName;
                // console.log(courses)
                $.each(courses, function(key,value){
                    console.log(111,value);
                    if(value.id == course_id){
                        course = value;
                        return false;
                    }
                });
                console.log(11,course);
                var html = '<div class="icon"></div>';
                if(course.leftPeriod){
                    html += '<div class="course-info" data-id="' + course.id + '">'+
                        '<div class="student-name">' + name + '</div>'+
                        '<div class="course-name">课程内容:《' + course.courseName + '》</div>'+
                        '<div class="left-period">剩余课时:' + course.leftPeriod + '</div>'+
                        '<div class="reserved-period">已上课时:' + (course.totalPeriod - course.leftPeriod) + '</div>'+
                        '<div class="detail">查看详情<i></i></div>'+
                        '</div>';
                }else{
                    html += '<span>暂无剩余课程哦~快去联系老师购买课程吧!</span>';
                }
                $("#detail").html(html).show();
                return false;
            }

        });
    });
    $("#detail").delegate(".course-info", "click", function(){
        // var category_id = $(this).attr("data-id");
        location.href = "/user/paint/list?student_id="+student_id+"&course_id="+course_id;
    });
});