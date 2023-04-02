package com.igorhenss.connectron.exception

data class NotFoundException(override val message: String): RuntimeException(message)
