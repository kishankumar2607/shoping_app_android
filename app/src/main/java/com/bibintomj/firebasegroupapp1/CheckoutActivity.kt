package com.bibintomj.firebasegroupapp1

import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibintomj.firebasegroupapp1.databinding.ActivityCheckoutBinding
import com.google.android.material.textfield.TextInputEditText

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var fullnameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var streetInput: TextInputEditText
    private lateinit var cityInput: TextInputEditText
    private lateinit var provinceInput: TextInputEditText
    private lateinit var postalInput: TextInputEditText
    private lateinit var cardNumberInput: TextInputEditText
    private lateinit var cardNameInput: TextInputEditText
    private lateinit var expiryInput: TextInputEditText
    private lateinit var cvvInput: TextInputEditText
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener({
            finish()
        })

        fullnameInput = findViewById(R.id.fullname_input)
        emailInput = findViewById(R.id.email_input)
        streetInput = findViewById(R.id.street_input)
        cityInput = findViewById(R.id.city_input)
        provinceInput = findViewById(R.id.province_input)
        postalInput = findViewById(R.id.postal_input)
        cardNumberInput = findViewById(R.id.cardno_input)
        cardNameInput = findViewById(R.id.cardname_input)
        expiryInput = findViewById(R.id.expiry_input)
        cvvInput = findViewById(R.id.cvv_input)

        confirmButton = findViewById(R.id.confirm_button)

        confirmButton.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, "Checkout successful!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val fullName = fullnameInput.text.toString().trim()
        if (fullName.isEmpty() || !fullName.contains(" ")) {
            fullnameInput.error = "Enter full name (First & Last name)"
            isValid = false
        }

        val email = emailInput.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email"
            isValid = false
        }

        if (streetInput.text.toString().trim().isEmpty()) {
            streetInput.error = "Enter your street address"
            isValid = false
        }

        if (cityInput.text.toString().trim().isEmpty()) {
            cityInput.error = "Enter your city"
            isValid = false
        }

        if (provinceInput.text.toString().trim().isEmpty()) {
            provinceInput.error = "Enter your province"
            isValid = false
        }

        val postalCodePattern = Regex("^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$")
        val postalCode = postalInput.text.toString().trim()
        if (postalCode.isEmpty() || !postalCode.matches(postalCodePattern)) {
            postalInput.error = "Enter a valid postal code (e.g., A1A 1A1)"
            isValid = false
        }

        val cardNumber = cardNumberInput.text.toString().trim()
        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            cardNumberInput.error = "Enter a valid 16-digit card number"
            isValid = false
        }

        if (cardNameInput.text.toString().trim().isEmpty()) {
            cardNameInput.error = "Enter cardholder's name"
            isValid = false
        }

        val expiryDate = expiryInput.text.toString().trim()
        val expiryPattern = Regex("^(0[1-9]|1[0-2])/[0-9]{2}$")
        if (!expiryDate.matches(expiryPattern)) {
            expiryInput.error = "Enter expiry in MM/YY format"
            isValid = false
        } else {
            val (month, year) = expiryDate.split("/").map { it.toInt() }
            val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                expiryInput.error = "Card is expired"
                isValid = false
            }
        }

        val cvv = cvvInput.text.toString().trim()
        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            cvvInput.error = "Enter a valid 3-digit CVV"
            isValid = false
        }

        return isValid

    }

}


