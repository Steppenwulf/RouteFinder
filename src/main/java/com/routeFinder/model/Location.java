package com.routeFinder.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a location which is a node within the graph
 * @author ramesh
 *
 */
public class Location {
	private String name;
	private Collection<Location> adjacents = new HashSet<>();
	
	public Location(String theName) {
		super();
		name = theName;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * Returns the collection of location nodes that are immediately adjacent to this one
	 * For now returns a set, since order is not important, but we don't want duplicates
	 * Return type defined as a collection, in case we later decide to refactor the collection type
	 * @return a collection of locations
	 */
	public Collection<Location> getAdjacents() {
		return adjacents;
	}
	
	/**
	 * When you add an adjacent, do it in both locations in one atomic operation, to avoid the possibility of invalid data.
	 * Since we are currently using a set, we don't have to worry about duplicates
	 * @param loc
	 */
	public void addAdjacent(Location loc) {
		addAdjacentPrim(loc);
		loc.addAdjacentPrim(this);
	}
	
	protected void addAdjacentPrim(Location loc) {
		getAdjacents().add(loc);
	}
	
	/**
	 * Locations are equal if their names are the same
	 */
	@Override
	public boolean equals(Object anObject) {
		if(anObject == null || getClass() != anObject.getClass()) {
			return false;
		}
		return ((Location)anObject).isNamed(getName());
	}
	
	/**
	 * Since we are overriding equals, we must override hashCode
	 * In this case, since equals depends on the name, 
	 * simplest answer is to return the hashCode of the name - we're unlikely to collide in a collection with raw strings
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	/**
	 * Check if the name of the location is the same as the param
	 * Since locations in the real world are not case sensitive,
	 * this is a case insensitive check
	 * @param aString
	 * @return a boolean
	 */
	public boolean isNamed(String aString) {
		if(aString == null) {
			return false;
		} else {
			return aString.equalsIgnoreCase(getName());
		}
	}
	
	public boolean searchRoute(Location end) {
		if(end.equals(this)) {
			return true;
		} else {
			Collection<Location> done = new HashSet<>();
			return searchRoute(end, done);
		}
	}
	
	protected boolean searchRoute(Location end, Collection<Location> done) {
		if(canEndRoute(end)) {
			return true;
		} else {
			done.add(this);
			Collection<Location> restrictedAdjacents = new HashSet<>();
			restrictedAdjacents.addAll(getAdjacents());
			restrictedAdjacents.removeAll(done);
			for(Location each: restrictedAdjacents) {
				if(each.searchRoute(end, done)) {
					return true;
				}
			}
			return false;
		}
	}
	
	protected boolean canEndRoute(Location end) {
		return getAdjacents().contains(end);
	}
}
