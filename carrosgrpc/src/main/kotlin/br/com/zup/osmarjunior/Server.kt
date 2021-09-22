package br.com.zup.osmarjunior

import com.google.protobuf.Timestamp
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun main() {

    val server = ServerBuilder
        .forPort(50051)
        .addService(CarrosEndpoint())
        .build()

    server.start()
    server.awaitTermination()
}

class CarrosEndpoint : CarrosServiceGrpc.CarrosServiceImplBase() {

    private val logger = LoggerFactory.getLogger(CarrosEndpoint::class.java.simpleName)

    override fun cadastrar(request: CarroRequest?, responseObserver: StreamObserver<CarroResponse>?) {

        val instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        val criadoEm = Timestamp
            .newBuilder()
            .setNanos(instant.nano)
            .setSeconds(instant.epochSecond)
            .build()

        val response = CarroResponse
            .newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCriadoEm(criadoEm)
            .build()

        logger.info("Novo Carro gerado para requisição: $request")
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}
