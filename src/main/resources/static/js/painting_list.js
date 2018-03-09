$(function(){
    var student_id = GetQueryString("student_id");
    var course_id = GetQueryString("course_id");
    var course_name;

    $.ajax({
        type: "GET",
        url: "/api/v1/Student",
        dataType: "json",
        success: function(data){
            var paintings = {};
            if(student_id != null && course_id!=null){
                for(var i=0; i<data.length; i++){
                    if(student_id == data[i].id){
                        for(var j=0; j<data[i].courseCategories.length; j++){
                            if(course_id == data[i].courseCategories[j].id){
                                course_name = data[i].courseCategories[j].courseName;
                                break;
                            }
                        }
                        document.title = course_name+"作品";
                        for(var j=0; j<data[i].paints.length; j++){
                            if(course_id == data[i].paints[j].courseCategoryId){
                                var date = data[i].paints[j]["date"];
                                date = date.split(" ")[0].split("-").join(".");
                                var painting = {
                                    "id": data[i].paints[j]["id"],
                                    "thumbnail": data[i].paints[j]["thumbnailUrl"],
                                    "material": data[i].paints[j]["material"],
                                    "teacher": data[i].paints[j]["teacher"],
                                    "name": data[i].paints[j]["imageName"],
                                    "date": date
                                };
                                date = date.split(".")[0]+"年"+date.split(".")[1]+"月";
                                if(!paintings[date]){
                                    paintings[date] = [];
                                }
                                paintings[date].push(painting);
                            }
                        }
                        break;
                    }
                }
            }else{
                for(var i=0; i<data.length; i++){
                    for(var j=0; j<data[i].paints.length; j++){
                        var date = data[i].paints[j]["date"];
                        date = date.split(" ")[0].split("-").join(".");
                        var painting = {
                            "id": data[i].paints[j]["id"],
                            "thumbnail": data[i].paints[j]["thumbnailUrl"],
                            "material": data[i].paints[j]["material"],
                            "teacher": data[i].paints[j]["teacher"],
                            "name": data[i].paints[j]["imageName"],
                            "date": date
                        };
                        date = date.split(".")[0]+"年"+date.split(".")[1]+"月";
                        if(!paintings[date]){
                            paintings[date] = [];
                        }
                        paintings[date].push(painting);
                    }
                }
            }
            var html = "";
            for(var i in paintings) {
                html += '<div class="date">' + i + '</div>';
                for(var j=0; j<paintings[i].length; j++){
                    html += '<div class="painting-info" data-id="' + paintings[i][j]["id"] + '">' +
                        '<div class="painting"><img src="' + paintings[i][j]["thumbnail"] + '" /></div>' +
                        '<div class="name">' + paintings[i][j]["name"] + '</div>' +
                        '<div class="material">材料:' + paintings[i][j]["material"] + '</div>' +
                        '<div class="teacher">指导老师:' + paintings[i][j]["teacher"] + '</div>' +
                        '<div class="date">创建时间：' + paintings[i][j]["date"] + '</div>' +
                        '<i class="detail"></i>' +
                        '</div>';
                }
            }

            $("#painting-list").append(html);

            $('.painting img').each(function() {
                $(this).load(function(){
                    var height = $(this)[0].height;
                    var width = $(this)[0].width;
                    if(width > height){
                        $(this).css("height","100%");
                    }else{
                        $(this).css("width","100%");
                    }
                });
            });
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    $("#painting-list").delegate(".painting-info", "click", function(){
        var id = $(this).attr("data-id");
        location.href = "/user/paint/info?id="+id;
        return false;
    });
});