package com.agr.proyectoapp.screens.home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton

import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.agr.proyectoapp.R
import com.agr.proyectoapp.navigation.Screens
import com.agr.proyectoapp.screens.Categoria.CategoryViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: CategoryViewModel = viewModel()) {

    val firebaseAuth = FirebaseAuth.getInstance()
    val userEmail = firebaseAuth.currentUser?.email ?: ""
    val categories by viewModel.categories.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    val expanded = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (userEmail == "alvaro@emasur.es") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screens.CategoryScreen.name) },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        containerColor = Color(0xFF00A4D3),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Bienvenido")
                },
                actions = {

                    IconButton(onClick = {
                        firebaseAuth.signOut()
                        navController.navigate(Screens.LoginScreen.name) // Navegar a la pantalla de login
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFFFC107)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_sin_fondoo),
                    contentDescription = "Logotipo La Mama Mía",
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(0.dp)
                )
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(categories) { index, category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                navController.navigate("${Screens.FoodScreen.name}/${category.name}")
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = category.imageUrl,
                            contentDescription = category.name,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = category.name,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )


                        if (userEmail == "alvaro@emasur.es") {

                            IconButton(
                                onClick = {

                                    viewModel.deleteCategory(category.name) {

                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar categoría",
                                    tint = Color.Red
                                )
                            }

                        }
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFC107))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate(Screens.OrderScreen.name) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                ) {
                    Text(
                        text = "Finalizar pedido",
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


