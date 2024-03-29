<?php
/**
 * This service is used to rate a driver or a passenger that have participated
 * on an ended trip.
 */
define('INCLUDE', TRUE);
require ('./../inc/json_framework.inc.php');

// Stop execution of script and print error message if user is not logged in
Json::printErrorIfNotLoggedIn();

try {

	if (General::validateId('user_id')) {
		$user_id = $_POST['user_id'];
		$driver = Session::getInstance()->getLoggedInUser();

		$trip_id = $driver->getCurrentTripId();
		if ($trip_id) {
			if (Ride::pick_up($user_id, $trip_id)) {
				$output = array('successful' => TRUE);
			} else {
				$output = array('successful' => FALSE,
				                'error'      => 'not_picked_up',
				                'msg'        => 'Not able to pick up the user');
			}
		} else {
			$output = array('successful' => FALSE,
			                'error'      => 'no_trip',
			                'msg'        => 'No trip found');
		}
		echo Json::arrayToJson($output);
	}

} catch (InvalidArgumentException $iae) {
	Json::printInvalidInputError();
} catch (DatabaseException1 $de) {
	Json::printDatabaseError($de);
}
Database::getInstance()->disconnect();

// EOF