// function birthToAge(str){
//     var r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
//     if(r == null){
//         return false;
//     }
//     var d= new Date(r[1], r[3]-1, r[4]);
//     if(d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]){
//         var Y = new Date().getFullYear();
//         return (Y-r[1]);
//     }
// }
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
                        // painting["age"] = birthToAge(data[i]["birthday"]);
                        painting["age"] = data[i]["age"];
                        break;
                    }
                }
            }
            console.log(painting);
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