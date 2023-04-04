package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.igorhenss.connectron.connector.dto.ConnectorRequestDTO
import com.igorhenss.connectron.connector.dto.ConnectorResponseDTO
import com.igorhenss.connectron.exception.ConnectException
import com.igorhenss.connectron.exception.NotFoundException
import com.igorhenss.connectron.mapping.MappingService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ConnectorService(
    private val restTemplate: RestTemplate,
    private val mappingService: MappingService
) {

    fun connect(dto: ConnectorRequestDTO): Any? {
        val mappings = getMappings(dto.mappingId)
        val resultingJson = translateJson(mappings, dto)
        val connectionResponse = makeConnectionRequest(dto, resultingJson)
        return buildResponse(resultingJson, connectionResponse)
    }

    private fun getMappings(mappingId: Long) = mappingService.findById(mappingId).getJsonAsMap()

    private fun translateJson(mappings: Map<String, String>, dto: ConnectorRequestDTO): ObjectNode {
        val resultingJson = JsonNodeFactory.instance.objectNode()
        mappings.map { (fromKey, toKey) ->
            val readValue = dto.json.readKeyRecursively(fromKey)
            resultingJson.putGroupingByOuterKey(toKey, readValue)
        }
        return resultingJson
    }

    private fun ObjectNode.putGroupingByOuterKey(completeKey: String, readValue: JsonNode) {
        val multiDepthKeys = completeKey.split(" > ")
        if (multiDepthKeys.size <= 1) {
            this.putIfAbsent(completeKey, readValue)
            return
        }

        multiDepthKeys.forEachIndexed { index, key -> run {
            if (index == multiDepthKeys.lastIndex) {
                getObjectNodeForPath(multiDepthKeys, index).putIfAbsent(key, readValue)
            } else if (index > 0) {
                if (nodeDoesNotExistForPath(multiDepthKeys, index)) getObjectNodeForPath(multiDepthKeys, index).putObject(key)
            } else {
                if (this.get(key) == null) this.putObject(key)
            }
        } }
    }

    private fun ObjectNode.getObjectNodeForPath(fullPath: List<String>, index: Int): ObjectNode {
        return findUntil(fullPath, index) as ObjectNode
    }

    private fun ObjectNode.nodeDoesNotExistForPath(fullPath: List<String>, index: Int): Boolean {
        return findUntil(fullPath, index)?.get(fullPath[index]) == null
    }

    private fun ObjectNode.findUntil(keys: List<String>, index: Int): JsonNode? {
        var lastFound = this.get(keys[0])
        keys.filterIndexed { i, _ -> i in 1 until index }.forEach { key -> lastFound = lastFound.get(key) }
        return lastFound
    }

    private fun JsonNode.readKeyRecursively(completeKey: String): JsonNode {
        val multiDepthKeys = completeKey.split(" > ")
        if (multiDepthKeys.size <= 1) {
            return readKey(completeKey)
        }

        var lastChild = this
        multiDepthKeys.forEach { lastChild = lastChild.readKeyRecursively(it) }
        return lastChild
    }

    private fun JsonNode.readKey(key: String) = this.get(key) ?: throw NotFoundException("Key $key not found.")

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
