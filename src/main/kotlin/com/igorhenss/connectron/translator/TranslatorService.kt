package com.igorhenss.connectron.translator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.igorhenss.connectron.connector.dto.ConnectorRequestDTO
import com.igorhenss.connectron.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class TranslatorService {

    fun translateJsonUsingMappings(mappings: Map<String, String>, dto: ConnectorRequestDTO): ObjectNode {
        val resultingJson = JsonNodeFactory.instance.objectNode()
        mappings.map { (fromKey, toKey) -> translateOriginalJson(dto.json, fromKey, resultingJson, toKey) }
        return resultingJson
    }

    private fun translateOriginalJson(json: JsonNode, fromKey: String, resultingJson: ObjectNode, toKey: String) {
        val readValue = json.readKeyRecursively(fromKey)
        resultingJson.putValueToKey(toKey, readValue)
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

    private fun ObjectNode.putValueToKey(completeKey: String, readValue: JsonNode) {
        val multiDepthKeys = completeKey.split(" > ")
        if (multiDepthKeys.size <= 1) {
            this.putIfAbsent(completeKey, readValue)
            return
        }

        this.putValueToCompletePath(multiDepthKeys, readValue)
    }

    private fun ObjectNode.putValueToCompletePath(multiDepthKeys: List<String>, readValue: JsonNode) {
        multiDepthKeys.forEachIndexed { index, key ->
            when (index) {
                0 -> createKeyIfNecessary(key)
                multiDepthKeys.lastIndex -> putValueToCurrentKey(multiDepthKeys, index, key, readValue)
                else -> createKeyIfNecessary(multiDepthKeys, index, key)
            }
        }
    }

    private fun ObjectNode.createKeyIfNecessary(key: String) {
        if (this.get(key) == null) {
            this.putObject(key)
        }
    }

    private fun ObjectNode.putValueToCurrentKey(multiDepthKeys: List<String>, index: Int, key: String, readValue: JsonNode) {
        getObjectNodeForPath(multiDepthKeys, index).putIfAbsent(key, readValue)
    }

    private fun ObjectNode.createKeyIfNecessary(multiDepthKeys: List<String>, index: Int, key: String) {
        if (nodeDoesNotExistForPath(multiDepthKeys, index)) {
            getObjectNodeForPath(multiDepthKeys, index).putObject(key)
        }
    }

    private fun ObjectNode.nodeDoesNotExistForPath(fullPath: List<String>, index: Int): Boolean {
        return findForCompletePath(fullPath, index)?.get(fullPath[index]) == null
    }

    private fun ObjectNode.getObjectNodeForPath(fullPath: List<String>, index: Int): ObjectNode {
        return findForCompletePath(fullPath, index) as ObjectNode
    }

    private fun ObjectNode.findForCompletePath(keys: List<String>, index: Int): JsonNode? {
        var lastFound = this.get(keys[0])
        keys.filterIndexed { i, _ -> i in 1 until index }.forEach { key -> lastFound = lastFound.get(key) }
        return lastFound
    }

}