package com.example.bookmanagement.service

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.exception.NotFoundException
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
class AuthorServiceTest {

	@InjectMockKs
	private lateinit var testSuite: AuthorService

	@MockK
	private lateinit var authorRepository: AuthorRepository

	@MockK
	private lateinit var bookRepository: BookRepository

	init {
		MockKAnnotations.init(this, relaxUnitFun = true)
	}

	@Nested
	@DisplayName("書籍一覧取得")
	inner class FetchAll {

		@Test
		@DisplayName("検索結果あり")
		fun found() {
			// init
			val limit = 100
			val offset = 0
			val response = listOf(createAuthor(1), createAuthor(2))
			every { authorRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(limit, offset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { authorRepository.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val limit = 100
			val offset = 0
			val response = emptyList<Author>()
			every { authorRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(limit, offset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { authorRepository.fetchAll(limit, offset) }
		}
	}

	@Nested
	@DisplayName("著者詳細取得")
	inner class FetchAuthor {

		@Test
		@DisplayName("検索結果あり")
		fun found() {
			// init
			val booksLimit = 100
			val booksOffset = 0
			val authorId = 1L
			val response = createAuthor(authorId)
			every { authorRepository.fetchAuthor(authorId) } returns response
			val books = listOf(createBook(1), createBook(2))
			every { bookRepository.fetchByAuthorId(authorId, booksLimit, booksOffset) } returns books
			response.books += books

			// execute
			val actual = testSuite.fetchAuthor(authorId, booksLimit, booksOffset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { authorRepository.fetchAuthor(authorId) }
			verify(exactly = 1) { bookRepository.fetchByAuthorId(authorId, booksLimit, booksOffset) }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val booksLimit = 100
			val booksOffset = 0
			val authorId = 1L
			val response = null
			every { authorRepository.fetchAuthor(authorId) } returns response
			val books = listOf(createBook(1), createBook(2))
			every { bookRepository.fetchByAuthorId(authorId, booksLimit, booksOffset) } returns books

			// execute
			val actual = assertThrows<NotFoundException>{
				testSuite.fetchAuthor(authorId, booksLimit, booksOffset)
			}

			// assert
			val expected = "著者が見つかりませんでした。authorId=$authorId"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { authorRepository.fetchAuthor(authorId) }
			verify(exactly = 0) { bookRepository.fetchByAuthorId(authorId, booksLimit, booksOffset) }
		}
	}

	@Nested
	@DisplayName("著者登録")
	inner class Create {

		@Test
		@DisplayName("登録成功")
		fun success() {
			// init
			val authorId = 1L
			val author = createAuthor(authorId)
			every { authorRepository.existsById(authorId) } returns false
			every { authorRepository.insert(author) } returns authorId

			// execute
			testSuite.create(author)

			// assert
			verify(exactly = 1) { authorRepository.existsById(authorId)  }
			verify(exactly = 1) { authorRepository.insert(author) }
		}

		@Test
		@DisplayName("登録なし:登録済の著者")
		fun existsAuthor() {
			// init
			val authorId = 1L
			val author = createAuthor(authorId)
			every { authorRepository.existsById(authorId) } returns true
			every { authorRepository.insert(author) } returns authorId

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.create(author)
			}

			// assert
			val expected = "登録済の著者です。authorId=${author.id}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { authorRepository.existsById(authorId)  }
			verify(exactly = 0) { authorRepository.insert(author) }
		}
	}

	@Nested
	@DisplayName("著者更新")
	inner class Update {

		@Test
		@DisplayName("更新成功:登録値あり")
		fun success() {
			// init
			val authorId = 1L
			val author = createAuthor(authorId)
			every { authorRepository.existsById(authorId) } returns true
			every { authorRepository.update(author) } returns Unit

			// execute
			testSuite.update(author)

			// assert
			verify(exactly = 1) { authorRepository.existsById(authorId)  }
			verify(exactly = 1) { authorRepository.update(author) }
		}

		@Test
		@DisplayName("更新成功:登録値なし")
		fun notEnteredBook() {
			// init
			val authorId = 1L
			val author = createDefaultAuthor(authorId)
			every { authorRepository.existsById(authorId) } returns true
			every { authorRepository.update(author) } returns Unit

			// execute
			testSuite.update(author)

			// assert
			verify(exactly = 1) { authorRepository.existsById(authorId)  }
			verify(exactly = 1) { authorRepository.update(author) }
		}

		@Test
		@DisplayName("更新なし:登録未済の著者")
		fun notExists() {
			// init
			val authorId = 1L
			val author = createAuthor(authorId)
			every { authorRepository.existsById(authorId) } returns false
			every { authorRepository.update(author) } returns Unit

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.update(author)
			}

			// assert
			val expected = "登録されていない著者です。authorId=${author.id}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { authorRepository.existsById(authorId)  }
			verify(exactly = 0) { authorRepository.update(author) }
		}
	}

	private fun createAuthor(id: Long): Author {
		return Author(
			id = id,
			name = "authorName",
			briefHistory = "briefHistory"
		)
	}

	private fun createDefaultAuthor(id: Long): Author {
		return Author(
			id = id,
			name = DefaultValueConstant.STRING,
			briefHistory = DefaultValueConstant.STRING
		)
	}

	private fun createBook(id: Long): Book {
		return Book(
			id = id,
			name = "name",
			totalPage = 100,
			isbn =  "isbn",
			publishedAt = LocalDate.of(2000, 12, 31),
			publisherId = 1,
			publisherName = "publisherName",
			genreId = 1,
			genreName = "genreName",
			authorId = 1,
			authorName = "authorName"
		)
	}
}
