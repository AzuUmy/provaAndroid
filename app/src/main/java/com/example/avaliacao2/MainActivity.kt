package com.example.avaliacao2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.avaliacao2.classes.Estoque
import com.example.avaliacao2.classes.Produto
import com.example.avaliacao2.ui.theme.Avaliacao2Theme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Avaliacao2Theme {
                LayoutView()
            }
        }
    }
}

@Composable
fun layoutMain() {

    val navController = rememberNavController();
    val estoque = Estoque()


    NavHost(navController = navController, startDestination = "Lista") {
        composable("lista") { cadastrarProdutos(navController, estoque)}
        composable("lista/{listaJson}") { backStackEntry ->
            val listaJson = backStackEntry.arguments?.getString("listaJson")
            val productListFromJson: List<Produto> = Gson().fromJson(listaJson, Array<Produto>::class.java).toList();
            ListaDeProdutos(navController, productListFromJson, estoque);
        }

        composable ("ProdutoInfo/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto: Produto = Gson().fromJson(produtoJson, Produto::class.java)
            ProdutoInfo(navController, produto)
        }

        composable("estoqueInfo/{estoqueJson}") { backStackEntry ->
            val estoqueJson = backStackEntry.arguments?.getString("estoqueJson")
            val totalValue: Float = Gson().fromJson(estoqueJson, Float::class.java)
            estoqueValues(navController, totalValue);
        }
        composable("estatisticas") { Estatisticas(navController, estoque) }

    }
}



@Composable
fun Estatisticas(navController: NavHostController, estoque: Estoque) {
    val totalValue = estoque.productList.sumOf { Estoque.calcularValorTotalEstoque(it) }
    val totalQuantity = estoque.productList.sumOf { it.quantidade }

    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Estatísticas", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Valor Total do Estoque: R$ ${totalValue}", fontSize = 20.sp)
        Text(text = "Quantidade Total de Produtos: $totalQuantity", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Voltar")
        }
    }
}



@Composable
fun ProdutoInfo(navController: NavHostController, produto: Produto) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Detalhes do Produto", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Nome: ${produto.nome}", fontSize = 20.sp)
        Text(text = "Categoria: ${produto.categoria}", fontSize = 20.sp)
        Text(text = "Preço: R$ ${produto.preco}", fontSize = 20.sp)
        Text(text = "Quantidade: ${produto.quantidade}", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text="Voltar")
        }
    }
}

@Composable
fun ListaDeProdutos(navController: NavHostController, produtos: List<Produto>, estoque: Estoque) {
    var estoqueValues by remember { mutableStateOf(0.0) };
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Lista de produtos", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(produtos) { produto ->
                val valorTotal = Estoque.calcularValorTotalEstoque(produto)
                estoqueValues = valorTotal;

                Row( modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(text = "Nome: ${produto.nome} Quantidade: (${produto.quantidade})", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("ProdutoInfo/$produtoJson")
                    }
                    ) {
                        Text(text = "Info")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {   Text(text = "Valor Total do estoque: R$ ${estoqueValues}", fontSize = 12.sp); }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Voltar")
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                navController.navigate("estatisticas") // Navigate to statistics
            }) {
                Text(text = "Estatísticas")
            }
        }
    }
}


@Composable
fun estoqueValues(navController: NavHostController, totalValue: Float){
    Text("hELLO WORDS")

}

@Composable
fun cadastrarProdutos(navController: NavHostController, estoque: Estoque){
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") };
    var preco by remember { mutableStateOf("") };
    var quantidade by remember { mutableStateOf("") };

    val contex = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Cadastrar Produtos", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Enter Name") }
        )


        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Enter Categoria") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Enter Preco") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Enter Quantidade") }
        )



        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {

            if (nome.isEmpty() || categoria.isEmpty() || preco.isEmpty() || quantidade.isEmpty()) {

                Toast.makeText(
                    contex,
                    "Por favor, preencha todos os campos", Toast.LENGTH_SHORT
                ).show()

            }
            if(quantidade.toInt() < 1){
                Toast.makeText(
                    contex,
                    "campo quantidade possui valores invalidos", Toast.LENGTH_SHORT
                ).show()
            }

            if(preco.toFloat() < 1){
                Toast.makeText(
                    contex,
                    "campo preço possui valores invalidos", Toast.LENGTH_SHORT
                ).show()
            }
            else {

                val newProduto = Produto(
                    nome,
                    categoria,
                    preco.toDoubleOrNull() ?: 0.0,
                    quantidade.toIntOrNull() ?: 0
                )

                estoque.adicionarProduto(newProduto);


                nome = "";
                categoria = "";
                preco = "";
                quantidade = "";
            }

        }) {
            Text(text = "Adicionar a lista")
        }

        Spacer(modifier = Modifier.height(35.dp))

        if (estoque.getProducts().isNotEmpty()) {
            Button(onClick = {

                val carrinhoJson = Gson().toJson(estoque.getProducts())
                navController.navigate("lista/$carrinhoJson")

            }) {
                Text(text = "Lista de Produtos")
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun LayoutView() {
    layoutMain()
}


