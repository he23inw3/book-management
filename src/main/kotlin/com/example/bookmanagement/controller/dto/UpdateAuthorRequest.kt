package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.constant.DefaultValueConstant
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.openapi.controller.dto.UpdateAuthorRequestIF

data class UpdateAuthorRequest(
	override val name: String?,
	override val briefHistory: String?
) : UpdateAuthorRequestIF {

	companion object {

		fun UpdateAuthorRequest.toModel(authorId: Long): Author {
			// default_value: default value is not update to DB.
			return Author(
				id = authorId,
				name = this.name ?: DefaultValueConstant.STRING,
				briefHistory = this.briefHistory ?: DefaultValueConstant.STRING
			)
		}
	}
}
