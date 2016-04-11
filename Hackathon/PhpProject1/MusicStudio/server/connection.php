<?php 

 $db_hostname = 'KC-ISIA-MYSQL1D.kc.umkc.edu';
  $db_database = 'txtd4';
  $db_username = 'txtd4';
  $db_password = 'gFDhLsgjy';
  
 $connection = mysqli_connect($db_hostname, $db_username,$db_password,$db_database);
 
 if (!$connection)
    die("Unable to connect to MySQL: " . mysqli_connect_errno());

?>
