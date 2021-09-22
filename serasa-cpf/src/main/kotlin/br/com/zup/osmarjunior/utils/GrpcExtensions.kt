package br.com.zup.osmarjunior.utils

import br.com.zup.osmarjunior.Situacao
import br.com.zup.osmarjunior.SituacaoDoClienteResponse
import br.com.zup.osmarjunior.enums.SituacaoCpf


fun SituacaoDoClienteResponse.toModel(): SituacaoCpf {
    return when(situacao){
        Situacao.REGULAR -> SituacaoCpf.REGULARIZADA
        Situacao.IRREGULAR -> SituacaoCpf.NAO_REGULARIZADA
        else -> SituacaoCpf.SEM_INFORMACOES
    }
}