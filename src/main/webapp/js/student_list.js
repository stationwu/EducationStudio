
$(function(){
    $.ajax({
        type: "GET",
        url: "/api/v1/Customer/Detail",
        dataType: "json",
        // contentType: "application/json",
        // data: JSON.stringify(children),
        success: function(data){
            var children = data;
            console.log(children);
        },
        error: function(){
            msg_alert("alert", "错误，请稍后重试");
        }
    });

    // "/api/v1/Customer/Detail": {
    //     "get": {
    //         "tags": [
    //             "customer-controller"
    //         ],
    //             "summary": "getCustomer",
    //             "operationId": "getCustomerUsingGET",
    //             "consumes": [
    //             "application/json"
    //         ],
    //             "produces": [
    //             "*/*"
    //         ],
    //             "responses": {
    //             "200": {
    //                 "description": "OK",
    //                     "schema": {
    //                     "$ref": "#/definitions/CustomerContainer"
    //                 }
    //             },
    //             "401": {
    //                 "description": "Unauthorized"
    //             },
    //             "403": {
    //                 "description": "Forbidden"
    //             },
    //             "404": {
    //                 "description": "Not Found"
    //             }
    //         }
    //     }
    // },
});