/*
 * Copyright 2012 pmp-android development team
 * Project: JPMPPS
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.jpmpps.server.controller.handler;

import de.unistuttgart.ipvs.pmp.jpmpps.io.request.AbstractRequest;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestResourceGroupPackage;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.AbstractResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.CachedRequestResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.NoSuchPackageResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.ResourceGroupPackageResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.model.Model;
import de.unistuttgart.ipvs.pmp.jpmpps.model.ResourceGroup;
import de.unistuttgart.ipvs.pmp.jpmpps.server.ResponseHasher;

/**
 * The {@link ResourceGroupPackageHandler} react on a {@link RequestResourceGroupPackage}.
 * It normally returns a {@link ResourceGroupPackageResponse}. When the {@link ResourceGroup} was cached it will return
 * a {@link CachedRequestResponse}.
 * When no {@link ResourceGroup} was found it will return a {@link NoSuchPackageResponse}.
 * 
 * @author Jakob Jarosch
 */
public class ResourceGroupPackageHandler implements IRequestHandler {
    
    @Override
    public AbstractResponse process(AbstractRequest request) {
        if (!(request instanceof RequestResourceGroupPackage)) {
            throw new IllegalArgumentException("Should be an instance of "
                    + RequestResourceGroupPackage.class.getSimpleName());
        }
        
        RequestResourceGroupPackage rgRequest = (RequestResourceGroupPackage) request;
        
        ResourceGroup rg = Model.get().getResourceGroups().get(rgRequest.getPackageName());
        
        if (rg == null) {
            return new NoSuchPackageResponse();
        } else if (ResponseHasher.checkHash(rg, rgRequest.getCacheHash())) {
            return new CachedRequestResponse();
        } else {
            return new ResourceGroupPackageResponse(rg.getInputStream(), ResponseHasher.hash(rg.getRevision()));
        }
    }
}
