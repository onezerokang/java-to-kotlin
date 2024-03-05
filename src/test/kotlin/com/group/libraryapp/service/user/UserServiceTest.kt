package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
        private val userRepository: UserRepository,
        private val userService: UserService
) {
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
}