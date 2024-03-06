package com.group.libraryapp.dto.user.response

data class UserLoanHistoryResponse(
    val name: String,
    val books: List<BooksHistoryResponse>
) {
}

data class BooksHistoryResponse(
    val name: String,
    val isReturn: Boolean,
) {}