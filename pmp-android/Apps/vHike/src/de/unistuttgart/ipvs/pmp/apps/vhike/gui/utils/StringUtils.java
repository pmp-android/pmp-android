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
package de.unistuttgart.ipvs.pmp.apps.vhike.gui.utils;

public class StringUtils {
    
    public static String cleanUpDestionationAndStopoversString(String s) {
        return s.trim().replaceAll("\\s*;\\s*", ";").replaceAll("(^;|;$)", "");
    }
    
    
    public static String getDestination(String destinationAndStopovers) {
        return cleanUpDestionationAndStopoversString(destinationAndStopovers).split(";")[0];
    }
    
    
    public static String getStopovers(String destinationAndStopovers) {
        String s = cleanUpDestionationAndStopoversString(destinationAndStopovers);
        if (s.indexOf(";") <= 0)
            return "";
        else
            return s.substring(s.indexOf(";"));
    }
}
