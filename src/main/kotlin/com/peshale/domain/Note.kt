package com.peshale.domain

import java.time.LocalDate
import java.util.*

data class Note(
    val noteId: Int,
    val ownerId: Int,
    val title: String,
    val text: String,
    val privacyView: String = "Test",
    val privacyComment: String = "Comment left by UFO",
    val isDeleted: Boolean = false,
    val dateCreated: LocalDate
)