package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "GetAllPublisherResponse", description = "著者一覧応答")
interface GetAllPublisherResponseIF {

	@get:Schema(description = "著者", type = "list", required = true)
	val publisher: List<PublisherResponseIF>

	@Schema(name = "GetAllPublisherResponse.Publisher", description = "著者情報")
	interface PublisherResponseIF {
		@get:Schema(description = "著者ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "著者名", type = "string", required = true, example = "会津若松")
		val name: String
	}
}
