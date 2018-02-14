
$(function(){
    $.ajax({
        type: "GET",
        url: "/api/v1/Student",
        dataType: "json",
        success: function(data){
            var paintings = {};
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
            // console.log(paintings);
            var html = "";
            for(var i in paintings) {
                // console.log(paintings[i])
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

            // console.log(html);
            $("#painting-list").append(html);
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