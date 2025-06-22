package com.example.justaddgelang

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: User?  // Optionally, you can include the user object if returned by the API
)

