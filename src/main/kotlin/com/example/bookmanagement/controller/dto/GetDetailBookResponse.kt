package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.model.Book
import com.example.bookmanagement.openapi.controller.dto.GetDetailBookResponseIF
import java.time.LocalDate

data class GetDetailBookResponse(
	override val book: BookDetailResponse
) : GetDetailBookResponseIF {

	data class BookDetailResponse(
		override val id: Long,
		override val name: String,
		override val totalPage: Int,
		override val isbn: String,
		override val publishedAt: LocalDate,
		override val author: AuthorResponse,
		override val publisher: PublisherResponse,
		override val genre: GenreResponse
	) : GetDetailBookResponseIF.BookDetailResponseIF

	data class AuthorResponse(
		override val id: Long,
		override val name: String
	) : GetDetailBookResponseIF.AuthorResponseIF

	data class PublisherResponse(
		override val id: Long,
		override val name: String
	) : GetDetailBookResponseIF.PublisherResponseIF

	data class GenreResponse(
		override val id: Long,
		override val name: String
	) : GetDetailBookResponseIF.GenreResponseIF

	companion object {

		fun Book.toDetailResponse(): BookDetailResponse {
			return BookDetailResponse(
				id = this.id,
				name = this.name,
				totalPage = this.totalPage,
				isbn = this.isbn,
				publishedAt = this.publishedAt,
				author = this.toAuthorResponse(),
				publisher = this.toPublisherResponse(),
				genre = this.toGenreResponse()
			)
		}

		private fun Book.toAuthorResponse(): AuthorResponse {
			return AuthorResponse(
				id = this.authorId,
				name = this.authorName
			)
		}

		private fun Book.toPublisherResponse(): PublisherResponse {
			return PublisherResponse(
				id = this.publisherId,
				name = this.publisherName
			)
		}

		private fun Book.toGenreResponse(): GenreResponse {
			return GenreResponse(
				id = this.genreId,
				name = this.genreName
			)
		}
	}
}
