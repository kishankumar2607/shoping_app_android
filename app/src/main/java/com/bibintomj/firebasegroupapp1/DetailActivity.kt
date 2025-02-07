package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibintomj.firebasegroupapp1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener({
            finish()
        })

        val cartButton: ImageButton = findViewById(R.id.cartButton)
        cartButton.setOnClickListener({
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        })
    }
}