/*
 * Copyright 2012 pmp-android development team
 * Project: RSSFeedResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.rssfeed;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.resource.Resource;

/**
 * @author Frieder Schüler
 * 
 */
public class RSSFeedResource extends Resource {
    
    @Override
    public IBinder getAndroidInterface(String appIdentifier) {
        RSSFeedResourceGroup ressFeed = (RSSFeedResourceGroup) getResourceGroup();
        return new RSSFeedAdapter(ressFeed.getContext(), this, appIdentifier);
    }
    
    
    @Override
    public IBinder getMockedAndroidInterface(String appIdentifier) {
        RSSFeedResourceGroup ressFeed = (RSSFeedResourceGroup) getResourceGroup();
        return new RSSFeedMockAdapter(ressFeed.getContext(), this, appIdentifier);
    }
    
    
    @Override
    public IBinder getCloakedAndroidInterface(String appIdentifier) {
        RSSFeedResourceGroup ressFeed = (RSSFeedResourceGroup) getResourceGroup();
        return new RSSFeedCloakAdapter(ressFeed.getContext(), this, appIdentifier);
    }
    
}
