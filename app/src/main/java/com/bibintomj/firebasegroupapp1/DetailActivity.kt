package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
        val cartButton: ImageButton = findViewById(R.id.cartButton)
        val addToCartButton: Button = findViewById(R.id.btnAddToCart)

        backButton.setOnClickListener({
            finish()
        })

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

        addToCartButton.setOnClickListener({
            Toast.makeText(this@DetailActivity, "Add to Cart Clicked", Toast.LENGTH_SHORT).show()
        })
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
        val txtTitle: TextView = findViewById(R.id.txtTitle)
        val txtPrice: TextView = findViewById(R.id.txtPrice)
        val txtDescription: TextView = findViewById(R.id.txtDescription)
        val txtSpecifications: TextView = findViewById(R.id.txtSpecifications)
        val productImage: ImageView = findViewById(R.id.productImage)

        txtTitle.text = product.title
        txtPrice.text = "$${product.price}"
        txtDescription.text = product.description
        txtSpecifications.text = product.specifications.replace("##", "\n")

        if (product.photos.isNotEmpty()) {
            val image: String = product.photos.first()

            if (image.indexOf("gs://") > -1 ){
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
                Glide.with(this)
                    .load(storageReference)
                    .into(productImage)
            } else {
                Glide.with(this)
                    .load(image)
                    .into(productImage)
            }
        }
    }
}