package com.example.bookmanagement.repository

import com.example.bookmanagement.common.util.UpdateIdUtil
import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.model.Book
import com.example.jooqexample.jooq.tables.records.TblBookRecord
import com.example.jooqexample.jooq.tables.references.TBL_BOOK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@JooqTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(BookRepository::class)
class BookRepositoryTest {

	val TESTDATA_INIT_BY = "init"
	val UPDATED_BY = "junit"

	@Autowired
	lateinit var testSuite: BookRepository

	@Autowired
	lateinit var jdbcTemplate: JdbcTemplate

	@Nested
	@DisplayName("Select系")
	@Sql("/repository/BookRepositoryTest/select.sql")
	inner class Select {

		@Test
		@DisplayName("書籍全件取得_有")
		fun fetchAll() {
			// init

			// execute
			val actual = testSuite.fetchAll(4, 0)

			// assert
			val expected = listOf(
				book(1, "bookA", "ISBN_A"),
				book(2, "bookB", "ISBN_B"),
				book(3, "bookC", "ISBN_C"),
				// 4番目は期限切れのため取得されないこと
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍全件取得_無")
		fun fetchAllIsEmpty() {
			// init

			// execute
			val actual = testSuite.fetchAll(0, 0)

			// assert
			val expected = emptyList<Book>()
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍詳細検索_著者ID指定_有")
		fun fetchByAuthorId() {
			// init

			// execute
			val actual = testSuite.fetchByAuthorId(1, 100, 0)

			// assert
			val expected = listOf(
				Book(
					id = 1,
					name = "bookA",
					totalPage = 100,
					isbn = "ISBN_A",
					publishedAt = LocalDate.of(2000, 10, 1),
					publisherId = 1,
					publisherName = "publisherA",
					genreId = 1,
					genreName = "genreA",
					authorId = 1,
					authorName = "authorA"
				),
				Book(
					id = 2,
					name = "bookB",
					totalPage = 200,
					isbn = "ISBN_B",
					publishedAt = LocalDate.of(2000, 10, 2),
					publisherId = 2,
					publisherName = "publisherB",
					genreId = 2,
					genreName = "genreB",
					authorId = 1,
					authorName = "authorA"
				),
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍詳細検索_著者ID指定_無")
		fun fetchByAuthorIdIsNull() {
			// init

			// execute
			val actual = testSuite.fetchByAuthorId(999, 100, 0)

			// assert
			val expected = emptyList<Book>()
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍詳細検索_ISBN指定_有")
		fun fetchDetailByIsbn() {
			// init

			// execute
			val actual = testSuite.fetchDetailByIsbn("ISBN_A")

			// assert
			val expected = Book(
				id = 1,
				name = "bookA",
				totalPage = 100,
				isbn = "ISBN_A",
				publishedAt = LocalDate.of(2000, 10, 1),
				publisherId = 1,
				publisherName = "publisherA",
				genreId = 1,
				genreName = "genreA",
				authorId = 1,
				authorName = "authorA"
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍詳細検索_ISBN指定_無")
		fun fetchDetailByIsbnIsNull() {
			// init

			// execute
			val actual = testSuite.fetchDetailByIsbn("ISBN_Z")

			// assert
			assertNull(actual)
		}

		@Test
		@DisplayName("書籍有無確認_書籍ID指定_有")
		fun existsById_true() {
			// init

			// execute
			val actual = testSuite.existsById(1)

			// assert
			assertTrue(actual)
		}

		@Test
		@DisplayName("書籍有無確認_書籍ID指定_無")
		fun existsById_false() {
			// init

			// execute
			val actual = testSuite.existsById(0)

			// assert
			assertFalse(actual)
		}

		@Test
		@DisplayName("書籍有無確認_ISBN指定_有")
		fun existsByIsbn_true() {
			// init

			// execute
			val actual = testSuite.existsByIsbn("ISBN_A")

			// assert
			assertTrue(actual)
		}

		@Test
		@DisplayName("書籍有無確認_ISBN指定_無")
		fun existsByIsbn_false() {
			// init

			// execute
			val actual = testSuite.existsByIsbn("ISBN_Z")

			// assert
			assertFalse(actual)
		}

		private fun book(id: Long, name: String, isbn: String): Book {
			return Book(
				id = id,
				name = name,
				totalPage = DefaultValueConstant.INT,
				isbn = isbn,
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

	@Nested
	@DisplayName("Insert系")
	@Sql("/repository/BookRepositoryTest/insert.sql")
	inner class Insert {

		@Test
		@DisplayName("登録成功")
		fun success() {
			// init
			val before = jdbcTemplate.queryForObject("select count(*) from tbl_book;", Int::class.java)
			assertEquals(0, before)
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			val book = Book(
				id = 1,
				name = "bookA",
				totalPage = 100,
				isbn = "ISBN_A",
				publishedAt = LocalDate.of(2000, 10, 1),
				publisherId = 1,
				publisherName = DefaultValueConstant.STRING,
				genreId = 1,
				genreName = DefaultValueConstant.STRING,
				authorId = 1,
				authorName = DefaultValueConstant.STRING
			)
			val generatedId = testSuite.insert(book)

			// assert
			val after = jdbcTemplate.queryForObject("select count(*) from tbl_book;", Int::class.java)
			assertEquals(1, after)
			val actual = jdbcTemplate.queryForObject("select * from tbl_book where book_id = ?;",
				TblBookRowMapper(), generatedId)
			val expected = book.toEntity(generatedId = generatedId, createdBy = UPDATED_BY)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("登録失敗")
		fun failed() {
			// init
			val before = jdbcTemplate.queryForObject("select count(*) from tbl_book;", Int::class.java)
			assertEquals(0, before)
			UpdateIdUtil.init(UPDATED_BY)

			// execute, assert
			val book = Book(
				id = 1,
				name = "bookA",
				totalPage = 100,
				isbn = "ISBN_A",
				publishedAt = LocalDate.MAX,
				publisherId = 1,
				publisherName = DefaultValueConstant.STRING,
				genreId = 1,
				genreName = DefaultValueConstant.STRING,
				authorId = 1,
				authorName = DefaultValueConstant.STRING
			)
			assertThrows<Exception> {
				testSuite.insert(book)

				val after = jdbcTemplate.queryForObject("select count(*) from tbl_book;", Int::class.java)
				assertEquals(0, after)
			}
		}
	}

	@Nested
	@DisplayName("Update系")
	@Sql("/repository/BookRepositoryTest/update.sql")
	inner class Update {

		@Test
		@DisplayName("更新成功:すべて設定")
		fun success() {
			// init
			val book = Book(
				id = 1,
				name = "bookZ",
				totalPage = 999,
				isbn = "ISBN_Z",
				publishedAt = LocalDate.of(2100, 12, 31),
				publisherId = 2,
				publisherName = DefaultValueConstant.STRING,
				genreId = 2,
				genreName = DefaultValueConstant.STRING,
				authorId = 2,
				authorName = DefaultValueConstant.STRING
			)
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			testSuite.update(book)

			// assert
			val actual = jdbcTemplate.queryForObject("select * from tbl_book where book_id = ?;", TblBookRowMapper(), book.id)
			val expected = book.toEntity()
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍更新なし:すべて設定なし")
		fun defaultValue() {
			// init
			val bookId = 1L
			val before = jdbcTemplate.queryForObject("select * from tbl_book where book_id = ?;", TblBookRowMapper(), bookId)
			val book = Book(
				id = bookId,
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
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			testSuite.update(book)

			// assert
			val actual = jdbcTemplate.queryForObject("select * from tbl_book where book_id = ?;", TblBookRowMapper(), book.id)
			// 共通項目以外更新なし
			before?.updatedBy = UPDATED_BY
			assertEquals(before, actual)
		}

		@Test
		@DisplayName("更新失敗")
		fun failed() {
			// init
			val book = Book(
				id = 1,
				name = "bookUpdate",
				totalPage = 999,
				isbn = "ISBN_Z",
				publishedAt = LocalDate.MAX,
				publisherId = 2,
				publisherName = DefaultValueConstant.STRING,
				genreId = 2,
				genreName = DefaultValueConstant.STRING,
				authorId = 2,
				authorName = DefaultValueConstant.STRING
			)
			UpdateIdUtil.init(UPDATED_BY)

			// execute, assert
			assertThrows<Exception> {
				testSuite.update(book)

				val expected = Book(
					id = 1,
					name = "bookUpdate",
					totalPage = 999,
					isbn = "ISBN_Z",
					publishedAt = LocalDate.MAX,
					publisherId = 2,
					publisherName = DefaultValueConstant.STRING,
					genreId = 2,
					genreName = DefaultValueConstant.STRING,
					authorId = 2,
					authorName = DefaultValueConstant.STRING
				).toEntity()
				val actual = jdbcTemplate.queryForObject("select * from tbl_book where book_id = ?;", TblBookRowMapper(), book.id)
				assertEquals(expected, actual)
			}
		}
	}

	@Nested
	inner class TblBookRowMapper: RowMapper<TblBookRecord> {

		@Throws(SQLException::class)
		override fun mapRow(rs: ResultSet, rowNum: Int): TblBookRecord {
			return TblBookRecord(
				bookId = rs.getLong(TBL_BOOK.BOOK_ID.name),
				bookName = rs.getString(TBL_BOOK.BOOK_NAME.name),
				totalPage = rs.getInt(TBL_BOOK.TOTAL_PAGE.name),
				isbn = rs.getString(TBL_BOOK.ISBN.name),
				publishedAt = rs.getDate(TBL_BOOK.PUBLISHED_AT.name).toLocalDate(),
				authorId = rs.getLong(TBL_BOOK.AUTHOR_ID.name),
				publisherId = rs.getLong(TBL_BOOK.PUBLISHER_ID.name),
				genreId = rs.getLong(TBL_BOOK.AUTHOR_ID.name),
				validEndDate = rs.getDate(TBL_BOOK.VALID_END_DATE.name).toLocalDate(),
				createdBy = rs.getString(TBL_BOOK.CREATED_BY.name),
				updatedBy = rs.getString(TBL_BOOK.UPDATED_BY.name)
			)
		}
	}

	private fun Book.toEntity(
		generatedId: Long = this.id,
		createdBy: String = TESTDATA_INIT_BY,
		updatedBy: String = UPDATED_BY
	): TblBookRecord {
		return TblBookRecord(
			bookId = generatedId,
			bookName = this.name,
			totalPage = this.totalPage,
			isbn = this.isbn,
			publishedAt = this.publishedAt,
			authorId = this.authorId,
			publisherId = this.publisherId,
			genreId = this.genreId,
			validEndDate = DefaultValueConstant.VALID_END_DATE,
			createdBy = createdBy,
			updatedBy = updatedBy
		)
	}
}
