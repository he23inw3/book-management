package com.example.bookmanagement.service

import com.example.bookmanagement.constant.DefaultValueConstant

abstract class AbstractService {

	protected fun isNotDefault(value: Long): Boolean {
		return value != DefaultValueConstant.LONG
	}
}
