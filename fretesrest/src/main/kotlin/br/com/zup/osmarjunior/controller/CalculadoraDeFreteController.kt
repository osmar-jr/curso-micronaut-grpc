package br.com.zup.osmarjunior.controller

import br.com.zup.osmarjunior.ErrorDetails
import br.com.zup.osmarjunior.FreteRequest
import br.com.zup.osmarjunior.FreteServiceGrpc
import br.com.zup.osmarjunior.controller.response.FreteCalculadoResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class CalculadoraDeFreteController(@Inject val grpcClient: FreteServiceGrpc.FreteServiceBlockingStub) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    @Get("/api/fretes")
    fun calcula(@QueryValue cep: String): HttpResponse<Any> {

        val request = FreteRequest.newBuilder().setCep(cep).build()

        try {
            val response = grpcClient.calcularFrete(request)

            val freteCalculadoResponse = FreteCalculadoResponse(response.cep, response.valor)
            logger.info("Novo Frete calculado: $freteCalculadoResponse")

            return HttpResponse.ok(freteCalculadoResponse)
        } catch (exception: StatusRuntimeException) {
            val statusCode = exception.status.code
            val description = exception.status.description ?: ""

            val (statusHttp, message) = when (statusCode) {
                Status.INVALID_ARGUMENT.code -> Pair(HttpStatus.BAD_REQUEST, description)
                Status.PERMISSION_DENIED.code -> {
                    val statusProto = StatusProto.fromThrowable(exception)

                    if (statusProto == null) {
                        Pair(HttpStatus.FORBIDDEN, description)
                    }
                    val anyErroDetails = statusProto!!.detailsList.get(0)
                    val errorDetails = anyErroDetails.unpack(ErrorDetails::class.java)

                    Pair(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
                }

                else -> {
                    logger.error("Erro inesperado ao consultar o frete $cep")
                    Pair(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel completar a requisição")
                }
            }

            throw HttpStatusException(statusHttp, message)
        }
    }
}