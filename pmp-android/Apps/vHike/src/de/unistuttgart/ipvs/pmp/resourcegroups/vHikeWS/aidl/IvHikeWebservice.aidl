package de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.aidl; 

/**
 * The IvHikeWebservice interface is used to provide all functions of
 * the webservice.
 * The ivHikeWebservice can only be used if "useWebService" 
 * Privacy Setting is set to true.
**/
interface IvHikeWebservice{

	/**
	 * This method send a request to the webservice 
	 * to register an user in the database 
	 * Parameters:
     *  username - 
     *  password - 
     *  email - 
     *  firstname - 
     *  lastname - 
     *  tel - 
     *  description - 
     *  email_public - 
     *  firstname_public - 
     *  lastname_public - 
     *  tel_public - 
     * Returns:
     *   status (see vHike/webservice/design.html)
     **/
     String register(String username,
                                        String password,
                                        String email,
                                        String firstname,
                                        String lastname,
                                        String tel,
                                        String description,
                                        boolean email_public,
                                        boolean firstname_public,
                                        boolean lastname_public,
                                        boolean tel_public);
	/**
	 * Try to login into the app with given username and password 
	 * Parameters:
     *  name - username
     *  pw - password 
     * Returns:
     *  status (see vHike/webservice/design.html)
     **/
     String login(String username, String pw);
     
     /**
      * Logout the user with given session id
	  * Parameters:
      *  session_id - 
      * Returns:
      *  true, if logout succeeded
      **/
    String logout(String session_id);
	
	String getOwnProfile(String sid);
	
	String getProfile(String session_id, int id);
	
	String tripUpdatePos(String sid, int trip_id, float current_lat,
							float current_lon);
	
	String userUpdatePos(String sid, float lat, float lon);
	
	String getUserPosition(String sid, int user_id);
	
    String tripUpdateData(String sid, int trip_id, int avail_seats);
    
    String getTripPassengers();
    
    String endTrip(String sid, int trip_id);

	String startQuery(String sid,
                             String destination,
                             float current_lat,
                             float current_lon,
                             int seats);
                             
	String stopQuery(String sid, int id);
    
    String searchQuery(String sid, float lat, 
    								float lon,int perimeter);
	
	String searchRides(String sid, float lat, 
								float lon, int perimeter);
	
	String sendOffer(String sid, int trip_id, int query_id, String message);
	
	String viewOffer(String sid);
	
	String handleOffer(String sid, int offer_id, boolean accept);
	
	String pick_up(String sid, int user_id);
	
	String isPicked(String sid);
	
	String offer_accepted(String sid, int offer_id);
	
	String getHistory(String sid, String role);
	
	String rateUser(String sid, int userid, int tripid, int rating);
                                        
	/**
	 * Announce a trip to the webservice
     * Returns:
     * true if succeeded
     **/
	String announceTrip(String session_id, String destination, float current_lat,
	float current_lon, int avail_seats);

	
}