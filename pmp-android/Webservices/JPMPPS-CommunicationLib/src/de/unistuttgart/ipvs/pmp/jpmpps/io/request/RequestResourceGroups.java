package de.unistuttgart.ipvs.pmp.jpmpps.io.request;


/**
 * Message should be submitted when a list of available {@link ResourceGroup}s
 * is requested.<br/>
 * 
 * <b>Filter options:<b>
 * <ul>
 * <li><b>package:</b> as a prefix before the queried package name, searches only for the package identifier.</li>
 * <li><b>comma:</b> a "," separates to different search requests.
 * </ul>
 * 
 * @author Jakob Jarosch
 */
public class RequestResourceGroups implements IRequest {

	private static final long serialVersionUID = 1L;
	
	private String locale;
	private String filter;
	private byte[] hash;

	/**
	 * Creates a new request for the given locale.
	 * 
	 * @param locale Locale for the response.
	 */
	public RequestResourceGroups(String locale) {
		this(locale, null);
	}

	/**
	 * Creates a new request for the given locale and the given filter.
	 * 
	 * @param locale Locale for the response.
	 * @param filter Filter which should be applied on the search.
	 */
	public RequestResourceGroups(String locale, String filter) {
		this(locale, filter, null);
	}
	
	public RequestResourceGroups(String locale, String filter, byte[] hash) {
		this.locale = locale;

		if (filter == null) {
			filter = "";
		}
		this.filter = filter;
		this.hash = hash;
	}

	/**
	 * @return Returns the locale of the request.
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * @return Returns the filter of the request.
	 */
	public String getFilter() {
		return this.filter;
	}
	
	/**
	 * @return Returns the hash of the request.
	 */
	public byte[] getHash() {
		return hash;
	}
}