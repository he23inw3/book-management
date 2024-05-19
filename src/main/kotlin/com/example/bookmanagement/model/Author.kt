package com.example.bookmanagement.model

data class Author(
	val id: Long,
	val name: String,
	val briefHistory: String,
	val books: MutableList<Book> = mutableListOf()
)
