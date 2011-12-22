package de.unistuttgart.ipvs.pmp.apps.vhike;

/**
 * Constants used by vHike
 * 
 * @author Alexander Wassiljew
 * 
 */
public class Constants {

	/*
	 * URL of the webservice which is used by {@link JSonRequestReader}
	 */
	public static final String WEBSERVICE_URL = "http://vhike.no-ip.org/json/";
	/**
	 * Test logins
	 */
	public static final String USER = "demo";
	public static final String PW = "test";
	// public static final String WEBSERVICE_URL
	// ="http://10.0.2.2/jsontest/call.php";

	//REGISTRATION MSG
	public static final int REG_NOT_SUCCESSFUL = 0;
	public static final int REG_STAT_REGISTERED = 1;
	public static final int REG_STAT_USED_USERNAME = 10;
	public static final int REG_STAT_USED_MAIL = 11;
	public static final int REG_STAT_INVALID_USERNAME = 20;
	public static final int REG_STAT_INVALID_PW = 21;
	public static final int REG_STAT_INVALID_FIRSTNAME = 22;
	public static final int REG_STAT_INVALID_LASTNAME = 23;
	public static final int REG_STAT_INVALID_TEL = 24;

	//STATUS MSG
	public static final int STATUS_UPDATED = 50;
	public static final int STATUS_UPTODATE = 51;
	public static final int STATUS_NOTRIP = 52;
	public static final int STATUS_HASENDED = 53;
	public static final int STATUS_INVALID_USER = 54;
	public static final int STATUS_QUERY_DELETED = 55;
	public static final int STATUS_NO_QUERY = 56;
	
	public static final int TRIP_STATUS_ANNOUNCED = 60;
	public static final int TRIP_STATUS_OPEN_TRIP = 61;
	
	// ERRORS
	public static final int STATUS_ERROR = 999;
	public static final int QUERY_ID_ERROR = 998;
	
}
