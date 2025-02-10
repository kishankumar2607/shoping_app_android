package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibintomj.firebasegroupapp1.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Error

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

        val productId = intent.getStringExtra("productId")
        if (productId != null) {
            fetchProductData(productId)
        } else {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchProductData(productId: String){
        val query = FirebaseDatabase.getInstance().reference.child("products").orderByKey().equalTo(productId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            displayProductDetails(product)
                            break
                        }
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailActivity, "Failed to load product: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayProductDetails(product: Product){
        binding.title.text = product.title
        binding.price.text = "$${product.price}"
        binding.description.text = product.description
        binding.specifications.text = product.specifications.replace("##", "\n")

        if (product.photos.isNotEmpty()) {
            val image = product.photos.first()
            if (image.startsWith("gs://")) {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(binding.productImage)
                }
            } else {
                Glide.with(this).load(image).into(binding.productImage)
            }
        }
    }
}