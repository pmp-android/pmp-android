/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
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
package de.unistuttgart.ipvs.pmp.model.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class to make certain actions in the server easier and faster because the briding functionality
 * from {@link OutputStream} to {@link InputStream} is required.
 * 
 * @author Tobias Kuhn
 * 
 */
public class ByteArrayStreamBridge extends ByteArrayOutputStream {
    
    public ByteArrayStreamBridge(int size) {
        super(size);
    }
    
    
    /**
     * Exports the contents of this {@link ByteArrayStreamBridge} to a {@link ByteArrayInputStream} by using the
     * reference directly.
     * Will clean the current output buffer that was written to.
     * 
     * @return
     */
    public ByteArrayInputStream toInputStream() {
        byte[] result = this.buf;
        
        this.buf = new byte[result.length];
        this.count = 0;
        
        return new ByteArrayInputStream(result);
    }
    
}
