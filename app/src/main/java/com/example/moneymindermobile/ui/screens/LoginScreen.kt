package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moneymindermobile.data.MainViewModel

@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavHostController) {
    val usernameState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }
    val submitButtonClicked = rememberSaveable { mutableStateOf(false) }
    val registerButtonClicked = rememberSaveable { mutableStateOf(false) }

    LoginScreenUI(usernameState, passwordState, submitButtonClicked, registerButtonClicked)

    // You can access the text field values here
    val username = usernameState.value
    val password = passwordState.value
    // ...
}

@Composable
fun LoginScreenUI(
    usernameState: MutableState<String>,
    passwordState: MutableState<String>,
    submitButtonClicked: MutableState<Boolean>,
    registerButtonClicked: MutableState<Boolean>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text("You don't seem to be logged in", modifier = Modifier.padding(16.dp))
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { submitButtonClicked.value = true },
            Modifier.padding(8.dp),
        ) {
            Text(
                text = "Submit",
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "You don't already have an account?", Modifier.padding(16.dp)
        )
        Button(
            onClick = { registerButtonClicked.value = true },
            Modifier.padding(8.dp),
        ) {
            Text(
                text = "No worries, setting up a new profile only takes a few seconds",
                textAlign = TextAlign.Center

            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val usernameState = rememberSaveable { mutableStateOf("") }
    val passwordState = rememberSaveable { mutableStateOf("") }
    val submitButtonClicked = rememberSaveable { mutableStateOf(false) }
    val registerButtonClicked = rememberSaveable { mutableStateOf(false) }

    LoginScreenUI(usernameState, passwordState, submitButtonClicked, registerButtonClicked)
}