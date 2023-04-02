package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.JsonNode

data class ConnectorDTO(

    val mappingId: Long,
    val json: JsonNode,
    val request: ConnectorRequestDTO

) {

    fun getConnectUsingURL() = request.url

    fun getConnectUsingMethod() = request.method

}
