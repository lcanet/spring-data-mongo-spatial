package org.tekila.datamongo.spatial;

import com.vividsolutions.jts.geom.Geometry;

public class SampleEntity {

	private String id;
	private String name;
	private Geometry location;
	
	public SampleEntity(String name, Geometry location) {
		this.name = name;
		this.location = location;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the location
	 */
	public Geometry getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(Geometry location) {
		this.location = location;
	}
	
	
}
