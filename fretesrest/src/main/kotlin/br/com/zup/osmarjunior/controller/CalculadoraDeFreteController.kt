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
            val description = exception.status.description

            if(statusCode.equals(Status.Code.INVALID_ARGUMENT)){
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode.equals(Status.Code.PERMISSION_DENIED)){
                val statusProto = StatusProto.fromThrowable(exception)

                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val anyErroDetails = statusProto.detailsList.get(0)
                val errorDetails = anyErroDetails.unpack(ErrorDetails::class.java)

                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")

            }

            throw HttpStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.message
            )
        }
    }
}