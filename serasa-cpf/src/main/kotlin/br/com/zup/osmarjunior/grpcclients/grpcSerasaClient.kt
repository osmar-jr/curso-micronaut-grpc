package br.com.zup.osmarjunior.grpcclients

import br.com.zup.osmarjunior.SerasaGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class grpcSerasaClient {

    @Singleton
    fun serasaClientStub(@GrpcChannel("serasa") channel: ManagedChannel): SerasaGrpcServiceGrpc.SerasaGrpcServiceBlockingStub{
        return SerasaGrpcServiceGrpc.newBlockingStub(channel)
    }
}