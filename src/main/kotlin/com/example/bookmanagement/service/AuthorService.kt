package com.example.bookmanagement.service

import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.exception.NotFoundException
import com.example.bookmanagement.model.Author
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
	private val authorRepository: AuthorRepository,
	private val bookRepository: BookRepository
): AbstractService() {

	@Transactional(readOnly = true)
	fun fetchAll(limit: Int, offset: Int): List<Author> {
		return authorRepository.fetchAll(limit, offset)
	}

	@Transactional(readOnly = true)
	fun fetchAuthor(authorId: Long, booksLimit: Int, booksOffset: Int): Author {
		val author = authorRepository.fetchAuthor(authorId)
			?: throw NotFoundException("著者が見つかりませんでした。authorId=$authorId")
		author.books += bookRepository.fetchByAuthorId(authorId, booksLimit, booksOffset)
		return author
	}

	@Transactional(rollbackFor = [Exception::class])
	fun create(author: Author) {
		checkCreateAuthor(author)
		authorRepository.insert(author)
	}

	@Transactional(rollbackFor = [Exception::class])
	fun update(author: Author) {
		checkUpdateAuthor(author)
		authorRepository.update(author)
	}

	private fun checkCreateAuthor(author: Author) {
		if (authorRepository.existsById(author.id)) {
			throw ExecuteRefusalException("登録済の著者です。authorId=${author.id}")
		}
	}

	private fun checkUpdateAuthor(author: Author) {
		if (!authorRepository.existsById(author.id)) {
			throw ExecuteRefusalException("登録されていない著者です。authorId=${author.id}")
		}
	}
}
