package com.example.bookmanagement.service

import com.example.bookmanagement.model.Genre
import com.example.bookmanagement.repository.GenreRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class GenreServiceTest {

	@InjectMockKs
	private lateinit var testSuite: GenreService

	@MockK
	private lateinit var genreRepository: GenreRepository

	@Nested
	@DisplayName("ジャンル一覧取得")
	inner class FetchAll {

		@Test
		@DisplayName("検索結果あり")
		fun found() {
			// init
			val response = listOf(createGenre(1), createGenre(2))
			every { genreRepository.fetchAll() } returns response

			// execute
			val actual = testSuite.fetchAll()

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { genreRepository.fetchAll() }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val response = emptyList<Genre>()
			every { genreRepository.fetchAll() } returns response

			// execute
			val actual = testSuite.fetchAll()

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { genreRepository.fetchAll() }
		}
	}

	private fun createGenre(id: Long = 1): Genre {
		return Genre(id, "genreName")
	}
}
