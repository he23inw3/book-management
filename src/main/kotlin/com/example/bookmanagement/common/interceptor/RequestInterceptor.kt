package com.example.bookmanagement.common.interceptor

import com.example.bookmanagement.common.util.TraceNumberUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Locale

@Component
class RequestInterceptor(
	private val messageSource: MessageSource
) : OncePerRequestFilter() {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		val traceNumber = TraceNumberUtil.init()
		val startTime = System.currentTimeMillis()
		log.info(
			messageSource.getMessage("BE0001", emptyArray(), Locale.JAPAN),
			request.requestURI, request.method, traceNumber
		)
		filterChain.doFilter(request, response)
		val endTime = System.currentTimeMillis() - startTime
		log.info(
			messageSource.getMessage("BE0002", emptyArray(), Locale.JAPAN),
			request.requestURI, request.method, traceNumber, response.status, endTime
		)
	}
}
