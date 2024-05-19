package com.example.bookmanagement.repository

import com.example.bookmanagement.common.annotation.DbAccess
import com.example.bookmanagement.common.util.DateUtil
import com.example.bookmanagement.model.Genre
import com.example.jooqexample.jooq.tables.references.TBL_GENRE
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.selectFrom
import org.springframework.stereotype.Repository

@Repository
class GenreRepository(
	private val dslContext: DSLContext
) {

	@DbAccess("ジャンル一覧取得")
	fun fetchAll(): List<Genre> {
		return dslContext.select(
			TBL_GENRE.GENRE_ID,
			TBL_GENRE.GENRE_NAME
		).from(TBL_GENRE)
			.orderBy(TBL_GENRE.SORT_ORDER)
			.fetch()
			.map { it.toModel() }
	}

	@DbAccess("ジャンル有無確認")
	fun existsById(genreId: Long): Boolean {
		val now = DateUtil.getCurrentDate()
		return dslContext.fetchExists(
			selectFrom(TBL_GENRE)
				.where(TBL_GENRE.GENRE_ID.eq(genreId))
				.and(TBL_GENRE.VALID_START_DATE.le(now))
				.and(TBL_GENRE.VALID_END_DATE.ge(now))
		)
	}

	private fun Record.toModel(): Genre {
		return Genre(
			id = this.getValue(TBL_GENRE.GENRE_ID) as Long,
			name = this.getValue(TBL_GENRE.GENRE_NAME) as String
		)
	}
}
