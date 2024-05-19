package com.example.bookmanagement.common.interceptor

import com.example.bookmanagement.common.annotation.DbAccess
import com.example.bookmanagement.common.util.TraceNumberUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Aspect
@Component
class DbAccessInterceptor(
	private val messageSource: MessageSource
) {

	private val log = LoggerFactory.getLogger(this::class.java)

	@Before("@annotation(com.example.bookmanagement.common.annotation.DbAccess)")
	fun before(joinPoint: JoinPoint) {
		val signature = joinPoint.signature as MethodSignature
		val annotation = signature.method.getAnnotation(DbAccess::class.java)
		log.info(messageSource.getMessage("BE0004", emptyArray(), Locale.JAPAN), annotation.name, TraceNumberUtil.get())
	}

	@After("@annotation(com.example.bookmanagement.common.annotation.DbAccess)")
	fun after(joinPoint: JoinPoint) {
		val signature = joinPoint.signature as MethodSignature
		val annotation = signature.method.getAnnotation(DbAccess::class.java)
		log.info(messageSource.getMessage("BE0005", emptyArray(), Locale.JAPAN), annotation.name, TraceNumberUtil.get())
	}

	@AfterThrowing(pointcut = "@annotation(com.example.bookmanagement.common.annotation.DbAccess)", throwing = "e")
	fun afterThrowing(joinPoint: JoinPoint, e: Exception) {
		val signature = joinPoint.signature as MethodSignature
		val annotation = signature.method.getAnnotation(DbAccess::class.java)
		log.error(messageSource.getMessage("BE9001", emptyArray(), Locale.JAPAN), annotation.name, TraceNumberUtil.get(), e)
		throw e
	}
}
