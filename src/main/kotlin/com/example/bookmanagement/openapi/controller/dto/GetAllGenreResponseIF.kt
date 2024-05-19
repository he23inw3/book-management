package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "GetAllGenreResponse", description = "出版社一覧応答")
interface GetAllGenreResponseIF {

	@get:Schema(description = "著者", type = "list", required = true)
	val genres: List<GenreResponseIF>

	@Schema(name = "GetAllGenreResponse.Genre", description = "出版社情報")
	interface GenreResponseIF {
		@get:Schema(description = "出版社ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "出版社名", type = "string", required = true, example = "会津若松")
		val name: String
	}
}
