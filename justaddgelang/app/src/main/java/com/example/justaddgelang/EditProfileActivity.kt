package com.example.justaddgelang

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        context = this

        etUsername = findViewById(R.id.etUsername)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)

        val userId = SharedPreferencesHelper.getUserId(this)?.toIntOrNull() ?: -1
        etUsername.setText(SharedPreferencesHelper.getUsername(this))
        etPhoneNumber.setText(SharedPreferencesHelper.getPhoneNumber(this))
        etEmail.setText(SharedPreferencesHelper.getEmail(this))

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UpdateProfileRequest(username, email, phoneNumber)

            Log.d("EditProfile", "Sending userId=$userId and request=$request")

            RetrofitClient.apiService.updateProfile(userId, request)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        Log.d("EditProfile", "Response: $response")
                        if (response.isSuccessful && response.body()?.success == true) {
                            SharedPreferencesHelper.getPassword(context)?.let { password ->
                                SharedPreferencesHelper.saveUserData(
                                    context,
                                    true,
                                    userId.toString(),
                                    username,
                                    password,
                                    phoneNumber,
                                    email,
                                    SharedPreferencesHelper.getUsertype(context) ?: "user"
                                )
                            }
                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, ProfileActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(context, response.body()?.message ?: "Failed to update", Toast.LENGTH_SHORT).show()
                            Log.e("EditProfile", "Error Body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Log.e("EditProfile", "Failure: ${t.localizedMessage}")
                        Toast.makeText(context, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
