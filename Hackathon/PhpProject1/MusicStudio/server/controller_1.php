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
            $result = get_cars($connection, sanitizeMYSQL($connection, $_POST["feature"]));
            break;
        case "rent":
            $result = rent_car($connection, sanitizeMYSQL($connection, $_SESSION["username"]), sanitizeMYSQL($connection, $_POST["carID"]));
            break;
        case "rented_cars":
            $result = get_rented_cars($connection);
            break;
        case "return_cars":
            $result = return_a_car($connection,sanitizeMYSQL($connection, $_POST["rental_ID"]));
            break;
        case "rental_history":
            $result = get_rental_history($connection);
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
    $query = "SELECT Usr_ID FROM Customer WHERE Usr_ID='" . $_SESSION["username"] . "'";
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

function get_cars($connection, $feature_str) {
    $final = array();
    $final["cars"] = array();
    $features = explode(", ", $feature_str);    
    //query will need all "where" criteria to be intersected
    $query = "SELECT * FROM Car "
        . "INNER JOIN CarSpecs ON Car.CarSpecsID = CarSpecs.ID "
        . "WHERE Car.Status = 1 AND (";
    
    for($i = 1; $i <= count($features); ++$i){
        if ($i == count($features)){ //last feature
            $query .= getQuery($connection, $features[$i-1]) . ")";
        }
        else
            $query .= getQuery($connection, $features[$i-1]) . " AND ";
    }
    
    $result = mysqli_query($connection, $query);
    if (!$result) 
        return json_encode($final); 
    $row_count = mysqli_num_rows($result);
    if ($row_count >= 1) { //if the car exists in the database
        for ($j = 0; $j < $row_count; ++$j){
            $row = mysqli_fetch_array($result);
            $array = array();
            $array["picture"] = 'data:'.$row["Picture_type"]. ';base64,' . base64_encode($row["Picture"]);
            $array["make"] = $row["Make"];
            $array["model"] = $row["Model"];
            $array["year"] = $row["YearMade"];
            $array["color"] = $row["Color"];
            $array["size"] = $row["Size"];
            $array["ID"] = $row[0]; //$row["ID"] will return the CarSpecs ID
            $final["cars"][] = $array;
        }
    } 
    return json_encode($final);
}

function getQuery($connection, $feature){
    $data_type = dataType($connection, $feature);
    switch($data_type){
        case "color":
            return "Car.Color LIKE '%$feature%'";
            break;
        case "model":
            return "CarSpecs.Model LIKE '%$feature%'";
            break;
        case "make":
            return "CarSpecs.Make LIKE '%$feature%'";
            break;
        case "year":
            return "CarSpecs.YearMade LIKE '%$feature%'";
            break;
        case "size":
            return "CarSpecs.Size LIKE '%$feature%'";
            break;
        case "";
            return "";
            break;
    }
}

function dataType($connection, $feature){
    if (isMake($connection, $feature)){
        return "make";
    }
    else if (isModel($connection, $feature)){
        return "model";
    }
    else if (isSize($connection, $feature)){
        return "size";
    }
    else if (isYear($connection, $feature)){
        return "year";
    }
    else if (isColor($connection, $feature)){
        return "color";
    }
    else if (isSize($connection, $feature)){
        return "size";
    }
    else{
        return ""; //user's feature is misspelled or does not exist in the database
    }
}

function isModel($connection, $feature){
    $query = "SELECT Model FROM CarSpecs";
    $result = mysqli_query($connection, $query);
    $row_count = mysqli_num_rows($result);
    for ($j = 0; $j < $row_count; ++$j){
        $row = mysqli_fetch_array($result);
        if ($feature == strtolower($row["Model"])){
            return true;
        }   
    }
    return false;
}

function isMake($connection, $feature){
    $query = "SELECT Make FROM CarSpecs";
    $result = mysqli_query($connection, $query);
    $row_count = mysqli_num_rows($result);
    for ($j = 0; $j < $row_count; ++$j){
        $row = mysqli_fetch_array($result);
        if ($feature == strtolower($row["Make"])){
            return true;
        }   
    }
    return false;
}

function isColor($connection, $feature){
    $query = "SELECT Color FROM Car";
    $result = mysqli_query($connection, $query);
    $row_count = mysqli_num_rows($result);
    for ($j = 0; $j < $row_count; ++$j){
        $row = mysqli_fetch_array($result);
        if ($feature == strtolower($row["Color"])){
            return true;
        }   
    }
    return false;
}

function isYear($connection, $feature){
    $query = "SELECT YearMade FROM CarSpecs";
    $result = mysqli_query($connection, $query);
    $row_count = mysqli_num_rows($result);
    for ($j = 0; $j < $row_count; ++$j){
        $row = mysqli_fetch_array($result);
        if ($feature == $row["YearMade"]){
            return true;
        }   
    }
    return false;
}

function isSize($connection, $feature){
    $query = "SELECT Size FROM CarSpecs";
    $result = mysqli_query($connection, $query);
    $row_count = mysqli_num_rows($result);
    for ($j = 0; $j < $row_count; ++$j){
        $row = mysqli_fetch_array($result);
        if ($feature == strtolower($row["Size"])){
            return true;
        }   
    }
    return false;
}

function rent_car($connection, $customerID, $carID){
    $query = "UPDATE Car SET Status = 2 WHERE ID = $carID";
    $result = mysqli_query($connection, $query);
    //insert into rental table with customer and car info
    $result = insert_rental($connection, $customerID, $carID);
    if(!$result)
        return "fail";
    return "success";
}

function insert_rental($connection, $customerID, $carID){
    $query = "INSERT INTO Rental(rentDate, status, CustomerID, carID) "
            . "VALUES(CURDATE(), '1', '$customerID', $carID)";
    
    $result = mysqli_query($connection, $query);
    if(!$result)
        return false; 
    return true; //insert worked
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
