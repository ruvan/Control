<?php
session_start();
if(!isset($_SESSION['authenticated']) || $_SESSION['authenticated']!=true)  {
    header('Location: index.php');
}

if(isset($_POST['config'])) {
    echo "got config";
}

// Read and display config file
if($_SESSION['userlevel']==1) {
    $config=fopen("config.properties","r") or exit("Unable to open config file!");
    $lastModTime = fgets($config);
    echo "Last modification time " . $lastModTime;
    while (!feof($config)) {
        $line = explode("=",fgets($config));
        echo $line[0] . " = " . $line[1] . "<br>";
    }
    fclose($config);
}

?>