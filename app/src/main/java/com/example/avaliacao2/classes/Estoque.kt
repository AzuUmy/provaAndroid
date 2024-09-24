package com.example.avaliacao2.classes
class Estoque : Produto("", "", 0.0, 0) {


    val productList = mutableListOf<Produto>()

    fun adicionarProduto(produto: Produto) {
        productList.add(produto)
    }

    fun getProducts(): List<Produto> {
        return productList
    }

    companion object {
        fun calcularValorTotalEstoque(produto: Produto): Double {
            return produto.quantidade.toInt() * produto.preco.toDouble()
        }
    }

}

