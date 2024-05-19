package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "GetAllBookResponse", description = "書籍一覧応答")
interface GetAllBookResponseIF {

	@get:Schema(description = "書籍一覧", type = "list", required = true)
	val books: List<BookResponseIF>

	@Schema(name = "GetAllBookResponse.Book", description = "書籍情報")
	interface BookResponseIF {
		@get:Schema(description = "書籍ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "書籍名", type = "string", required = true, example = "雨傘")
		val name: String

		@get:Schema(description = "ISBN", type = "string", required = true, example = "978-3-16-148410-0")
		val isbn: String
	}
}
