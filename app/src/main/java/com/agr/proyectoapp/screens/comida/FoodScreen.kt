package com.agr.proyectoapp.screens.comida

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.agr.proyectoapp.navigation.Screens
import com.agr.proyectoapp.screens.pedido.OrderViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    navController: NavController,
    categoryName: String,
    viewModel: FoodViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()
) {
    val products by viewModel.products.observeAsState(emptyList())
    val firebaseAuth = FirebaseAuth.getInstance()
    val userEmail = firebaseAuth.currentUser?.email ?: ""
    val context = LocalContext.current

    LaunchedEffect(categoryName) {
        viewModel.loadProducts(categoryName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC107)) // Amarillo
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 72.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(products) { index, product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                orderViewModel.addProductToOrder(userEmail, product)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product.ingredients.joinToString(", ") { it },
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }


                        if (userEmail == "alvaro@emasur.es") {
                            IconButton(
                                onClick = {

                                    viewModel.deleteProduct(product.name, categoryName, context) { result ->
                                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Producto",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }


            if (userEmail == "alvaro@emasur.es") {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("${Screens.AddProductScreen.name}/$categoryName")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = Color(0xFF00A4D3),
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar Producto"
                    )
                }
            }
        }
    }
}



