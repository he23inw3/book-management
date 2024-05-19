package com.example.bookmanagement.repository

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import java.util.Properties
import javax.sql.DataSource

@SpringBootApplication
class RepositoryConfiguration {

	@Bean
	fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
		return JdbcTemplate(dataSource)
	}

	@Bean
	fun dataSource(): DataSource {
		val dataSource = SimpleDriverDataSource()
		dataSource.setDriverClass(org.postgresql.Driver::class.java)
		dataSource.url = System.getenv("DATABASE_URL")
		dataSource.username = System.getenv("DATABASE_USERNAME")
		dataSource.password = System.getenv("DATABASE_PASSWORD")
		val properties = Properties()
		properties.setProperty("autoCommit", "false")
		dataSource.connectionProperties = properties
		return dataSource
	}
}
