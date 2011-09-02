package de.unistuttgart.ipvs.pmp.service.resource;


/**
 * The Service of a ResourceGroup provided for PMP.
 *
 * @author Jakob Jarosch
 */
interface IResourceGroupServicePMP {

	/**
	 * @return Returns the name of the ResourceGroup.
	 */
	String getName(String locale);
	
	/**
	 * @return Returns the description of the ResourceGroup.
	 */
	String getDescription(String locale);
	
	/**
	 * @return Returns the privacy levels for the ResourceGroup.
	 */
	List getPrivacyLevelIdentifiers();
	
	/**
	 * @return the name of the privacy level identified by identifier for locale locale,
	 *  null if the privacy level is not present.
	 */
	String getPrivacyLevelName(String locale, String identifier);
	
	/**
	 * @return the description of the privacy level identified by identifier for locale locale,
	 *  null if the privacy level is not present.
	 */
	String getPrivacyLevelDescription(String locale, String identifier);
	
	/**
	 * @return the human readable representation of the privacy level value identified by 
	 *  identifier and value for locale locale, null if the privacy level is not present.
	 */
	String getHumanReadablePrivacyLevelValue(String locale, String identifier, String value);

	/**
	 * Accepts the access configuration for the resource group.
	 * Exact declaration: List&lt;ResourceGroupAccess&gt;
	 */
	void setAccesses(in List accesses);
	
	/**
	 * Checks if an privacy level value satisfies the currently set value or not.
	 * 
	 * @param reference the original value which should be referenced for testing
	 * @param value the value which should be compared to reference
	 *
	 * @return Returns true if value is equal or better than reference, otherwise false
	 */
	boolean satisfiesPrivacyLevel(String privacyLevel, String reference, String value);
	
	/**
	 * Opens the Activity for changing the PrivacyLevels
	 * and returns the results over the PMPService.
	 *
	 * @param appIdentifier The Identifier of the App.
	 */
	void changePrivacyLevels(String appIdentifier);
	
}