package com.group.libraryapp.domain.book

import java.lang.IllegalArgumentException
import javax.persistence.*

@Entity
class Book(
    val name: String,

    @Enumerated(value = EnumType.STRING)
    val type: BookType,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // default paramenter는 맨 마자막으로 내리는 것이 관례
) {
    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
    }

    companion object {
        fun fixture(
            name: String = "책 이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null,
        ): Book {
            return Book(name = name, type = type, id = id)
        }
    }
}