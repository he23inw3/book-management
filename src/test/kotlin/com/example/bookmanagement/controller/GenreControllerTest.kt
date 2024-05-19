package com.example.bookmanagement.controller

import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.constant.ErrorCode
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.ErrorResponse
import com.example.bookmanagement.controller.dto.GetAllGenreResponse
import com.example.bookmanagement.controller.dto.GetAllGenreResponse.Companion.toResponse
import com.example.bookmanagement.exception.ExceptionHandler
import com.example.bookmanagement.model.Genre
import com.example.bookmanagement.service.GenreService
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

@WebMvcTest(GenreController::class)
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [RequestProperty::class])
class GenreControllerTest: AbstractControllerTest() {

	private var testSuite: MockMvc

	@InjectMockKs
	private lateinit var genreController: GenreController

	@MockK
	private lateinit var requestProperty: RequestProperty

	@MockK
	private lateinit var genreService: GenreService

	@MockK
	private lateinit var messageSource: MessageSource

	init {
		MockKAnnotations.init(this, relaxUnitFun = true)
		testSuite = MockMvcBuilders.standaloneSetup(genreController)
			.setControllerAdvice(ExceptionHandler(messageSource))
			.setMessageConverters(MappingJackson2HttpMessageConverter())
			.build()
	}

	@Nested
	@DisplayName("ジャンル一覧")
	inner class GetAll {

		@Test
		@DisplayName("取得成功")
		fun found() {
			// init
			TraceNumberUtil.init()
			val genres = listOf(
				createModel(1),
				createModel(2)
			)
			every { genreService.fetchAll() } returns genres
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/genre")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllGenreResponse::class.java)
			assertEquals(GetAllGenreResponse(genres = genres.map { it.toResponse() }), response)
			verify(exactly = 1) { genreService.fetchAll() }
		}

		@Test
		@DisplayName("取得なし")
		fun notFound() {
			// init
			TraceNumberUtil.init()
			every { genreService.fetchAll() } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"

			// execute
			val actual = testSuite.perform(
				get("/genre")
					.header(apiKey, "TEST")
					.accept(MediaType.APPLICATION_JSON_VALUE)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andReturn()

			// assert
			assertEquals(HttpStatus.OK.value(), actual.response.status)
			val response = readValue(actual.response.contentAsByteArray, GetAllGenreResponse::class.java)
			assertEquals(GetAllGenreResponse(genres = emptyList()), response)
			verify(exactly = 1) { genreService.fetchAll() }
		}

		@Test
		@DisplayName("APIキーエラー")
		fun invalidApiKeyError() {
			// init
			val traceNumber = TraceNumberUtil.init()
			every { genreService.fetchAll() } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) } returns "API Key Invalid."

			// execute
			val actual = testSuite.perform(
				get("/genre")
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
			verify(exactly = 0) { genreService.fetchAll() }
			verify(exactly = 1) { messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN) }
		}

		@Test
		@DisplayName("Method指定エラー")
		fun httpRequestMethodNotSupported() {
			// init
			val traceNumber = TraceNumberUtil.init()
			every { genreService.fetchAll() } returns emptyList()
			every { requestProperty.apiKey } returns "TEST"
			every { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) } returns "Not Allowed."

			// execute
			val actual = testSuite.perform(
				post("/genre")
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
			verify(exactly = 0) { genreService.fetchAll() }
			verify(exactly = 1) { messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN) }
		}
	}

	private fun createModel(id: Long): Genre {
		return Genre(
			id = id,
			name = "genreName"
		)
	}
}
