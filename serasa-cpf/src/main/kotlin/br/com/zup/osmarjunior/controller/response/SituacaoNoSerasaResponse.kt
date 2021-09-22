package br.com.zup.osmarjunior.controller.response

import br.com.zup.osmarjunior.enums.SituacaoCpf

data class SituacaoNoSerasaResponse(val cpf: String, val situacaoCpf: SituacaoCpf)