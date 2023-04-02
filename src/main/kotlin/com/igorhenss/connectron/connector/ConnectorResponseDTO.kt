package com.igorhenss.connectron.connector

import com.fasterxml.jackson.databind.JsonNode

data class ConnectorResponseDTO(

    val connectionResponse: Any?,
    val translatedJson: JsonNode

)
