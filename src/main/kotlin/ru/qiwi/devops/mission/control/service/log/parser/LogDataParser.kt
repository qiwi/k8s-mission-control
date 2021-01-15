package ru.qiwi.devops.mission.control.service.log.parser

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import java.lang.Exception

class LogDataParser {

    fun parse(input: String): Map<String, String> {
        val jsonTree: JsonNode
        try {
            jsonTree = ObjectMapper().readTree(input)
        } catch (ex: Exception) {
            return emptyMap()
        }
        val resultMap = HashMap<String, String>()
        for (node in jsonTree) {
            addKeys("", jsonTree, resultMap)
        }
        return resultMap
    }

    private fun addKeys(currentPath: String, jsonNode: JsonNode, map: MutableMap<String, String>) {
        when {
            jsonNode.isObject -> {
                val objectNode = jsonNode as ObjectNode
                val iter = objectNode.fields()
                val pathPrefix = if (currentPath.isEmpty()) "" else "$currentPath."

                while (iter.hasNext()) {
                    val entry = iter.next()
                    addKeys(pathPrefix + entry.key, entry.value, map)
                }
            }
            jsonNode.isArray -> {
                val arrayNode = jsonNode as ArrayNode
                for (i in 0..arrayNode.size()) {
                    addKeys("$currentPath[$i]", arrayNode.get(i), map)
                }
            }
            jsonNode.isValueNode -> {
                val valueNode = jsonNode as ValueNode
                map[currentPath] = valueNode.asText()
            }
        }
    }
}