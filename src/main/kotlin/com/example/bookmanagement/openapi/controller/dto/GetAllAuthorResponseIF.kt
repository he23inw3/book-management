package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "GetAllAuthorResponse", description = "著者一覧応答")
interface GetAllAuthorResponseIF {

	@get:Schema(description = "著者", required = true)
	val authors: List<AuthorResponseIF>

	@Schema(name = "GetAllAuthorResponse.Author", description = "著者")
	interface AuthorResponseIF {
		@get:Schema(description = "著者ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "著者名", type = "string", required = true, example = "鳥山昭")
		val name: String

		@get:Schema(description = "略歴", type = "string", required = true)
		val briefHistory: String
	}
}
