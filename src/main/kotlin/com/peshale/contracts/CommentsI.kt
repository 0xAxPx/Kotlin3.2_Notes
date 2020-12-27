package com.peshale.contracts

import com.peshale.domain.Comment
import com.peshale.domain.Sort

interface CommentsI {

    fun createComment(noteId: Int, ownerId: Int, replyTo: Int, message: String): Int

    fun deleteComment(commentId: Int, ownerId: Int):  Boolean

    fun editComment(commentId: Int, ownerId: Int, message: String): Boolean

    fun getComments(noteId: Int, ownerId: Int, sort: Sort, offset: Int, count: Int = 20): ArrayList<Comment>

    fun restoreComment(commentId: Int, ownerId: Int): Boolean

}