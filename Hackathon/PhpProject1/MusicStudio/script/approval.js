$(document).ready(init);

function init() {
    show_name(); //get the user's name for display
    approval(); 
    decline();
    $("#logout-link").on("click",logout);
    search();
}

function show_name(){
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "text",
        data: {type: "username"},
        success: function (data){
            $("#username").html(data);
        }
    });
}

function search(){
    $("#search_results").html("");
    var html = ""; //will be HTML for all car candidates
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "json",
        data: {type: "search"}, 
        success: function (data){
            if (data["bookings"].length > 0){
                var html_maker = new htmlMaker($("#find-request-template").html());
                html += html_maker.getHTML(data["bookings"]);
                $("#search_results").html(html);
            }
        }
    });
}

function approval(){
    $("body").on("click", "div.approval", function(){ 
        var Booking_ID = $(this).attr("Booking_ID");
        $.ajax({
            method: "POST",
            url: "server/controller.php",
            dataType: "text",
            data: {type: "approval", Booking_ID: Booking_ID},
            success: function (data) {
                if(data.search("success") !== -1){
                    search();
                    alert("The studio has been approved successfully!");
                }
            }
        });
    });
}

function decline(){
    $("body").on("click", "div.decline", function(){ 
        var Booking_ID = $(this).attr("Booking_ID");
        $.ajax({
            method: "POST",
            url: "server/controller.php",
            dataType: "text",
            data: {type: "decline", Booking_ID: Booking_ID},
            success: function (data) {
                if(data.search("success") !== -1){
                    search();
                    alert("The studio has been declined successfully!");
                }
            }
        });
    });
}

function logout() {
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "text",
        data: {type: "logout"},
        success: function () {
            window.location.assign("index.html"); //redirect the page to index.html
        }
    });
}


