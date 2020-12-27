package com.peshale.contracts

import com.peshale.domain.Note
import com.peshale.domain.Sort

interface NotesI {

    fun add(title: String, text: String, privacyView: String, privacyComment: String): Int

    fun delete(noteId: Int): Boolean

    fun edit(noteId: Int, title: String, text: String, privacyView: String, privacyComment: String): Boolean

    fun get(noteIds: ArrayList<Int>, userId: Int, offset: Int = 0, count: Int = 20, sort: Sort): ArrayList<Note>

    fun getById(noteId: Int, ownerId: Int, needWiki: Int  = 0): Note

}

