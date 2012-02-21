/*
 * Copyright 2012 pmp-android development team
 * Project: vHike
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
package de.unistuttgart.ipvs.pmp.apps.vhike.tools;

import java.util.List;

public class HistoryRideObject {

	int tripid;
	int avail_seats;

	String creation = "";
	String ending;
	String destination = "";
	List<HistoryPersonObject> persons;

	public HistoryRideObject(int tripid, int avail_seats, String creation,
			String ending, String destination, List<HistoryPersonObject> persons) {
		this.tripid = tripid;
		this.avail_seats = avail_seats;
		this.creation = creation;
		this.ending = ending;
		this.destination = destination;
		this.persons = persons;
	}
	
	public void addPerson(HistoryPersonObject person){
		persons.add(person);
	}
	
	public int getTripid() {
		return tripid;
	}

	public int getAvail_seats() {
		return avail_seats;
	}

	public String getCreation() {
		return creation;
	}

	public String getEnding() {
		return ending;
	}

	public String getDestination() {
		return destination;
	}

	public List<HistoryPersonObject> getPersons() {
		return persons;
	}

}
