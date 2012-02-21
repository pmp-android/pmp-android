package de.unistuttgart.ipvs.pmp.jpmpps.server.controller.handler;

import de.unistuttgart.ipvs.pmp.jpmpps.io.request.AbstractRequest;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.AbstractResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.InvalidRequestResponse;

/**
 * The {@link DefaultHandler} can be used when no other handler matches the request.
 * It returns always a {@link InvalidRequestResponse}.
 * 
 * @author Jakob Jarosch
 */
public class DefaultHandler implements IRequestHandler {
    
    @Override
    public AbstractResponse process(AbstractRequest request) {
        return new InvalidRequestResponse("");
    }
    
}