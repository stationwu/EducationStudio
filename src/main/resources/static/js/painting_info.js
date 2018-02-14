
$(function(){
    $.ajax({
        type: "GET",
        url: "/api/v1/Customer/Detail",
        dataType: "json",
        success: function(data){
            var children = data.children;
            console.log(children);
            var student_html = "";
            for(var i=0; i<children.length; i++){
                student_html += showStudent(children[i].id, children[i].childName, children[i].birthday, children[i].gender);
            }
            $("#student-list").prepend(student_html);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    function showStudent(id, name, birth, gender) {
        var html = '<div class="student-info" data-id="' + id + '">' +
            '<div class="content">' +
            '<div class="thumbnail ' + (gender=="BOY"?"boy":"girl") + '"></div>' +
            '<div class="name">' + name + '</div>' +
            '<div class="birth"><i></i>' + birth + '</div>' +
            '<div class="add-class-btn">去约课</div>' +
            '</div>' +
            '</div>';
        return html;
    }

    $("#student-list").delegate(".add-class-btn", "click", function(){
        var id = $(this).parents(".student-info").attr("data-id");
        location.href = "/user/course/book?id="+id;
        return false;
    });
});