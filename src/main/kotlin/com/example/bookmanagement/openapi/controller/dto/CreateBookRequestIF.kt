package com.example.bookmanagement.openapi.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

@Schema(name = "CreateBookRequest", description = "書籍登録入力")
interface CreateBookRequestIF {

	@get:Schema(description = "書籍名", type = "string", required = true, example = "雨傘")
	@get:NotBlank
	@get:Length(max = 255)
	val name: String?

	@get:Schema(description = "総ページ数", type = "int", required = true, example = "200")
	@get:NotNull
	@get:Min(1)
	@get:Max(1000)
	val totalPage: Int?

	@get:Schema(description = "ISBN", type = "string", required = true, example = "978-3-16-148410-0")
	@get:NotBlank
	@get:Size(min = 10, max = 20)
	val isbn: String?

	@get:Schema(description = "出版日", type = "date", required = true, example = "1990-01-01", format = "yyyy/MM/dd")
	@get:NotNull
	@get:JsonFormat(pattern = "yyyy-MM-dd")
	val publishedAt: LocalDate?

	@get:Schema(description = "ジャンルID", type = "long", required = true, example = "1")
	@get:NotNull
	@get:Min(1)
	val genreId: Long?

	@get:Schema(description = "出版社ID", type = "long", required = true, example = "1")
	@get:NotNull
	@get:Min(1)
	val publisherId: Long?

	@get:Schema(description = "著者ID", type = "long", required = true, example = "1")
	@get:NotNull
	@get:Min(1)
	val authorId: Long?
}
