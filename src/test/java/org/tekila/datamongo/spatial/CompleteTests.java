package org.tekila.datamongo.spatial;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.tekila.datamongo.spatial.CompleteTests.AppConfig;

import com.mongodb.Mongo;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class CompleteTests {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private WKTReader wktReader;
	
	
	
	@Before
	public void inject() throws ParseException {
		for (int i = 0; i < 10; i++) {
			String geomStr = "POINT (" + (45 + i) + " " + (4 + i) + ")";
			mongoTemplate.save(new SampleEntity("Entity-" + i, wktReader.read(geomStr)));
		}
	}
	@After
	public void cleanup() {
		mongoTemplate.remove(new Query(), SampleEntity.class);
	}
	
	
	@Test
	public void numberOfEntities() {
		assertThat(mongoTemplate.count(new Query(), SampleEntity.class), is(10L));
	}

	@Test
	public void read() {
		List<SampleEntity> ls = mongoTemplate.find(Query.query(Criteria.where("name").is("Entity-0")), SampleEntity.class);
		assertThat(ls.size(), is(1));
		assertThat(ls.get(0).getName(), is("Entity-0"));
		assertThat(ls.get(0).getLocation().toString(), is("POINT (45 4)"));
	}

	@Test
	public void geoCriteria() throws ParseException {
		List<SampleEntity> ls = mongoTemplate.find(Query.query(GeoCriteria.where("location").geoWithin(
				wktReader.read("POLYGON ((43 0, 45.5 0, 45.5 10, 43 10, 43 0))"))), SampleEntity.class);
		assertThat(ls.size(), is(1));
		assertThat(ls.get(0).getName(), is("Entity-0"));
		assertThat(ls.get(0).getLocation().toString(), is("POINT (45 4)"));
	}

	@Test
	public void geoCriteriaNear() throws ParseException {
		List<SampleEntity> ls = mongoTemplate.find(Query.query(GeoCriteria.where("location").nearSphere(
				(Point) wktReader.read("POINT (48 7)"))), SampleEntity.class);
		assertThat(ls.size(), is(10));
		assertThat(ls.get(0).getName(), is("Entity-3"));
	}


	
	@Configuration
	static class AppConfig extends AbstractMongoConfiguration {
		@Override
		public Mongo mongo() throws Exception {
			return new Mongo("192.168.0.2", 27017);
		}
		@Override
		protected String getDatabaseName() {
			return "ci";
		}
		
		@Override
		public CustomConversions customConversions() {
			return new CustomConversions(GeometryConverters.geometryConverters());
		}
		@Bean
		public WKTReader wkbReader() {
			GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
			return new WKTReader(geometryFactory);
		}
	}
	
}
