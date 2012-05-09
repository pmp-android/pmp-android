<?php

/**
 * This service is used by an user to enable observation
 */
define('INCLUDE', true);
require('./../inc/json_framework.inc.php');

// Stop execution of script and print error message if user is not logged in
Json::printErrorIfNotLoggedIn();

try {
	$user = Session::getInstance()->getLoggedInUser();
  $zahl =	Observation::enableObservation($user);
  $output[] = array('successful' => true,
  									'obs_nr' => $zahl);
  echo Json::arrayToJson($output);
  
}catch (DatabaseException $de) {
	Json::printDatabaseError($de);
}
Database::getInstance()->disconnect();
?>
