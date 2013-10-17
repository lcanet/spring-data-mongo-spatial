package org.tekila.datamongo.spatial;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.DBObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 
 * @author Laurent Canet
 *
 */
public class GeoCriteriaTests {

	private WKTReader wktReader;

	@Before
	public void setUp() throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		wktReader = new WKTReader(geometryFactory);
	}

	@After
	public void tearDown() throws Exception {}

	private Geometry buildGeometry(String wkt) {
		try {
			return wktReader.read(wkt);
		} catch (ParseException e) {
			throw new AssertionError("Cannot parse WKT '"  + wkt  + "' : " +  e.getMessage());
		}
	}

	@Test
	public void testWhereString() {
		GeoCriteria criteria = GeoCriteria.where("location");
		assertNotNull(criteria);
		assertEquals("location", criteria.getKey());
	}

	@Test
	public void testNearPoint() {
		GeoCriteria c = GeoCriteria.where("location").near((Point) buildGeometry("POINT(45 4)"));
		DBObject co = c.getCriteriaObject();

		assertThat(co, is(notNullValue()));
		assertThat(
				co.toString(),
				is("{ \"location\" : { \"$near\" : { \"$geometry\" : { \"type\" : \"Point\" , \"coordinates\" : [ 45.0 , 4.0]}}}}"));
	}

	@Test
	public void testNearSpherePoint() {
		GeoCriteria c = GeoCriteria.where("location").nearSphere((Point) buildGeometry("POINT(45 4)"));
		DBObject co = c.getCriteriaObject();

		assertThat(co, is(notNullValue()));
		assertThat(
				co.toString(),
				is("{ \"location\" : { \"$nearSphere\" : { \"$geometry\" : { \"type\" : \"Point\" , \"coordinates\" : [ 45.0 , 4.0]}}}}"));
	}

	@Test
	public void testGeoWithinGeometry() {
		GeoCriteria c = GeoCriteria.where("location").geoWithin(buildGeometry("POLYGON((1 1,5 1,5 5,1 5,1 1))"));
		DBObject co = c.getCriteriaObject();

		assertThat(co, is(notNullValue()));
		assertThat(
				co.toString(),
				is("{ \"location\" : { \"$geoWithin\" : { \"$geometry\" : { \"type\" : \"Polygon\" , \"coordinates\" : [ [ [ 1.0 , 1.0] , [ 5.0 , 1.0] , [ 5.0 , 5.0] , [ 1.0 , 5.0] , [ 1.0 , 1.0]]]}}}}"));
	}

	@Test
	public void testGeoIntersects() {
		GeoCriteria c = GeoCriteria.where("location").geoIntersects(buildGeometry("POLYGON((1 1,5 1,5 5,1 5,1 1))"));
		DBObject co = c.getCriteriaObject();

		assertThat(co, is(notNullValue()));
		assertThat(
				co.toString(),
				is("{ \"location\" : { \"$geoIntersects\" : { \"$geometry\" : { \"type\" : \"Polygon\" , \"coordinates\" : [ [ [ 1.0 , 1.0] , [ 5.0 , 1.0] , [ 5.0 , 5.0] , [ 1.0 , 5.0] , [ 1.0 , 1.0]]]}}}}"));
	}
	
	@Test
	public void testGeoWithinGeometryAndOtherCriteria() {
		Criteria c = GeoCriteria.where("location").geoWithin(buildGeometry("POLYGON((1 1,5 1,5 5,1 5,1 1))")).and("name").is("Hello");
		DBObject co = c.getCriteriaObject();

		assertThat(co, is(notNullValue()));
		assertThat(
				co.toString(),
				is("{ \"location\" : { \"$geoWithin\" : { \"$geometry\" : { \"type\" : \"Polygon\" , \"coordinates\" : [ [ [ 1.0 , 1.0] , [ 5.0 , 1.0] , [ 5.0 , 5.0] , [ 1.0 , 5.0] , [ 1.0 , 1.0]]]}}} , \"name\" : \"Hello\"}"));
	}

}