package com.example.bookmanagement.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object DateUtil {

	fun getCurrentDate(
		zoneId: ZoneId = ZoneId.of("Asia/Tokyo")
	): LocalDate {
		return LocalDate.now(zoneId)
	}

	fun getCurrentDateTime(
		zoneId: ZoneId = ZoneId.of("Asia/Tokyo")
	): LocalDateTime {
		return LocalDateTime.now(zoneId)
	}
}
