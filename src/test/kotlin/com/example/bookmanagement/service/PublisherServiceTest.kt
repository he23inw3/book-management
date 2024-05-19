package com.example.bookmanagement.service

import com.example.bookmanagement.model.Publisher
import com.example.bookmanagement.repository.PublisherRepository
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
class PublisherServiceTest {

	@InjectMockKs
	private lateinit var testSuite: PublisherService

	@MockK
	private lateinit var publisherRepository: PublisherRepository

	@Nested
	@DisplayName("ジャンル一覧取得")
	inner class FetchAll {

		@Test
		@DisplayName("検索結果あり")
		fun found() {
			// init
			val limit = 100
			val offset = 0

			val response = listOf(createPublisher(1), createPublisher(2))
			every { publisherRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(limit, offset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { publisherRepository.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val limit = 100
			val offset = 0

			val response = emptyList<Publisher>()
			every { publisherRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(limit, offset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { publisherRepository.fetchAll(limit, offset) }
		}
	}

	private fun createPublisher(id: Long): Publisher {
		return Publisher(
			id = id,
			name = "publisherName"
		)
	}
}
