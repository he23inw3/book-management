package com.example.bookmanagement.exception

import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.controller.dto.ErrorResponse
import com.example.bookmanagement.constant.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import java.util.Locale

@Component
@RestControllerAdvice
class ExceptionHandler(
	private val messageSource: MessageSource
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	@ExceptionHandler(NotFoundException::class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	fun handleNotFoundException(e: NotFoundException): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8000", emptyArray(), Locale.JAPAN), traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = listOf(
				ErrorResponse.Error(
					errorCode = ErrorCode.NOT_FOUND_ERROR,
					errorMessage = e.message ?: "未発見エラー"
				)
			)
		)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN), traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = e.fieldErrors.map {
				ErrorResponse.Error(
					errorCode = ErrorCode.VALIDATION_ERROR,
					errorMessage = "${it.field}:${it.defaultMessage}"
				)
			}
		)
	}

	@ExceptionHandler(HandlerMethodValidationException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleConstraintViolationException(e: HandlerMethodValidationException): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8001", emptyArray(), Locale.JAPAN), traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = e.allValidationResults.map {
				val parameterName = it.methodParameter.parameterName
				val errorMessage = if (it.resolvableErrors.isNotEmpty()) {
					it.resolvableErrors.first().defaultMessage
				} else {
					"リクエストエラー"
				}

				ErrorResponse.Error(
					errorCode = ErrorCode.VALIDATION_ERROR,
					errorMessage = "${parameterName}:${errorMessage}"
				)
			}
		)
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8002", emptyArray(), Locale.JAPAN), traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = listOf(
				ErrorResponse.Error(
					errorCode = ErrorCode.NOT_ALLOWED_ERROR,
					errorMessage = "該当エンドポイントで${e.method}メソッドの処理は許可されていません。"
				)
			)
		)
	}

	@ExceptionHandler(InvalidApiKeyException::class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	fun handleInvalidApiKeyException(): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8003", emptyArray(), Locale.JAPAN), traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = listOf(
				ErrorResponse.Error(
					errorCode = ErrorCode.INVALID_API_KEY_ERROR,
					errorMessage = "APIキーが不正です。"
				)
			)
		)
	}

	@ExceptionHandler(ExecuteRefusalException::class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	fun handleNotAcceptException(e: ExecuteRefusalException): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.warn(messageSource.getMessage("BE8004", emptyArray(), Locale.JAPAN), e.message, traceNumber)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = listOf(
				ErrorResponse.Error(
					errorCode = ErrorCode.EXECUTION_REFUSAL_ERROR,
					errorMessage = e.message ?: "実行拒否エラー"
				)
			)
		)
	}

	@ExceptionHandler(Exception::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	fun handleException(e: Exception): ErrorResponse {
		val traceNumber = TraceNumberUtil.get()
		log.error(messageSource.getMessage("BE9000", emptyArray(), Locale.JAPAN), traceNumber, e)
		return ErrorResponse(
			traceNumber = traceNumber,
			errors = listOf(
				ErrorResponse.Error(
					errorCode = ErrorCode.INTERNAL_SERVER_ERROR,
					errorMessage = "想定外エラー"
				)
			)
		)
	}
}
