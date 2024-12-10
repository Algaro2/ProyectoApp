package com.agr.proyectoapp.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData(false)

    // Función para registrar un nuevo usuario
    fun createUserWithEmailAndPassword(
        name: String,
        phone: String,
        email: String,
        password: String,
        home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Guarda los datos del usuario en Firestore
                        createUser(name, phone, email, password)
                        home()
                    } else {
                        Log.d("MyLogin", "Error al crear usuario: ${task.exception?.message}")
                    }
                    _loading.value = false
                }
        }
    }
    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task -> //si la tarea tuve exito escribimos mensaje en log
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithGoogle: ${task.result.toString()}"
                            )
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
            }
        }

    // Función para guardar los datos del usuario en Firestore
    private fun createUser(name: String, phone: String, email: String, password: String) {
        val userId = auth.currentUser?.uid
        val user = mapOf(
            "nombre" to name,
            "telefono" to phone,
            "email" to email,
            "password" to password // OJO: En producción, encripta esta información
        )

        firestore.collection("users")
            .document(userId ?: "") // Usamos el UID del usuario como ID del documento
            .set(user)
            .addOnSuccessListener {
                Log.d("MyLogin", "Usuario guardado exitosamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.d("MyLogin", "Error al guardar usuario en Firestore: $e")
            }
    }

    // Función para iniciar sesión con email y contraseña
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Inicio de sesión exitoso")
                            onResult(true) // Retorna éxito
                        } else {
                            Log.d("MyLogin", "Error al iniciar sesión: ${task.exception?.message}")
                            onResult(false) // Retorna error
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Excepción al iniciar sesión: ${ex.message}")
                onResult(false) // En caso de excepción, también se considera error
            }
        }
    }

}

