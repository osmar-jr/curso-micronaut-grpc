package br.com.zup.osmarjunior.endpoints

import br.com.zup.osmarjunior.CarroRequest
import br.com.zup.osmarjunior.CarroResponse
import br.com.zup.osmarjunior.CarrosGrpcServiceGrpc
import br.com.zup.osmarjunior.model.Carro
import br.com.zup.osmarjunior.repository.CarroRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.validation.ConstraintViolationException

@Singleton
class CarrosEndpoint(@Inject val carroRepository: CarroRepository) : CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(CarrosEndpoint::class.java.simpleName)

    override fun cadastrar(request: CarroRequest?, responseObserver: StreamObserver<CarroResponse>?) {

        val placa = request?.placa
        val modelo = request?.modelo

        if (placa.isNullOrBlank()) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Placa deve ser preenchido.")
                    .asRuntimeException()
            )
            return
        }

        if (placa.matches("[A-Z]{3}[0-9][0-9A-Z][0-9]{2}".toRegex())) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Placa inválida.")
                    .augmentDescription("Formato esperado: AAA-0A00")
                    .asRuntimeException()
            )
            return
        }

        if (modelo.isNullOrBlank()) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Modelo deve ser preenchido.")
                    .asRuntimeException()
            )
            return
        }

        if (carroRepository.existsByPlaca(placa)) {
            responseObserver?.onError(
                Status.ALREADY_EXISTS
                    .withDescription("Placa já existe.")
                    .asRuntimeException()
            )
            return
        }

        val carro = Carro(placa, modelo)

        try {
            carroRepository.save(carro)
            logger.info("Novo carro salvo em banco de dados para a solicitação: $request")

        } catch (e: ConstraintViolationException) {
            logger.error("Erro ao tentar salvar novo carro em banco de dados: ${e.message}")
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Propriedades de carro inválidos.")
                    .withCause(e)
                    .asRuntimeException()
            )
            return
        } catch (e: Exception) {
            logger.error("Erro desconhecido ao tentar salvar novo carro em banco de dados: ${e.message}")
            responseObserver?.onError(
                Status.UNKNOWN
                    .withDescription("Erro inesperado.")
                    .withCause(e)
                    .asRuntimeException()
            )
            return
        }

        val response = CarroResponse
            .newBuilder()
            .setId(carro.id!!)
            .build()

        logger.info("Novo Carro gerado para requisição: $request")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}