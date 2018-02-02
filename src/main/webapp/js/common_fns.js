

    function validate(value, type){
        if(value == ""){
            return false;
        }
        var reg = "";
        switch(type){
            case "mobile":
                reg = /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/;
                break;
            case "name":
                reg = /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
                break;
            case "date":
                reg = /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/;
                break;
            default:
                return false;
        }

        return reg.test(value);
    }

    function msg_alert(type, content) {
        switch(type){
            case "alert":
                layer.open({
                    content: content,
                    skin: "msg",
                    time: 2
                });
                break;
            case "confirm_one_btn":
                layer.open({
                    content:content,
                    btn: ["确定"],
                    className: "popup"
                });
                break;
            default:
                return false;
        }
    }

    function GetQueryString(name){
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }

