<?php

    session_start();

if(isset($_SESSION['authenticated']) && $_SESSION['authenticated']) {
    header('Location: main.php');
}

if(isset($_POST['password'])) {
    if($_POST['password']=='password1') {
        $_SESSION['userlevel']=1;
        $_SESSION['authenticated']=true;
        header('Location: main.php');
    } elseif($_POST['password']=='password2') {
        $_SESSION['userlevel']=2;
        $_SESSION['authenticated']=true;
        header('Location: main.php');
    } else {
        echo "<b>Incorrect Password</b>";
    }
}

?>

<html>
<div align='center'>
<form action='index.php' method='post'>
Password: <input type='password' name='password' />
<input type="submit" value="Submit" />
</form>
</div>
</html>