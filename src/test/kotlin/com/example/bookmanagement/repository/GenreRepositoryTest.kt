package com.example.bookmanagement.repository

import com.example.bookmanagement.model.Genre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@JooqTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(GenreRepository::class)
class GenreRepositoryTest {

	@Autowired
	lateinit var testSuite: GenreRepository

	@Nested
	@DisplayName("Select系")
	@Sql("/repository/GenreRepositoryTest/select.sql")
	inner class Select {

		@Test
		@DisplayName("ジャンル全件取得")
		fun fetchAll() {
			// init

			// execute
			val actual = testSuite.fetchAll()

			// assert
			val expected = listOf(
				Genre(id = 1, "genreA"),
				Genre(id = 2, "genreB"),
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("ジャンル有無確認_有")
		fun existsById_true() {
			// init

			// execute
			val actual = testSuite.existsById(1)

			// assert
			assertTrue(actual)
		}

		@Test
		@DisplayName("ジャンル有無確認_無")
		fun existsById_false() {
			// init

			// execute
			val actual = testSuite.existsById(0)

			// assert
			assertFalse(actual)
		}
	}
}
