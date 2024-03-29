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

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RSSFeed implements Parcelable {
    
    public String name;
    public String url;
    public List<RSSItem> itemList = new ArrayList<RSSItem>();
    
    public static final Parcelable.Creator<RSSFeed> CREATOR = new Parcelable.Creator<RSSFeed>() {
        
        @Override
        public RSSFeed createFromParcel(Parcel source) {
            return RSSFeed.createFromParcel(source);
        }
        
        
        @Override
        public RSSFeed[] newArray(int size) {
            return new RSSFeed[size];
        }
    };
    
    
    public RSSFeed() {
    }
    
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.url);
        out.writeTypedList(this.itemList);
    }
    
    
    private static RSSFeed createFromParcel(Parcel in) {
        RSSFeed feed = new RSSFeed();
        feed.name = in.readString();
        feed.url = in.readString();
        in.readTypedList(feed.itemList, RSSItem.CREATOR);
        return feed;
    }
}
