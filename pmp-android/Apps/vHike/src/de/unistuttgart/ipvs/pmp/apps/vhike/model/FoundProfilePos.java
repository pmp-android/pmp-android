/*
 * Copyright 2012 pmp-android development team
 * Project: vHikeApp
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
package de.unistuttgart.ipvs.pmp.apps.vhike.model;

public class FoundProfilePos {
    
    int userid;
    float lat;
    float lon;
    int query_id;
    
    
    public FoundProfilePos(int userid, float lat, float lon, int query_id) {
        super();
        this.userid = userid;
        this.lat = lat;
        this.lon = lon;
        this.query_id = query_id;
    }
    
    
    public int getUserid() {
        return this.userid;
    }
    
    
    public int getQueyId() {
        return this.query_id;
    }
    
    
    public float getLat() {
        return this.lat;
    }
    
    
    public float getLon() {
        return this.lon;
    }
    
}
