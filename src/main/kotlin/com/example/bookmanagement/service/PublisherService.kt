package com.example.bookmanagement.service

import com.example.bookmanagement.model.Publisher
import com.example.bookmanagement.repository.PublisherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublisherService(
	private val publisherRepository: PublisherRepository
): AbstractService() {

	@Transactional(readOnly = true)
	fun fetchAll(limit: Int, offset: Int): List<Publisher> {
		return publisherRepository.fetchAll(limit, offset)
	}
}
