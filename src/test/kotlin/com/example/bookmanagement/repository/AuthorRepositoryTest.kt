package com.example.bookmanagement.repository

import com.example.bookmanagement.common.util.UpdateIdUtil
import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.model.Author
import com.example.jooqexample.jooq.tables.records.TblAuthorRecord
import com.example.jooqexample.jooq.tables.references.TBL_AUTHOR
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

@ExtendWith(SpringExtension::class)
@JooqTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(AuthorRepository::class)
class AuthorRepositoryTest {

	val TESTDATA_INIT_BY = "init"
	val UPDATED_BY = "junit"

	@Autowired
	lateinit var testSuite: AuthorRepository

	@Autowired
	lateinit var jdbcTemplate: JdbcTemplate

	@Nested
	@DisplayName("Select系")
	@Sql("/repository/AuthorRepositoryTest/select.sql")
	inner class Select {

		@Test
		@DisplayName("著者詳細取得_有")
		fun fetchAuthor() {
			// init

			// execute
			val actual = testSuite.fetchAuthor(1)

			// assert
			val expected = Author(
				id = 1,
				name = "authorA",
				briefHistory = "commentA"
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("著者詳細取得_無")
		fun fetchAuthorIsNull() {
			// init

			// execute
			val actual = testSuite.fetchAuthor(999)

			// assert
			assertNull(actual)
		}

		@Test
		@DisplayName("著者一覧取得_有")
		fun fetchAll() {
			// init

			// execute
			val actual = testSuite.fetchAll(1, 0)

			// assert
			val expected = listOf(
				Author(
					id = 1,
					name = "authorA",
					briefHistory = "commentA"
				)
			)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("著者一覧取得_無")
		fun fetchAllIsEmptyList() {
			// init

			// execute
			val actual = testSuite.fetchAll(0, 0)

			// assert
			val expected = emptyList<Author>()
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("著者有無確認_有")
		fun existsById_true() {
			// init

			// execute
			val actual = testSuite.existsById(1)

			// assert
			assertTrue(actual)
		}

		@Test
		@DisplayName("著者有無確認_無")
		fun existsById_false() {
			// init

			// execute
			val actual = testSuite.existsById(0)

			// assert
			assertFalse(actual)
		}
	}

	@Nested
	@DisplayName("Insert系")
	inner class Insert {

		@Test
		@DisplayName("書籍登録成功")
		fun success() {
			// init
			val before = jdbcTemplate.queryForObject("select count(*) from tbl_author;", Int::class.java)
			assertEquals(0, before)
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			val author = Author(
				id = 1,
				name = "authorA",
				briefHistory = "historyA"
			)
			val generatedId = testSuite.insert(author)

			// assert
			val after = jdbcTemplate.queryForObject("select count(*) from tbl_author;", Int::class.java)
			assertEquals(1, after)
			val actual = jdbcTemplate.queryForObject("select * from tbl_author where author_id = ?;",
				TblAuthorRowMapper(), generatedId)
			val expected = author.toEntity(generatedId = generatedId, createdBy = UPDATED_BY)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("書籍登録失敗")
		fun failed() {
			// init
			val before = jdbcTemplate.queryForObject("select count(*) from tbl_author;", Int::class.java)
			assertEquals(0, before)
			UpdateIdUtil.init(UPDATED_BY)

			// execute, assert
			val author = Author(
				id = 1,
				name = "a".repeat(256),
				briefHistory = "historyA"
			)
			assertThrows<Exception> {
				testSuite.insert(author)

				val after = jdbcTemplate.queryForObject("select count(*) from tbl_author;", Int::class.java)
				assertEquals(0, after)
			}
		}
	}

	@Nested
	@DisplayName("Update系")
	@Sql("/repository/AuthorRepositoryTest/update.sql")
	inner class Update {

		@Test
		@DisplayName("著者更新成功")
		fun success() {
			// init
			val author = Author(
				id = 1,
				name = "authorZ",
				briefHistory = "historyZ"
			)
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			testSuite.update(author)

			// assert
			val actual = jdbcTemplate.queryForObject("select * from tbl_author where author_id = ?;",
				TblAuthorRowMapper(), author.id)
			val expected = author.toEntity(author.id, TESTDATA_INIT_BY, UPDATED_BY)
			assertEquals(expected, actual)
		}

		@Test
		@DisplayName("著者更新なし:すべて設定なし")
		fun defaultValue() {
			// init
			val authorId = 1L
			val before = jdbcTemplate.queryForObject("select * from tbl_author where author_id = ?;",
				TblAuthorRowMapper(), authorId)
			val author = Author(
				id = 1,
				name = DefaultValueConstant.STRING,
				briefHistory = DefaultValueConstant.STRING
			)
			UpdateIdUtil.init(UPDATED_BY)

			// execute
			testSuite.update(author)

			// assert
			val actual = jdbcTemplate.queryForObject("select * from tbl_author where author_id = ?;",
				TblAuthorRowMapper(), author.id)
			before?.updatedBy = UPDATED_BY
			// 共通項目以外更新なし
			before?.updatedBy = UPDATED_BY
			assertEquals(before, actual)
		}

		@Test
		@DisplayName("著者更新失敗")
		fun failed() {
			// init
			val author = Author(
				id = 1,
				name = "a".repeat(256),
				briefHistory = "historyZ"
			)
			UpdateIdUtil.init(UPDATED_BY)

			// execute, assert
			assertThrows<Exception> {
				testSuite.update(author)
				val expected = Author(id = 1, name = "authorA", "commentA").toEntity()
				val actual = jdbcTemplate.queryForObject("select * from tbl_author where author_id = ?;",
					TblAuthorRowMapper(), author.id)
				assertEquals(expected, actual)
			}
		}
	}

	@Nested
	inner class TblAuthorRowMapper: RowMapper<TblAuthorRecord> {

		@Throws(SQLException::class)
		override fun mapRow(rs: ResultSet, rowNum: Int): TblAuthorRecord {
			return TblAuthorRecord(
				authorId = rs.getLong(TBL_AUTHOR.AUTHOR_ID.name),
				authorName = rs.getString(TBL_AUTHOR.AUTHOR_NAME.name),
				briefHistory = rs.getString(TBL_AUTHOR.BRIEF_HISTORY.name),
				validEndDate = rs.getDate(TBL_BOOK.VALID_END_DATE.name).toLocalDate(),
				createdBy = rs.getString(TBL_BOOK.CREATED_BY.name),
				updatedBy = rs.getString(TBL_BOOK.UPDATED_BY.name)
			)
		}
	}

	private fun Author.toEntity(
		generatedId: Long = this.id,
		createdBy: String = TESTDATA_INIT_BY,
		updatedBy: String = UPDATED_BY
	): TblAuthorRecord {
		return TblAuthorRecord(
			authorId = generatedId,
			authorName = this.name,
			briefHistory = this.briefHistory,
			validEndDate = DefaultValueConstant.VALID_END_DATE,
			createdBy = createdBy,
			updatedBy = updatedBy
		)
	}
}
