package com.example.bookmanagement.openapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.Length

@Schema(name = "UpdateAuthorRequest", description = "著者更新入力")
interface UpdateAuthorRequestIF {

	@get:Schema(description = "著者名", type = "string", required = true, example = "鳥山克之")
	@get:Length(max = 255)
	val name: String?

	@get:Schema(description = "略歴", type = "string", required = true, example = "ドラゴンボールを作りました。")
	val briefHistory: String?
}
