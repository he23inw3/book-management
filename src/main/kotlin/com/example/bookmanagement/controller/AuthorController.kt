package com.example.bookmanagement.controller

import com.example.bookmanagement.common.annotation.Api
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.CreateAuthorRequest
import com.example.bookmanagement.controller.dto.CreateAuthorRequest.Companion.toModel
import com.example.bookmanagement.controller.dto.GetAllAuthorResponse
import com.example.bookmanagement.controller.dto.GetAllAuthorResponse.Companion.toResponse
import com.example.bookmanagement.controller.dto.GetDetailAuthorResponse
import com.example.bookmanagement.controller.dto.GetDetailAuthorResponse.Companion.toDetailResponse
import com.example.bookmanagement.controller.dto.UpdateAuthorRequest
import com.example.bookmanagement.controller.dto.UpdateAuthorRequest.Companion.toModel
import com.example.bookmanagement.openapi.controller.AuthorControllerIF
import com.example.bookmanagement.service.AuthorService
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorController(
	requestProperty: RequestProperty,
	private val authorService: AuthorService
) : AbstractController(requestProperty), AuthorControllerIF {

	@Api(id = "BE-API005", name = "著者一覧API")
	override fun getAll(apiKey: String?, limit: Int, offset: Int): GetAllAuthorResponse {
		checkApiKey(apiKey)
		val authors = authorService.fetchAll(limit, offset)
		return GetAllAuthorResponse(
			authors = authors.map { it.toResponse() }
		)
	}

	@Api(id = "BE-API006", name = "著者詳細API")
	override fun getDetail(apiKey: String?, authorId: Long, limit: Int, offset: Int
	): GetDetailAuthorResponse {
		checkApiKey(apiKey)
		val author = authorService.fetchAuthor(authorId, limit, offset)
		return GetDetailAuthorResponse(
			author = author.toDetailResponse()
		)
	}

	@Api(id = "BE-API007", name = "著者登録API")
	override fun create(apiKey: String?, request: CreateAuthorRequest) {
		checkApiKey(apiKey)
		authorService.create(request.toModel())
	}

	@Api(id = "BE-API008", name = "著者更新API")
	override fun update(apiKey: String?, authorId: Long, request: UpdateAuthorRequest) {
		checkApiKey(apiKey)
		authorService.update(request.toModel(authorId))
	}
}
