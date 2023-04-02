package com.igorhenss.connectron.exception

data class ConnectException(override val message: String): RuntimeException(message)
