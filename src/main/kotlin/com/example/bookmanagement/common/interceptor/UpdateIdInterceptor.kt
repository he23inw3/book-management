package com.example.bookmanagement.common.interceptor

import com.example.bookmanagement.common.annotation.Api
import com.example.bookmanagement.common.util.TraceNumberUtil
import com.example.bookmanagement.common.util.UpdateIdUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Aspect
@Component
class UpdateIdInterceptor(
	private val messageSource: MessageSource
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	@Before("@annotation(com.example.bookmanagement.common.annotation.Api)")
	fun setUpdateId(joinPoint: JoinPoint) {
		val signature = joinPoint.signature as MethodSignature
		val annotation = signature.method.getAnnotation(Api::class.java)
		val updateId = UpdateIdUtil.init(annotation.id)
		val traceNumber = TraceNumberUtil.get()
		log.info(
			messageSource.getMessage("BE0003", emptyArray(), Locale.JAPAN),
			annotation.name, updateId, traceNumber
		)
	}
}
