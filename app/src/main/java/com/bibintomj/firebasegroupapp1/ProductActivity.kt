package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibintomj.firebasegroupapp1.databinding.ActivityProductBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding
    private var adapter: ProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val query = FirebaseDatabase.getInstance().reference.child("products")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()

        adapter = ProductsAdapter(options)
        val productsRecyclerView: RecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productsRecyclerView.adapter = adapter

        val cartButton: ImageButton = findViewById(R.id.cartButton)
        cartButton.setOnClickListener({
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}