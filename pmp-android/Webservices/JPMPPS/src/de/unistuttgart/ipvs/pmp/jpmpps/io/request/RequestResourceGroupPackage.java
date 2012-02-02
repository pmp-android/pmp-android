package de.unistuttgart.ipvs.pmp.jpmpps.io.request;


/**
 * Message should be submitted when a {@link ResourceGroupPackage} is requested.
 * 
 * @author Jakob Jarosch
 */
public class RequestResourceGroupPackage implements IRequest {

	private static final long serialVersionUID = 1L;
	
	private String packageName;

	/**
	 * Creates a new request for the given package name.
	 * 
	 * @param packageName Name of the package which should be returned.
	 */
	public RequestResourceGroupPackage(String packageName) {
		this.packageName = packageName;
	}
	
	public String getPackageName() {
		return packageName;
	}
}
