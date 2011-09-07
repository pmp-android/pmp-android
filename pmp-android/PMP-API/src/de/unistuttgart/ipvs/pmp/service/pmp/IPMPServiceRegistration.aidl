package de.unistuttgart.ipvs.pmp.service.pmp;

/**
 * Interface for communicating between PMPService and Resources/Apps.
 *
 * @author Jakob Jarosch
 */
interface IPMPServiceRegistration {
	
	/**
	 * Method for registering a new App at PMP.
	 *
	 * @param publicKey the public key, for validating the submitted token in further bindings
	 * @return the public key of PMP
	 */
	byte[] registerApp(in byte[] publicKey);

	/**
	 * Method for registering a new Resource at PMP.
	 *
	 * @param publicKey the public key, for validating the submitted token in further bindings
	 * @return the public key of PMP
	 */
	byte[] registerResourceGroup(in byte[] publicKey);
}