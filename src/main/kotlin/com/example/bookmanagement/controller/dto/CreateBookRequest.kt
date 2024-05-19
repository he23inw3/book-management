package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.openapi.controller.dto.CreateBookRequestIF
import com.example.bookmanagement.model.Book
import java.time.LocalDate

data class CreateBookRequest(
	override val name: String?,
	override val totalPage: Int?,
	override val isbn: String?,
	override val publishedAt: LocalDate?,
	override val publisherId: Long?,
	override val genreId: Long?,
	override val authorId: Long?
) : CreateBookRequestIF {

	companion object {

		fun CreateBookRequest.toModel(): Book {
			// !!: bean validated.
			// id: because db data type is bigserial.
			// default_value: not use.
			return Book(
				id = DefaultValueConstant.LONG,
				name = this.name!!,
				totalPage = this.totalPage!!,
				isbn = this.isbn!!,
				publishedAt = this.publishedAt!!,
				publisherId = this.publisherId!!,
				publisherName = DefaultValueConstant.STRING,
				genreId = this.genreId!!,
				genreName = DefaultValueConstant.STRING,
				authorId = this.authorId!!,
				authorName = DefaultValueConstant.STRING
			)
		}
	}
}
