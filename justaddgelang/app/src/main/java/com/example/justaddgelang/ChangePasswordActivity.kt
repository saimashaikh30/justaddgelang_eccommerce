package com.example.justaddgelang
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmitPassword: Button
    private lateinit var tvPasswordStrength: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmitPassword = findViewById(R.id.btnSubmitPassword)
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength)
        progressBar = findViewById(R.id.progressBar)

        email = intent.getStringExtra("email") ?: ""

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = etNewPassword.text.toString()
                val confirm = etConfirmPassword.text.toString()

                btnSubmitPassword.isEnabled = password.length >= 6 && password == confirm
                updatePasswordStrength(password)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etNewPassword.addTextChangedListener(watcher)
        etConfirmPassword.addTextChangedListener(watcher)

        btnSubmitPassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString()

            progressBar.visibility = View.VISIBLE
            btnSubmitPassword.isEnabled = false

            val request = ChangePasswordRequest(email, newPassword)
            RetrofitClient.apiService.changePassword(request).enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    progressBar.visibility = View.GONE
                    btnSubmitPassword.isEnabled = true

                    if (response.isSuccessful && response.body()?.get("success") == true) {
                        Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ChangePasswordActivity, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Failed to change password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnSubmitPassword.isEnabled = true
                    Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updatePasswordStrength(password: String) {
        val strength = when {
            password.length >= 12 && password.any { it.isDigit() } && password.any { it.isUpperCase() } && password.any { "!@#$%^&*()".contains(it) } -> "Strong 💪"
            password.length >= 8 -> "Medium 🙂"
            password.isNotEmpty() -> "Weak 😬"
            else -> ""
        }

        tvPasswordStrength.text = "Strength: $strength"
        tvPasswordStrength.setTextColor(
            when (strength) {
                "Strong 💪" -> getColor(android.R.color.holo_green_dark)
                "Medium 🙂" -> getColor(android.R.color.holo_orange_light)
                "Weak 😬" -> getColor(android.R.color.holo_red_dark)
                else -> getColor(android.R.color.darker_gray)
            }
        )
    }
}
