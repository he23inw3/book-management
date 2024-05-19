package com.example.bookmanagement.service

import com.example.bookmanagement.exception.ExecuteRefusalException
import com.example.bookmanagement.exception.NotFoundException
import com.example.bookmanagement.model.Book
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import com.example.bookmanagement.repository.GenreRepository
import com.example.bookmanagement.repository.PublisherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
	private val authorRepository: AuthorRepository,
	private val bookRepository: BookRepository,
	private val genreRepository: GenreRepository,
	private val publisherRepository: PublisherRepository
): AbstractService() {

	@Transactional(readOnly = true)
	fun fetchAll(limit: Int, offset: Int): List<Book> {
		return bookRepository.fetchAll(limit, offset)
	}

	@Transactional(readOnly = true)
	fun fetchByIsbn(isbn: String): Book {
		return bookRepository.fetchDetailByIsbn(isbn)
			?: throw NotFoundException("書籍が見つかりませんでした。ISBN=$isbn")
	}

	@Transactional(rollbackFor = [Exception::class])
	fun create(book: Book) {
		checkCreateBook(book)
		bookRepository.insert(book)
	}

	@Transactional(rollbackFor = [Exception::class])
	fun update(book: Book) {
		checkUpdateBook(book)
		bookRepository.update(book)
	}

	private fun checkCreateBook(book: Book) {
		if (bookRepository.existsByIsbn(book.isbn)) {
			throw ExecuteRefusalException("既に登録済の書籍です。ISBN=${book.isbn}")
		}

		if (!publisherRepository.existsById(book.publisherId)) {
			throw ExecuteRefusalException("登録されていない出版社を指定しています。publisherId=${book.publisherId}")
		}

		if (!genreRepository.existsById(book.genreId)) {
			throw ExecuteRefusalException("登録されていないジャンルを指定しています。genreId=${book.genreId}")
		}

		if (!authorRepository.existsById(book.authorId)) {
			throw ExecuteRefusalException("登録されていない著者を指定しています。authorId=${book.authorId}")
		}
	}

	private fun checkUpdateBook(book: Book) {
		if (!bookRepository.existsById(book.id)) {
			throw ExecuteRefusalException("登録されていない書籍です。bookId=${book.id}")
		}

		if (isNotDefault(book.genreId) && !genreRepository.existsById(book.genreId)) {
			throw ExecuteRefusalException("登録されていないジャンルを指定しています。genreId=${book.genreId}")
		}

		if (isNotDefault(book.authorId) && !authorRepository.existsById(book.authorId)) {
			throw ExecuteRefusalException("登録されていない著者を指定しています。authorId=${book.authorId}")
		}
	}
}
