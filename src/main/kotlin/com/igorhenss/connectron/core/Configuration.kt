package com.igorhenss.connectron.core

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Configuration {

    @Bean
    fun restTemplate() = RestTemplateBuilder().build()

}