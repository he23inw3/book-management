package com.example.bookmanagement.controller

import com.example.bookmanagement.common.annotation.Api
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.GetAllGenreResponse
import com.example.bookmanagement.controller.dto.GetAllGenreResponse.Companion.toResponse
import com.example.bookmanagement.openapi.controller.GenreControllerIF
import com.example.bookmanagement.service.GenreService
import org.springframework.web.bind.annotation.RestController

@RestController
class GenreController(
	requestProperty: RequestProperty,
	private val genreService: GenreService
) : AbstractController(requestProperty), GenreControllerIF {

	@Api(id = "BE-API009", name = "ジャンル一覧API")
	override fun getAll(apiKey: String?): GetAllGenreResponse {
		checkApiKey(apiKey)
		val genres = genreService.fetchAll()
		return GetAllGenreResponse(
			genres = genres.map { it.toResponse() }
		)
	}
}
