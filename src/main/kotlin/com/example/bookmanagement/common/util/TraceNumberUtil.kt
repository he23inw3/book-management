package com.example.bookmanagement.common.util

import com.example.bookmanagement.constant.ApiConstant
import org.slf4j.MDC
import java.util.UUID

object TraceNumberUtil {

	fun init(): String {
		MDC.put(ApiConstant.X_TRACE_ID, UUID.randomUUID().toString())
		return get()
	}

	fun get(): String {
		return MDC.get(ApiConstant.X_TRACE_ID)
	}
}
