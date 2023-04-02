package com.igorhenss.connectron.user

import jakarta.persistence.*

@Entity
@Table(name = "user", schema = "connectron")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(length = 60, nullable = false, unique = true)
    val username: String,

    @Column(length = 80, nullable = false, unique = true)
    val email: String

)
