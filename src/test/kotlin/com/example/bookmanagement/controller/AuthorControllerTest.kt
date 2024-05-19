package com.example.bookmanagement.controller

import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.constant.ErrorCode
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.CreateAuthorRequest
import com.example.bookmanagement.controller.dto.CreateAuthorRequest.Companion.toModel
import com.example.bookmanagement.controller.dto.ErrorResponse
import com.example.bookmanagement.controller.dto.GetAllAuthorResponse
import com.example.bookmanagement.controller.dto.GetAllAuthorResponse.Companion.toResponse
import com.example.bookmanagement.controller.dto.GetDetailAuthorResponse
import com.example.bookmanagement.controller.dto.GetDetailAuthorResponse.Companion.toDetailResponse
import com.example.bookmanagement.controller.dto.UpdateAuthorRequest
import com.example.bookmanagement.controller.dto.UpdateAuthorRequest.Companion.toModel
import com.example.bookmanagement.exception.ExceptionHandler
import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.service.AuthorService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.sql.SQLException
import java.time.LocalDate
import java.util.Locale

@WebMvcTest(AuthorController::class)
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [RequestProperty::class])
class AuthorControllerTest: AbstractControllerTest() {

	private var testSuite: MockMvc

	@InjectMockKs
	private lateinit var authorController: AuthorController

	@MockK
	private lateinit var requestProperty: RequestProperty

	@MockK
	private lateinit var authorService: AuthorService

	@MockK
	private lateinit var messageSource: MessageSource

	init {
		MockKAnnotations.init(this, relaxUnitFun = true)
		testSuite = MockMvcBuilders.standaloneSetup(authorController)
			.setControllerAdvice(ExceptionHandler(messageSource))
			.setMessageConverters(MappingJackson2HttpMessageConverter())
			.build()
	}

	@Nested
	@DisplayName("著者一覧")
	inner class GetAll {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			val authors = listOf(
				createModel(1),
				createModel(2)
			)
			every { authorService.fetchAll(limit, offset) } returns authors
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/author")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllAuthorResponse::class.java)
			assertEquals(GetAllAuthorResponse(authors = authors.map { it.toResponse() }), response)
			verify(exactly = 1) { authorService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("取得なし")
		fun notFound() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { authorService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/author")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllAuthorResponse::class.java)
			assertEquals(GetAllAuthorResponse(authors = emptyList()), response)
			verify(exactly = 1) { authorService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 101
			val offset = 0
			every { authorService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Bad Request."

			// execute
			val actual = testSuite.perform(
				get("/author")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.BAD_REQUEST.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.VALIDATION_ERROR, response.errors[0].errorCode)
			assertEquals("limit:100以下にしてください。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { authorService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/author")
					.header(apiKey, "DUMMY")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.FORBIDDEN.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INVALID_API_KEY_ERROR, response.errors[0].errorCode)
			assertEquals("APIキーが不正です。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { authorService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				patch("/author")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content("{}")
			).andReturn()

			// assert
			assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.NOT_ALLOWED_ERROR, response.errors[0].errorCode)
			assertEquals("該当エンドポイントでPATCHメソッドの処理は許可されていません。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("著者詳細")
	inner class GetDetail {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			val booksLimit = 100
			val booksOffset = 0
			val authorId = 1L
			TraceNumberUtil.init()
			val author = createModel(authorId)
			every { authorService.fetchAuthor(authorId, booksLimit, booksOffset) } returns author
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetDetailAuthorResponse::class.java)
			assertEquals(GetDetailAuthorResponse(author = author.toDetailResponse()), response)
			verify(exactly = 1) { authorService.fetchAuthor(authorId, booksLimit, booksOffset) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val booksLimit = 100
			val booksOffset = 0
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val author = createModel(authorId)
			every { authorService.fetchAuthor(authorId, booksLimit, booksOffset) } returns author
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/author/$authorId")
					.header(apiKey, "DUMMY")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.FORBIDDEN.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INVALID_API_KEY_ERROR, response.errors[0].errorCode)
			assertEquals("APIキーが不正です。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.fetchAuthor(authorId, booksLimit, booksOffset) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val booksLimit = 100
			val booksOffset = 0
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val author = createModel(authorId)
			every { authorService.fetchAuthor(authorId, booksLimit, booksOffset) } returns author
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content("{}")
			).andReturn()

			// assert
			assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.NOT_ALLOWED_ERROR, response.errors[0].errorCode)
			assertEquals("該当エンドポイントでPOSTメソッドの処理は許可されていません。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.fetchAuthor(authorId, booksLimit, booksOffset) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("著者登録")
	inner class Create {

		@Test
		@DisplayName("登録成功")
		fun success() {
			// init
			TraceNumberUtil.init()
			val input = createRequest()
			every { authorService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				post("/author")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.CREATED.value(), actual.response.status)
			verify(exactly = 1) { authorService.create(input.toModel()) }
		}

		@Test
		@DisplayName("登録失敗")
		fun failed() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { authorService.create(input.toModel()) } throws SQLException("DBエラー")
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) } returns "Internal Server Error."

			// execute
			val actual = testSuite.perform(
				post("/author")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, response.errors[0].errorCode)
			assertEquals("想定外エラー", response.errors[0].errorMessage)
			verify(exactly = 1) { authorService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { authorService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				post("/author")
					.header(apiKey, "DUMMY")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.FORBIDDEN.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INVALID_API_KEY_ERROR, response.errors[0].errorCode)
			assertEquals("APIキーが不正です。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { authorService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				put("/author")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.NOT_ALLOWED_ERROR, response.errors[0].errorCode)
			assertEquals("該当エンドポイントでPUTメソッドの処理は許可されていません。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest().copy(name = "")
			every { authorService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Request Error."

			// execute
			val actual = testSuite.perform(
				post("/author")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.BAD_REQUEST.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.VALIDATION_ERROR, response.errors[0].errorCode)
			assertEquals("name:入力必須です。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("実行拒否エラー")
		fun executeRefusalError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			val errorMessage = "実行拒否エラー"
			every { authorService.create(input.toModel()) } throws ExecuteRefusalException(errorMessage)
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) } returns "Execute Refusal Error."

			// execute
			val actual = testSuite.perform(
				post("/author")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.EXECUTION_REFUSAL_ERROR, response.errors[0].errorCode)
			assertEquals(errorMessage, response.errors[0].errorMessage)
			verify(exactly = 1) { authorService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("著者更新")
	inner class Update {

		@Test
		@DisplayName("更新成功")
		fun success() {
			// init
			val authorId = 1L
			TraceNumberUtil.init()
			val input = updateRequest()
			every { authorService.update(input.toModel(authorId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				put("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.NO_CONTENT.value(), actual.response.status)
			verify(exactly = 1) { authorService.update(input.toModel(authorId)) }
		}

		@Test
		@DisplayName("更新失敗")
		fun failed() {
			// init
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { authorService.update(input.toModel(authorId)) } throws SQLException()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) } returns "Internal Server Error."

			// execute
			val actual = testSuite.perform(
				put("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, response.errors[0].errorCode)
			assertEquals("想定外エラー", response.errors[0].errorMessage)
			verify(exactly = 1) { authorService.update(input.toModel(authorId)) }
			verify(exactly = 1) { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { authorService.update(input.toModel(authorId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				put("/author/$authorId")
					.header(apiKey, "DUMMY")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.FORBIDDEN.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INVALID_API_KEY_ERROR, response.errors[0].errorCode)
			assertEquals("APIキーが不正です。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.update(input.toModel(authorId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { authorService.update(input.toModel(authorId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.NOT_ALLOWED_ERROR, response.errors[0].errorCode)
			assertEquals("該当エンドポイントでPOSTメソッドの処理は許可されていません。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.update(input.toModel(authorId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest().copy(name = "a".repeat(256))
			every { authorService.update(input.toModel(authorId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Request Error."

			// execute
			val actual = testSuite.perform(
				put("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.BAD_REQUEST.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.VALIDATION_ERROR, response.errors[0].errorCode)
			assertEquals("name:0文字以上255文字以下にしてください。", response.errors[0].errorMessage)
			verify(exactly = 0) { authorService.update(input.toModel(authorId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("実行拒否エラー")
		fun executeRefusalError() {
			// init
			val authorId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			val errorMessage = "実行拒否エラー"
			every { authorService.update(input.toModel(authorId)) } throws ExecuteRefusalException(errorMessage)
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) } returns "Request Error."

			// execute
			val actual = testSuite.perform(
				put("/author/$authorId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.EXECUTION_REFUSAL_ERROR, response.errors[0].errorCode)
			assertEquals(errorMessage, response.errors[0].errorMessage)
			verify(exactly = 1) { authorService.update(input.toModel(authorId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) }
		}
	}

	private fun createRequest(): CreateAuthorRequest {
		return CreateAuthorRequest(
			name = "authorName",
			briefHistory = "briefHistory"
		)
	}

	private fun updateRequest(): UpdateAuthorRequest {
		return UpdateAuthorRequest(
			name = "authorName",
			briefHistory = "briefHistory"
		)
	}

	private fun createModel(id: Long): Author {
		return Author(
			id = id,
			name = "authorName",
			briefHistory = "briefHistory",
			books = mutableListOf(
				Book(
					id = 1,
					name = "bookName",
					totalPage = 100,
					isbn = "978-3-16-148410-0",
					publishedAt = LocalDate.of(2000, 10, 20),
					publisherId = 1,
					publisherName = "publisherName",
					genreId = 1,
					genreName = "genreName",
					authorId = 1,
					authorName = "authorName"
				)
			)
		)
	}
}
