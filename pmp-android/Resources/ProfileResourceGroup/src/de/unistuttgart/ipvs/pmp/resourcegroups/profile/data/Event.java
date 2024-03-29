/*
 * Copyright 2012 pmp-android development team
 * Project: ProfileResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.profile.data;

public class Event {
    
    private int id;
    private long timestamp;
    private int type;
    private int special;
    
    
    public int getId() {
        return this.id;
    }
    
    
    public void setId(int id) {
        this.id = id;
    }
    
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    
    public int getType() {
        return this.type;
    }
    
    
    public void setType(int type) {
        this.type = type;
    }
    
    
    public int getSpecial() {
        return this.special;
    }
    
    
    public void setSpecial(int special) {
        this.special = special;
    }
    
}
