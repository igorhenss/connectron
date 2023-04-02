package com.igorhenss.connectron.connector

import org.springframework.http.HttpMethod

data class ConnectorRequestDetailsDTO(

    val url: String,
    val method: HttpMethod

)
