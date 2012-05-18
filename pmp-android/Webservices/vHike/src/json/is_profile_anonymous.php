<?php
    /**
     * This service is used identify anonymous profiles
     */
    define("INCLUDE", true);
    require ("./../inc/json_framework.inc.php");
    
    // Stop execution of script and print error message if user is not logged in
    Json::printErrorIfNotLoggedIn();
    
    try {
		$user = Session::getInstance() -> getLoggedInUser();
		$ret = User::isProfileAnonymous($user->getID());
		$output = array("successful" => true, "anonymous" => $ret);
		echo Json::arrayToJson($output);
        
    } catch (InvalidArgumentException $iae) {
        Json::printInvalidInputError();
    } catch (DatabaseException1 $de) {
        Json::printDatabaseError($de);
    }
    Database::getInstance() -> disconnect();
    
    // EOF