package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.model.Publisher
import com.example.bookmanagement.openapi.controller.dto.GetAllPublisherResponseIF

data class GetAllPublisherResponse(
	override val publisher: List<PublisherResponse>
) : GetAllPublisherResponseIF {

	data class PublisherResponse(
		override val id: Long,
		override val name: String
	) : GetAllPublisherResponseIF.PublisherResponseIF

	companion object {

		fun Publisher.toResponse(): PublisherResponse {
			return PublisherResponse(
				id = this.id,
				name = this.name
			)
		}
	}
}
