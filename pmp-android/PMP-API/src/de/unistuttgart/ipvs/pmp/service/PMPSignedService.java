package de.unistuttgart.ipvs.pmp.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.unistuttgart.ipvs.pmp.Constants;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.service.app.AppService;
import de.unistuttgart.ipvs.pmp.service.helper.PMPSignature;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service encapsulates all the dirty signature stuff necessary for
 * authentication of services.
 * 
 * This service requires several informations in the intent, used to bind the
 * {@link PMPService} or the {@link AppService}, put as extra into the
 * {@link Intent}. </p>
 * 
 * <pre>
 * intent.putExtraString(Constants.INTENT_IDENTIFIER, &lt;App/PMP-Identifier>);
 * intent.putExtraString(Constants.INTENT_TYPE, &lt;APP|PMP>);
 * intent.putExtraByteArray(Constants.INTENT_SIGNATURE, PMPSignature signing identifier);
 * </pre>
 * 
 * *
 * <p>
 * Use {@link PMPSignature#signContent(byte[])} with the identifier you're
 * sending the Intent to (likely
 * YourService.class.getClass().getName().getBytes()). Transmit the result of
 * the signing in "signature".
 * </p>
 * 
 * <p>
 * If you need to have access to a correct copy (one which changes will be
 * reflected in the service and vice versa) create one and override
 * createSignature() to copy that signature to this service.
 * </p>
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class PMPSignedService extends Service {

    /**
     * The signature used for this service.
     */
    private PMPSignature signature;

    /**
     * Overwrite this method if you want to use your own signature object.
     * 
     * @return null, if the service shall create and administer the signature,
     *         or a reference to a signature for the service to use.
     */
    protected PMPSignature createSignature() {
	return null;
    }

    @Override
    public void onCreate() {
	PMPSignature pmps = createSignature();
	if (pmps == null) {
	    signature = new PMPSignature();
	    signature.load(getApplicationContext(), this.getClass());
	} else {
	    signature = pmps;
	}
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public final IBinder onBind(Intent intent) {
	String boundType = intent.getStringExtra(Constants.INTENT_TYPE);
	String boundIdentifier = intent
		.getStringExtra(Constants.INTENT_IDENTIFIER);
	byte[] boundSignature = intent
		.getByteArrayExtra(Constants.INTENT_SIGNATURE);

	if ((boundSignature != null)
		&& (signature.isSignatureValid(boundType, boundIdentifier,
			getClass().getName().getBytes(), boundSignature))) {
	    return onSignedBind(intent);
	} else {
	    return onUnsignedBind(intent);
	}
    }

    /**
     * <p>
     * Sets the remote public key fetched from a different, remote
     * {@link PMPSignature} that is identified by identifier and makes sure the
     * signature is saved.
     * </p>
     * 
     * <p>
     * <b>Attention:</b> Only call this method if you are sure the source is
     * valid! This method is a likely target for attackers.
     * </p>
     * 
     * <p>
     * Make sure that remoteType always has the correct type from Constants.*
     * specified and not a value that the remote can freely set!
     * </p>
     * 
     * @param remoteType
     * @param remoteIdentifier
     * @param remotePublicKey
     *            the new public key or null to remove the current one.
     * @see {@link PMPSignature#setRemotePublicKey(String, String, byte[])}
     */
    protected void setAndSaveRemotePublicKey(String remoteType,
	    String remoteIdentifier, byte[] remotePublicKey) {
	signature.setRemotePublicKey(remoteType, remoteIdentifier,
		remotePublicKey);
	signature.save(getApplicationContext(), this.getClass());
    }

    /**
     * 
     * @return the signature used for signing messages.
     */
    public PMPSignature getSignature() {
	return this.signature;
    }

    /**
     * onBind() callback for all trusted and correctly signed messages.
     * 
     * @see {@link Service#onBind(Intent)}
     */
    public abstract IBinder onSignedBind(Intent intent);

    /**
     * onBind() callback for all messages of unknown origin.
     * 
     * @see {@link Service#onBind(Intent)}
     */
    public abstract IBinder onUnsignedBind(Intent intent);

}
