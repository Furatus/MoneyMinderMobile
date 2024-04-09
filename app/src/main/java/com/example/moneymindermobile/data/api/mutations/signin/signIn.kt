package com.example.moneymindermobile.data.api.mutations.signin

fun signInMutationBuilder(username: String, password: String, rememberMe: Boolean): String {
    return """
        mutation {
            signIn(appUserLoginDto: {username: "$username", password: "$password", rememberMe: $rememberMe}) {
                succeeded
            }
        }
    """.trimIndent()
}