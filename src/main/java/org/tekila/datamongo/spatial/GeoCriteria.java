package org.tekila.datamongo.spatial;

import java.util.LinkedHashMap;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class for building geospatial 2D queries using JTS mapped {@link Geometry}.
 * 
 * @author Laurent Canet
 */
public class GeoCriteria extends Criteria {

	private LinkedHashMap<String, Object> criteria = new LinkedHashMap<String, Object>();
	private String key;

	private GeoCriteria(String key) {
		super(key);
		this.key = key;
	}

	/**
	 * Static factory method to create a Criteria using the provided key
	 * 
	 * @param key
	 * @return
	 */
	public static GeoCriteria where(String key) {
		return new GeoCriteria(key);
	}

	/**
	 * Creates a geospatial criterion using a $near operation. This is only available for Mongo 2.4 and higher.
	 * 
	 * @param point must not be {@literal null}
	 * @return
	 */
	public GeoCriteria near(com.vividsolutions.jts.geom.Point point) {
		Assert.notNull(point);
		criteria.put("$near", new BasicDBObject("$geometry", GeometryWriteConverter.INSTANCE.convert(point)));
		return this;
	}

	/**
	 * Creates a geospatial criterion using a $nearSphere operation. This is only available for Mongo 2.4 and higher.
	 * 
	 * @param point must not be {@literal null}
	 * @return
	 */
	public GeoCriteria nearSphere(com.vividsolutions.jts.geom.Point point) {
		Assert.notNull(point);
		criteria.put("$nearSphere", new BasicDBObject("$geometry", GeometryWriteConverter.INSTANCE.convert(point)));
		return this;
	}

	/**
	 * Creates a geospatial criterion using $geoWithin operation on the given geometry. This is only available for Mongo
	 * 2.4 and higher.
	 * 
	 * @param geometry
	 * @return
	 */
	public GeoCriteria geoWithin(Geometry geometry) {
		Assert.notNull(geometry);
		criteria.put("$geoWithin", new BasicDBObject("$geometry", GeometryWriteConverter.INSTANCE.convert(geometry)));
		return this;
	}

	/**
	 * Creates a geospatial criterion using $geoIntersects operation on the given geometry. This is only available for
	 * Mongo 2.4 and higher.
	 * 
	 * @param geometry
	 * @return
	 */
	public GeoCriteria geoIntersects(Geometry geometry) {
		Assert.notNull(geometry);
		criteria.put("$geoIntersects", new BasicDBObject("$geometry", GeometryWriteConverter.INSTANCE.convert(geometry)));
		return this;
	}

	@Override
	protected DBObject getSingleCriteriaObject() {
		DBObject dbo = new BasicDBObject();
		boolean not = false;
		for (String k : this.criteria.keySet()) {
			Object value = this.criteria.get(k);
			if (not) {
				DBObject notDbo = new BasicDBObject();
				notDbo.put(k, value);
				dbo.put("$not", notDbo);
				not = false;
			} else {
				if ("$not".equals(k) && value == null) {
					not = true;
				} else {
					dbo.put(k, value);
				}
			}
		}

		DBObject queryCriteria = new BasicDBObject();
		queryCriteria.putAll(super.getSingleCriteriaObject());
		queryCriteria.put(this.key, dbo);
		return queryCriteria;
	}

	/**
	 * Return the key of this criteria
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

}
