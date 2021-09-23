package br.com.zup.osmarjunior.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    @field:NotBlank
    @Column(nullable = true)
    val placa: String,

    @field:NotBlank
    @Column(nullable = true)
    val modelo: String
) {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    var id: Long? = null
}
