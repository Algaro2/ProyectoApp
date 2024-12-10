package com.agr.proyectoapp.screens.pedido

import android.util.Log
import androidx.lifecycle.ViewModel
import com.agr.proyectoapp.model.Order
import com.agr.proyectoapp.model.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class OrderViewModel : ViewModel() {
    private val db = Firebase.firestore


    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders


    fun loadOrders(userEmail: String) {
        db.collection("pedidos")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                val ordersList = result.documents.mapNotNull { document ->
                    document.toObject(Order::class.java)
                }
                _orders.value = ordersList
            }
            .addOnFailureListener { exception ->
                Log.e("OrderViewModel", "Error al cargar los pedidos", exception)
            }
    }


    fun addProductToOrder(userEmail: String, product: Product) {
        db.collection("pedidos")
            .whereEqualTo("email", userEmail)
            .whereEqualTo("productName", product.name)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {

                    val newOrder = Order(
                        email = userEmail,
                        productNumber = 1,
                        productName = product.name,
                        observations = "",
                        address = ""
                    )
                    db.collection("pedidos").add(newOrder)
                } else {

                    for (document in result) {
                        val currentProduct = document.toObject(Order::class.java)
                        val updatedProductNumber = currentProduct.productNumber + 1
                        db.collection("pedidos").document(document.id)
                            .update("productNumber", updatedProductNumber)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderViewModel", "Error al agregar el producto", exception)
            }
    }


    fun updateObservations(userEmail: String, observations: String) {
        db.collection("pedidos")
            .whereEqualTo("email", userEmail) // Filtrar por email
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("pedidos").document(document.id)
                        .update("observations", observations)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderViewModel", "Error al actualizar las observaciones", exception)
            }
    }


    fun updateOrderAddress(userEmail: String, address: String) {
        db.collection("pedidos")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("pedidos").document(document.id)
                        .update("address", address)
                        .addOnSuccessListener {
                            Log.d("OrderViewModel", "Dirección actualizada correctamente")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("OrderViewModel", "Error al actualizar la dirección", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderViewModel", "Error al cargar los pedidos", exception)
            }
    }
    fun removeProductFromOrder(userEmail: String, order: Order) {
        db.collection("pedidos")
            .whereEqualTo("email", userEmail)
            .whereEqualTo("productName", order.productName)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {

                    val document = result.documents[0]
                    val currentProduct = document.toObject(Order::class.java)
                    val updatedProductNumber = (currentProduct?.productNumber ?: 1) - 1

                    if (updatedProductNumber > 0) {

                        db.collection("pedidos").document(document.id)
                            .update("productNumber", updatedProductNumber)
                            .addOnSuccessListener {
                                Log.d("OrderViewModel", "Producto actualizado correctamente")

                                loadOrders(userEmail)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("OrderViewModel", "Error al actualizar el número del producto", exception)
                            }
                    } else {

                        db.collection("pedidos").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("OrderViewModel", "Producto eliminado correctamente")

                                loadOrders(userEmail)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("OrderViewModel", "Error al eliminar el producto", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderViewModel", "Error al obtener el producto", exception)
            }
    }




}

