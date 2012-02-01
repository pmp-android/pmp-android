package de.unistuttgart.ipvs.pmp.xmlutil.app;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class PrivacySetting {

	/**
	 * The identifier of the Privacy Setting
	 */
	private String identifier;

	/**
	 * The value of the Privacy Setting
	 */
	private String value;

	/**
	 * Constructor to set the attributes
	 * 
	 * @param identifier
	 *            identifier of the Privacy Setting
	 * @param value
	 *            value of the Privacy Setting
	 */
	public PrivacySetting(String identifier, String value) {
		this.setIdentifier(identifier);
		this.setValue(value);
	}

	/**
	 * Get the identifier
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Set the identifier
	 * 
	 * @param identifier
	 *            identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Get the value
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value
	 * 
	 * @param value
	 *            value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}