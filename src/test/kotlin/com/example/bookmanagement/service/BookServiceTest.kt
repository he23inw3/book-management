package com.example.bookmanagement.service

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.exception.NotFoundException
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import com.example.bookmanagement.repository.GenreRepository
import com.example.bookmanagement.repository.PublisherRepository
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
class BookServiceTest {

	@InjectMockKs
	private lateinit var testSuite: BookService

	@MockK
	private lateinit var bookRepository: BookRepository

	@MockK
	private lateinit var authorRepository: AuthorRepository

	@MockK
	private lateinit var genreRepository: GenreRepository

	@MockK
	private lateinit var publisherRepository: PublisherRepository

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

			val response = listOf(createBook(1), createBook(2))
			every { bookRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(limit, offset)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { bookRepository.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val limit = 100
			val offset = 0

			val response = emptyList<Book>()
			every { bookRepository.fetchAll(limit, offset) } returns response

			// execute
			val actual = testSuite.fetchAll(100, 0)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { bookRepository.fetchAll(limit, offset) }
		}
	}

	@Nested
	@DisplayName("書籍ISBN検索")
	inner class FetchByISBN {

		@Test
		@DisplayName("検索結果あり")
		fun found() {
			// init
			val isbn = "test isbn"
			val response = createBook(1)
			every { bookRepository.fetchDetailByIsbn(isbn) } returns response

			// execute
			val actual = testSuite.fetchByIsbn(isbn)

			// assert
			assertEquals(response, actual)
			verify(exactly = 1) { bookRepository.fetchDetailByIsbn(isbn) }
		}

		@Test
		@DisplayName("検索結果なし")
		fun notFound() {
			// init
			val isbn = "test isbn"
			every { bookRepository.fetchDetailByIsbn(isbn) } returns null

			// execute
			val actual = assertThrows<NotFoundException> {
				testSuite.fetchByIsbn(isbn)
			}

			// assert
			val expected = "書籍が見つかりませんでした。ISBN=$isbn"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.fetchDetailByIsbn(isbn) }
		}
	}

	@Nested
	@DisplayName("書籍登録")
	inner class Create {

		@Test
		@DisplayName("登録成功")
		fun success() {
			// init
			val input = createBook(1)
			every { bookRepository.existsByIsbn(input.isbn) } returns false
			every { publisherRepository.existsById(input.publisherId) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.insert(input) } returns 1L

			// execute
			testSuite.create(input)

			// assert
			verify(exactly = 1) { bookRepository.existsByIsbn(input.isbn) }
			verify(exactly = 1) { publisherRepository.existsById(input.publisherId) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 1) { authorRepository.existsById(input.authorId) }
			verify(exactly = 1)  { bookRepository.insert(input) }
		}

		@Test
		@DisplayName("登録済書籍を指定し登録失敗")
		fun existsByIsbn() {
			// init
			val input = createBook(1)
			every { bookRepository.existsByIsbn(input.isbn) } returns true
			every { publisherRepository.existsById(input.publisherId) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.insert(input) } returns 1L

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.create(input)
			}

			// assert
			val expected = "既に登録済の書籍です。ISBN=${input.isbn}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsByIsbn(input.isbn) }
			verify(exactly = 0) { publisherRepository.existsById(input.publisherId) }
			verify(exactly = 0) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0)  { bookRepository.insert(input) }
		}

		@Test
		@DisplayName("登録されていない出版社を指定し登録失敗")
		fun notExistsPublisher() {
			// init
			val input = createBook(1)
			every { bookRepository.existsByIsbn(input.isbn) } returns false
			every { publisherRepository.existsById(input.publisherId) } returns false
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.insert(input) } returns 1L

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.create(input)
			}

			// assert
			val expected = "登録されていない出版社を指定しています。publisherId=${input.publisherId}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsByIsbn(input.isbn) }
			verify(exactly = 1) { publisherRepository.existsById(input.publisherId) }
			verify(exactly = 0) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0)  { bookRepository.insert(input) }
		}

		@Test
		@DisplayName("登録されていないジャンルを指定し登録失敗")
		fun existsGenre() {
			// init
			val input = createBook(1)
			every { bookRepository.existsByIsbn(input.isbn) } returns false
			every { publisherRepository.existsById(input.publisherId) } returns true
			every { genreRepository.existsById(input.genreId) } returns false
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.insert(input) } returns 1L

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.create(input)
			}

			// assert
			val expected = "登録されていないジャンルを指定しています。genreId=${input.genreId}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsByIsbn(input.isbn) }
			verify(exactly = 1) { publisherRepository.existsById(input.publisherId) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0)  { bookRepository.insert(input) }
		}

		@Test
		@DisplayName("登録されていないジャンルを指定し登録失敗")
		fun notExistsAuthor() {
			// init
			val input = createBook(1)
			every { bookRepository.existsByIsbn(input.isbn) } returns false
			every { publisherRepository.existsById(input.publisherId) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns false
			every { bookRepository.insert(input) } returns 1L

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.create(input)
			}

			// assert
			val expected = "登録されていない著者を指定しています。authorId=${input.authorId}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsByIsbn(input.isbn) }
			verify(exactly = 1) { publisherRepository.existsById(input.publisherId) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 1) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0)  { bookRepository.insert(input) }
		}
	}

	@Nested
	@DisplayName("書籍更新")
	inner class Update {

		@Test
		@DisplayName("更新成功:登録値あり")
		fun success() {
			// init
			val input = createBook(1)
			every { bookRepository.existsById(input.id) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.update(input) } returns Unit

			// execute
			testSuite.update(input)

			// assert
			verify(exactly = 1) { bookRepository.existsById(input.id) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 1) { authorRepository.existsById(input.authorId) }
			verify(exactly = 1) { bookRepository.update(input) }
		}

		@Test
		@DisplayName("更新成功:登録値なし")
		fun notEnteredBook() {
			// init
			val input = createDefaultBook(1)
			every { bookRepository.existsById(input.id) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.update(input) } returns Unit

			// execute
			testSuite.update(input)

			// assert
			verify(exactly = 1) { bookRepository.existsById(input.id) }
			verify(exactly = 0) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 1) { bookRepository.update(input) }
		}

		@Test
		@DisplayName("登録未済の書籍により更新失敗")
		fun notExists() {
			// init
			val input = createBook(1)
			every { bookRepository.existsById(input.id) } returns false
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.update(input) } returns Unit

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.update(input)
			}

			// assert
			val expected = "登録されていない書籍です。bookId=${input.id}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsById(input.id) }
			verify(exactly = 0) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0) { bookRepository.update(input) }
		}

		@Test
		@DisplayName("登録未済のジャンルにより更新失敗")
		fun notExistsGenre() {
			// init
			val input = createBook(1)
			every { bookRepository.existsById(input.id) } returns true
			every { genreRepository.existsById(input.genreId) } returns false
			every { authorRepository.existsById(input.authorId) } returns true
			every { bookRepository.update(input) } returns Unit

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.update(input)
			}

			// assert
			val expected = "登録されていないジャンルを指定しています。genreId=${input.genreId}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsById(input.id) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 0) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0) { bookRepository.update(input) }
		}

		@Test
		@DisplayName("登録未済の著者により更新失敗")
		fun notExistsAuthor() {
			// init
			val input = createBook(1)
			every { bookRepository.existsById(input.id) } returns true
			every { genreRepository.existsById(input.genreId) } returns true
			every { authorRepository.existsById(input.authorId) } returns false
			every { bookRepository.update(input) } returns Unit

			// execute
			val actual = assertThrows<ExecuteRefusalException> {
				testSuite.update(input)
			}

			// assert
			val expected = "登録されていない著者を指定しています。authorId=${input.authorId}"
			assertEquals(expected, actual.message)
			verify(exactly = 1) { bookRepository.existsById(input.id) }
			verify(exactly = 1) { genreRepository.existsById(input.genreId) }
			verify(exactly = 1) { authorRepository.existsById(input.authorId) }
			verify(exactly = 0) { bookRepository.update(input) }
		}
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

	private fun createDefaultBook(id: Long): Book {
		return Book(
			id = id,
			name = DefaultValueConstant.STRING,
			totalPage = DefaultValueConstant.INT,
			isbn = DefaultValueConstant.STRING,
			publishedAt = DefaultValueConstant.DATE,
			publisherId = DefaultValueConstant.LONG,
			publisherName = DefaultValueConstant.STRING,
			genreId = DefaultValueConstant.LONG,
			genreName = DefaultValueConstant.STRING,
			authorId = DefaultValueConstant.LONG,
			authorName = DefaultValueConstant.STRING
		)
	}
}
