package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.JsonNode

data class ConnectorDTO(

    val request: ConnectorRequestDTO,
    val mappingId: Long,
    val json: JsonNode

) {

    fun getConnectUsingURL() = request.url

    fun getConnectUsingMethod() = request.method

}
