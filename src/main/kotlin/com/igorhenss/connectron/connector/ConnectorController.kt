package com.igorhenss.connectron.connector

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController(value = "/connect")
class ConnectorController(private val service: ConnectorService) {

    @PostMapping
    fun connect(@RequestBody body: ConnectorDTO) = service.connect(body)

}