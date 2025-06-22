package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvUsernameError: TextView
    private lateinit var tvPhoneError: TextView
    private lateinit var tvEmailError: TextView
    private lateinit var tvPasswordError: TextView
    private lateinit var tvConfirmPasswordError: TextView
    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button
    private lateinit var tvLogin: TextView

   // private val BASE_URL = "https://yourapiurl.com/" // Replace with your actual base URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)

        // Initialize Views
        etUsername = findViewById(R.id.etUsername)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvUsernameError = findViewById(R.id.tvUsernameError)
        tvPhoneError = findViewById(R.id.tvPhoneError)
        tvEmailError = findViewById(R.id.tvEmailError)
        tvPasswordError = findViewById(R.id.tvPasswordError)
        tvConfirmPasswordError = findViewById(R.id.tvConfirmPasswordError)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)
        tvLogin = findViewById(R.id.tvLogin)

        // Register Button Click
        btnRegister.setOnClickListener {
            validateAndRegister()
        }

        // Back Button Click
        btnBack.setOnClickListener {
            finish() // Closes activity and goes back
        }

        // Login Text Click (Go to Login Page)
        tvLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun validateAndRegister() {
        val username = etUsername.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        var isValid = true

        // Validate inputs here
        if (isValid) {
            val user = User(
                user_id = null,
                username = username,
                phone_no = phoneNumber,
                email_id = email,
                password = password,
                usertype = "user" // Add usertype if needed
            )
            registerUser(user)
        }
    }

    private fun registerUser(user: User) {
        // Use RetrofitClient to get Retrofit instance
        val apiService = RetrofitClient.apiService

        // Make the API call
        apiService.registerUser(user).enqueue(object : Callback<UserResponse> { // Correct the Callback type here
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    if (registrationResponse != null && registrationResponse.success) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, Login::class.java))
                        finish() // Close RegisterActivity
                    } else {
                        Toast.makeText(this@RegisterActivity, registrationResponse?.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
