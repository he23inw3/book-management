package com.example.bookmanagement.openapi.controller

import com.example.bookmanagement.controller.AbstractController.Companion.API_KEY
import com.example.bookmanagement.controller.dto.GetAllPublisherResponse
import com.example.bookmanagement.openapi.controller.dto.ErrorResponseIF
import com.example.bookmanagement.openapi.controller.dto.GetAllPublisherResponseIF
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(name = "Publisher", description = "出版社に関するエンドポイント")
@RequestMapping("/publisher")
fun interface PublisherControllerIF {

	@Operation(summary = "BE-API010: 出版社一覧API")
	@ApiResponses(
		ApiResponse(
			responseCode = "200", description = "取得成功",
			content = [Content(schema = Schema(implementation = GetAllPublisherResponseIF::class))]
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
	@ResponseStatus(HttpStatus.OK)
	fun getAll(
		@Parameter(description = "APIキー", required = true, example = "TEST")
		@RequestHeader(value = API_KEY, required = true) apiKey: String?,
		@Parameter(description = "limit", required = true, example = "100")
		@RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) limit: Int,
		@Parameter(description = "offset", required = true, example = "0")
		@RequestParam(required = false, defaultValue = "0") @Min(0) @Max(100) offset: Int
	): GetAllPublisherResponse
}
