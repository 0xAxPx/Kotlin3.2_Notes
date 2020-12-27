package com.peshale.exceptions

import java.io.IOException

class CommentNotFoundException(message: String?) : IOException(message) {
}