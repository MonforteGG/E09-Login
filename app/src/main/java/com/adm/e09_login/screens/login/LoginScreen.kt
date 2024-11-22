package com.adm.e09_login.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

import com.adm.e09_login.R
import com.adm.e09_login.navigation.Screens
import com.adm.e09_login.ui.theme.BlueIgColor


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    // Google
    // este token se consigue en Firebase->Proveedores de Acceso->Google->Conf del SKD->Id de cliente web
    val token = "484211156584-8v31io0esgtbmothcsfase8l7o2f41dl.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts
            .StartActivityForResult() // esto abrirá un activity para hacer el login de Google
    ) {
        val task =
            GoogleSignIn.getSignedInAccountFromIntent(it.data) // esto lo facilita la librería añadida
        // el intent será enviado cuando se lance el launcher
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló")
        }
    }

    //Facebook
//    val scope = rememberCoroutineScope()
//    val loginManager = LoginManager.getInstance()
//    val callbackManager = remember { CallbackManager.Factory.create() }
//    val launcherFb = rememberLauncherForActivityResult(
//        loginManager.createLogInActivityResultContract(callbackManager, null)) {
//        // nothing to do. handled in FacebookCallback
//
//        scope.launch {
//            val tokenFB = AccessToken.getCurrentAccessToken()
//            val credentialFB = tokenFB?.let { FacebookAuthProvider.getCredential(it.token)}
//            if(credentialFB != null){
//                viewModel.signInWithFacebook(credentialFB){
//                    navController.navigate(Screens.HomeScreen.name)
//                }
//            }
//        }
//    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ig_logo),
            contentDescription = "Instagram logo",
            modifier = Modifier.size(200.dp)
        )

        if (showLoginForm.value) { //si es true crea cuenta sino inicia sesion
            UserForm(isCreateAccount = false) { email, password ->
                Log.d("My Login", "Logueando con $email y $password")
                viewModel.signInWithEmailAndPassword(
                    email,
                    password
                ) {//pasamos email, password, y la funcion que navega hacia home
                    navController.navigate(Screens.HomeScreen.name)
                }

            }
        } else {
            UserForm(isCreateAccount = true) { email, password ->
                Log.d("My Login", "Creando cuenta con $email y $password")
                viewModel.createUserWithEmailAndPassword(
                    email,
                    password
                ) {//pasamos email, password, y la funcion que navega hacia home
                    navController.navigate(Screens.HomeScreen.name)
                }

            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.White
            )

            Text(
                "OR",
                Modifier.padding(20.dp),
                style = TextStyle(fontSize = 12.sp),
                color = Color.White
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.White
            )
        }

        // GOOGLE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { // Se crea un buider de opciones, una de ellas incluye un token

                    val opciones = GoogleSignInOptions
                        .Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN
                        )
                        .requestIdToken(token) //requiere el token
                        .requestEmail() //y tb requiere el email
                        .build()
                    //creamos un cliente de logueo con estas opciones
                    val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
                    launcher.launch(googleSingInCliente.signInIntent)
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google logo",
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified
            )

            Text(
                "Log In with Google",
                Modifier.padding(start = 10.dp),
                style = TextStyle(fontSize = 12.sp),
                color = BlueIgColor
            )

        }

        //FACEBOOK
              Row(
                  modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .clickable {
//                          launcherFb.launch(listOf("email","public_profile"))
                      },
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
              ) {
                  Icon(
                      painter = painterResource(R.drawable.facebook_logo),
                      contentDescription = "Google logo",
                      modifier = Modifier.size(18.dp),
                      tint = Color.Unspecified
                  )

                  Text(
                      "Log In with Facebook",
                      Modifier.padding(start = 10.dp),
                      style = TextStyle(fontSize = 12.sp),
                      color = BlueIgColor
                  )
              }

        Spacer(Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 25.dp),
            thickness = 1.dp,
            color = Color.White
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val text1 = if (showLoginForm.value) "Don't have an account?"
            else "Have an account?"
            val text2 = if (showLoginForm.value) "Sign Up"
            else "Log In"
            Text(text = text1, color = Color.White)
            Text(text = text2,
                modifier = Modifier
                    .clickable {
                        showLoginForm.value = !showLoginForm.value
                    }
                    .padding(start = 5.dp),
                color = BlueIgColor)
        }
    }

}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {

    val email = rememberSaveable {
        mutableStateOf("")
    }

    val password = rememberSaveable {
        mutableStateOf("")
    }

    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    //controla que al hacer clic en el boton submite, el teclado se oculte
    val keyboardController = LocalSoftwareKeyboardController.current

    val forgottenPassword = buildAnnotatedString {
        withStyle(style = SpanStyle(color = BlueIgColor, fontSize = 12.sp)) {
            append("Forgotten password?")
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(
            emailState = email
        )

        PasswordInput(
            passwordState = password,
            passwordVisible = passwordVisible
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 20.dp)) {

            Spacer(Modifier.weight(1f))

            ClickableText(
                text = forgottenPassword,
                onClick = { },
            )
        }

        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ) {
            onDone(email.value.trim(), password.value.trim())
            //se oculta el teclado, el ? es que se llama la función en modo seguro
            keyboardController?.hide()
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
            .padding(3.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(BlueIgColor),
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}


@Composable
fun EmailInput(
    emailState: MutableState<String>,
    placeholder: String = "Email"
) {
    InputField(
        valuestate = emailState,
        placeholder = placeholder,
        keyboardType = KeyboardType.Email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    placeholder: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        placeholder = { Text(text = placeholder, color = Color.White) },
        singleLine = isSingleLine,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedContainerColor = Color.DarkGray,
            focusedContainerColor = Color.DarkGray
        ),
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)

    )
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    placeholder: String = "Password",
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        placeholder = { Text(text = placeholder, color = Color.White) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedContainerColor = Color.DarkGray,
            focusedContainerColor = Color.DarkGray
        ),
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image = if (passwordVisible.value)
        Icons.Default.VisibilityOff
    else
        Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}


//@Composable
//fun LoginScreen(
//    navController: NavController,
//    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
//) {
//    //Text(text = "Login")
//    val showLoginForm = rememberSaveable {
//        mutableStateOf(true)
//    }
//
//    // Facebook
//    /*val scope = rememberCoroutineScope()
//    val loginManager = LoginManager.getInstance()
//    val callbackManager = remember { CallbackManager.Factory.create() }
//    val launcherFb = rememberLauncherForActivityResult(
//        loginManager.createLogInActivityResultContract(callbackManager, null)) {
//        // nothing to do. handled in FacebookCallback
//
//        scope.launch {
//            val tokenFB = AccessToken.getCurrentAccessToken()
//            val credentialFB = tokenFB?.let { FacebookAuthProvider.getCredential(it.token)}
//            if(credentialFB != null){
//                viewModel.signInWithFacebook(credentialFB){
//                    navController.navigate(Screens.HomeScreen.name)
//                }
//            }
//        }
//    }*/
//
//    // Google
//    // este token se consigue en Firebase->Proveedores de Acceso->Google->Conf del SKD->Id de cliente web
//    val token = "484211156584-8v31io0esgtbmothcsfase8l7o2f41dl.apps.googleusercontent.com"
//    val context = LocalContext.current
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts
//            .StartActivityForResult() // esto abrirá un activity para hacer el login de Google
//    ) {
//        val task =
//            GoogleSignIn.getSignedInAccountFromIntent(it.data) // esto lo facilita la librería añadida
//        // el intent será enviado cuando se lance el launcher
//        try {
//            val account = task.getResult(ApiException::class.java)
//            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//            viewModel.signInWithGoogleCredential(credential) {
//                navController.navigate(Screens.HomeScreen.name)
//            }
//        } catch (ex: Exception) {
//            Log.d("My Login", "GoogleSignIn falló")
//        }
//    }
//
//
//
//
//    Surface(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxSize()
//
//        ) {
//            if (showLoginForm.value) { //si es true crea cuenta sino inicia sesion
//                Text(text = "Inicia sesion")
//                UserForm(isCreateAccount = false) { email, password ->
//                    Log.d("My Login", "Logueando con $email y $password")
//                    viewModel.signInWithEmailAndPassword(
//                        email,
//                        password
//                    ) {//pasamos email, password, y la funcion que navega hacia home
//                        navController.navigate(Screens.HomeScreen.name)
//                    }
//
//                }
//            } else {
//                Text(text = "Crear una cuenta")
//                UserForm(isCreateAccount = true) { email, password ->
//                    Log.d("My Login", "Creando cuenta con $email y $password")
//                    viewModel.createUserWithEmailAndPassword(
//                        email,
//                        password
//                    ) {//pasamos email, password, y la funcion que navega hacia home
//                        navController.navigate(Screens.HomeScreen.name)
//                    }
//
//                }
//            }
//            // alternar entre Crear cuenta e iniciar sesion
//            Spacer(modifier = Modifier.height(15.dp))
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                val text1 = if (showLoginForm.value) "¿No tienes cuenta?"
//                else "¿Ya tienes cuenta?"
//                val text2 = if (showLoginForm.value) "Registrate"
//                else "Inicia sesión"
//                Text(text = text1)
//                Text(text = text2,
//                    modifier = Modifier
//                        .clickable {
//                            showLoginForm.value = !showLoginForm.value
//                        }
//                        .padding(start = 5.dp),
//                    color = MaterialTheme.colorScheme.secondary)
//            }
//
//
//            // GOOGLE
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp)
//                    .clip(RoundedCornerShape(10.dp))
//                    .clickable { // Se crea un buider de opciones, una de ellas incluye un token
//
//                        val opciones = GoogleSignInOptions
//                            .Builder(
//                                GoogleSignInOptions.DEFAULT_SIGN_IN
//                            )
//                            .requestIdToken(token) //requiere el token
//                            .requestEmail() //y tb requiere el email
//                            .build()
//                        //creamos un cliente de logueo con estas opciones
//                        val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
//                        launcher.launch(googleSingInCliente.signInIntent)
//                    },
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_google),
//                    contentDescription = "Login con GOOGLE",
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .size(40.dp)
//                )
//
//                Text(
//                    text = "Login con Google",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // FACEBOOK
////              Row(
////                  modifier = Modifier
////                      .fillMaxWidth()
////                      .padding(10.dp)
////                      .clip(RoundedCornerShape(10.dp))
////                      .clickable {
////                          launcherFb.launch(listOf("email","public_profile"))
////                      },
////                  horizontalArrangement = Arrangement.Center,
////                  verticalAlignment = Alignment.CenterVertically
////              ) {
////                  Image(
////                      painter = painterResource(id = R.drawable.ic_fb),
////                      contentDescription = "Login con facebook",
////                      modifier = Modifier
////                          .padding(10.dp)
////                          .size(40.dp)
////                  )
////
////                  Text(
////                      text = "Login con Facebook",
////                      fontSize = 18.sp,
////                      fontWeight = FontWeight.Bold
////                  )
////              }
//
//
//        }
//    }
//}
//
//@Composable
//fun UserForm(
//    isCreateAccount: Boolean,
//    onDone: (String, String) -> Unit = { email, pwd -> }
//) {
//
//    val email = rememberSaveable {
//        mutableStateOf("")
//    }
//
//    val password = rememberSaveable {
//        mutableStateOf("")
//    }
//
//    val passwordVisible = rememberSaveable {
//        mutableStateOf(false)
//    }
//
//    val valido = remember(email.value, password.value) {
//        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
//    }
//
//    //controla que al hacer clic en el boton submite, el teclado se oculte
//    val keyboardController = LocalSoftwareKeyboardController.current
//
//    val forgottenPassword = buildAnnotatedString {
//        withStyle(style = SpanStyle(color = BlueIgColor, fontSize = 12.sp)) {
//            append("Forgotten password?")
//        }
//    }
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        EmailInput(
//            emailState = email
//        )
//
//        PasswordInput(
//            passwordState = password,
//            passwordVisible = passwordVisible
//        )
//
//        Row (Modifier.fillMaxWidth().padding(end = 20.dp)) {
//
//            Spacer(Modifier.weight(1f))
//
//            ClickableText(
//                text = forgottenPassword,
//                onClick = { },
//            )
//        }
//
//        SubmitButton(
//            textId = if (isCreateAccount) "Crear cuenta" else "Login",
//            inputValido = valido
//        ) {
//            onDone(email.value.trim(), password.value.trim())
//            //se oculta el teclado, el ? es que se llama la función en modo seguro
//            keyboardController?.hide()
//        }
//    }
//}
//
//@Composable
//fun SubmitButton(
//    textId: String,
//    inputValido: Boolean,
//    onClic: () -> Unit
//) {
//    Button(
//        onClick = onClic,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(3.dp),
//        shape = RoundedCornerShape(8.dp),
//        colors = ButtonDefaults.buttonColors(BlueIgColor),
//        enabled = inputValido
//    ) {
//        Text(
//            text = textId,
//            modifier = Modifier.padding(5.dp)
//        )
//    }
//}
//
//
//@Composable
//fun EmailInput(
//    emailState: MutableState<String>,
//    labelId: String = "Email"
//) {
//    InputField(
//        valuestate = emailState,
//        labelId = labelId,
//        keyboardType = KeyboardType.Email
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun InputField(
//    valuestate: MutableState<String>,
//    labelId: String,
//    keyboardType: KeyboardType,
//    isSingleLine: Boolean = true,
//) {
//    OutlinedTextField(
//        value = valuestate.value,
//        onValueChange = { valuestate.value = it },
//        label = { Text(text = labelId) },
//        singleLine = isSingleLine,
//        modifier = Modifier
//            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
//            .fillMaxWidth(),
//        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
//
//    )
//}
//
//@Composable
//fun PasswordInput(
//    passwordState: MutableState<String>,
//    labelId: String = "Password",
//    passwordVisible: MutableState<Boolean>
//) {
//    val visualTransformation = if (passwordVisible.value)
//        VisualTransformation.None
//    else PasswordVisualTransformation()
//
//    OutlinedTextField(
//        value = passwordState.value,
//        onValueChange = { passwordState.value = it },
//        label = { Text(text = labelId) },
//        singleLine = true,
//        modifier = Modifier
//            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
//            .fillMaxWidth(),
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//        visualTransformation = visualTransformation,
//        trailingIcon = {
//            if (passwordState.value.isNotBlank()) {
//                PasswordVisibleIcon(passwordVisible)
//            } else null
//        }
//    )
//}
//
//@Composable
//fun PasswordVisibleIcon(
//    passwordVisible: MutableState<Boolean>
//) {
//    val image = if (passwordVisible.value)
//        Icons.Default.VisibilityOff
//    else
//        Icons.Default.Visibility
//
//    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
//        Icon(
//            imageVector = image,
//            contentDescription = ""
//        )
//    }
//}
