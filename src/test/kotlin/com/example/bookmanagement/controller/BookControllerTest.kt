package com.example.bookmanagement.controller

import com.example.bookmanagement.common.util.DateUtil
import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.constant.ErrorCode
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.CreateBookRequest
import com.example.bookmanagement.controller.dto.CreateBookRequest.Companion.toModel
import com.example.bookmanagement.controller.dto.ErrorResponse
import com.example.bookmanagement.controller.dto.GetAllBookResponse
import com.example.bookmanagement.controller.dto.GetAllBookResponse.Companion.toResponse
import com.example.bookmanagement.controller.dto.GetDetailBookResponse
import com.example.bookmanagement.controller.dto.GetDetailBookResponse.Companion.toDetailResponse
import com.example.bookmanagement.controller.dto.UpdateBookRequest
import com.example.bookmanagement.controller.dto.UpdateBookRequest.Companion.toModel
import com.example.bookmanagement.exception.ExceptionHandler
import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.exception.NotFoundException
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.service.BookService
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
import java.util.Locale

@WebMvcTest(BookController::class)
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [RequestProperty::class])
class BookControllerTest: AbstractControllerTest() {

	private var testSuite: MockMvc

	@InjectMockKs
	private lateinit var bookController: BookController

	@MockK
	private lateinit var requestProperty: RequestProperty

	@MockK
	private lateinit var bookService: BookService

	@MockK
	private lateinit var messageSource: MessageSource

	init {
		MockKAnnotations.init(this, relaxUnitFun = true)
		testSuite = MockMvcBuilders.standaloneSetup(bookController)
			.setControllerAdvice(ExceptionHandler(messageSource))
			.setMessageConverters(MappingJackson2HttpMessageConverter())
			.build()
	}

	@Nested
	@DisplayName("書籍一覧")
	inner class GetAll {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			val books = listOf(
				createModel(1),
				createModel(2)
			)
			every { bookService.fetchAll(limit, offset) } returns books
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/book")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllBookResponse::class.java)
			assertEquals(GetAllBookResponse(books = books.map { it.toResponse() }), response)
			verify(exactly = 1) { bookService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("取得なし")
		fun notFound() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			val books = emptyList<Book>()
			every { bookService.fetchAll(limit, offset) } returns books
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(get("/book")
				.header(apiKey, "TEST")
				.queryParam("limit", "$limit")
				.queryParam("offset", "$offset")
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllBookResponse::class.java)
			assertEquals(GetAllBookResponse(books = emptyList()), response)
			verify(exactly = 1) { bookService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 101
			val offset = 0
			every { bookService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Bad Request."

			// execute
			val actual = testSuite.perform(get("/book")
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
			verify(exactly = 0) { bookService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { bookService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/book")
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
			verify(exactly = 0) { bookService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val authorId = null
			val limit = 100
			val offset = 0
			every { bookService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				patch("/book")
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
			verify(exactly = 0) { bookService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("書籍検索")
	inner class Search {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			TraceNumberUtil.init()
			val bookId = 1L
			val book = createModel(bookId)
			every { bookService.fetchByIsbn(book.isbn) } returns book
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/book/search")
					.header(apiKey, "TEST")
					.queryParam("isbn", book.isbn)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetDetailBookResponse::class.java)
			assertEquals(GetDetailBookResponse(book = book.toDetailResponse()), response)
			verify(exactly = 1) { bookService.fetchByIsbn(book.isbn) }
		}

		@Test
		@DisplayName("取得なし")
		fun notFound() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val book = createModel(bookId)
			val errorMessage = "対象の書籍が見つかりませんでした。"
			every { bookService.fetchByIsbn(book.isbn) } throws NotFoundException(errorMessage)
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8000", emptyArray(), Locale.JAPAN) } returns "Not Found."

			// execute
			val actual = testSuite.perform(
				get("/book/search")
					.header(apiKey, "TEST")
					.queryParam("isbn", book.isbn)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.NOT_FOUND.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.NOT_FOUND_ERROR, response.errors[0].errorCode)
			assertEquals(errorMessage, response.errors[0].errorMessage)
			verify(exactly = 1) { bookService.fetchByIsbn(book.isbn) }
			verify(exactly = 1) { messageSource.getMessage("BE8000", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val book = createModel(bookId).copy(isbn = "")
			every { bookService.fetchByIsbn(book.isbn) } returns book
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Bad Request."

			// execute
			val actual = testSuite.perform(
				get("/book/search")
					.header(apiKey, "TEST")
					.queryParam("isbn", book.isbn)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.BAD_REQUEST.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.VALIDATION_ERROR, response.errors[0].errorCode)
			assertEquals("isbn:入力必須です。", response.errors[0].errorMessage)
			verify(exactly = 0) { bookService.fetchByIsbn(book.isbn) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val book = createModel(bookId)
			every { bookService.fetchByIsbn(book.isbn) } returns book
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/book/search")
					.header(apiKey, "DUMMY")
					.queryParam("isbn", book.isbn)
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.FORBIDDEN.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, ErrorResponse::class.java)
			assertEquals(response.traceNumber, traceNumber)
			assertEquals(ErrorCode.INVALID_API_KEY_ERROR, response.errors[0].errorCode)
			assertEquals("APIキーが不正です。", response.errors[0].errorMessage)
			verify(exactly = 0) { bookService.fetchByIsbn(book.isbn) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val book = createModel(bookId)
			every { bookService.fetchByIsbn(book.isbn) } returns book
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/book/search")
					.header(apiKey, "TEST")
					.queryParam("isbn", book.isbn)
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
			verify(exactly = 0) { bookService.fetchByIsbn(book.isbn) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("書籍登録")
	inner class Create {

		@Test
		@DisplayName("登録成功")
		fun success() {
			// init
			TraceNumberUtil.init()
			val input = createRequest()
			every { bookService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				post("/book")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.CREATED.value(), actual.response.status)
			verify(exactly = 1) { bookService.create(input.toModel()) }
		}

		@Test
		@DisplayName("登録失敗")
		fun failed() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { bookService.create(input.toModel()) } throws SQLException("DBエラー")
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) } returns "Internal Server Error."

			// execute
			val actual = testSuite.perform(
				post("/book")
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
			verify(exactly = 1) { bookService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { bookService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				post("/book")
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
			verify(exactly = 0) { bookService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			every { bookService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				put("/book")
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
			verify(exactly = 0) { bookService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest().copy(name = "")
			every { bookService.create(input.toModel()) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Request Error."

			// execute
			val actual = testSuite.perform(
				post("/book")
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
			verify(exactly = 0) { bookService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("実行拒否エラー")
		fun executeRefusalError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val input = createRequest()
			val errorMessage = "実行拒否エラー"
			every { bookService.create(input.toModel()) } throws ExecuteRefusalException(errorMessage)
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) } returns "Execute Refusal Error."

			// execute
			val actual = testSuite.perform(
				post("/book")
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
			verify(exactly = 1) { bookService.create(input.toModel()) }
			verify(exactly = 1) { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) }
		}
	}

	@Nested
	@DisplayName("書籍更新")
	inner class Update {

		@Test
		@DisplayName("更新成功")
		fun success() {
			// init
			val bookId = 1L
			TraceNumberUtil.init()
			val input = updateRequest()
			every { bookService.update(input.toModel(bookId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				put("/book/$bookId")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(input).trimIndent())
			).andReturn()

			// assert
			assertEquals(HttpStatus.NO_CONTENT.value(), actual.response.status)
			verify(exactly = 1) { bookService.update(input.toModel(bookId)) }
		}

		@Test
		@DisplayName("更新失敗")
		fun failed() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { bookService.update(input.toModel(bookId)) } throws SQLException()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) } returns "Internal Server Error."

			// execute
			val actual = testSuite.perform(
				put("/book/$bookId")
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
			verify(exactly = 1) { bookService.update(input.toModel(bookId)) }
			verify(exactly = 1) { messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { bookService.update(input.toModel(bookId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				put("/book/$bookId")
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
			verify(exactly = 0) { bookService.update(input.toModel(bookId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			every { bookService.update(input.toModel(bookId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/book/$bookId")
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
			verify(exactly = 0) { bookService.update(input.toModel(bookId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest().copy(isbn = "")
			every { bookService.update(input.toModel(bookId)) } returns Unit
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Request Error."

			// execute
			val actual = testSuite.perform(
				put("/book/$bookId")
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
			assertEquals("isbn:10文字以上20文字以下にしてください。", response.errors[0].errorMessage)
			verify(exactly = 0) { bookService.update(input.toModel(bookId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("実行拒否エラー")
		fun executeRefusalError() {
			// init
			val bookId = 1L
			val traceNumber = TraceNumberUtil.init()
			val input = updateRequest()
			val errorMessage = "実行拒否エラー"
			every { bookService.update(input.toModel(bookId)) } throws ExecuteRefusalException(errorMessage)
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) } returns "Execute Refusal Error."

			// execute
			val actual = testSuite.perform(
				put("/book/$bookId")
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
			verify(exactly = 1) { bookService.update(input.toModel(bookId)) }
			verify(exactly = 1) { messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN) }
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

	private fun updateRequest(): UpdateBookRequest {
		return UpdateBookRequest(
			name = "bookName",
			totalPage = 100,
			isbn = "978-3-16-148410-0",
			publishedAt = DateUtil.getCurrentDate(),
			publisherId = 1,
			genreId = 1,
			authorId = 1
		)
	}

	private fun createModel(id: Long): Book {
		return Book(
			id = 1,
			name = "bookName$id",
			totalPage = 100,
			isbn = "978-3-16-148410-$id",
			publishedAt = DateUtil.getCurrentDate(),
			publisherId = 1,
			publisherName = DefaultValueConstant.STRING,
			genreId = 1,
			genreName = DefaultValueConstant.STRING,
			authorId = 1,
			authorName = DefaultValueConstant.STRING
		)
	}
}
