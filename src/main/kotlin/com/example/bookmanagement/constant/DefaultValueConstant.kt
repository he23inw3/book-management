package com.example.bookmanagement.constant

import java.time.LocalDate
import java.time.LocalDateTime

object DefaultValueConstant {
	const val STRING = ""
	const val INT = -1
	const val LONG = -1L
	val DATE = LocalDate.of(2999, 12, 31)
	val DATETIME = LocalDateTime.of(2999, 12, 31, 12, 31, 0)
	val VALID_END_DATE = LocalDate.of(9999, 12, 31)
}
