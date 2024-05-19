package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.openapi.controller.dto.GetAllGenreResponseIF
import com.example.bookmanagement.model.Genre

data class GetAllGenreResponse(
	override val genres: List<GenreResponse>
) : GetAllGenreResponseIF {

	data class GenreResponse(
		override val id: Long,
		override val name: String
	) : GetAllGenreResponseIF.GenreResponseIF

	companion object {

		fun Genre.toResponse(): GenreResponse {
			return GenreResponse(
				id = this.id,
				name = this.name
			)
		}
	}
}
