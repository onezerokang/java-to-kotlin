package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserLoanStatus
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun tearDown() {
        userLoanHistoryRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("회원을 등록할 수 있다.")
    @Test
    fun saveUser() {
        // given
        val request = UserCreateRequest("최태현", null)

        // when
        userService.saveUser(request)

        //then
        val users = userRepository.findAll()
        assertThat(users).hasSize(1)
        assertThat(users[0].name).isEqualTo("최태현")
        assertThat(users[0].age).isNull()
    }

    @DisplayName("등록된 회원을 조회할 수 있다.")
    @Test
    fun getUsers() {
        // given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null),
            )
        )

        // when
        val users = userService.getUsers()

        //then
        assertThat(users).hasSize(2)
            .extracting("name", "age")
            .containsExactlyInAnyOrder(
                tuple("A", 20),
                tuple("B", null)
            )
    }

    @DisplayName("회원의 이름을 변경할 수 있다.")
    @Test
    fun updateUserName() {
        // given
        val savedUser = userRepository.save(User("A", null))
        val request = UserUpdateRequest(savedUser.id!!, "B")

        // when
        userService.updateUserName(request)

        //then
        val user = userRepository.findAll()[0]
        assertThat(user.name).isEqualTo("B")
    }

    @DisplayName("회원 계정을 삭제할 수 있다.")
    @Test
    fun deleteUser() {
        // given
        val name = "A"
        val savedUser = userRepository.save(User(name, null))

        // when
        userService.deleteUser(name)

        //then
        val users = userRepository.findAll()
        assertThat(users).isEmpty()
    }

    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다.")
    @Test
    fun getUserLoanHistories() {
        // given
        userRepository.save(User("A", null))

        // when
        val userLoanHistories = userService.getUserLoanHistories()

        //then
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].name).isEqualTo("A")
        assertThat(userLoanHistories[0].books).isEmpty()
    }

    @DisplayName("대출 기록이 많은 유저의 응답이 정상 동작한다.")
    @Test
    fun getUserLoanHistories2() {
        // given
        val user = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(user, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(user, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(user, "책3", UserLoanStatus.RETURNED),
            )
        )

        // when
        val userLoanHistories = userService.getUserLoanHistories()

        //then
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].name).isEqualTo("A")
        assertThat(userLoanHistories[0].books).extracting("name")
            .containsExactlyInAnyOrder("책1", "책2", "책3")
        assertThat(userLoanHistories[0].books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true)

    }
}