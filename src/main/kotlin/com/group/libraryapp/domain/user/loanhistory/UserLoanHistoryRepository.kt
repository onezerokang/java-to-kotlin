package com.group.libraryapp.domain.user.loanhistory

import com.group.libraryapp.domain.user.UserLoanStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserLoanHistoryRepository : JpaRepository<UserLoanHistory, Long> {
    fun findByBookNameAndStatus(bookName: String, status: UserLoanStatus): UserLoanHistory?

    fun countByStatus(status: UserLoanStatus): Long
}