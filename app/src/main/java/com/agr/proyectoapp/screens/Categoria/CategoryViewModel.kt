package com.agr.proyectoapp.screens.Categoria

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agr.proyectoapp.model.Category
import com.google.firebase.firestore.FirebaseFirestore
class CategoryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    // Función para agregar una categoría
    fun addCategory(name: String, imageUrl: String, onComplete: () -> Unit) {
        val categoryData = hashMapOf(
            "name" to name,
            "imageUrl" to imageUrl
        )


        firestore.collection("categoria")
            .add(categoryData)
            .addOnSuccessListener {
                Log.d("Category", "Categoría agregada correctamente")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Category", "Error al agregar categoría: $e")
                onComplete()
            }
    }


    fun loadCategories() {
        firestore.collection("categoria")
            .get()
            .addOnSuccessListener { result ->
                val categoryList = result.map { document ->
                    Category(
                        name = document.getString("name") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                }
                _categories.value = categoryList
            }
            .addOnFailureListener { e ->
                Log.e("CategoryViewModel", "Error al obtener categorías: $e")
            }
    }


    fun deleteCategory(categoryName: String, onComplete: () -> Unit) {
        firestore.collection("categoria")
            .whereEqualTo("name", categoryName)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("Category", "No se encontró la categoría con el nombre: $categoryName")
                    onComplete()
                    return@addOnSuccessListener
                }

                val documentId = result.documents.first().id
                firestore.collection("categoria").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Category", "Categoría '$categoryName' eliminada correctamente")

                        loadCategories()
                        onComplete()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Category", "Error al eliminar categoría: $e")
                        onComplete()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Category", "Error al buscar categoría para eliminar: $e")
                onComplete()
            }
    }
}


