package com.example.bookmanagement.openapi.controller

import com.example.bookmanagement.controller.AbstractController.Companion.API_KEY
import com.example.bookmanagement.controller.dto.GetAllGenreResponse
import com.example.bookmanagement.openapi.controller.dto.ErrorResponseIF
import com.example.bookmanagement.openapi.controller.dto.GetAllGenreResponseIF
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Genre", description = "ジャンルに関するエンドポイント")
@RequestMapping("/genre")
fun interface GenreControllerIF {

	@Operation(summary = "BE-API009: ジャンル一覧API")
	@ApiResponses(
		ApiResponse(
			responseCode = "200", description = "取得成功",
			content = [Content(schema = Schema(implementation = GetAllGenreResponseIF::class))]
		),
		ApiResponse(
			responseCode = "403", description = "権限エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "404", description = "検索なしエラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "405", description = "HTTPメソッド指定エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "500", description = "想定外エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		)
	)
	@GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
	fun getAll(
		@Parameter(description = "APIキー", required = true, example = "TEST")
		@RequestHeader(value = API_KEY, required = true) apiKey: String?,
	): GetAllGenreResponse
}
