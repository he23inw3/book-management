package com.example.bookmanagement.controller

import com.example.bookmanagement.constant.RequestProperty
import com.example.bookmanagement.exception.InvalidApiKeyException

abstract class AbstractController(
	private val requestProperty: RequestProperty
) {

	protected fun checkApiKey(apiKey: String?) {
		if (requestProperty.apiKey != apiKey) {
			throw InvalidApiKeyException("APIキーに誤りがあります。")
		}
	}

	companion object {
		const val API_KEY = "Api-Key"
	}
}
