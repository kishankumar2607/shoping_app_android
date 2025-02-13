package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibintomj.firebasegroupapp1.ProductsAdapter.MyViewHolder
import com.bibintomj.firebasegroupapp1.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Error

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var productId: String = ""

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

        productId = intent.getStringExtra("productId") ?: ""
        fetchProductData(productId)
        updateCountForProductInCart(productId, 0)


        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)

        btnPlus.setOnClickListener({
            updateCountForProductInCart(productId, 1)
        })

        btnMinus.setOnClickListener({
            updateCountForProductInCart(productId, -1)
        })

        addToCartButton.setOnClickListener({
            val productId = intent.getStringExtra("productId")
            if (productId != null) {
                updateCountForProductInCart(productId, 1)
            }
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

    private fun updateCountForProductInCart(productId: String, change: Int) {
        if (productId.isEmpty()) {
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart/$userId/$productId")
        val txtCount: TextView = findViewById(R.id.txtCount)
        val linearLayoutCount: LinearLayout = findViewById(R.id.linearLayoutCount)
        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                val newCount = currentCount + change

                txtCount.text = "${newCount}"
                if (newCount > 0) {
                    val cartItem = CartItem(null, newCount)
                    if (currentCount != newCount) {
                        cartRef.setValue(cartItem)
                    }
                    linearLayoutCount.visibility = View.VISIBLE
                    btnAddToCart.visibility = View.GONE
                } else {
                    cartRef.removeValue()
                    linearLayoutCount.visibility = View.GONE
                    btnAddToCart.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cart Remove", "Failed to remove item to cart")
            }
        })
    }
}