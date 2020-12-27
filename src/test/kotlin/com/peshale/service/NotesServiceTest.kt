package com.peshale.service

import com.peshale.domain.Sort
import com.peshale.exceptions.NoteNotFoundException
import com.peshale.util.Utilities
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NotesServiceTest {

    @BeforeEach
    fun cleanUp() {
        NotesService.notesRepository.clear()
        NotesService.commentRepository.clear()
    }

    @Test
    fun `test adding a new note`() {
        val notesService = NotesService()
        val noteId = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        assertTrue(1 == NotesService.notesRepository.size)
        assertTrue(noteId == NotesService.notesRepository[noteId]!!.noteId)
        assertTrue("Fairy tale about Frodo Beggins".equals(NotesService.notesRepository[noteId]?.title))
        assertTrue("Once upon time in...".equals(NotesService.notesRepository[noteId]?.text))
        assertFalse(NotesService.notesRepository[noteId]?.privacyComment.isNullOrEmpty())
        assertFalse(NotesService.notesRepository[noteId]?.ownerId == 0)
        assertFalse(NotesService.notesRepository[noteId]?.privacyView.isNullOrEmpty())
        assertTrue(NotesService.notesRepository[noteId]?.isDeleted == false)
    }

    @Test
    fun `test deleting a new note`() {
        val notesService = NotesService()
        val noteId = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        assertTrue(1 == NotesService.notesRepository.size)
        assertTrue(noteId == NotesService.notesRepository[noteId]!!.noteId)

        val result = notesService.delete(noteId)
        assertTrue(result)
        assertTrue(1 == NotesService.notesRepository.size)
        assertTrue(NotesService.notesRepository[noteId]?.isDeleted == true)
    }

    @Test
    fun `test deleting not existing note`() {
        val notesService = NotesService()
        val result = notesService.delete(33333)
        assertFalse(result)
    }

    @Test
    fun `test update existing note`() {
        val notesService = NotesService()
        val noteId = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        assertTrue(1 == NotesService.notesRepository.size)
        assertTrue(noteId == NotesService.notesRepository[noteId]!!.noteId)

        val result = notesService.edit(
            noteId = noteId,
            title = "Title Update",
            text = "Text Update",
            privacyView = "Privacy View 1",
            privacyComment = "Privacy Comment 1"
        )
        assertTrue(result)
        assertTrue(1 == NotesService.notesRepository.size)
        assertTrue(noteId == NotesService.notesRepository[noteId]!!.noteId)
        assertTrue("Title Update".equals(NotesService.notesRepository[noteId]?.title))
        assertTrue("Text Update".equals(NotesService.notesRepository[noteId]?.text))
        assertTrue(NotesService.notesRepository[noteId]?.isDeleted == false)
    }

    @Test
    fun `test update non existing note`() {
        val notesService = NotesService()
        val exception = Assertions.assertThrows(NoteNotFoundException::class.java) {
            notesService.edit(
                noteId = 1111,
                title = "Title Update",
                text = "Text Update",
                privacyView = "Privacy View 1",
                privacyComment = "Privacy Comment 1"
            )
        }
        assertTrue("No note found for 1111!".equals(exception.message))
    }

    @Test
    fun `test getting all notes of the default user`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        println(noteId_1)

        val noteId_2 = notesService.add(
            title = "Fairy tale about Bilbo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        println(noteId_2)

        val noteId_3 = notesService.add(
            title = "DUMM",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        println(noteId_3)

        val ids = ArrayList<Int>()
        ids.add(noteId_1)
        ids.add(noteId_2)
        ids.add(noteId_3)
        val userNotes = notesService.get(
            noteIds = ids,
            userId = NotesService.getDefaultOwnerId(),
            offset = 1,
            sort = Sort.ASCENDING
        )
        assertTrue(3 == userNotes.size)

        println(userNotes.toString())
    }

    @Test
    fun `test return note by id`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val note = notesService.getById(noteId_1, NotesService.getDefaultOwnerId())
        assertTrue(noteId_1 == note.noteId)
    }

    @Test
    fun `test does not return note by fake id`() {
        val notesService = NotesService()
        val note = Assertions.assertThrows(NoteNotFoundException::class.java) {
            notesService.getById(1111, NotesService.getDefaultOwnerId())
        }
        assertTrue("Note not found for 1111".equals(note.message))
    }


    @Test
    fun `test add new comment`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val commentId = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message"
        )
        assertTrue(1 == NotesService.commentRepository.size)
        assertTrue(noteId_1 == NotesService.commentRepository[commentId]!!.noteId)
        assertFalse(NotesService.commentRepository[commentId]!!.isDeleted)
    }

    @Test
    fun `test add new comment to non existing note`() {
        val notesService = NotesService()
        val exception = Assertions.assertThrows(NoteNotFoundException::class.java) {
            notesService.createComment(
                noteId = 11111,
                ownerId = NotesService.getDefaultOwnerId(),
                replyTo = Utilities.randomInt(),
                message = "Test message"
            )
        }
        assertTrue(0 == NotesService.commentRepository.size)
        assertTrue("You're trying to add comment to not existing noteId 11111".equals(exception.message))
    }

    @Test
    fun `test delete comment from note`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val commentId = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message"
        )
        val result = notesService.deleteComment(commentId, NotesService.getDefaultOwnerId())
        assertTrue(result)
        assertTrue(NotesService.commentRepository[commentId]!!.isDeleted)

    }

    @Test
    fun editComment() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val commentId = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message"
        )
        val result =  notesService.editComment(commentId = commentId, NotesService.getDefaultOwnerId(), "Test Comment")

        assertTrue(result)
        assertTrue(noteId_1 == NotesService.commentRepository.get(commentId)!!.noteId)
        assertTrue("Test Comment".equals(NotesService.commentRepository[commentId]!!.message))
    }

    @Test
    fun `test get user comments in ascending way`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val commentId_1 = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message1"
        )
        val commentId_2 = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message2"
        )
        val commentId_3 = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message3"
        )

        val comments = notesService.getComments(noteId_1, NotesService.getDefaultOwnerId(), sort = Sort.ASCENDING, 0, 2)
        assertTrue(2 == comments.size)
    }

    @Test
    fun `test restore comment`() {
        val notesService = NotesService()
        val noteId_1 = notesService.add(
            title = "Fairy tale about Frodo Beggins",
            text = "Once upon time in...",
            privacyView = "Test",
            privacyComment = "Comment left UFO"
        )
        val commentId = notesService.createComment(
            noteId = noteId_1,
            ownerId = NotesService.getDefaultOwnerId(),
            replyTo = Utilities.randomInt(),
            message = "Test message"
        )
        assertTrue(1 == NotesService.commentRepository.size)
        val result = notesService.deleteComment(commentId, NotesService.getDefaultOwnerId())
        assertTrue(result)
        assertTrue(NotesService.commentRepository[commentId]!!.isDeleted)
        val restore = notesService.restoreComment(commentId, NotesService.getDefaultOwnerId())
        assertTrue(restore)
        assertTrue(noteId_1 == NotesService.commentRepository[commentId]!!.noteId)
        assertFalse(NotesService.commentRepository[commentId]!!.isDeleted)
    }
}