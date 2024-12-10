package com.agr.proyectoapp.screens.comida

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.agr.proyectoapp.model.Product

class FoodViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: MutableLiveData<List<Product>> = _products

    fun loadProducts(categoryName: String) {
        FirebaseFirestore.getInstance()
            .collection(categoryName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                _products.value = productList
            }
    }


    fun addProduct(newProduct: Product, param: (Any) -> Any) {
        val categoryName = newProduct.category


        FirebaseFirestore.getInstance()
            .collection(categoryName)
            .add(newProduct)
            .addOnSuccessListener {

                loadProducts(categoryName)
            }
            .addOnFailureListener {
                param("Erro al añadir el producto")
            }
    }

    fun deleteProduct(productName: String, categoryName: String, context: Context, param: (String) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(categoryName)
            .whereEqualTo("name", productName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentId = querySnapshot.documents.first().id
                    FirebaseFirestore.getInstance()
                        .collection(categoryName)
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            loadProducts(categoryName) // Recargamos los productos de la categoría
                            param("Producto eliminado con éxito")
                        }
                        .addOnFailureListener {
                            param("Error al eliminar el producto")
                        }
                } else {
                    param("Producto no encontrado")
                }
            }
            .addOnFailureListener {
                param("Error al buscar el producto")
            }
    }


}


