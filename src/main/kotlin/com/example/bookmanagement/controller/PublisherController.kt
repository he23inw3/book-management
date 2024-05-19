package com.example.bookmanagement.controller

import com.example.bookmanagement.common.annotation.Api
import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.controller.dto.GetAllPublisherResponse
import com.example.bookmanagement.controller.dto.GetAllPublisherResponse.Companion.toResponse
import com.example.bookmanagement.openapi.controller.PublisherControllerIF
import com.example.bookmanagement.service.PublisherService
import org.springframework.web.bind.annotation.RestController

@RestController
class PublisherController(
	requestProperty: RequestProperty,
	private val publisherService: PublisherService
) : AbstractController(requestProperty), PublisherControllerIF {

	@Api(id = "BE-API010", name = "出版社一覧API")
	override fun getAll(apiKey: String?, limit: Int, offset: Int): GetAllPublisherResponse {
		checkApiKey(apiKey)
		val publishers = publisherService.fetchAll(limit, offset)
		return GetAllPublisherResponse(
			publisher = publishers.map { it.toResponse() }
		)
	}
}
