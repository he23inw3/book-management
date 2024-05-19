package com.example.bookmanagement.controller.dto

import com.example.bookmanagement.openapi.controller.dto.ErrorResponseIF

data class ErrorResponse(
	override val traceNumber: String,
	override val errors: List<Error>
) : ErrorResponseIF {

	data class Error(
		override val errorCode: String,
		override val errorMessage: String
	) : ErrorResponseIF.ErrorIF
}
