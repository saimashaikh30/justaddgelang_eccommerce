package com.example.justaddgelang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvforgot: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvforgot=findViewById(R.id.tvForgotPassword)
        btnLogin.setOnClickListener { validateLogin() }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvforgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    private fun validateLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        authenticateUser(username, password)
    }

    private fun authenticateUser(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        val apiService = RetrofitClient.apiService

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        val userId = loginResponse.user?.user_id ?: "N/A"
                        val username = loginResponse.user?.username ?: "Guest"
                        val phone = loginResponse.user?.phone_no ?: "N/A"
                        val email = loginResponse.user?.email_id ?: "N/A"
                        val userType = loginResponse.user?.usertype ?: "user" // Get user type

                        SharedPreferencesHelper.saveUserData(
                            context = this@Login,
                            isLoggedIn = true,
                            userId = userId.toString(),
                            username = username,
                            password = password,
                            phoneNumber = phone,
                            email = email,
                            usertype = userType
                        )


                        Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // Redirect based on user type
                        val intent = if (userType == "admin") {
                            Intent(this@Login, AdminHomePageActivity::class.java)
                        } else {
                            Intent(this@Login, MainActivity::class.java)
                        }

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Login Failed: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Login, "Username or Password invalid", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@Login, "Network Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
