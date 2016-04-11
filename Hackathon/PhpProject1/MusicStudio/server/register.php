<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
require_once 'connection.php';

$p1=isset($_POST['psd'])?$_POST['psd']:"";

$p2=isset($_POST['psd2'])?$_POST['psd2']:"";
if ($p1 = $p2){
$uname=isset($_POST['usrname'])?$_POST['usrname']:"";
$fname=isset($_POST['fname'])?$_POST['fname']:"";
$lname=isset($_POST['lname'])?$_POST['lname']:"";
$email=isset($_POST['email'])?$_POST['email']:"";


$question=isset($_POST['question'])?$_POST['question']:"";
$answer=isset($_POST['answer'])?$_POST['answer']:"";

/**insert a new record into the student table*/
$SQL = "INSERT INTO Usr(Usr_ID, First_Name, Last_Name, Email, Password, Admininstrator, Priority, Security_Question, Security_Answer) VALUES(";
$SQL.="'".$uname."','".$fname."', '".$lname."', '".$email."', '".$p1."' , 0, 0,'".$question."','".$answer."')";
$result = mysqli_query($connection,$SQL);

if(!$result) //if the query fails
    die("Insertion failed: " . mysqli_error($connection));
//otherwise
echo "A new account has been added successfully";
echo '<a href="../index.html">Click here to login</a>';
}

else echo 'Password does not match, try again!';