package br.com.zup.osmarjunior.grpcclients

import br.com.zup.osmarjunior.FreteServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun fretesClientStub(@GrpcChannel("fretes") channel: ManagedChannel): FreteServiceGrpc.FreteServiceBlockingStub? {
        return FreteServiceGrpc.newBlockingStub(channel)
    }
}