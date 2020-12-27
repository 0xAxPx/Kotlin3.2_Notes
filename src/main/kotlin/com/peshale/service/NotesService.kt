package com.peshale.service

import com.peshale.contracts.CommentsI
import com.peshale.contracts.NotesI
import com.peshale.util.Utilities
import com.peshale.domain.Comment
import com.peshale.domain.Note
import com.peshale.domain.Sort

import com.peshale.exceptions.CommentNotFoundException
import com.peshale.exceptions.NoteNotFoundException
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class NotesService() : NotesI, CommentsI {

    companion object {
        //State of Notes Ids
        val idsUnique = ArrayList<Int>()

        //NoteId, Note
        val notesRepository = HashMap<Int, Note>()

        //UUID, Comment
        val commentRepository = HashMap<Int, Comment>()

        //Default ownerId
        private var defaultOwnerId = Utilities.randomInt()

        fun getDefaultOwnerId() = defaultOwnerId

        //When we need to change default owner id
        fun changeOwnerId(ownerId: Int) {
            defaultOwnerId = ownerId
        }

        fun checkUniqueId(id: Int): Int {
            return if (idsUnique.contains(id)) {
                checkUniqueId(Utilities.randomInt())
            } else {
                id
            }
        }
    }

    /*
    Notes CRUD implementation
     */
    override fun add(title: String, text: String, privacyView: String, privacyComment: String): Int {
        val noteId = checkUniqueId(Utilities.randomInt())
        val note = Note(noteId, defaultOwnerId, title, text, dateCreated = generateRandomDate())
        notesRepository[noteId] = note
        return noteId
    }

    override fun delete(noteId: Int): Boolean {
        var result = false
        val note = notesRepository[noteId]
        if (note?.isDeleted == false) {
            val newNote = note.copy(isDeleted = true)
            notesRepository[noteId] = newNote
            result = true
        } else {
            println("Note $noteId has been already deleted")
        }
        return result
    }

    override fun edit(noteId: Int, title: String, text: String, privacyView: String, privacyComment: String): Boolean {
        val result: Boolean
        val note = notesRepository[noteId]
        if (note != null) {
            val newNote =
                note.copy(title = title, text = text, privacyView = privacyView, privacyComment = privacyComment)
            notesRepository[noteId] = newNote
            result = true
        } else {
            throw NoteNotFoundException("No note found for $noteId!")
        }
        return result
    }

    override fun get(noteIds: ArrayList<Int>, userId: Int, offset: Int, count: Int, sort: Sort): ArrayList<Note> {
        val userNotes = ArrayList<Note>()

        for (i in noteIds) {
            if (notesRepository[i] != null) {
                userNotes.add(notesRepository[i]!!)
            } else {
                throw NoteNotFoundException("No notes found for $userId")
            }
        }

        //descending as default
        if (sort.sorting == 1) {
            println("Sorting descending....")
            userNotes.sortedByDescending { it.dateCreated }
        } else {
            println("Sorting ascending....")
            userNotes.sortBy { it.dateCreated }
        }
        return userNotes
    }

    override fun getById(noteId: Int, ownerId: Int, needWiki: Int): Note {
        val note = notesRepository[noteId]
        if (defaultOwnerId == ownerId && note != null) {
            return note
        } else {
            throw  NoteNotFoundException("Note not found for $noteId")
        }
    }


    /*
    Comments CRUD implementation
     */
    override fun createComment(noteId: Int, ownerId: Int, replyTo: Int, message: String): Int {
        val commentId: Int
        if (notesRepository[noteId] != null) {
            val comment = Comment(noteId, ownerId, replyTo, message, generateRandomDate(), false)
            commentId = Utilities.randomInt()
            commentRepository[commentId] = comment
        } else {
            throw NoteNotFoundException("You're trying to add comment to not existing noteId $noteId")
        }
        return commentId
    }

    override fun deleteComment(commentId: Int, ownerId: Int): Boolean {
        return deleteRestoreComment(commentId, ownerId, true)
    }

    override fun editComment(commentId: Int, ownerId: Int, message: String): Boolean {
        val comment = commentRepository[commentId]
        var result = false
        if (comment != null) {
            if (defaultOwnerId == ownerId) {
                val newComment = comment.copy(message = message)
                commentRepository[commentId] = newComment
                result = true
            }
        } else {
            throw CommentNotFoundException("No comment found for $commentId!")
        }
        return result
    }

    override fun getComments(noteId: Int, ownerId: Int, sort: Sort, offset: Int, count: Int): ArrayList<Comment> {
        var userComments = ArrayList<Comment>()

        for ((_, comment) in commentRepository) {
            if (comment.noteId == noteId && comment.ownerId == ownerId) {
                userComments.add(comment)
            }
        }

        //descending as default
        if (sort.sorting == 1) {
            println("Sorting descending....")
            userComments.sortedByDescending { it.dateCreated }
        } else {
            println("Sorting ascending....")
            userComments.sortBy { it.dateCreated }
        }

        //Count
        if (count != userComments.size) {
            val newList = ArrayList<Comment>()
            for (i in 1..count) {
                newList.add(userComments[i])
            }
            userComments = newList
        }

        return userComments
    }

    override fun restoreComment(commentId: Int, ownerId: Int): Boolean {
        return deleteRestoreComment(commentId, ownerId, false)
    }

    private fun deleteRestoreComment(commentId: Int, ownerId: Int, isDeleted: Boolean): Boolean {
        val comment = commentRepository[commentId]
        var result = false
        if (comment != null) {
            if (defaultOwnerId == ownerId) {
                val newComment = comment.copy(isDeleted = isDeleted)
                commentRepository[commentId] = newComment
                result = true
            }
        } else {
            throw CommentNotFoundException("No comment found for $commentId!")
        }
        return result
    }

    //for testing purpose only
    private fun generateRandomDate(): LocalDate {
        val month = Random.nextInt(1, 12)
        //excluding leap year case
        val day = Random.nextInt(1, 28)
        return LocalDate.of(2020, month, day)
    }
}