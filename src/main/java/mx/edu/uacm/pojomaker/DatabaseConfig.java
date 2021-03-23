package mx.edu.uacm.pojomaker;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration //Una clase de configuracion
@EnableTransactionManagement //habilitacion del manejo de las transacciones
public class DatabaseConfig {
	
	//Elemento autoinyectado que nos permite leer
	//la configuracion de las distintas propiedades
	@Autowired
	private Environment env;
	
	//Nos va a permitir que cuando se cargue la configuracion 
	//se auto inyecte el bean del datasource
	@Autowired
	private DataSource dataSource;
	
	//Objeto que nos permitira generar el entityManager donde lo vayamos
	//a necesitar
	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;
	
	//Tenemos que crear una serie de beans
	
		//1.- Codigo donde cargamos las propiedades 
		// Configuramos el Bean del origen los datos
		
		@Bean
		public DataSource dataSource() {
			
			//crear el datasource a traves del drivermanager
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			
			//propiedades del origen de los datos
			dataSource.setDriverClassName(env.getProperty("db.driver"));
			dataSource.setUrl(env.getProperty("db.url"));
			dataSource.setUsername(env.getProperty("db.username"));
			dataSource.setPassword(env.getProperty("db.password"));
			
			return dataSource;
		}
		
		//Nos permite definir en EntityManagerFactory de manera local
		@Bean
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean entityManagerFactory = 
					new LocalContainerEntityManagerFactoryBean();
			
			//Asignar una serie de propiedades
			entityManagerFactory.setDataSource(dataSource);
			
			entityManagerFactory.
			setPackagesToScan(env.getProperty("entityManager.packagesToScan"));
			
			//Como implementacion de JPA utilizaremos hibernate
			
			HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
			
			entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
			
			//agregar las propieades que hemos visto antes, dialecto, show_sql
			//, generacion ddl
			
			//Conjunto de propiedades
			Properties additionalProperties = new Properties();
			additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
			additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
			additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
			
			
			entityManagerFactory.setJpaProperties(additionalProperties);
			
			return entityManagerFactory;
		}
		
		//Definir el gestor de transacciones
		@Bean
		public JpaTransactionManager transactionManager() {
			JpaTransactionManager transactionManager = 
					new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(
					entityManagerFactory.getObject());
			return transactionManager;
			
		}
		
		//bean post processor
		//Nos aqyudara a relanzar una serie de excepciones a nivel de base datos a traves de las distintas capas
		//para que nosotros podamos utilizarla de manera efectiva
		@Bean
		public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
			
			return new PersistenceExceptionTranslationPostProcessor();
		}
}
