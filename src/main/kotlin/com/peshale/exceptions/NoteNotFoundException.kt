package com.peshale.exceptions

import java.io.IOException

class NoteNotFoundException(message: String?) : IOException(message) {
}