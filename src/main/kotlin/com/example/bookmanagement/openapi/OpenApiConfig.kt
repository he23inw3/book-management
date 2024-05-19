package com.example.bookmanagement.openapi

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get

@Configuration
class OpenApiConfig(
	private val environment: Environment
) {

	@Bean
	fun openApi(): GroupedOpenApi {
		return GroupedOpenApi.builder()
			.group("書籍管理")
			.addOpenApiCustomizer {
				it.info = getInfo()
				it.servers = listOf(getDevEnvironment(), getLocalEnvironment())
			}
			.build()
	}

	private fun getInfo(): Info {
		val info = Info()
		info.title("書籍管理")
		info.description("書籍管理サンプル")
		return info
	}

	private fun getDevEnvironment(): Server {
		val server = Server()
		server.url = environment["openapi.dev.url"]
		server.description = environment["openapi.dev.description"]
		return server
	}

	private fun getLocalEnvironment(): Server {
		val server = Server()
		server.url = environment["openapi.local.url"]
		server.description = environment["openapi.local.description"]
		return server
	}
}
