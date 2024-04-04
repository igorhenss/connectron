package com.igorhenss.connectron.connector

import com.igorhenss.connectron.connector.dto.ConnectorRequestDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/process")
class ConnectorController(private val service: ConnectorService) {

    @PostMapping
    fun connect(@RequestBody body: ConnectorRequestDTO) = service.connect(body)

}