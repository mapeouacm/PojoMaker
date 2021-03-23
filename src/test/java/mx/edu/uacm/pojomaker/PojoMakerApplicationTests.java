package mx.edu.uacm.pojomaker;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PojoMakerApplicationTests {
	
	private static final Logger log = 
			LogManager.getLogger(PojoMakerApplicationTests.class);
	
	@Autowired
	DataSource dataSource;
	
	@Test
	void contextLoads() throws SQLException {
		
		DatabaseMetaData md = 
				dataSource.getConnection().getMetaData();
		ResultSet rs = md.getTables("hibernate",  null, "%", null);//(null, null, "%", null);
		
		while(rs.next()) {
			log.debug(rs.getString(3));
		}
	}

}
