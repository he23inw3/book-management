package com.example.bookmanagement.repository

import com.example.bookmanagement.common.annotation.DbAccess
import com.example.bookmanagement.common.util.DateUtil
import com.example.bookmanagement.common.util.UpdateIdUtil
import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.model.Author
import com.example.jooqexample.jooq.tables.references.TBL_AUTHOR
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.selectFrom
import org.jooq.impl.DSL.`val`
import org.jooq.impl.DSL.`when`
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(
	private val dslContext: DSLContext
) {

	@DbAccess("著者取得_著者ID指定")
	fun fetchAuthor(authorId: Long): Author? {
		val now = DateUtil.getCurrentDate()
		return dslContext.select(
			TBL_AUTHOR.AUTHOR_ID,
			TBL_AUTHOR.AUTHOR_NAME,
			TBL_AUTHOR.BRIEF_HISTORY
		).from(TBL_AUTHOR)
			.where(TBL_AUTHOR.AUTHOR_ID.eq(authorId))
			.and(TBL_AUTHOR.VALID_START_DATE.le(now))
			.and(TBL_AUTHOR.VALID_END_DATE.ge(now))
			.fetchOne()
			?.map { it.toModel() }
	}

	@DbAccess("著者一覧取得")
	fun fetchAll(limit: Int, offset: Int): List<Author> {
		val now = DateUtil.getCurrentDate()
		return dslContext.select(
			TBL_AUTHOR.AUTHOR_ID,
			TBL_AUTHOR.AUTHOR_NAME,
			TBL_AUTHOR.BRIEF_HISTORY
		).from(TBL_AUTHOR)
			.where(TBL_AUTHOR.VALID_START_DATE.le(now))
			.and(TBL_AUTHOR.VALID_END_DATE.ge(now))
			.orderBy(TBL_AUTHOR.AUTHOR_ID)
			.limit(limit)
			.offset(offset)
			.fetch()
			.map { it.toModel() }
	}

	@DbAccess("著者有無確認_著者ID指定")
	fun existsById(authorId: Long): Boolean {
		val now = DateUtil.getCurrentDate()
		return dslContext.fetchExists(
			selectFrom(TBL_AUTHOR)
				.where(TBL_AUTHOR.AUTHOR_ID.eq(authorId))
				.and(TBL_AUTHOR.VALID_START_DATE.le(now))
				.and(TBL_AUTHOR.VALID_END_DATE.ge(now))
		)
	}

	@DbAccess("著者登録")
	fun insert(author: Author): Long {
		val now = DateUtil.getCurrentDate()
		return dslContext.insertInto(TBL_AUTHOR)
			.set(TBL_AUTHOR.AUTHOR_NAME, author.name)
			.set(TBL_AUTHOR.BRIEF_HISTORY, author.briefHistory)
			.set(TBL_AUTHOR.VALID_START_DATE, now)
			.set(TBL_AUTHOR.VALID_END_DATE, DefaultValueConstant.VALID_END_DATE)
			.set(TBL_AUTHOR.CREATED_BY, UpdateIdUtil.get())
			.set(TBL_AUTHOR.UPDATED_BY, UpdateIdUtil.get())
			.returningResult(TBL_AUTHOR.AUTHOR_ID)
			.fetchSingle { it.value1() }
	}

	@DbAccess("著者更新")
	fun update(author: Author) {
		val now = DateUtil.getCurrentDateTime()
		dslContext.update(TBL_AUTHOR)
			.set(
				TBL_AUTHOR.AUTHOR_NAME,
				`when`(`val`(author.name).notEqual(DefaultValueConstant.STRING), author.name)
					.otherwise(TBL_AUTHOR.AUTHOR_NAME)
			)
			.set(
				TBL_AUTHOR.BRIEF_HISTORY,
				`when`(`val`(author.briefHistory).notEqual(DefaultValueConstant.STRING), author.briefHistory)
					.otherwise(TBL_AUTHOR.BRIEF_HISTORY)
			)
			.set(TBL_AUTHOR.UPDATED_AT, now)
			.set(TBL_AUTHOR.UPDATED_BY, UpdateIdUtil.get())
			.where(TBL_AUTHOR.AUTHOR_ID.eq(author.id))
			.execute()
	}

	private fun Record.toModel(): Author {
		return Author(
			id = this.getValue(TBL_AUTHOR.AUTHOR_ID) as Long,
			name = this.getValue(TBL_AUTHOR.AUTHOR_NAME) as String,
			briefHistory = this.getValue(TBL_AUTHOR.BRIEF_HISTORY) as String
		)
	}
}
