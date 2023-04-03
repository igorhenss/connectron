package com.igorhenss.connectron.connector.dto

import com.fasterxml.jackson.databind.JsonNode

data class ConnectorRequestDTO(

    val details: ConnectorRequestDetailsDTO,
    val mappingId: Long,
    val json: JsonNode

) {

    fun getConnectUsingURL() = details.url

    fun getConnectUsingMethod() = details.method

}
