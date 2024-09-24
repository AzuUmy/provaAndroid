package com.example.avaliacao2.classes

open class Produto(
    var nome: String,
    var categoria: String,
    var preco: Double,
    var quantidade: Int
) {
    companion object {
        const val DEFAULT_CATEGORIA = "Nao especificado"


        fun createDefault(): Produto {
            return Produto("Produto Padrão", DEFAULT_CATEGORIA, 0.0, 0)
        }
    }
}