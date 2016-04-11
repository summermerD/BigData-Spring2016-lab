<?php

include 'connection.php';

function sanitizeString($var){
    
    if(get_magic_quotes_gpc()) //get rid of unwanted slashes using magic_quotes_gpc
        $var= stripslashes($var);
    
    $var=  htmlentities($var,ENT_COMPAT, 'UTF-8'); //get rid of html entities e.g. &lt;b&gt;hi&lt;/b&gt; = <b>hi</b>
    $var= strip_tags($var); //get rid of html tags e.g. <b>
    return $var;
}

function sanitizeMYSQL($connection,$var){
    $var = mysqli_real_escape_string($connection,$var); //Escapes special characters in a string for use in an SQL statement
    $var=  sanitizeString($var);
    return $var;
}

?>
