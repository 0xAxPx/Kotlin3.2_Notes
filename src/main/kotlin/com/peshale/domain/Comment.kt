package com.peshale.domain

import java.time.LocalDate

data class Comment(
    val noteId: Int,
    val ownerId: Int,
    val replyTo: Int,
    val message: String,
    val dateCreated: LocalDate,
    val isDeleted: Boolean
) {
}