package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(name = "GetDetailAuthorResponse", description = "著者詳細応答")
interface GetDetailAuthorResponseIF {

	@get:Schema(description = "著者詳細情報", type = "object", required = false)
	val author: AuthorDetailResponseIF

	@Schema(name = "GetDetailAuthorResponse.Author", description = "著者詳細応答.著者情報")
	interface AuthorDetailResponseIF {
		@get:Schema(description = "著者ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "著者名", type = "string", required = true, example = "鳥山昭")
		val name: String

		@get:Schema(description = "略歴", type = "string", required = true)
		val briefHistory: String

		@get:Schema(description = "書籍一覧", type = "list", required = true)
		val books: List<BookResponseIF>
	}

	@Schema(name = "GetDetailAuthorResponse.Book", description = "著者詳細応答.書籍情報")
	interface BookResponseIF {
		@get:Schema(description = "書籍ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "書籍名", type = "string", required = true, example = "雨傘")
		val name: String

		@get:Schema(description = "総ページ数", type = "int", required = true, example = "200")
		val totalPage: Int

		@get:Schema(description = "ISBN", type = "string", required = true, example = "978-3-16-148410-0")
		val isbn: String

		@get:Schema(description = "出版日", type = "date", required = true, example = "1999-01-01")
		val publishedAt: LocalDate

		@get:Schema(description = "出版社", type = "object", required = true)
		val publisher: PublisherResponseIF

		@get:Schema(description = "ジャンル", type = "object", required = true)
		val genre: GenreResponseIF
	}

	@Schema(name = "GetDetailAuthorResponse.Publisher", description = "著者詳細応答.出版社情報")
	interface PublisherResponseIF {
		@get:Schema(description = "出版社ID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "出版名", type = "string", required = true, example = "A社")
		val name: String
	}

	@Schema(name = "GetDetailAuthorResponse.Genre", description = "著者詳細応答.ジャンル情報")
	interface GenreResponseIF {
		@get:Schema(description = "ジャンルID", type = "long", required = true, example = "1")
		val id: Long

		@get:Schema(description = "ジャンル名", type = "string", required = true, example = "IT")
		val name: String
	}
}
