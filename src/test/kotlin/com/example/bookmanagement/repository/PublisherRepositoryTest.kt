package com.example.bookmanagement.repository

import com.example.bookmanagement.model.Publisher
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
@Import(PublisherRepository::class)
class PublisherRepositoryTest {

	@Autowired
	lateinit var testSuite: PublisherRepository

	@Nested
	@DisplayName("Select系")
	@Sql("/repository/PublisherRepositoryTest/select.sql")
	inner class Select {

		@Test
		@DisplayName("著者全件取得_有")
		fun fetchAll() {
			// init

			// execute
			val actual = testSuite.fetchAll(2, 0)

			// assert
			val expected = listOf(
				Publisher(1, "publisherA"),
				Publisher(2, "publisherB"),
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("著者全件取得_無")
		fun fetchAllIsEmpty() {
			// init

			// execute
			val actual = testSuite.fetchAll(0, 0)

			// assert
			val expected = emptyList<Publisher>()
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("出版社有無確認_有")
		fun existsPublisher_true() {
			// init

			// execute
			val actual = testSuite.existsById(1)

			// assert
			assertTrue(actual)
		}

		@Test
		@DisplayName("出版社有無確認_無")
		fun existsPublisher_false() {
			// init

			// execute
			val actual = testSuite.existsById(0)

			// assert
			assertFalse(actual)
		}
	}
}
