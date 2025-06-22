package com.example.justaddgelang

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAddressActivity : AppCompatActivity() {

    private lateinit var etAddress: EditText
    private lateinit var etState: EditText
    private lateinit var etPostalCode: EditText
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerAddressType: Spinner
    private lateinit var btnSaveAddress: Button

    private var selectedCity: String? = null
    private var selectedPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        etAddress = findViewById(R.id.etAddress)
        etState = findViewById(R.id.etState)
        etPostalCode = findViewById(R.id.etPostalCode)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerAddressType = findViewById(R.id.spinnerAddressType)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)

        val addressTypes = arrayOf("Home", "Office", "Other")
        spinnerAddressType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, addressTypes)

        btnSaveAddress.isEnabled = false

        // Input validation
        val inputWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etAddress.addTextChangedListener(inputWatcher)

        // Fetch city and state when 6-digit pin is entered
        etPostalCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pincode = s.toString().trim()
                if (pincode.length == 6) {
                    fetchDetailsByPinCode(pincode) { postOffice ->
                        runOnUiThread {
                            postOffice?.let {
                                etState.setText(it.State)

                                val cityList = listOf(it.District, it.Name).distinct()
                                val adapter = ArrayAdapter(this@AddAddressActivity, android.R.layout.simple_spinner_item, cityList)
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerCity.adapter = adapter

                                spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                        selectedCity = cityList[position]
                                        selectedPin = pincode
                                        validateInputs()
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }

                                selectedPin = pincode
                                selectedCity = cityList.firstOrNull()
                            } ?: Toast.makeText(this@AddAddressActivity, "Invalid pin code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                validateInputs()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnSaveAddress.setOnClickListener {
            val address = etAddress.text.toString().trim()
            val addressType = spinnerAddressType.selectedItem.toString()
            val userIdStr = SharedPreferencesHelper.getUserId(this)
            val userId = userIdStr?.toIntOrNull()

            if (userId != null && selectedCity != null && selectedPin != null) {
                val fullAddress = "$address, $selectedCity, ${etState.text}, $selectedPin"
                val userAddress = UserAddress(
                    address_id = null,
                    user_id = userId,
                    address = address,
                    address_type = addressType ?: "Home",
                    city = selectedCity ?: "",
                    pincode = selectedPin ?: ""
                )


                RetrofitClient.apiService.addUserAddress(userAddress)
                    .enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AddAddressActivity, "Address saved", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@AddAddressActivity, "Duplicate Address Not Allowed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(this@AddAddressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun validateInputs() {
        val isAddressValid = etAddress.text.toString().isNotEmpty()
        val isStateValid = etState.text.toString().isNotEmpty()
        val isCityValid = selectedCity != null
        val isPinValid = selectedPin?.length == 6
        btnSaveAddress.isEnabled = isAddressValid && isStateValid && isCityValid && isPinValid
    }
}
