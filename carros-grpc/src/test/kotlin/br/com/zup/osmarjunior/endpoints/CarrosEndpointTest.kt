package br.com.zup.osmarjunior.endpoints

import br.com.zup.osmarjunior.CarroRequest
import br.com.zup.osmarjunior.CarrosGrpcServiceGrpc
import br.com.zup.osmarjunior.model.Carro
import br.com.zup.osmarjunior.repository.CarroRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(
    val carroRepository: CarroRepository,
    val grpcClient: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        //cenario
        carroRepository.deleteAll()
    }

    @Test
    fun `deve inserir um novo carro`() {

        //acao
        val response = grpcClient.cadastrar(
            CarroRequest
                .newBuilder()
                .setModelo("PI Breeze")
                .setPlaca("LWE-2141")
                .build()
        )

        //validacao
        with(response) {
            assertNotNull(id)
            assertTrue(carroRepository.existsById(id)) //efeito colateral
        }

    }

    @Test
    fun `nao deve inserir um carro quando a placa do carro ja existe no banco de dados`() {

        val savedCarro = carroRepository.save(Carro(placa = "LVI-8957", modelo = "Opala"))

        //acao
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                CarroRequest
                    .newBuilder()
                    .setModelo(savedCarro.modelo)
                    .setPlaca(savedCarro.placa)
                    .build()
            )
        }

        //validacao
        with(exception) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Placa já existe.", status.description)
        }

    }

    @Test
    fun `nao deve inserir um carro quando a placa for nula ou vazia`() {

        //acao
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                CarroRequest
                    .newBuilder()
                    .setModelo("Ferrari")
                    .setPlaca("")
                    .build()
            )
        }

        //validacao
        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Placa deve ser preenchido.", status.description)
        }

    }

    @Test
    fun `nao deve inserir um carro quando a placa for invalida`() {

        //acao
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                CarroRequest
                    .newBuilder()
                    .setModelo("Ferrari")
                    .setPlaca("KEH0024")
                    .build()
            )
        }

        //validacao
        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Placa inválida.\nFormato esperado: AAA-0A00", status.description)
        }

    }

    @Test
    fun `nao deve inserir um carro quando a modelo for nulo ou vazia`() {

        //acao
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                CarroRequest
                    .newBuilder()
                    .setModelo("")
                    .setPlaca("LWK-8524")
                    .build()
            )
        }

        //validacao
        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Modelo deve ser preenchido.", status.description)
        }

    }

    @Factory
    class Clients {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}