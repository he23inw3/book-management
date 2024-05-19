package com.example.bookmanagement.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

abstract class AbstractControllerTest {

	val apiKey = "Api-Key"
	val objectMapper = jacksonObjectMapper()

	init {
		objectMapper.registerModules(JavaTimeModule())
	}

	protected fun <T> readValue(
		byteArray: ByteArray,
		clazz: Class<T>,
		charset: Charset = Charsets.UTF_8
	): T {
		val bufferedReader = BufferedReader(InputStreamReader(byteArray.inputStream(), charset))
		return objectMapper.readValue(bufferedReader, clazz)
	}
}
