/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-XML-UTILITIES
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
package de.unistuttgart.ipvs.pmp.xmlutil.validator.issue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Vetter
 */
public class Issue implements IIssue {
    
    /**
     * The {@link IIssueLocation}
     */
    private IIssueLocation location;
    
    /**
     * The {@link IssueType}
     */
    private IssueType type;
    
    /**
     * List of parameters
     */
    private List<String> parameters = new ArrayList<String>();
    
    
    /**
     * The constructor to set the {@link IssueType} and location
     * 
     * @param type
     *            {@link IssueType} of the issue
     * @param location
     *            {@link IIssueLocation} of the issue
     */
    public Issue(IssueType type, IIssueLocation location) {
        this.type = type;
        this.location = location;
    }
    
    
    @Override
    public IIssueLocation getLocation() {
        return this.location;
    }
    
    
    @Override
    public void setLocation(IIssueLocation location) {
        this.location = location;
    }
    
    
    @Override
    public IssueType getType() {
        return this.type;
    }
    
    
    @Override
    public void setType(IssueType type) {
        this.type = type;
    }
    
    
    @Override
    public List<String> getParameters() {
        return this.parameters;
    }
    
    
    @Override
    public void addParameter(String parameter) {
        this.parameters.add(parameter);
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Issue: \"" + getType() + "\"");
        sb.append(" at " + "\"" + getLocation().getClass().getSimpleName() + "\"");
        if (getParameters().size() > 0) {
            sb.append(" with Parameters: ");
            boolean notFirst = false;
            for (String param : getParameters()) {
                if (notFirst) {
                    sb.append(", ");
                }
                sb.append(param);
                notFirst = true;
            }
        }
        return sb.toString();
    }
    
}
