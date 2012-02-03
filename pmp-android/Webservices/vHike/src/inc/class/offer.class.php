<?php
if (!defined("INCLUDE")) {
    exit;
}

class OfferException extends Exception {
    const EXISTS_ALREADY = 0;
    const QUERY_NOT_FOUND = 1;
    const INVALID_TRIP = 2;
}

/**
 * Handles offers and allows to create a new offer
 * @author Patrick Strobel 
 * @version 1.0.2
 */
class Offer {
    
    /** @var int */
    private $id = -1;
    
    /** @var Trip */
    private $trip = null;
    
    /** @var int */
    private $queryId = -1;
    
    /** @var String */
    private $message = null;
    
    /** @var float */
    //private $current_lat = null;
    /** @var float */
    //private $current_lon = null;
    
    private function __construct() { }
    
    /**
     * Loads information for the given offer-id from the databse
     * @param int $id   ID of the offer to load from the database
     * @return Offer    Offer object storing the loaded information 
     */
    public static function loadOffer($id) {
        if (!General::validId($id)) {
            throw new InvalidArgumentException("The offer ID is invalid");
        }
        
        $db = Database::getInstance();
        $row = $db->fetch($db->query("SELECT 
                                        o.`id` AS oid, o.`query`, o.`message`, 
                                        t.*, t.`id` AS tid,
                                        u.*, u.`id` AS uid
                                      FROM 
                                        `".DB_PREFIX."_offer` AS o, 
                                        `".DB_PREFIX."_trip` AS t, 
                                        `".DB_PREFIX."_user` AS u      
                                      WHERE o.`id` = ".$id."
                                      AND o.`trip` = t.`id`
                                      AND t.`driver` = u.`id`"));
        /*echo "SELECT
                                        u.*, u.`id` AS uid,
                                        o.`id` AS oid, o.`query`, o.`message`,
                                        t.`id` AS tid
                                      FROM
                                        `".DB_PREFIX."_offer` AS o,
                                        `".DB_PREFIX."_trip` AS t,
                                        `".DB_PREFIX."_user` AS u
                                      WHERE o.`trip` = t.`id`
                                      AND t.`driver` = u.`id`
                                      AND o.`id` = $id";*/
        
        if ($db->getAffectedRows() <= 0) {
            return null;
        }
        
        $offer = new Offer();
        
        $offer->trip = Trip::loadTripBySqlResult($row, "tid", "uid");
        $offer->id = $row["oid"];
        $offer->message = $row["message"];
        $offer->queryId = $row["query"];
        /*
        $offer->current_lat = $row["lat"];
        $offer->current_lon = $row["lon"];
         */
        return $offer;
    }
    
    /**
     * Load's all messages that have been send to a given user's queries.
     * E.g. if a user has opened 3 queries, this will return all offers
     * that have been send to one of these queries.
     * @param User $inquirer  Person's id for which offers should be searched
     * @return Offer[]  All offers that have been send to the given inquirer or
     *                  null if no offers where found
     */
    public static function loadOffers($inquirer) {
        if (!($inquirer instanceof User)) {
            throw new InvalidArgumentException("Parameter is of wrong type");
        }
        
        $db = Database::getInstance();
        /* , 
                                t.`current_lat` AS lat,
                                t.`current_lon` AS lon*/
        $query = $db->query("SELECT
                                u.*, u.`id` AS uid,
                                o.`id` AS oid, o.`query`, o.`message`, 
                                t.*, t.`id` AS tid
                             FROM 
                                `".DB_PREFIX."_offer` AS o, 
                                `".DB_PREFIX."_query` AS q, 
                                `".DB_PREFIX."_trip` AS t, 
                                `".DB_PREFIX."_user` AS u 
                             WHERE o.`query` = q.`id`
                             AND o.`trip` = t.`id`
                             AND t.`driver` = u.`id`
                             AND q.`passenger` = ".$inquirer->getId());

        $offers = array();
        
        while (($row = $db->fetch($query)) != null) {
            $offer = new Offer();
            $offer->trip = Trip::loadTripBySqlResult($row, "tid", "uid");
            $offer->tripId = $row["tid"];
            $offer->id = $row["oid"];
            $offer->queryId = $row["query"];
            $offer->message = $row["message"];
            /*$offer->current_lat = $row["lat"];
            $offer->current_lon = $row["lon"];*/
            $offers[] = $offer;
        }
        
        return $offers;
    }
    
    /**
     *
     * @param Query $query  Query to create the offer for
     * @param Trip $trip    Trip for which the driver wants to create the offer
     * @param User $driver  Driver that wants to create the offer
     * @param String $message Message to the receiver
     * @return int  ID of the new offer
     * @throws InvalidArgumentException Thrown, if one of the parameters is of a wrong type
     * @throws OfferException Thrown, if the given driver has already send a offer for the given query 
     *                          (code = ALREADY_EXISTS) or if the trip does not belong to the given driver
     *                          (code = INVALID_TRIP)
     */
    public static function make($query, $trip, $driver, $message) {
        if (!($query instanceof Query) || !($trip instanceof Trip) || !($driver instanceof User)) {
            throw new InvalidArgumentException("At least one parameter is of wrong type");
        }
        
        if (!$driver->isEqual($trip->getDriver()) || $trip->hasEnded()) {
            throw new OfferException("The given trip does not belong to the given driver or has ended", OfferException::INVALID_TRIP);            
        }
        
        if (self::offerExists($query,$trip)) {
            throw new OfferException("The given user has already send an offer for the given query", OfferException::EXISTS_ALREADY);
        }
        
        // Write data into table
        $db = Database::getInstance();
        
        $db->query("INSERT INTO `".DB_PREFIX."_offer` (
                        `trip`,
                        `query`,
                        `message`
                    ) VALUES (
                        ".$trip->getId().",
                        ".$query->getId().",
                        \"".$message."\"
                    )");
        
       return $db->getId();      
    }
    
    /**
     * Checks if the given driver has already sent an offer for the given query
     * @param Query $query  
     * @param Trip $trip
     * @return boolean  True, if there's already an offer with the given details
     * @throws InvalidArgumentException Thrown, if one of the parameters is of wrong type
     */
    public static function offerExists($query, $trip) {        
        if (!($query instanceof Query) || !($trip instanceof Trip)) {
            throw new InvalidArgumentException("At least one parameter is of wrong type");
        }
        
        $db = Database::getInstance();
        $count = $db->fetch($db->query("SELECT count(*) AS count 
                                        FROM `".DB_PREFIX."_offer`
                                        WHERE `trip` = ".$trip->getId()." 
                                        AND `query` = ".$query->getId()));
        
        return $count["count"] > 0;
    }
  
    /**
     * Returns the offer's id
     * @return int 
     */
    public function getId() {
        return $this->id;
    }

    /**
     * Returns the driver's current latitude.
     * @return float The latitude 
     * @deprecated Use getTrip()->getCurrentLat() instead!
     */
    public  function getCurrentLat(){
        return $this->trip->getCurrentLat();
    }

    
    /**
     * Returns the driver's current longitude.
     * @return float The longitude 
     * @deprecated Use getTrip()->getCurrentLon() instead!
     */
    public  function getCurrentLon(){
        return $this->trip->getCurrentLon();
    }

    /**
     * Return the driver for this offer
     * @return User The driver
     * @deprecated Use getTrip()->getDriver() instead!
     */
    public function getDriver() {
        return $this->trip->getDriver();
    }
    
    /**
     * Returns the query belonging to this offer
     * @return Query Query-Object or null if the query couldn't be loaded 
     */
    public function getQuery() {
        echo "Queryid: ".$this->queryId;
        return Query::loadQuery($this->queryId);
    }
    /**
     * Returns the query belonging to this offer
     * @return int Query's id
     */
    public function getQueryId() {
        return $this->queryId;
    }
    
    /**
     * Returns the trip this offer belongs to
     * @return Trip The trip 
     */
    public function getTrip() {
        return $this->trip;
    }
    
    /**
     *
     * @return String 
     */
    public function getMessage() {
        return $this->message;
    }
      
    public function accept() {
        $db = Database::getInstance();
        $db->query("INSERT INTO `".DB_PREFIX."_ride` (
                        `passenger`,
                        `trip`
                    ) VALUES (
                        \"".$this->getQuery()->getId()."\",
                        \"".$this->tripId."\"
                    )");
        $this->delete();
    }
    
    public function deny() {
        $this->delete();
    }


    /**
     * Deletes this offer from the table
     */
    private function delete() {
        $db = Database::getInstance();
        $db->query("DELETE FROM  `".DB_PREFIX."_offer` WHERE `id` = '".$this->id."'");
    }
}
?>
