package com.example.bookmanagement.repository

import com.example.bookmanagement.common.annotation.DbAccess
import com.example.bookmanagement.common.util.DateUtil
import com.example.bookmanagement.common.util.UpdateIdUtil
import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.model.Book
import com.example.jooqexample.jooq.tables.references.TBL_AUTHOR
import com.example.jooqexample.jooq.tables.references.TBL_BOOK
import com.example.jooqexample.jooq.tables.references.TBL_GENRE
import com.example.jooqexample.jooq.tables.references.TBL_PUBLISHER
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.selectFrom
import org.jooq.impl.DSL.`val`
import org.jooq.impl.DSL.`when`
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class BookRepository(
	private val dslContext: DSLContext
) {

	@DbAccess("書籍一覧取得")
	fun fetchAll(limit: Int, offset: Int): List<Book> {
		val now = DateUtil.getCurrentDate()
		return dslContext.select(
			TBL_BOOK.BOOK_ID,
			TBL_BOOK.BOOK_NAME,
			TBL_BOOK.ISBN
		).from(TBL_BOOK)
			.where(TBL_BOOK.VALID_START_DATE.le(now))
			.and(TBL_BOOK.VALID_END_DATE.ge(now))
			.orderBy(TBL_BOOK.BOOK_ID)
			.limit(limit)
			.offset(offset)
			.fetch()
			.map { it.toModel() }
	}

	@DbAccess("書籍詳細検索_著者ID指定")
	fun fetchByAuthorId(authorId: Long, limit: Int, offset: Int): List<Book> {
		val now = DateUtil.getCurrentDate()
		return dslContext.select(
			TBL_BOOK.BOOK_ID,
			TBL_BOOK.BOOK_NAME,
			TBL_BOOK.TOTAL_PAGE,
			TBL_BOOK.ISBN,
			TBL_BOOK.PUBLISHED_AT,
			TBL_BOOK.PUBLISHER_ID,
			TBL_PUBLISHER.PUBLISHER_NAME,
			TBL_PUBLISHER.PUBLISHER_ID,
			TBL_BOOK.GENRE_ID,
			TBL_GENRE.GENRE_NAME,
			TBL_BOOK.AUTHOR_ID,
			TBL_AUTHOR.AUTHOR_NAME
		).from(TBL_BOOK)
			.innerJoin(TBL_AUTHOR)
			.on(TBL_BOOK.AUTHOR_ID.eq(TBL_AUTHOR.AUTHOR_ID))
			.and(TBL_AUTHOR.VALID_START_DATE.le(now))
			.and(TBL_AUTHOR.VALID_END_DATE.ge(now))
			.innerJoin(TBL_GENRE)
			.on(TBL_GENRE.GENRE_ID.eq(TBL_BOOK.GENRE_ID))
			.and(TBL_GENRE.VALID_START_DATE.le(now))
			.and(TBL_GENRE.VALID_END_DATE.ge(now))
			.innerJoin(TBL_PUBLISHER)
			.on(TBL_PUBLISHER.PUBLISHER_ID.eq(TBL_BOOK.PUBLISHER_ID))
			.and(TBL_PUBLISHER.VALID_START_DATE.le(now))
			.and(TBL_PUBLISHER.VALID_END_DATE.ge(now))
			.where(TBL_AUTHOR.AUTHOR_ID.eq(authorId))
			.and(TBL_BOOK.VALID_START_DATE.le(now))
			.and(TBL_BOOK.VALID_END_DATE.ge(now))
			.orderBy(TBL_BOOK.BOOK_ID)
			.limit(limit)
			.offset(offset)
			.fetch()
			.map { it.toDetailModel() }
	}

	@DbAccess("書籍詳細検索_ISBN指定")
	fun fetchDetailByIsbn(isbn: String): Book? {
		val now = DateUtil.getCurrentDate()
		return dslContext.select(
			TBL_BOOK.BOOK_ID,
			TBL_BOOK.BOOK_NAME,
			TBL_BOOK.TOTAL_PAGE,
			TBL_BOOK.ISBN,
			TBL_BOOK.PUBLISHED_AT,
			TBL_BOOK.PUBLISHER_ID,
			TBL_PUBLISHER.PUBLISHER_NAME,
			TBL_PUBLISHER.PUBLISHER_ID,
			TBL_BOOK.GENRE_ID,
			TBL_GENRE.GENRE_NAME,
			TBL_BOOK.AUTHOR_ID,
			TBL_AUTHOR.AUTHOR_NAME
		).from(TBL_BOOK)
			.innerJoin(TBL_AUTHOR)
			.on(TBL_AUTHOR.AUTHOR_ID.eq(TBL_BOOK.AUTHOR_ID))
			.and(TBL_AUTHOR.VALID_START_DATE.le(now))
			.and(TBL_AUTHOR.VALID_END_DATE.ge(now))
			.innerJoin(TBL_GENRE)
			.on(TBL_GENRE.GENRE_ID.eq(TBL_BOOK.GENRE_ID))
			.and(TBL_GENRE.VALID_START_DATE.le(now))
			.and(TBL_GENRE.VALID_END_DATE.ge(now))
			.innerJoin(TBL_PUBLISHER)
			.on(TBL_PUBLISHER.PUBLISHER_ID.eq(TBL_BOOK.PUBLISHER_ID))
			.and(TBL_PUBLISHER.VALID_START_DATE.le(now))
			.and(TBL_PUBLISHER.VALID_END_DATE.ge(now))
			.where(TBL_BOOK.ISBN.eq(isbn))
			.and(TBL_BOOK.VALID_START_DATE.le(now))
			.and(TBL_BOOK.VALID_END_DATE.ge(now))
			.fetchAny()
			?.toDetailModel()
	}

	@DbAccess("書籍有無確認_書籍ID指定")
	fun existsById(bookId: Long): Boolean {
		val now = DateUtil.getCurrentDate()
		return dslContext.fetchExists(
			selectFrom(TBL_BOOK)
				.where(TBL_BOOK.BOOK_ID.eq(bookId))
				.and(TBL_BOOK.VALID_START_DATE.le(now))
				.and(TBL_BOOK.VALID_END_DATE.ge(now))
		)
	}

	@DbAccess("書籍有無確認_ISBN指定")
	fun existsByIsbn(isbn: String): Boolean {
		val now = DateUtil.getCurrentDate()
		return dslContext.fetchExists(
			selectFrom(TBL_BOOK)
				.where(TBL_BOOK.ISBN.eq(isbn))
				.and(TBL_BOOK.VALID_START_DATE.le(now))
				.and(TBL_BOOK.VALID_END_DATE.ge(now))
		)
	}

	@DbAccess("書籍登録")
	fun insert(book: Book): Long {
		return dslContext.insertInto(TBL_BOOK)
			.set(TBL_BOOK.BOOK_NAME, book.name)
			.set(TBL_BOOK.TOTAL_PAGE, book.totalPage)
			.set(TBL_BOOK.ISBN, book.isbn)
			.set(TBL_BOOK.PUBLISHED_AT, book.publishedAt)
			.set(TBL_BOOK.PUBLISHER_ID, book.publisherId)
			.set(TBL_BOOK.GENRE_ID, book.genreId)
			.set(TBL_BOOK.AUTHOR_ID, book.authorId)
			.set(TBL_BOOK.VALID_START_DATE, DateUtil.getCurrentDate())
			.set(TBL_BOOK.VALID_END_DATE, DefaultValueConstant.VALID_END_DATE)
			.set(TBL_BOOK.CREATED_BY, UpdateIdUtil.get())
			.set(TBL_BOOK.UPDATED_BY, UpdateIdUtil.get())
			.returningResult(TBL_BOOK.BOOK_ID)
			.fetchSingle { it.value1() }
	}

	@DbAccess("書籍更新")
	fun update(book: Book) {
		dslContext.update(TBL_BOOK)
			.set(
				TBL_BOOK.BOOK_NAME,
				`when`(`val`(book.name).notEqual(DefaultValueConstant.STRING), book.name)
					.otherwise(TBL_BOOK.BOOK_NAME)
			)
			.set(
				TBL_BOOK.TOTAL_PAGE,
				`when`(`val`(book.totalPage).notEqual(DefaultValueConstant.INT), book.totalPage)
					.otherwise(TBL_BOOK.TOTAL_PAGE)
			)
			.set(
				TBL_BOOK.ISBN,
				`when`(`val`(book.isbn).notEqual(DefaultValueConstant.STRING), book.isbn)
					.otherwise(TBL_BOOK.ISBN)
			)
			.set(
				TBL_BOOK.PUBLISHED_AT,
				`when`(`val`(book.publishedAt).notEqual(DefaultValueConstant.DATE), book.publishedAt)
					.otherwise(TBL_BOOK.PUBLISHED_AT)
			)
			.set(
				TBL_BOOK.PUBLISHER_ID,
				`when`(`val`(book.publisherId).notEqual(DefaultValueConstant.LONG), book.publisherId)
					.otherwise(TBL_BOOK.PUBLISHER_ID)
			)
			.set(
				TBL_BOOK.GENRE_ID,
				`when`(`val`(book.genreId).notEqual(DefaultValueConstant.LONG), book.genreId)
					.otherwise(TBL_BOOK.GENRE_ID)
			)
			.set(
				TBL_BOOK.AUTHOR_ID,
				`when`(`val`(book.authorId).notEqual(DefaultValueConstant.LONG), book.authorId)
					.otherwise(TBL_BOOK.AUTHOR_ID)
			)
			.set(TBL_BOOK.UPDATED_BY, UpdateIdUtil.get())
			.set(TBL_BOOK.UPDATED_AT, DateUtil.getCurrentDateTime())
			.where(TBL_BOOK.BOOK_ID.eq(book.id))
			.execute()
	}

	private fun Record.toModel(): Book {
		return Book(
			id = this.getValue(TBL_BOOK.BOOK_ID) as Long,
			name = this.getValue(TBL_BOOK.BOOK_NAME) as String,
			totalPage = DefaultValueConstant.INT,
			isbn = this.getValue(TBL_BOOK.ISBN) as String,
			publishedAt = DefaultValueConstant.DATE,
			publisherId = DefaultValueConstant.LONG,
			publisherName = DefaultValueConstant.STRING,
			genreId = DefaultValueConstant.LONG,
			genreName = DefaultValueConstant.STRING,
			authorId = DefaultValueConstant.LONG,
			authorName = DefaultValueConstant.STRING
		)
	}

	private fun Record.toDetailModel(): Book {
		return Book(
			id = this.getValue(TBL_BOOK.BOOK_ID) as Long,
			name = this.getValue(TBL_BOOK.BOOK_NAME) as String,
			totalPage = this.getValue(TBL_BOOK.TOTAL_PAGE) as Int,
			isbn = this.getValue(TBL_BOOK.ISBN) as String,
			publishedAt = this.getValue(TBL_BOOK.PUBLISHED_AT) as LocalDate,
			publisherId = this.getValue(TBL_BOOK.PUBLISHER_ID) as Long,
			publisherName = this.getValue(TBL_PUBLISHER.PUBLISHER_NAME) as String,
			genreId = this.getValue(TBL_BOOK.GENRE_ID) as Long,
			genreName = this.getValue(TBL_GENRE.GENRE_NAME) as String,
			authorId = this.getValue(TBL_BOOK.AUTHOR_ID) as Long,
			authorName = this.getValue(TBL_AUTHOR.AUTHOR_NAME) as String
		)
	}
}
