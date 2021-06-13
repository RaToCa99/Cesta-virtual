package com.example.cestavirtual.cesta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cestavirtual.R
import com.example.cestavirtual.data.Articulo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class adapter_cesta(val articulos: MutableList<Articulo>, val cestaPropia: Boolean) :
    RecyclerView.Adapter<adapter_cesta.ArticuloHolder>() {

    private var clickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticuloHolder(layoutInflater.inflate(R.layout.item_articulo, parent, false))
    }

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        holder.bindItems(articulos[position], cestaPropia)
    }

    override fun getItemCount(): Int = articulos.size

    fun getItem(position: Int): Articulo {
        return articulos[position]
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun removeAt(index: Int) {
        articulos.removeAt(index)
        notifyItemRemoved(index)
    }

    inner class ArticuloHolder(v: View) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        private val nombreArticulo: TextView = v.findViewById(R.id.tv_nombre_articulo)
        private val nombreCategoria: TextView = v.findViewById(R.id.tv_categoria_articulo)
        private val checkBoxBought: CheckBox = v.findViewById(R.id.cb_article_bought)

        init {
            if (clickListener != null) {
                itemView.setOnClickListener(this)
            }
        }

        fun bindItems(data: Articulo, cestaPropia: Boolean) {
            val db= Firebase.firestore

            nombreArticulo.text = data.nombre
            nombreCategoria.text = data.categoria
            checkBoxBought.isChecked=data.comprado
            if (cestaPropia) {
               checkBoxBought.isEnabled=false
            } else {
                checkBoxBought.setOnClickListener {
                    db.collection("articulos")
                        .document(data.id_articulo)
                        .update("comprado",checkBoxBought.isChecked)
                }
            }
        }

        override fun onClick(v: View?) {
            if (v != null) {
                clickListener?.onItemClick(v, adapterPosition)
            }
        }

    }

    interface ClickListener {
        fun onItemClick(v: View, position: Int)
    }
}