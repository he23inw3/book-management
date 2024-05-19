package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.common.util.DateUtil
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CreateBookRequestTest {

	private var validator = Validation.buildDefaultValidatorFactory().validator

	@Nested
	@DisplayName("書籍名の入力チェック")
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
	@DisplayName("ページ数の入力チェック")
	inner class TotalPage {

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
		@DisplayName("正常値:未入力")
		fun nullValue() {
			// init
			val request = createRequest().copy(totalPage = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:最小桁数以下")
		fun min() {
			// init
			val request = createRequest().copy(totalPage = 0)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("1以上にしてください。"))
		}

		@Test
		@DisplayName("異常値:最大桁数以上")
		fun max() {
			// init
			val request = createRequest().copy(totalPage = 1001)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("1000以下にしてください。"))
		}
	}

	@Nested
	@DisplayName("ISBNの入力チェック")
	inner class Isbn {

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
		@DisplayName("異常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(isbn = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:空文字")
		fun emptyString() {
			// init
			val request = createRequest().copy(isbn = "")

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 2
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
			assertTrue(errorMessages.contains("10文字以上20文字以下にしてください。"))
		}

		@Test
		@DisplayName("異常値:最小桁数以下")
		fun min() {
			// init
			val request = createRequest().copy(isbn = "a".repeat(9))

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("10文字以上20文字以下にしてください。"))
		}

		@Test
		@DisplayName("異常値:最大桁数以上")
		fun max() {
			// init
			val request = createRequest().copy(isbn = "a".repeat(21))

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("10文字以上20文字以下にしてください。"))
		}
	}

	@Nested
	@DisplayName("ジャンルIDの入力チェック")
	inner class GenreId {

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
		@DisplayName("異常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(genreId = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:最小桁数以下")
		fun format() {
			// init
			val request = createRequest().copy(genreId = 0)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("1以上にしてください。"))
		}
	}

	@Nested
	@DisplayName("発行社IDの入力チェック")
	inner class PublisherId {

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
		@DisplayName("異常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(publisherId = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:最小桁数以下")
		fun format() {
			// init
			val request = createRequest().copy(publisherId = 0)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("1以上にしてください。"))
		}
	}

	@Nested
	@DisplayName("著者IDの入力チェック")
	inner class AuthorId {

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
		@DisplayName("異常値:null")
		fun nullValue() {
			// init
			val request = createRequest().copy(authorId = null)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("入力必須です。"))
		}

		@Test
		@DisplayName("異常値:最小桁数以下")
		fun format() {
			// init
			val request = createRequest().copy(authorId = 0)

			// execute
			val actual = validator.validate(request)

			// assert
			val expected = 1
			assertEquals(expected, actual.size)
			val errorMessages = actual.map { it.message }
			assertTrue(errorMessages.contains("1以上にしてください。"))
		}
	}

	private fun createRequest(): CreateBookRequest {
		return CreateBookRequest(
			name = "bookName",
			totalPage = 100,
			isbn = "978-3-16-148410-0",
			publishedAt = DateUtil.getCurrentDate(),
			publisherId = 1,
			genreId = 1,
			authorId = 1
		)
	}
}
