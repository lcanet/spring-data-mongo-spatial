package org.tekila.datamongo.spatial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.mongodb.BasicDBObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Converter for writing Mongo DB GeoJSON shapes from JTS {@link Geometry}
 * 
 * @author Laurent Canet
 */
@WritingConverter
public enum GeometryWriteConverter implements Converter<Geometry, BasicDBObject> {
  INSTANCE;

  public BasicDBObject convert(Geometry g) {
    BasicDBObject dbo = new BasicDBObject();

    if (Polygon.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "Polygon");
      Polygon p = (Polygon) g;
      dbo.put("coordinates", buildCoordinates(p));
    } else if (Point.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "Point");
      Point pt = (Point) g;
      dbo.put("coordinates", buildCoordinates(pt));
    } else if (LineString.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "LineString");
      LineString ls = (LineString) g;
      dbo.put("coordinates", buildCoordinates(ls));
    } else if (MultiPoint.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "MultiPoint");
      MultiPoint mp = (MultiPoint) g;
      dbo.put("coordinates", buildCoordinates(mp));
    } else if (MultiLineString.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "MultiLineString");
      MultiLineString mls = (MultiLineString) g;
      dbo.put("coordinates", buildCoordinates(mls));
    } else if (MultiPolygon.class.isAssignableFrom(g.getClass())) {
      dbo.put("type", "MultiPolygon");
      MultiPolygon mp = (MultiPolygon) g;
      dbo.put("coordinates", buildCoordinates(mp));

    } else {
      throw new IllegalArgumentException("Unsupported geometry type " + g.getClass().getName());
    }

    return dbo;
  }

  private List<List<List<Double>>> buildCoordinates(Polygon p) {
    List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>(
        1 + p.getNumInteriorRing());
    coordinates.add(buildCoordinates(p.getExteriorRing()));
    for (int i = 0; i < p.getNumInteriorRing(); i++) {
      coordinates.add(buildCoordinates(p.getInteriorRingN(i)));
    }

    return coordinates;
  }

  private List<List<Double>> buildCoordinates(LineString line) {
    List<List<Double>> points = new ArrayList<List<Double>>(line.getNumPoints());
    for (int i = 0; i < line.getNumPoints(); i++) {
      points.add(buildCoordinates(line.getPointN(i)));
    }
    return points;
  }

  private List<Double> buildCoordinates(Point pt) {
    return buildCoordinates(pt.getCoordinate());
  }

  private List<Double> buildCoordinates(Coordinate c) {
    List<Double> ld = new ArrayList<Double>(2);
    ld.add(c.x);
    ld.add(c.y);
    return ld;
  }

  private List<List<Double>> buildCoordinates(MultiPoint mp) {
    List<List<Double>> points = new ArrayList<List<Double>>(mp.getNumGeometries());
    for (int i = 0; i < mp.getNumPoints(); i++) {
      points.add(buildCoordinates(mp.getCoordinates()[i]));
    }
    return points;
  }

  private List<List<List<Double>>> buildCoordinates(MultiLineString mls) {
    List<List<List<Double>>> points = new ArrayList<List<List<Double>>>(mls.getNumGeometries());
    for (int i = 0; i < mls.getNumGeometries(); i++) {
      points.add(buildCoordinates((LineString) mls.getGeometryN(i)));
    }
    return points;
  }

  private List<List<List<List<Double>>>> buildCoordinates(MultiPolygon mls) {
    List<List<List<List<Double>>>> points = new ArrayList<List<List<List<Double>>>>(
        mls.getNumGeometries());
    for (int i = 0; i < mls.getNumGeometries(); i++) {
      points.add(buildCoordinates((Polygon) mls.getGeometryN(i)));
    }
    return points;
  }

}
