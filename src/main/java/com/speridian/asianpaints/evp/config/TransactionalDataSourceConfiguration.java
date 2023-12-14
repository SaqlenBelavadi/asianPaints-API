package com.speridian.asianpaints.evp.config;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EntityScan(basePackages = "com.speridian.asianpaints.evp.entity")
@EnableJpaRepositories(transactionManagerRef = "transactionalTransactionManager", entityManagerFactoryRef = "transactionalEntityManagerFactory",basePackages ="com.speridian.asianpaints.evp.transactional.repository")
public class TransactionalDataSourceConfiguration {

	@Value("${spring.transactional.datasource.password}")
	private String password;

	@Value("${spring.transactional.datasource.username}")
	private String username;

	@Value("${spring.transactional.datasource.url}")
	private String url;

	@Value("${spring.transactional.datasource.driverClassName}")
	private String driverClassName;

	@Bean
	DataSource buildTransactionalDataSource() {
		return DataSourceBuilder.create()
				.driverClassName(driverClassName)
				.password(password)
				.username(username)
				.url(url).build();
	}
	
    @Bean(name = "transactionalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean schema1EntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(buildTransactionalDataSource());
        emf.setPersistenceUnitName(username);
        emf.setPersistenceProvider(new HibernatePersistenceProvider());
        emf.setPackagesToScan("com.speridian.asianpaints.evp.entity");
        return emf;
    }

    @Bean(name = "transactionalTransactionManager")
    public JpaTransactionManager schema1TransactionManager(@Qualifier("transactionalEntityManagerFactory") final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
     }

}
