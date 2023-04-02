package com.igorhenss.connectron.mapping

import com.igorhenss.connectron.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MappingService(private val mappingRepository: MappingRepository) {

    fun findById(id: Long) = mappingRepository.findByIdOrNull(id)
        ?: throw NotFoundException("No mapping for ID [$id] could be found.")

}
