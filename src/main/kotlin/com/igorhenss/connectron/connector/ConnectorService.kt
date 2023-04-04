package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.node.ObjectNode
import com.igorhenss.connectron.connector.dto.ConnectorRequestDTO
import com.igorhenss.connectron.connector.dto.ConnectorResponseDTO
import com.igorhenss.connectron.exception.ConnectException
import com.igorhenss.connectron.mapping.MappingService
import com.igorhenss.connectron.translator.TranslatorService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ConnectorService(
    private val restTemplate: RestTemplate,
    private val mappingService: MappingService,
    private val translator: TranslatorService,
) {

    fun connect(dto: ConnectorRequestDTO): Any? {
        val mappings = getMappings(dto.mappingId)
        val resultingJson = translator.translateJsonUsingMappings(mappings, dto)
        val connectionResponse = makeConnectionRequest(dto, resultingJson)
        return buildResponse(resultingJson, connectionResponse)
    }

    private fun getMappings(mappingId: Long) = mappingService.findById(mappingId).getJsonAsMap()

    private fun makeConnectionRequest(
        dto: ConnectorRequestDTO,
        resultingJson: ObjectNode
    ): Any? {
        val requestUrl = dto.getConnectUsingURL()
        val requestMethod = dto.getConnectUsingMethod()
        val requestEntity = HttpEntity(resultingJson)
        return requestAndProcessResponse(requestUrl, requestMethod, requestEntity)
    }

    private fun requestAndProcessResponse(
        requestUrl: String,
        requestMethod: HttpMethod,
        requestEntity: HttpEntity<ObjectNode>
    ): Any? {
        val response = restTemplate.exchange(requestUrl, requestMethod, requestEntity, Any::class.java)
        if (response.statusCode.is2xxSuccessful) {
            return response.body
        }
        throw ConnectException("Could not complete request [$requestMethod] \"$requestUrl\". Reason: ${response.body}")
    }

    private fun buildResponse(resultingJson: ObjectNode, connectionResponse: Any?) = ConnectorResponseDTO(
        connectionResponse = connectionResponse,
        translatedJson = resultingJson
    )

}
