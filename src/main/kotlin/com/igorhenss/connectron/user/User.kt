package com.igorhenss.connectron.user

import jakarta.persistence.*

@Table
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(length = 60, nullable = false)
    val username: String,

    @Column(length = 80, nullable = false)
    val email: String

)
