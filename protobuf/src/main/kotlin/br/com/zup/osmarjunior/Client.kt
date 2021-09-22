package br.com.zup.osmarjunior

import io.grpc.ManagedChannelBuilder

fun main() {

    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val client = FuncionarioServiceGrpc.newBlockingStub(channel)

    val request = FuncionarioRequest
        .newBuilder()
        .setNome("Osmar Junior")
        .setIdade(26)
        .setCargo(Cargo.GERENTE)
        .setSalario(12005.50)
        .addEnderecos(Endereco
            .newBuilder()
            .setCep("00000-000")
            .setComplemento("Zona de Perigo")
            .setLogradouro("Rua da Jornada")
            .build())
        .setAtivo(true)
        .build()
    val response = client.cadastrar(request)

    println(response)

}