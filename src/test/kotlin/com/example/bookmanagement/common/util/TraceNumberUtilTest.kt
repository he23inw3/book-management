package com.example.bookmanagement.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TraceNumberUtilTest {

	@Test
	@DisplayName("更新IDテスト")
	fun test() {
		// init

		// execute
		val actual = TraceNumberUtil.init()

		// assert
		val expected = TraceNumberUtil.get()
		assertEquals(expected, actual)
	}
}
