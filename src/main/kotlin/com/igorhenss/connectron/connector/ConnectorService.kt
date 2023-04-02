package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.igorhenss.connectron.exception.ConnectException
import com.igorhenss.connectron.exception.NotFoundException
import com.igorhenss.connectron.mapping.MappingService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ConnectorService(
    private val restTemplate: RestTemplate,
    private val mappingService: MappingService
) {

    fun connect(dto: ConnectorDTO): Any? {
        val mappings = getMappings(dto.mappingId)
        val resultingJson = translateJson(mappings, dto)
        return makeConnectionRequest(dto, resultingJson)
    }

    private fun makeConnectionRequest(
        dto: ConnectorDTO,
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

    private fun translateJson(mappings: Map<String, String>, dto: ConnectorDTO): ObjectNode {
        val resultingJson = JsonNodeFactory.instance.objectNode()
        mappings.map { (fromKey, toKey) ->
            val readValue = dto.json.readKey(fromKey)
            resultingJson.putIfAbsent(toKey, readValue)
        }
        return resultingJson
    }

    private fun getMappings(mappingId: Long) = mappingService.findById(mappingId).getJsonAsMap()


    private fun JsonNode.readKey(key: String) = this.get(key) ?: throw NotFoundException("Key $key not found.")

}
