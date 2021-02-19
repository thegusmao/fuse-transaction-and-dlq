package br.com.redhat.configuration;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AppBeans {
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public ConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory("admin", "r3dh4t1!","tcp://localhost:61616");
		PooledConnectionFactory factory = new PooledConnectionFactory(amq);
		
		return factory;
	}
	
//	@Bean
//	public PlatformTransactionManager transactionManager(DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
//
//	@Bean(name = "PROPAGATION_REQUIRES_NEW")
//	public SpringTransactionPolicy propogationRequired(PlatformTransactionManager transactionManager) {
//		SpringTransactionPolicy propagationRequired = new SpringTransactionPolicy();
//		propagationRequired.setTransactionManager(transactionManager);
//		propagationRequired.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
//		return propagationRequired;
//	}
//	
//	@Bean
//	public SqlComponent sql(DataSource dataSource) {
//		SqlComponent sql = new SqlComponent();
//		sql.setDataSource(dataSource);
//		return sql;
//	}
}
