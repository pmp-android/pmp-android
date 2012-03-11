<?php
/**
 * This service is used to announce a new trip
 *
 * GET  Parameters:
 * - sid Session ID
 *
 * POST Parameters:<br/>
 * - <b>id</b> <i>(optional)</i> The trip ID. If trip ID is not provided, this script will end all open trips of the logged in user (which there should not be more than one).<br/>
 * - current_lat (optional)
 * - current_lon (optional)
 * - avail_seats
 * - destination
 *
 * @return string A JSON response
 * - <b>error</b> Error string if available
 * - <b>msg</b> Error message if available
 * - <b>status</b> If the action was successful
 * <pre>
 * 'announced'
 * 'open_trip_exists'
 * </pre>
 *
 * @author  Patrick Strobel, Dang Huynh
 * @version 1.2
 * @package vhike.services
 */
/**
 * @ignore
 */
define('INCLUDE', true);
/**
 * @ignore
 */
require('./../inc/json_framework.inc.php');

// Stop execution of script and print error message if user is not logged in
Json::printErrorIfNotLoggedIn();

try {
	// Set user input. Cancel if there's a invalid value in a input string
	$driver = Session::getInstance()->getLoggedInUser();
	if (General::validateLatitude('current_lat') && General::validateLongitude('current_lon')) {
		$driver->updatePosition($_POST['current_lat'], $_POST['current_lon']);
	}
	if (!General::validateId('avail_seats')) {
		throw new InvalidArgumentException('avail_seats');
	}
	$trip = Trip::create($driver, $_POST['avail_seats'], $_POST['destination']);

	$output = array('status' => 'announced',
					'id'	 => $trip->getId());
	echo Json::arrayToJson($output);

} catch (InvalidArgumentException $iae) {
	if ($iae->getCode() == Trip::OPEN_TRIP_EXISTS) {
		echo Json::arrayToJson(array('status' => 'open_trip_exists'));
	} else {
		Json::printInvalidInputError();
	}
} catch (DatabaseException $de) {
	Json::printDatabaseError($de);
}
Database::getInstance()->disconnect();

// EOF