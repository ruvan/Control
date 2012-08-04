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
    $lastModTime = explode(" ",fgets($config));
    echo "<table border='1'>";
    //echo "Last modification time " . $lastModTime;
    echo "<form action='main.php' method='post'> <br> \n";
    while (!feof($config)) {
        $line = explode("=",fgets($config));
        echo "<tr> <td> $line[0] </td> <td> <input type='text' name='$line[0]' value='$line[1]' /> </td> </tr> \n";
    }
    echo "<tr> <td> Last mod: $lastModTime[1] $lastModTime[2] $lastModTime[3]</td> <td> <input type='submit' value='Submit' /> </td>  </tr> \n </table>";
    fclose($config);
}

?>