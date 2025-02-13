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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class CartAdapter(options: FirebaseRecyclerOptions<CartItem>) : FirebaseRecyclerAdapter<CartItem, CartAdapter.MyViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(
        holder: CartAdapter.MyViewHolder,
        position: Int,
        model: CartItem
    ) {
        holder.txtPrice.text = "$${model.product?.price}"
        holder.txtTitle.text = model.product?.title

        val image: String = model.product?.photos?.first() ?: ""

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

        updateCountForProductInCart(model, 0, holder)

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
    }

    private fun updateCountForProductInCart(cartItem: CartItem, change: Int, holder: MyViewHolder) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart/$userId/${cartItem.product?.id ?: ""}")

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                val newCount = currentCount + change

                holder.txtCount.text = "${newCount}"
                if (newCount > 0) {
                    val cartItem = CartItem(cartItem.product, newCount)
                    if (currentCount != newCount) {
                        cartRef.setValue(cartItem)
                    }
                } else {
                    cartRef.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cart Remove", "Failed to remove item to cart")
            }
        })
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.cart_product_cart_layout, parent, false)) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val txtCount: TextView = itemView.findViewById(R.id.txtCount)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
    }
}