package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "ErrorResponse", description = "エラー応答")
interface ErrorResponseIF {

	@get:Schema(description = "追跡番号", type = "string", example = "d53af6ea-8f05-4d59-9d3e-8030e1352ef0")
	val traceNumber: String

	@get:Schema(description = "エラー情報", type = "list")
	val errors: List<ErrorIF>

	@Schema(name = "ErrorResponse.Error", description = "エラー情報")
	interface ErrorIF {
		@get:Schema(description = "エラーコード", type = "string", example = "001")
		val errorCode: String

		@get:Schema(description = "エラーメッセージ", type = "string", example = "エラーメッセージ")
		val errorMessage: String
	}
}
