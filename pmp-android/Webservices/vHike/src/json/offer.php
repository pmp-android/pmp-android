<?php

/**
 * This service is used by a driver to send an offer for a given query
 */
define('INCLUDE', TRUE);
require('./../inc/json_framework.inc.php');

// Stop execution of script and print error message if user is not logged in
Json::printErrorIfNotLoggedIn();

try {
	// Load data for the given parameters^^
	$trip = Trip::loadTrip($_POST['trip']);
	$query = Query::loadQuery($_POST['query']);
	$user = Session::getInstance()->getLoggedInUser();

	// Show message if there's no data for the given values
	if ($trip == NULL) {
		$status = 'invalid_trip';
	} elseif ($query == NULL) {
		$status = 'invalid_query';
	} else {
		$id = Offer::make($query, $trip, $user, $_POST['message']);
		$status = 'sent';
	}

	$output = array('successful' => TRUE,
	                'status'     => $status);
	if (isset($id)) {
		$output['offer_id'] = $id;
	}

	echo Json::arrayToJson($output);

} catch (OfferException $oe) {
	// Show message if the given parameter don't match
	switch ($oe->getCode()) {
		case OfferException::INVALID_TRIP:
			$status = 'invalid_trip';
			break;
		case OfferException::EXISTS_ALREADY:
			$status = 'already_sent';
			break;
	}
	$output = array('successful' => TRUE,
	                'status'     => $status,
	                'message'    => $oe->getMessage());
	echo Json::arrayToJson($output);
} catch (InvalidArgumentException $iae) {
	Json::printInvalidInputError();
} catch (DatabaseException $de) {
	Json::printDatabaseError($de);
}
Database::getInstance()->disconnect();

// EOF offer.php