package com.example.bookmanagement.repository

import com.example.bookmanagement.common.annotation.DbAccess
import com.example.bookmanagement.common.util.DateUtil
import com.example.bookmanagement.model.Publisher
import com.example.jooqexample.jooq.tables.references.TBL_PUBLISHER
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.selectFrom
import org.springframework.stereotype.Repository

@Repository
class PublisherRepository(
	private val dslContext: DSLContext
) {

	@DbAccess("出版社一覧取得")
	fun fetchAll(limit: Int, offset: Int): List<Publisher> {
		return dslContext.select(
			TBL_PUBLISHER.PUBLISHER_ID,
			TBL_PUBLISHER.PUBLISHER_NAME
		).from(TBL_PUBLISHER)
			.orderBy(TBL_PUBLISHER.SORT_ORDER)
			.limit(limit)
			.offset(offset)
			.fetch()
			.map { it.toModel() }
	}

	@DbAccess("出版社有無確認")
	fun existsById(publisherId: Long): Boolean {
		val now = DateUtil.getCurrentDate()
		return dslContext.fetchExists(
			selectFrom(TBL_PUBLISHER)
				.where(TBL_PUBLISHER.PUBLISHER_ID.eq(publisherId))
				.and(TBL_PUBLISHER.VALID_START_DATE.le(now))
				.and(TBL_PUBLISHER.VALID_END_DATE.ge(now))
		)
	}

	private fun Record.toModel(): Publisher {
		return Publisher(
			id = this.getValue(TBL_PUBLISHER.PUBLISHER_ID) as Long,
			name = this.getValue(TBL_PUBLISHER.PUBLISHER_NAME) as String
		)
	}
}
