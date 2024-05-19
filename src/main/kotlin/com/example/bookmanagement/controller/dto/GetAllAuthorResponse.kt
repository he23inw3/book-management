package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.openapi.controller.dto.GetAllAuthorResponseIF
import com.example.bookmanagement.model.Author

data class GetAllAuthorResponse(
	override val authors: List<AuthorResponse>
) : GetAllAuthorResponseIF {

	data class AuthorResponse(
		override val id: Long,
		override val name: String,
		override val briefHistory: String
	) : GetAllAuthorResponseIF.AuthorResponseIF

	companion object {

		fun Author.toResponse(): AuthorResponse {
			return AuthorResponse(
				id = this.id,
				name = this.name,
				briefHistory = this.briefHistory
			)
		}
	}
}
