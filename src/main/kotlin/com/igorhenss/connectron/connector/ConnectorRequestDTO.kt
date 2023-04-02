package com.igorhenss.connectron.connector

import org.springframework.http.HttpMethod

data class ConnectorRequestDTO(

    val url: String,
    val method: HttpMethod

)
