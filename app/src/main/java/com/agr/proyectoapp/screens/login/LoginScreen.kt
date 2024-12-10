package com.agr.proyectoapp.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

import com.agr.proyectoapp.R
import com.agr.proyectoapp.navigation.Screens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    // Estado para manejar el mensaje de error
    val errorMessage = remember { mutableStateOf("") }

    // Verificar si el usuario está autenticado
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    if (currentUser != null) {
        // Si ya está autenticado, navegar a la pantalla principal
        LaunchedEffect(Unit) {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(Screens.LoginScreen.name) { inclusive = true } // Remover la pantalla de login de la pila de navegación
            }
        }
    }

    val showLoginForm = rememberSaveable { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló: ${ex.message}")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFb71d1f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )

            // Formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showLoginForm.value) {
                        Text(text = "Inicia Sesión", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        UserLoginForm { email, password ->
                            viewModel.signInWithEmailAndPassword(email, password) { success ->
                                if (success) {
                                    navController.navigate(Screens.HomeScreen.name)
                                } else {
                                    errorMessage.value = "Usuario o contraseña incorrectos"
                                }
                            }
                        }
                    } else {
                        Text(text = "Crear una Cuenta", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        UserSignUpForm { name, phone, email, password ->
                            viewModel.createUserWithEmailAndPassword(name, phone, email, password) {
                                navController.navigate(Screens.HomeScreen.name)
                            }
                        }
                    }
                }
            }

            // Mostrar el mensaje de error si hay uno
            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Black,  // Cambio a Color.Black
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alternar entre Crear Cuenta e Iniciar Sesión
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val text1 = if (showLoginForm.value) "¿No tienes cuenta aún?" else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Crear cuenta" else "Iniciar sesión"
                Text(
                    text = text1,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text2,
                    modifier = Modifier
                        .clickable { showLoginForm.value = !showLoginForm.value }
                        .padding(start = 5.dp),
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Login con Google
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable {
                        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("YOUR_GOOGLE_CLIENT_ID")
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, options)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar sesión con Google",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UserLoginForm(onDone: (String, String) -> Unit) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EmailInput(emailState = email)
        PasswordInput(passwordState = password, passwordVisible = rememberSaveable { mutableStateOf(false) })
        Spacer(modifier = Modifier.height(10.dp))
        SubmitButton(textId = "Iniciar Sesión", inputValido = email.value.isNotBlank() && password.value.isNotBlank()) {
            onDone(email.value, password.value)
        }
    }
}

@Composable
fun UserSignUpForm(onDone: (String, String, String, String) -> Unit) {
    val name = rememberSaveable { mutableStateOf("") }
    val phone = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InputField(valuestate = name, labelId = "Nombre", keyboardType = KeyboardType.Text)
        InputField(valuestate = phone, labelId = "Teléfono", keyboardType = KeyboardType.Phone)
        EmailInput(emailState = email)
        PasswordInput(passwordState = password, passwordVisible = rememberSaveable { mutableStateOf(false) })
        Spacer(modifier = Modifier.height(10.dp))
        SubmitButton(
            textId = "Crear Cuenta",
            inputValido = name.value.isNotBlank() && phone.value.isNotBlank() &&
                    email.value.isNotBlank() && password.value.isNotBlank()
        ) {
            onDone(name.value, phone.value, email.value, password.value)
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        enabled = inputValido,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFC107),
            contentColor = Color.Black
        )
    ) {
        Text(text = textId, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun EmailInput(emailState: MutableState<String>) {
    InputField(
        valuestate = emailState,
        labelId = "Email",
        keyboardType = KeyboardType.Email,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription = null)
        }
    )
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    passwordVisible: MutableState<Boolean>
) {
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = "Contraseña") },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                val image = if (passwordVisible.value) Icons.Default.VisibilityOff else Icons.Default.Visibility
                Icon(imageVector = image, contentDescription = null)
            }
        }
    )
}

@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon
    )
}


