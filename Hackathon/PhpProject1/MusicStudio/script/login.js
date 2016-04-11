$(document).ready(init);

function init(){
    $("#search-button").on("click",login);
    $("#search-button2").on("click",login2);
    $("#password-input").on("keydown",function(event){maybe_login(event);});
}

function maybe_login(event){
    if (event.keyCode === 13) //ENTER KEY
        login();
}

function login() {
    $("#loading").attr("class","loading"); //show the loading icon
    $.ajax({
        method: "POST",
        url: "server/login_session.php",
        dataType: "text",
        data: new FormData($("#login_form")[0]),
        processData: false,
        contentType: false,
        success: function (data) {
            if($.trim(data)==="success"){
                window.location.assign("weekcalenda/weekcalendar.html"); //redirect the page to cars.html
            }
            else{
                $("#loading").attr("class","loading_hidden"); //hide the loading icon
                $("#login_feedback").html("Invalid username or password"); //show feedback
            }
        }
    });
    
    
}

function login2() {
    $("#loading").attr("class","loading"); //show the loading icon
    $.ajax({
        method: "POST",
        url: "server/login_session_admin.php",
        dataType: "text",
        data: new FormData($("#login_form2")[0]),
        processData: false,
        contentType: false,
        success: function (data) {
            if($.trim(data)==="success"){
                window.location.assign("approval.html"); //redirect the page to cars.html
            }
            else{
                $("#loading").attr("class","loading_hidden"); //hide the loading icon
                $("#login_feedback2").html("Invalid username or password"); //show feedback
            }
        }
    });




}


