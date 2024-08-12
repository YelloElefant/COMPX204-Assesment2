<?php
parse_str(implode('&', array_slice($argv, 1)), $_GET);

echo "yoza from test.php";
echo "<br>";	
echo $_GET["test"];