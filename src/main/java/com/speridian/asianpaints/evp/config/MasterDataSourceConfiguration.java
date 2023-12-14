package com.speridian.asianpaints.evp.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EntityScan(basePackages = "com.speridian.asianpaints.evp.master.entity")
@EnableJpaRepositories(transactionManagerRef = "masterTransactionManager", entityManagerFactoryRef = "masterEntityManagerFactory", basePackages = "com.speridian.asianpaints.evp.master.repository")
public class MasterDataSourceConfiguration {

	@Value("${spring.master.datasource.password}")
	private String password;

	@Value("${spring.master.datasource.username}")
	private String username;

	@Value("${spring.master.datasource.url}")
	private String url;

	@Value("${spring.master.datasource.driverClassName}")
	private String driverClassName;

	@Bean
	@Primary
	DataSource buildMasterDataSource() {
		return DataSourceBuilder.create().driverClassName(driverClassName).password(password).username(username)
				.url(url).build();
	}

	@Bean(name = "masterEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean schema1EntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(buildMasterDataSource());
		emf.setPersistenceUnitName(username);
		emf.setPersistenceProvider(new HibernatePersistenceProvider());
		emf.setPackagesToScan("com.speridian.asianpaints.evp.master.entity");
		return emf;
	}

	@Bean(name = "masterTransactionManager")
	@Primary
	public JpaTransactionManager schema1TransactionManager(
			@Qualifier("masterEntityManagerFactory") final EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

}
