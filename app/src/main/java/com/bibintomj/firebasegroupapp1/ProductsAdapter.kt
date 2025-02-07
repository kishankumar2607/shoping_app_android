package com.bibintomj.firebasegroupapp1

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.api.Distribution.BucketOptions.Linear
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

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("productId", getRef(position).key)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnPlus.setOnClickListener({

        })

        holder.btnMinus.setOnClickListener({

        })

        holder.btnAddToCart.setOnClickListener({
            holder.linearLayoutCount.visibility = View.VISIBLE
            holder.btnAddToCart.visibility = View.INVISIBLE
        })
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
    }
}