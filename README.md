# Spatial extensions for Spring data MongoDB

----
## Features
* Mapping of [JTS](http://www.vividsolutions.com/jts/JTSHome.htm) geometry values in mongo entities (for Mongo 2.4 +). Multi geometries are also supported with mongo 2.5+
* Use mongo geospatial criteria that can take advantages of `2dsphere` indexes

---
## Usage

### Mapping of geometries

All JTS geometry types excepted geometrycollection are supported from mongo 2.5. With mongodb 2.4, only simple (not multiple) geometries are supprted.

    import com.vividsolutions.jts.geom.Geometry;
    public class SampleEntity {

        private String id;
        private String name;
        private Geometry location;
        ...
    }

The `location` field will be converted to GeoJSON format, suitable for creating `2dsphere` geospatial index.

It is recommended to use WGS84 as the spatial reference system for your data, as it is the only supported datum for mongodb's `2dsphere` indexes.

Next, register the converters in your spring-data-mongo config.
Using the java config:

```java

    @Configuration
    class AppConfig extends AbstractMongoConfiguration {
        @Override
        public CustomConversions customConversions() {
            return new CustomConversions(GeometryConverters.geometryConverters());
        }
        ...
```

Using the XML config

```xml
    <mongo:mapping-converter>
        <mongo:custom-converters>
            <mongo:converter>
                 <util:constant static-field="org.tekila.datamongo.spatial.GeometryReadConverter.INSTANCE" />
                 <util:constant static-field="org.tekila.datamongo.spatial.GeometryWriteCnverter.INSTANCE" />
            </mongo:converter>
        </mongo:custom-converters>
    </mongo:mapping-converter>
```

### Criterias

Use the `criteria` extented class `GeoCriteria` that includes new geometry operators :

* Documents whose geometry field is near a point `$near`
* Documents whose geometry field is near a point in the sphere plane `$nearSphere`
* Documents whose geometry field is contained by the query geometry `$geoWithin`
* Documents whose geometry field intersects the query geometry `$geoIntersects`


The `GeoCriteria` class is compatible with all field restrictions

    Geometry myGeom = wktReader.read("... wkt ...");
    List<SampleEntity> ls = mongoTemplate.find(Query.query(GeoCriteria.where("location")
                .geoWithin(myGeom)
                .and("name").is("Roger Saucisse")), 
                SampleEntity.class);




---
## Licence

BSD


