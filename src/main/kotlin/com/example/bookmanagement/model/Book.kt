package com.example.bookmanagement.model

import java.time.LocalDate

data class Book(
	val id: Long,
	val name: String,
	val totalPage: Int,
	val isbn: String,
	val publishedAt: LocalDate,
	val publisherId: Long,
	val publisherName: String,
	val genreId: Long,
	val genreName: String,
	val authorId: Long,
	val authorName: String
)
