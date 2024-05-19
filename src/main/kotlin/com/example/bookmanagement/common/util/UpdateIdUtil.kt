package com.example.bookmanagement.common.util

import com.example.bookmanagement.constant.ApiConstant
import org.slf4j.MDC

object UpdateIdUtil {

	fun init(updateId: String): String {
		MDC.put(ApiConstant.X_UPDATE_ID, updateId)
		return MDC.get(ApiConstant.X_UPDATE_ID)
	}

	fun get(): String {
		return MDC.get(ApiConstant.X_UPDATE_ID)
	}
}
