$(function(){
    var id = GetQueryString("id");
    $.ajax({
        type: "GET",
        url: "/api/v1/Student",
        dataType: "json",
        success: function(data){
            var painting = {};
            for(var i=0; i<data.length; i++){
                for(var j=0; j<data[i].paints.length; j++){
                    if(data[i].paints[j]["id"] == id){
                        painting = data[i].paints[j];
                        painting["name"] = data[i]["childName"];
                        painting["age"] = data[i]["age"];
                        break;
                    }
                }
            }
            var html = '<div class="name">' + painting.imageName + '</div>'+
                '<div class="content">'+
                '<img src="' + painting.imageUrl + '" />'+
                '</div>'+
                '<div class="info">'+
                '<div class="student_name">姓名：<span>' + painting.name + '</span></div>'+
                '<div class="age">年龄：<span>' + painting.age + '岁</span></div>'+
                '<div class="material">材料：<span>' + painting.material + '</span></div>'+
                '<div class="teacher">指导老师：<span>' + painting.teacher + '</span></div>'+
                '<div class="date">创作时间：<span>' + painting.date.split(" ")[0].split("-").join(".") + '</span></div>'+
                '</div>';

            $("#painting-info").append(html);

        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });
});