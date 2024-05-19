package com.example.bookmanagement.service

import com.example.bookmanagement.model.Genre
import com.example.bookmanagement.repository.GenreRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GenreService(
	private val genreRepository: GenreRepository
): AbstractService() {

	@Transactional(readOnly = true)
	fun fetchAll(): List<Genre> {
		return genreRepository.fetchAll()
	}
}
