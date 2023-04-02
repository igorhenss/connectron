package com.igorhenss.connectron.mapping

import com.fasterxml.jackson.core.type.TypeReference
import com.igorhenss.connectron.core.mapper
import com.igorhenss.connectron.user.User
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(name = "mapping", schema = "connectron")
class Mapping(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(targetEntity = User::class)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Type(JsonType::class)
    @Column(length = 5120, columnDefinition = "json", nullable = false)
    val json: String

) {
    fun getJsonAsMap(): Map<String, String> {
        val jsonAsNode = mapper().readTree(json)
        return mapper().convertValue(jsonAsNode, object: TypeReference<Map<String, String>>(){})
    }
}
