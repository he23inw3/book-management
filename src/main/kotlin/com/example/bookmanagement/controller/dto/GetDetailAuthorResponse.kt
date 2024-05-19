package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.openapi.controller.dto.GetDetailAuthorResponseIF
import java.time.LocalDate

data class GetDetailAuthorResponse(
	override val author: AuthorResponse
) : GetDetailAuthorResponseIF {

	data class AuthorResponse(
		override val id: Long,
		override val name: String,
		override val briefHistory: String,
		override val books: List<BookResponse>
	) : GetDetailAuthorResponseIF.AuthorDetailResponseIF

	data class BookResponse(
		override val id: Long,
		override val name: String,
		override val isbn: String,
		override val publishedAt: LocalDate,
		override val totalPage: Int,
		override val genre: GenreResponse,
		override val publisher: PublisherResponse
	) : GetDetailAuthorResponseIF.BookResponseIF

	data class PublisherResponse(
		override val id: Long,
		override val name: String
	) : GetDetailAuthorResponseIF.PublisherResponseIF

	data class GenreResponse(
		override val id: Long,
		override val name: String
	) : GetDetailAuthorResponseIF.GenreResponseIF

	companion object {

		fun Author.toDetailResponse(): AuthorResponse {
			return AuthorResponse(
				id = this.id,
				name = this.name,
				briefHistory = this.briefHistory,
				books = this.books.map { it.toResponse() }
			)
		}

		private fun Book.toResponse(): BookResponse {
			return BookResponse(
				id = this.id,
				name = this.name,
				isbn = this.isbn,
				publishedAt = this.publishedAt,
				totalPage = this.totalPage,
				genre = GenreResponse(
					id = this.genreId,
					name = this.genreName
				),
				publisher = PublisherResponse(
					id = this.publisherId,
					name = this.publisherName
				)
			)
		}
	}
}
