package com.example.bookmanagement.constant

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RequestProperty {

	@Value("\${request.http-headers.api-key}")
	val apiKey = ""
}
