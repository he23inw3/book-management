package com.example.bookmanagement.controller

import com.example.bookmanagement.common.annotation.Api
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.CreateBookRequest
import com.example.bookmanagement.controller.dto.CreateBookRequest.Companion.toModel
import com.example.bookmanagement.controller.dto.GetAllBookResponse
import com.example.bookmanagement.controller.dto.GetAllBookResponse.Companion.toResponse
import com.example.bookmanagement.controller.dto.GetDetailBookResponse
import com.example.bookmanagement.controller.dto.GetDetailBookResponse.Companion.toDetailResponse
import com.example.bookmanagement.controller.dto.UpdateBookRequest
import com.example.bookmanagement.controller.dto.UpdateBookRequest.Companion.toModel
import com.example.bookmanagement.openapi.controller.BookControllerIF
import com.example.bookmanagement.service.BookService
import org.springframework.web.bind.annotation.RestController

@RestController
class BookController(
	requestProperty: RequestProperty,
	private val bookService: BookService
) : AbstractController(requestProperty), BookControllerIF {

	@Api(id = "BE-API001", name = "書籍一覧API")
	override fun getAll(apiKey: String?, limit: Int, offset: Int): GetAllBookResponse {
		checkApiKey(apiKey)
		val books = bookService.fetchAll(limit, offset)
		return GetAllBookResponse(
			books = books.map { it.toResponse() }
		)
	}

	@Api(id = "BE-API002", name = "書籍検索API")
	override fun search(apiKey: String?, isbn: String): GetDetailBookResponse {
		checkApiKey(apiKey)
		val book = bookService.fetchByIsbn(isbn)
		return GetDetailBookResponse(book = book.toDetailResponse())
	}

	@Api(id = "BE-API003", name = "書籍登録API")
	override fun create(apiKey: String?, request: CreateBookRequest) {
		checkApiKey(apiKey)
		bookService.create(request.toModel())
	}

	@Api(id = "BE-API004", name = "書籍更新API")
	override fun update(apiKey: String?, bookId: Long, request: UpdateBookRequest) {
		checkApiKey(apiKey)
		bookService.update(request.toModel(bookId))
	}
}
