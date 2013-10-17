package org.tekila.datamongo.spatial;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import static org.junit.Assert.*;


public class GeometryConverterTest {
	private WKTReader wktReader;

	@Before
	public void setUp() throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		wktReader = new WKTReader(geometryFactory);
	}

	private void testGeometryReadWrite(String wkt) {
				try {
					Geometry origin = wktReader.read(wkt);
					BasicDBObject dbo = GeometryWriteConverter.INSTANCE.convert(origin);
					Geometry converted = GeometryReadConverter.INSTANCE.convert(dbo);
					assertTrue(origin.equals(converted));
					
				} catch (ParseException e) {
					throw new AssertionError("Cannot parse WKT '"  + wkt  + "' : " +  e.getMessage());
				}
			}

	@Test
	public void testPoint() {
		testGeometryReadWrite("POINT(6 10)");
	}

	@Test
	public void testLineString() {
		testGeometryReadWrite("LINESTRING(3 4,10 50,20 25)");
	}

	@Test
	public void testPolygon() {
		testGeometryReadWrite("POLYGON((1 1,5 1,5 5,1 5,1 1))");
	}

	@Test
	public void testPolygonWithHole() {
		testGeometryReadWrite("POLYGON ((35 10, 10 20, 15 40, 45 45, 35 10),(20 30, 35 35, 30 20, 20 30))");
	}

	@Test
	public void testMultiPoint() {
		testGeometryReadWrite("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))");
	}

	@Test
	public void testMultiLineString() {
		testGeometryReadWrite("MULTILINESTRING ((10 10, 20 20, 10 40),(40 40, 30 30, 40 20, 30 10))");
	}

	@Test
	public void testMultiPolygon() {
		testGeometryReadWrite("MULTIPOLYGON (((30 20, 10 40, 45 40, 30 20)), ((15 5, 40 10, 10 20, 5 10, 15 5)))");
	}

	@Test
	public void testMultiPolygonWithHoles() {
		testGeometryReadWrite("MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 45 20, 30 5, 10 10, 10 30, 20 35),(30 20, 20 25, 20 15, 30 20)))");
	}

}
