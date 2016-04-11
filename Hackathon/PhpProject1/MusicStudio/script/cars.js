$(document).ready(init);

function init() {
    show_name(); //get the user's name for display
    rent_car(); //rent car of choice
    $("#logout-link").on("click",logout);
    $("#find-car").on("click",search);
    show_rented_cars(); //show the rented cars
    show_rental_history();//show the rental history
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
    $("#user_loading").attr("class", "user_loading"); //show the loading icon
    $("#search_results").html("");
    var html = ""; //will be HTML for all car candidates
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "json",
        data: {type: "search", feature: $("#find-car-input").val().toLowerCase().trim()}, 
        success: function (data){
            if (data["cars"].length > 0){
                var html_maker = new htmlMaker($("#find-car-template").html());
                html += html_maker.getHTML(data["cars"]);
                $("#search_results").html(html);
                $("#user_loading").attr("class", "user_loading_hidden"); //hide the loading icon
            }
        }
    });
}

function rent_car(){
    $("body").on("click", "div.car_rent", function(){ //special case where element is dynamic
        var carID = $(this).attr("id");
        $.ajax({
            method: "POST",
            url: "server/controller.php",
            dataType: "text",
            data: {type: "rent", carID: carID},
            success: function (data) {
                if(data.search("success") !== -1){
                    search();
                    show_rented_cars();
                    alert("The car has been rented successfully!");
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

function show_rented_cars(){
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "json",
        data: {type: "rented_cars"},
        success: function (data) {
            if (data["rented_car"].length > 0){ //if at least 1 car is rented, get HTML
                var info_template=$("#rented-car-template").html();
                var html_maker=new htmlMaker(info_template);
                var html=html_maker.getHTML(data);
                $("#rented_cars").html(html);
                $("div.return_car").on("click", function(){return_A_car(this);});
            }
            else
                $("#rented_cars").html("");
        }
    });    
}

function show_rental_history(){
    $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "json",
        data: {type: "rental_history"},
        success: function (data) {
            if (data["returned_car"].length > 0){ //if at least 1 car is in rental history, get HTML
                var info_template=$("#returned-car-template").html();
                var html_maker=new htmlMaker(info_template);
                var html=html_maker.getHTML(data);
                $("#returned_cars").html(html);
            }
        }
    });    
}

function return_A_car(car){
     var rental_ID=$(car).attr("data-rental-id");
     $.ajax({
        method: "POST",
        url: "server/controller.php",
        dataType: "text",
        data: {type: "return_cars",rental_ID: rental_ID},
        success: function (data) {
            if ($.trim(data) === "success") {
                show_rented_cars(); //refresh
                show_rental_history();
                search();
                alert("The car has been returned successfully");
            }
        }
       
    }); 
}
