package com.example.cestavirtual.cestas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cestavirtual.R
import com.example.cestavirtual.data.Cesta


class adapter_cestas(val cestas:MutableList<Cesta>,val cestaPropia:Boolean):RecyclerView.Adapter<adapter_cestas.MyViewHolder>() {
    private var itemLongClickListener:ItemLongClickListener? = null
    private var clickListener: ClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val layoutInflater=LayoutInflater.from(parent.context)
        return MyViewHolder(layoutInflater.inflate(R.layout.item_cesta_propia,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(cestas[position])
    }

    override fun getItemCount(): Int =cestas.size

    fun getItem(position: Int): Cesta {
        //return if (mDataSet != null) mDataSet[position] else null
        return cestas[position]
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    fun setItemLongClickListener(ic: ItemLongClickListener?) {
        itemLongClickListener = ic
    }

    fun removeAt(index: Int) {
        cestas.removeAt(index)
        notifyItemRemoved(index)
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener,View.OnLongClickListener {
        private val nombreCesta=v.findViewById(R.id.tv_nombre_cesta) as TextView
        private val botonEditar=v.findViewById<ImageButton>(R.id.bt_editar_cesta)
        private val botonBorrar=v.findViewById<ImageButton>(R.id.bt_eliminar_cesta)
        init {
            if (clickListener != null) {
                itemView.setOnClickListener(this)
            }
            if(itemLongClickListener!=null){
                itemView.setOnLongClickListener(this)
            }
        }

        fun bindItems(data: Cesta) {
            nombreCesta.text=data.nombre
            if(cestaPropia)
                botonEditar.setOnClickListener {
                    clickListener!!.editClick(getItem(adapterPosition))
                }
            else
                botonEditar.visibility=View.GONE

            botonBorrar.setOnClickListener {
                clickListener!!.deleteClick(adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            if (v != null) {
                clickListener?.onItemClick(v,adapterPosition)
            }
        }


        override fun onLongClick(v: View?): Boolean {
            if (v != null) {
                itemLongClickListener?.onItemLongClick(v,adapterPosition)
            }
            return true
        }
    }

    interface ClickListener {
        fun onItemClick(v: View,position: Int)
        fun deleteClick(cestaPos:Int)
        fun editClick(cesta:Cesta)
    }

    interface ItemLongClickListener{
        fun onItemLongClick(v: View,position: Int)
    }

   

}