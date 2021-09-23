package br.com.zup.osmarjunior

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class FreteGrpcServer : FreteServiceGrpc.FreteServiceImplBase() {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    override fun calcularFrete(request: FreteRequest?, responseObserver: StreamObserver<FreteResponse>?) {

        val cep: String? = request?.cep
        //simulando erro de validacao

        if (cep.isNullOrBlank()) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Cep deve ser preenchido.")
                    .asRuntimeException()
            )
        }

        //Retornando erro com descricao extra.
        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Cep inválido.")
                    .augmentDescription("Formato esperado: 99999-999")
                    .asRuntimeException()
            )
        }

        //simulando erro de seguranca
        if (cep.endsWith("333")) {

            val statusProto = com.google.rpc.Status
                .newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuario não pode acessar esse recurso.")
                .addDetails(
                    Any.pack(
                        ErrorDetails
                            .newBuilder()
                            .setCode(401)
                            .setMessage("Token expirado.")
                            .build()
                    )
                )
                .build()
            val exception = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(exception)
        }


        logger.info("Nova calculo de frete solicitado: $request")

        val response = FreteResponse
            .newBuilder()
            .setCep(request.cep)
            .setValor(Random.nextDouble(0.00, 140.00))
            .build()

        logger.info("Frete calculado: $response")

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}