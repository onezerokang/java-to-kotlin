package com.group.libraryapp.dto.user.response

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory

data class UserLoanHistoryResponse(
    val name: String,
    val books: List<BooksHistoryResponse>
) {
    companion object {
        fun of(user: User): UserLoanHistoryResponse {
            return UserLoanHistoryResponse(
                name = user.name,
                books = user.userLoanHistories.map(BooksHistoryResponse::of)
            )
        }
    }
}

data class BooksHistoryResponse(
    val name: String,
    val isReturn: Boolean,
) {
    companion object {
        fun of(history: UserLoanHistory): BooksHistoryResponse {
            return BooksHistoryResponse(
                name = history.bookName,
                isReturn = history.isReturn,
            )
        }
    }
}