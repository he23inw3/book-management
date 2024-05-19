package com.example.bookmanagement.controller

import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.constant.ErrorCode
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.ErrorResponse
import com.example.bookmanagement.controller.dto.GetAllPublisherResponse
import com.example.bookmanagement.controller.dto.GetAllPublisherResponse.Companion.toResponse
import com.example.bookmanagement.exception.ExceptionHandler
import com.example.bookmanagement.model.Publisher
import com.example.bookmanagement.service.PublisherService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.Locale

@WebMvcTest(PublisherController::class)
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [RequestProperty::class])
class PublisherControllerTest: AbstractControllerTest() {

	private var testSuite: MockMvc

	@InjectMockKs
	private lateinit var publisherController: PublisherController

	@MockK
	private lateinit var requestProperty: RequestProperty

	@MockK
	private lateinit var publisherService: PublisherService

	@MockK
	private lateinit var messageSource: MessageSource

	init {
		MockKAnnotations.init(this, relaxUnitFun = true)
		testSuite = MockMvcBuilders.standaloneSetup(publisherController)
			.setControllerAdvice(ExceptionHandler(messageSource))
			.setMessageConverters(MappingJackson2HttpMessageConverter())
			.build()
	}

	@Nested
	@DisplayName("出版社一覧")
	inner class GetAll {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			val publishers = listOf(
				createModel(1),
				createModel(2)
			)
			every { publisherService.fetchAll(limit, offset) } returns publishers
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/publisher")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllPublisherResponse::class.java)
			assertEquals(GetAllPublisherResponse(publisher = publishers.map { it.toResponse() }), response)
			verify(exactly = 1) { publisherService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("取得なし")
		fun notFound() {
			// init
			TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { publisherService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/publisher")
					.header(apiKey, "TEST")
					.queryParam("limit", "$limit")
					.queryParam("offset", "$offset")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllPublisherResponse::class.java)
			assertEquals(GetAllPublisherResponse(publisher = emptyList()), response)
			verify(exactly = 1) { publisherService.fetchAll(limit, offset) }
		}

		@Test
		@DisplayName("リクエストエラー")
		fun requestError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 101
			val offset = 0
			every { publisherService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) } returns "Bad Request."

			// execute
			val actual = testSuite.perform(
				get("/publisher")
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
			verify(exactly = 0) { publisherService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { publisherService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/publisher")
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
			verify(exactly = 0) { publisherService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			val limit = 100
			val offset = 0
			every { publisherService.fetchAll(limit, offset) } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/publisher")
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
			assertEquals("該当エンドポイントでPOSTメソッドの処理は許可されていません。", response.errors[0].errorMessage)
			verify(exactly = 0) { publisherService.fetchAll(limit, offset) }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	private fun createModel(id: Long): Publisher {
		return Publisher(
			id = id,
			name = "publisherName"
		)
	}
}
