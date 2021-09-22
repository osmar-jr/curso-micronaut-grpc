package br.com.zup.osmarjunior.controller

import br.com.zup.osmarjunior.SerasaGrpcServiceGrpc
import br.com.zup.osmarjunior.SituacaoDoClienteRequest
import br.com.zup.osmarjunior.controller.response.SituacaoNoSerasaResponse
import br.com.zup.osmarjunior.utils.toModel
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Controller
class SerasaController(@Inject val grpcClient: SerasaGrpcServiceGrpc.SerasaGrpcServiceBlockingStub) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    @Post("/api/serasa/clientes/verificar-situacao")
    fun verificar(@QueryValue @Valid @NotBlank cpf: String): HttpResponse<Any> {
        val request = SituacaoDoClienteRequest.newBuilder().setCpf(cpf).build()

        val response = grpcClient.verificarSituacaoDoCliente(request)

        val situacaoCpf = response.toModel()
        logger.info("Situcao CPF averiguada: $response")

        return HttpResponse.ok(SituacaoNoSerasaResponse(cpf = cpf, situacaoCpf = situacaoCpf))
    }

}