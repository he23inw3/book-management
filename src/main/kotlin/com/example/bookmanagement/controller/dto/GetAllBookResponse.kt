package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.openapi.controller.dto.GetAllBookResponseIF
import com.example.bookmanagement.model.Book

data class GetAllBookResponse(
	override val books: List<BookResponse>
) : GetAllBookResponseIF {

	data class BookResponse(
		override val id: Long,
		override val name: String,
		override val isbn: String
	) : GetAllBookResponseIF.BookResponseIF

	companion object {

		fun Book.toResponse(): BookResponse {
			return BookResponse(
				id = this.id,
				name = this.name,
				isbn = this.isbn
			)
		}
	}
}
