package com.example.bookmanagement.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UpdateIdUtilTest {

	@Test
	@DisplayName("更新IDテスト")
	fun test() {
		// init

		// execute
		val actual = UpdateIdUtil.init("junit")

		// assert
		val expected = UpdateIdUtil.get()
		assertEquals(expected, actual)
	}
}
