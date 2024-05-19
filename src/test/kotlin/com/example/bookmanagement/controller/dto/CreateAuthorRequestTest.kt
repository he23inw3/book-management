package com.example.bookmanagement.controller.dto

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CreateAuthorRequestTest {

	private var validator = Validation.buildDefaultValidatorFactory().validator

	@Nested
	@DisplayName("著者名の入力チェック")
	inner class Name {

		@Test
		@DisplayName("正常値")
		fun success() {
			// init
			val request = createRequest()

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 0
			assertEquals(expected, actual.size)
		}

		@Test
		@DisplayName("正常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(name = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("正常値:空文字")
		fun emptyString() {
			// init
			val request = createRequest().copy(name = "")

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:桁数超過")
		fun maxLength() {
			// init
			val request = createRequest().copy(name = "a".repeat(256))

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("0文字以上255文字以下にしてください。"))
		}
	}

	@Nested
	@DisplayName("略歴の入力チェック")
	inner class BriefHistory {

		@Test
		@DisplayName("正常値")
		fun success() {
			// init
			val request = createRequest()

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 0
			assertEquals(expected, actual.size)
		}

		@Test
		@DisplayName("正常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(briefHistory = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("正常値:空文字")
		fun emptyString() {
			// init
			val request = createRequest().copy(briefHistory = "")

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}
	}

	private fun createRequest(): CreateAuthorRequest {
		return CreateAuthorRequest(
			name = "authorName",
			briefHistory = "briefHistory"
		)
	}
}
