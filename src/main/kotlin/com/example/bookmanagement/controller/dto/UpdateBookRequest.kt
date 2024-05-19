package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.openapi.controller.dto.UpdateBookRequestIF
import com.example.bookmanagement.model.Book
import java.time.LocalDate

data class UpdateBookRequest(
	override val name: String?,
	override val totalPage: Int?,
	override val isbn: String?,
	override val publishedAt: LocalDate?,
	override val genreId: Long?,
	override val publisherId: Long?,
	override val authorId: Long?
) : UpdateBookRequestIF {

	companion object {

		fun UpdateBookRequest.toModel(bookId: Long): Book {
			// default_value: default value is not update to DB.
			return Book(
				id = bookId,
				name = this.name ?: DefaultValueConstant.STRING,
				totalPage = this.totalPage ?: DefaultValueConstant.INT,
				isbn = this.isbn ?: DefaultValueConstant.STRING,
				publishedAt = this.publishedAt ?: DefaultValueConstant.DATE,
				publisherId = this.publisherId ?: DefaultValueConstant.LONG,
				publisherName = DefaultValueConstant.STRING,
				genreId = this.genreId ?: DefaultValueConstant.LONG,
				genreName = DefaultValueConstant.STRING,
				authorId = this.authorId ?: DefaultValueConstant.LONG,
				authorName = DefaultValueConstant.STRING
			)
		}
	}
}
