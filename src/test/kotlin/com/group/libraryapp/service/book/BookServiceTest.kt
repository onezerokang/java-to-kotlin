package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserLoanStatus
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAllInBatch()
        userLoanHistoryRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("책을 등록할 수 있다.")
    @Test
    fun saveBook() {
        // given
        val request = BookRequest("이상한 나라의 엘리스", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        //then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("이상한 나라의 엘리스")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @DisplayName("책을 대출할 수 있다.")
    @Test
    fun loanBook() {
        // given
        val savedBook = bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("최태현", null))
        val request = BookLoanRequest("최태현", "이상한 나라의 엘리스")

        // when
        bookService.loanBook(request)

        //then
        val histories = userLoanHistoryRepository.findAll()
        assertThat(histories).hasSize(1)
        assertThat(histories[0].bookName).isEqualTo(savedBook.name)
        assertThat(histories[0].user.name).isEqualTo(savedUser.name)
        assertThat(histories[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @DisplayName("책이 진작 대출되어 있다면, 신규 대출에 실패한다.")
    @Test
    fun loanBookFail() {
        // given
        val savedBook = bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("최태현", null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, savedBook.name, UserLoanStatus.LOANED))

        val request = BookLoanRequest("최태현", "이상한 나라의 엘리스")

        // when then
        val message = assertThrows<IllegalArgumentException> { bookService.loanBook(request) }.message
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @DisplayName("책 반납이 정상 동작한다.")
    @Test
    fun returnBook() {
        // given
        val savedBook = bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("최태현", null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, savedBook.name, UserLoanStatus.LOANED))
        val request = BookReturnRequest(savedUser.name, savedBook.name)

        // when
        bookService.returnBook(request)

        // then
        val histories = userLoanHistoryRepository.findAll()
        assertThat(histories).hasSize(1)
        assertThat(histories[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }
}