package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.apollographql.apollo3.api.Error
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.Routes

@Composable
fun RegistrationScreen(viewModel: MainViewModel, navController: NavHostController) {
    val usernameState = rememberSaveable { mutableStateOf("") }
    val emailState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }

    val createAccountButtonClicked = rememberSaveable { mutableStateOf(false) }
    val loginButtonClicked = rememberSaveable { mutableStateOf(false) }

    val createAccountButtonCounter = rememberSaveable { mutableIntStateOf(0) }
    val loginButtonCounter = rememberSaveable { mutableIntStateOf(0) }

    val registerMessage by viewModel.graphQlError.collectAsState()
    val registerResponse by viewModel.registerResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(registerResponse) {
        if (registerResponse != null) {
            if (registerResponse!!.createUser?.id != null) {
                navController.navigate(route = Routes.HOME)
            }
        }
    }

    RegistrationScreenUI(
        usernameState = usernameState,
        emailState = emailState,
        passwordState = passwordState,
        createAccountButtonClicked = createAccountButtonClicked,
        loginButtonClicked = loginButtonClicked,
        createAccountButtonCounter = createAccountButtonCounter,
        loginButtonCounter = loginButtonCounter,
        registerMessage = registerMessage,
        isLoading = isLoading
    )

    val username = usernameState.value
    val password = passwordState.value
    val email = emailState.value

    LaunchedEffect(createAccountButtonCounter.intValue) {
        if (createAccountButtonClicked.value) {
            viewModel.register(password = password, username = username, email = email)
            usernameState.value = ""
            emailState.value = ""
            passwordState.value = ""
            createAccountButtonClicked.value = false
        }
    }

    LaunchedEffect(loginButtonCounter.intValue) {
        if (loginButtonClicked.value) {
            println("login button clicked!")
            createAccountButtonClicked.value = false
            navController.navigate(Routes.LOGIN)
        }
    }
}

@Composable
fun RegistrationScreenUI(
    usernameState: MutableState<String>,
    passwordState: MutableState<String>,
    emailState: MutableState<String>,
    createAccountButtonClicked: MutableState<Boolean>,
    loginButtonClicked: MutableState<Boolean>,
    createAccountButtonCounter: MutableIntState,
    loginButtonCounter: MutableIntState,
    registerMessage: List<Error>?,
    isLoading: Boolean,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text("You don't seem to have an account", modifier = Modifier.padding(16.dp))
                TextField(
                    value = usernameState.value,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Username"
                        )
                    },
                    onValueChange = { usernameState.value = it },
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                TextField(
                    value = emailState.value,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email"
                        )
                    },
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Email),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                TextField(
                    value = passwordState.value,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Password"
                        )
                    },
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = {
                        createAccountButtonClicked.value = true
                        createAccountButtonCounter.intValue++
                    },
                    Modifier.padding(8.dp),
                ) {
                    Text(
                        text = "Create an account",
                        textAlign = TextAlign.Center
                    )
                }
                if (registerMessage != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        items(registerMessage) { error ->
                            Text(
                                text = error.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "You already have an account?", Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        loginButtonClicked.value = true
                        loginButtonCounter.intValue++
                    },
                    Modifier.padding(8.dp),
                ) {
                    Text(
                        text = "Click here to go to the login page",
                        textAlign = TextAlign.Center

                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RegistrationScreen() {
    val usernameState = rememberSaveable { mutableStateOf("") }
    val emailState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }

    val submitButtonClicked = rememberSaveable { mutableStateOf(false) }
    val registerButtonClicked = rememberSaveable { mutableStateOf(false) }

    val submitButtonCounter = rememberSaveable { mutableIntStateOf(0) }
    val registerButtonCounter = rememberSaveable { mutableIntStateOf(0) }

    val signInMessage = null
    val isLoading = false

    RegistrationScreenUI(
        usernameState = usernameState,
        passwordState = passwordState,
        emailState = emailState,
        createAccountButtonClicked = submitButtonClicked,
        loginButtonClicked = registerButtonClicked,
        createAccountButtonCounter = submitButtonCounter,
        loginButtonCounter = registerButtonCounter,
        registerMessage = signInMessage,
        isLoading = isLoading,
    )
}