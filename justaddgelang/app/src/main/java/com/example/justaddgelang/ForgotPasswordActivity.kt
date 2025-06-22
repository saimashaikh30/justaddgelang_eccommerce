package com.example.justaddgelang

import RetrofitClient
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var tvOtpMessage: TextView
    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: Button
    private lateinit var etEmail: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var context: Context
    private var currentEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        context = this

        tvOtpMessage = findViewById(R.id.tvOtpMessage)
        etOtp = findViewById(R.id.etOTP)
        btnVerifyOtp = findViewById(R.id.btnVerifyOTP)
        etEmail = findViewById(R.id.etEmail)
        btnSendOtp = findViewById(R.id.btnSendOtp)

        // Initially disable send OTP button
        btnSendOtp.isEnabled = false

        // Enable send OTP only if email is valid
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSendOtp.isEnabled = isValidEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnSendOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            } else {
                currentEmail = email
                sendOtpToServer(email)
            }
        }

        btnVerifyOtp.setOnClickListener {
            val otp = etOtp.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP sent to your email", Toast.LENGTH_SHORT).show()
            } else {
                verifyOtpOnServer(currentEmail, otp)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendOtpToServer(email: String) {
        val otpRequest = EmailOtpRequest(email)

        RetrofitClient.apiService.sendOtp(otpRequest).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "OTP has been sent to your email address", Toast.LENGTH_LONG).show()
                    tvOtpMessage.text = "An OTP has been sent to $email. Please enter it below."
                    tvOtpMessage.visibility = View.VISIBLE
                    etOtp.visibility = View.VISIBLE
                    btnVerifyOtp.visibility = View.VISIBLE
                } else {
                    Toast.makeText(context, "This email address is not registered with us", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Failed to send OTP. Please try again later. Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun verifyOtpOnServer(email: String, otp: String) {
        val otpValidationRequest = EmailOtpValidationRequest(email, otp)

        RetrofitClient.apiService.verifyOtp(otpValidationRequest).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body()?.get("success") == true) {
                    Toast.makeText(context, "OTP verified successfully", Toast.LENGTH_LONG).show()
                    // Proceed to reset password or navigate to next screen
                    val intent = Intent(context, ChangePasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Invalid OTP. Please check your email and try again.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(context, "Failed to verify OTP. Please try again later. Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
