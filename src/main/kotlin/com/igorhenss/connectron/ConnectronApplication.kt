package com.igorhenss.connectron

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConnectronApplication

fun main(args: Array<String>) {
	runApplication<ConnectronApplication>(*args)
}
