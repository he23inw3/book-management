package com.example.bookmanagement.openapi.controller

import com.example.bookmanagement.controller.AbstractController.Companion.API_KEY
import com.example.bookmanagement.controller.dto.CreateAuthorRequest
import com.example.bookmanagement.controller.dto.GetAllAuthorResponse
import com.example.bookmanagement.controller.dto.GetDetailAuthorResponse
import com.example.bookmanagement.controller.dto.UpdateAuthorRequest
import com.example.bookmanagement.openapi.controller.dto.GetAllAuthorResponseIF
import com.example.bookmanagement.openapi.controller.dto.GetDetailAuthorResponseIF
import com.example.bookmanagement.openapi.controller.dto.ErrorResponseIF
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
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Tag(name = "Author", description = "著者に関するエンドポイント")
@RequestMapping("/author")
interface AuthorControllerIF {

	@Operation(summary = "BE-API005: 著者一覧API")
	@ApiResponses(
		ApiResponse(
			responseCode = "200", description = "取得成功",
			content = [Content(schema = Schema(implementation = GetAllAuthorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "400", description = "入力チェックエラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
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
			responseCode = "406", description = "実行拒否エラー",
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
		@Parameter(description = "著者の最大取得件数", required = false, example = "100")
		@RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) limit: Int,
		@Parameter(description = "著者の取得開始位置", required = false, example = "0")
		@RequestParam(required = false, defaultValue = "0") @Min(0) @Max(100) offset: Int
	): GetAllAuthorResponse

	@Operation(summary = "BE-API006: 著者詳細API")
	@ApiResponses(
		ApiResponse(
			responseCode = "200", description = "取得成功",
			content = [Content(schema = Schema(implementation = GetDetailAuthorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "400", description = "入力チェックエラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
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
			responseCode = "406", description = "実行拒否エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "500", description = "想定外エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		)
	)
	@GetMapping("/{authorId}", produces = [MediaType.APPLICATION_JSON_VALUE])
	@ResponseStatus(HttpStatus.OK)
	fun getDetail(
		@Parameter(description = "APIキー", required = true, example = "TEST")
		@RequestHeader(value = API_KEY, required = false)
		apiKey: String?,
		@Parameter(description = "authorId", required = true, example = "1")
		@PathVariable("authorId", required = true)
		authorId: Long,
		@Parameter(description = "執筆した書籍の最大取得件数", required = false, example = "100")
		@RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) limit: Int,
		@Parameter(description = "執筆した書籍の取得開始位置", required = false, example = "0")
		@RequestParam(required = false, defaultValue = "0") @Min(0) @Max(100) offset: Int
	): GetDetailAuthorResponse

	@Operation(summary = "BE-API007: 著者登録API")
	@ApiResponses(
		ApiResponse(responseCode = "201", description = "登録成功"),
		ApiResponse(
			responseCode = "400", description = "入力チェックエラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
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
			responseCode = "406", description = "実行拒否エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "500", description = "想定外エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		)
	)
	@PostMapping(
		consumes = [MediaType.APPLICATION_JSON_VALUE],
		produces = [MediaType.APPLICATION_JSON_VALUE]
	)
	@ResponseStatus(HttpStatus.CREATED)
	fun create(
		@Parameter(description = "APIキー", required = true, example = "TEST")
		@RequestHeader(value = API_KEY, required = false)
		apiKey: String?,
		@Parameter(description = "書籍情報", required = true)
		@Validated @RequestBody
		request: CreateAuthorRequest
	)

	@Operation(summary = "BE-API008: 著者更新API")
	@ApiResponses(
		ApiResponse(responseCode = "204", description = "更新成功"),
		ApiResponse(
			responseCode = "400", description = "入力チェックエラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
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
			responseCode = "406", description = "実行拒否エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		),
		ApiResponse(
			responseCode = "500", description = "想定外エラー",
			content = [Content(schema = Schema(implementation = ErrorResponseIF::class))]
		)
	)
	@PutMapping(
		"/{authorId}",
		consumes = [MediaType.APPLICATION_JSON_VALUE],
		produces = [MediaType.APPLICATION_JSON_VALUE]
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun update(
		@Parameter(description = "APIキー", required = true, example = "TEST")
		@RequestHeader(value = API_KEY, required = false)
		apiKey: String?,
		@Parameter(description = "著者ID", required = true, example = "1")
		@PathVariable("authorId", required = true)
		authorId: Long,
		@Parameter(description = "著者情報", required = true)
		@Validated @RequestBody
		request: UpdateAuthorRequest
	)
}
