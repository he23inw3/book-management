package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.openapi.controller.dto.CreateAuthorRequestIF
import com.example.bookmanagement.model.Author

data class CreateAuthorRequest(
	override val name: String?,
	override val briefHistory: String?
) : CreateAuthorRequestIF {


	companion object {

		fun CreateAuthorRequest.toModel(): Author {
			// !!: bean validated.
			// id: because db data type is bigserial.
			return Author(
				id = DefaultValueConstant.LONG,
				name = this.name!!,
				briefHistory = this.briefHistory!!
			)
		}
	}
}
