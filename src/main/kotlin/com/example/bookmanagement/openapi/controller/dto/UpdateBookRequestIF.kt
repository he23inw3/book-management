package com.example.bookmanagement.openapi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

@Schema(name = "UpdateBookRequest", description = "書籍更新入力")
interface UpdateBookRequestIF {

	@get:Schema(description = "書籍名", type = "string", required = false, example = "水俣病")
	@get:Length(max = 255)
	val name: String?

	@get:Schema(description = "総ページ数", type = "int", required = false, example = "1000")
	@get:Min(1)
	@get:Max(1000)
	val totalPage: Int?

	@get:Schema(description = "ISBN", type = "string", required = false, example = "978-3-16-999999-0")
	@get:Length(min = 10, max = 20)
	val isbn: String?

	@get:Schema(description = "出版日", type = "date", required = false, example = "1999-01-01")
	@get:JsonFormat(pattern = "yyyy-MM-dd")
	val publishedAt: LocalDate?

	@get:Schema(description = "ジャンルID", type = "long", required = false, example = "1")
	@get:Min(1)
	val genreId: Long?

	@get:Schema(description = "出版社ID", type = "long", required = false, example = "1")
	@get:Min(1)
	val publisherId: Long?

	@get:Schema(description = "著者ID", type = "long", required = false, example = "1")
	@get:Min(1)
	val authorId: Long?
}
