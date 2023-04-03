package com.igorhenss.connectron.connector.dto

import com.fasterxml.jackson.databind.JsonNode

data class ConnectorResponseDTO(

    val connectionResponse: Any?,
    val translatedJson: JsonNode

)
