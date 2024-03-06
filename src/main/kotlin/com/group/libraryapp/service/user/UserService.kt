package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserLoanStatus
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.BooksHistoryResponse
import com.group.libraryapp.dto.user.response.UserLoanHistoryResponse
import com.group.libraryapp.dto.user.response.UserResponse
import com.group.libraryapp.util.fail
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun saveUser(request: UserCreateRequest) {
        val user = User(request.name, request.age)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUsers(): List<UserResponse> {
        return userRepository.findAll()
            .map { user -> UserResponse.of(user) }
    }

    @Transactional
    fun updateUserName(request: UserUpdateRequest) {
        val user = userRepository.findByIdOrNull(request.id) ?: fail()
        user.updateName(request.name)
    }

    @Transactional
    fun deleteUser(name: String) {
        val user = userRepository.findByName(name) ?: fail()
        userRepository.delete(user)
    }

    fun getUserLoanHistories(): List<UserLoanHistoryResponse> {

        return userRepository.findAll().map { user ->
            UserLoanHistoryResponse(
                name = user.name,
                books = user.userLoanHistories.map { history ->
                    BooksHistoryResponse(
                        name = history.bookName,
                        isReturn = history.status == UserLoanStatus.RETURNED,
                    )
                }
            )
        }
    }
}