package com.routeFinder.model;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.stereotype.Component;

/**
 * Represents the map of all the locations currently defined in the application
 * MapService is a singleton in this application, set as a Spring Component
 * Note: this is called MapService rather than Map, to avoid semantic collision with a Map collection
 * @author ramesh
 *
 */
@Component
public class MapService {
	private Collection<Location> locations = new HashSet<>();

	/**
	 * Returns the collection of location nodes in this map
	 * For now returns a set, since order is not important, but we don't want duplicates
	 * Return type defined as a collection, in case we later decide to refactor the collection type
	 * @return a collection of locations
	 */
	public Collection<Location> getLocations() {
		return locations;
	}
	
	public void initialize() {
		locations = new HashSet<Location>();
	}
	
	public Location getLocationNamed(String theName) {
		String theTrimmedName = theName.trim();
		for(Location each: getLocations()) {
			if(each.isNamed(theTrimmedName)) {
				return each;
			}
		}
		return null;
	}
	
	public Location findOrCreateLocation(String theName) {
		String theTrimmedName = theName.trim();
		Location answer = getLocationNamed(theTrimmedName);
		if(answer == null) {
			answer = new Location(theTrimmedName);
		}
		return answer;
	}
	
	public void addAdjacents(String s1, String s2) {
		if(s1 != null && s2 != null) {
			Location loc1 = findOrCreateLocation(s1);
			Location loc2 = findOrCreateLocation(s2);
			loc1.addAdjacent(loc2);
			getLocations().add(loc1);
			getLocations().add(loc2);
		}
	}
	
	public boolean findRoute(String s1, String s2) {
		Location loc1 = getLocationNamed(s1);
		Location loc2 = getLocationNamed(s2);
		if(loc1 == null || loc2 == null) {
			return false;
		} else {
			return loc1.searchRoute(loc2);
		}
	}
	
	public String getLocationsAsString() {
		StringBuffer answer = new StringBuffer("{");
		getLocations().stream().forEach(loc -> {
			answer.append(loc.getName());
			answer.append(" ");
		});
		answer.append('}');
		return answer.toString();
	}
}