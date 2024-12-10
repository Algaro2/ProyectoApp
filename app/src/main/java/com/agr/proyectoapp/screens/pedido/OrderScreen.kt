package com.agr.proyectoapp.screens.pedido

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.agr.proyectoapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = viewModel()
) {

    val firebaseAuth = FirebaseAuth.getInstance()
    val userEmail = firebaseAuth.currentUser?.email ?: ""
    val orders by viewModel.orders.collectAsState(initial = emptyList())
    var observations by remember { mutableStateOf("") }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            viewModel.loadOrders(userEmail)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Image(
                        painter = painterResource(id = R.drawable.logo_sin_fondoo ),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFFFC107))
            )
        },
        bottomBar = {
            Button(
                onClick = {

                    if (userEmail.isNotEmpty()) {
                        viewModel.updateObservations(userEmail, observations)

                        navController.navigate("confirmOrderScreen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text(text = "Aceptar", color = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Lista de pedido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(orders) { order ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(text = "${order.productNumber} - ${order.productName}", fontSize = 16.sp)
                        }


                        IconButton(onClick = { viewModel.removeProductFromOrder(userEmail, order) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar producto")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Observaciones de la comida") },
                singleLine = false,
                maxLines = 5
            )
        }
    }
}

