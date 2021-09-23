package br.com.zup.osmarjunior.repository

import br.com.zup.osmarjunior.model.Carro
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CarroRepository : JpaRepository<Carro, Long> {

    fun existsByPlaca(placa: String): Boolean

}
