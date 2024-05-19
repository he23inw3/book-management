package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

@Schema(name = "CreateAuthorRequest", description = "著者登録入力")
interface CreateAuthorRequestIF {

	@get:Schema(description = "著者名", type = "string", required = true, example = "鳥山克之")
	@get:NotBlank
	@get:Length(max = 255)
	val name: String?

	@get:Schema(description = "略歴", type = "string", required = true, example = "ドラゴンボールを作りました。")
	@get:NotBlank
	val briefHistory: String?
}
