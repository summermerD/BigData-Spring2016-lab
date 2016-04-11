<?php

include "sanitization.php";
session_start(); //start the session
$result = "";

//only process the data if there a request was made and the session is active
if (isset($_POST['type']) && is_session_active()) {
    // session_regenerate_id(); //regenerate the session to prevent fixation
    $_SESSION['start'] = time(); //reset the session start time
    $request_type = sanitizeMYSQL($connection, $_POST['type']);

    switch ($request_type) { //check the request type
        case "username":
            $result = get_name($connection);
            break;
        case "search":
            $result = get_booking($connection);
            break;
        case "approval":
            $result = approval($connection, sanitizeMYSQL($connection, $_POST["Booking_ID"]));
            break;
        case "decline":
            $result = approval($connection, sanitizeMYSQL($connection, $_POST["Booking_ID"]));
            break;
        case "logout":
            logout();
            $result = "success";
            break;
    }
}

echo $result;

function is_session_active() {
    return isset($_SESSION) && count($_SESSION) > 0 && time() < $_SESSION['start'] + 60 * 5; //check if it has been 5 minutes
}

function get_name($connection) {
    $text = "";
    $query = "SELECT Usr_ID FROM usr WHERE usr_ID='" . $_SESSION["username"] . "'";
    $result = mysqli_query($connection, $query);
    if (!$result)
        return $text;
    else {
        $row_count = mysqli_num_rows($result);
        if ($row_count == 1) { //the customer exists in the database
            $row = mysqli_fetch_array($result);
            $text .= $row["Usr_ID"];
        }
    }
    return $text;
}

function get_booking($connection) {
    $final = array();
    $final["bookings"] = array();
    //query will need all "where" criteria to be intersected
    $query = "SELECT * FROM Booking "
        . "INNER JOIN usr ON usr.usr_ID = booking.usr_ID "
        . "WHERE Status = 1";
   
    $result = mysqli_query($connection, $query);
    if (!$result) 
        return json_encode($final); 
    $row_count = mysqli_num_rows($result);
    if ($row_count >= 1) { //if the car exists in the database
        for ($j = 0; $j < $row_count; ++$j){
            $row = mysqli_fetch_array($result);
            $array = array();
            $array["Booking_ID"] = $row["Booking_ID"];
            $array["Studio_ID"] = $row["Studio_ID"];
            $array["StartFrom"] = $row["StartFrom"];
            $array["EndTo"] = $row["EndTo"];
            $array["Usr_ID"] = $row["Usr_ID"];
            $array["Request_Time"] = $row["Request_Time"];
            $array["Priority"] = $row["Priority"];

//            $array["ID"] = $row[0]; //$row["ID"] will return the CarSpecs ID
            $final["cars"][] = $array;
        }
    } 
    return json_encode($final);
}

function approval($connection, $Booking_ID){
    $query = "UPDATE Booking SET Status = 2 WHERE Booking_ID = $Booking_ID";
    $result = mysqli_query($connection, $query);
    if(!$result)
        return "fail";
    return "success";
}

function decline($connection, $Booking_ID){
    $query = "UPDATE Booking SET Status = -1 WHERE Booking_ID = $Booking_ID";
    $result = mysqli_query($connection, $query);
    if(!$result)
        return "fail";
    return "success";
}

function logout() {
    // Unset all of the session variables.
    $_SESSION = array();

// If it's desired to kill the session, also delete the session cookie.
// Note: This will destroy the session, and not just the session data!
    if (ini_get("session.use_cookies")) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', time() - 42000, $params["path"], $params["domain"], $params["secure"], $params["httponly"]
        );
    }

// Finally, destroy the session.
    session_destroy();
}

function get_rented_cars($connection) {
    $final = array();
    $final["rented_car"] = array();
    //write a query about the rented cars for a customer. The customer ID is from the session
    $query = "SELECT car.Picture, car.Picture_Type, carspecs.Make, carspecs.Model, carspecs.YearMade,"
            . "carspecs.Size, rental.ID, rental.rentDate"
            . " FROM car INNER JOIN carspecs ON carspecs.ID=car.CarSpecsID"
            . " INNER JOIN rental ON car.ID=rental.carID"
            . " INNER JOIN customer ON customer.ID=rental.CustomerID"
            . " WHERE rental.status=1 AND customer.ID='" . $_SESSION["username"] . "'";
           
    $result = mysqli_query($connection, $query);
    if (!$result)
        return json_encode($array);
    else {
        $row_count = mysqli_num_rows($result);
        for ($i = 0; $i < $row_count; $i++) {
            $row = mysqli_fetch_array($result);
            $array = array();
            $array["picture"] = 'data:' . $row["Picture_Type"] . ';base64,' . base64_encode($row["Picture"]);
            $array["make"] = $row["Make"];
            $array["model"] = $row["Model"];
            $array["year"] = $row["YearMade"];
            $array["size"]= $row["Size"];
            $array["rental_ID"] = $row["ID"];
            $array["rent_date"]=$row["rentDate"];
            $final["rented_car"][] = $array;
        }
    }
    return json_encode($final);
}

function return_a_car($connection, $rental_ID){
    $query= "UPDATE rental INNER JOIN car on car.ID=rental.carID "
            . "SET rental.status='2',car.Status='1', returnDate=CURDATE() "
            . "WHERE rental.ID='$rental_ID' AND rental.CustomerID='" . $_SESSION["username"] . "'" ;   
    
    $result = mysqli_query($connection, $query);
    if (!$result){
        return "fail";
    }
    return "success";
}

function get_rental_history($connection) {
    $final = array();
    $final["returned_car"] = array();
    //write a query about the rental history for a customer. The customer ID is from the session
    $query = "SELECT car.Picture, car.Picture_Type, carspecs.Make, carspecs.Model, carspecs.YearMade,"
            . "carspecs.Size, rental.ID, rental.returnDate"
            . " FROM car INNER JOIN carspecs ON carspecs.ID=car.CarSpecsID"
            . " INNER JOIN rental ON car.ID=rental.carID"
            . " INNER JOIN customer ON customer.ID=rental.CustomerID"
            . " WHERE rental.status=2 AND customer.ID='" . $_SESSION["username"] . "'";
           
    $result = mysqli_query($connection, $query);
    if (!$result)
        return json_encode($array);
    else {
        $row_count = mysqli_num_rows($result);
        for ($i = 0; $i < $row_count; $i++) {
            $row = mysqli_fetch_array($result);
            $array = array();
            $array["picture"] = 'data:' . $row["Picture_Type"] . ';base64,' . base64_encode($row["Picture"]);
            $array["make"] = $row["Make"];
            $array["model"] = $row["Model"];
            $array["year"] = $row["YearMade"];
            $array["size"]= $row["Size"];
            $array["rental_ID"] = $row["ID"];
            $array["return_date"]=$row["returnDate"];
            $final["returned_car"][] = $array;  
        }
    }
    return json_encode($final);
}

?>
