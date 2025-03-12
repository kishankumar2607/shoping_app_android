package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProductsAdapter(options: FirebaseRecyclerOptions<Product>) : FirebaseRecyclerAdapter<Product, ProductsAdapter.MyViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductsAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(
        holder: ProductsAdapter.MyViewHolder,
        position: Int,
        model: Product
    ) {
        holder.txtPrice.text = "$${model.price}"
        holder.txtTitle.text = model.title

        val image: String = model.photos.first()

        if (image.indexOf("gs://") > -1) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
            Glide.with(holder.imgProduct.context)
                .load(storageReference)
                .into(holder.imgProduct)
        } else {
            Glide.with(holder.imgProduct.context)
                .load(image)
                .into(holder.imgProduct)
        }

        attachCartListener(model, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("productId", getRef(position).key)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnPlus.setOnClickListener({
            updateCountForProductInCart(model, 1, holder)
        })

        holder.btnMinus.setOnClickListener({
            updateCountForProductInCart(model, -1, holder)
        })

        holder.btnAddToCart.setOnClickListener({
            updateCountForProductInCart(model, 1, holder)

        })
    }

    private fun attachCartListener(product: Product, holder: MyViewHolder) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart/$userId/${product.id}")

        holder.cartListener?.let {
            cartRef.removeEventListener(it)
        }
        holder.cartRef = cartRef

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.child("count").getValue(Int::class.java) ?: 0
                holder.txtCount.text = count.toString()
                if (count > 0) {
                    holder.linearLayoutCount.visibility = View.VISIBLE
                    holder.btnAddToCart.visibility = View.GONE
                } else {
                    holder.linearLayoutCount.visibility = View.GONE
                    holder.btnAddToCart.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartListener", "Error: ${error.message}")
            }
        }
        holder.cartListener = listener
        cartRef.addValueEventListener(listener)
    }

    private fun updateCountForProductInCart(product: Product, change: Int, holder: MyViewHolder) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart/$userId/${product.id}")
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                val newCount = currentCount + change
                if (newCount > 10 && change > 0) {
                    Toast.makeText(holder.itemView.context, "Only 10 items allowed", Toast.LENGTH_SHORT).show()
                    return
                }
                if (newCount > 0) {
                    val cartItem = CartItem(product, newCount)
                    cartRef.setValue(cartItem)
                } else {
                    cartRef.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cart", "Failed to update count: ${error.message}")
            }
        })
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        super.onViewRecycled(holder)
        holder.cartListener?.let {
            holder.cartRef?.removeEventListener(it)
            holder.cartListener = null
        }
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.product_card_layout, parent, false)) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val linearLayoutCount: LinearLayout = itemView.findViewById(R.id.linearLayoutCount)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val txtCount: TextView = itemView.findViewById(R.id.txtCount)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)

        var cartListener: ValueEventListener? = null
        var cartRef: DatabaseReference? = null
    }
}