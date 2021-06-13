package com.example.cestavirtual.cesta.details

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cestavirtual.R
import com.example.cestavirtual.cesta.adapter.adapter_cesta
import com.example.cestavirtual.data.Articulo
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_cesta.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "idCesta"
private const val ARG_PARAM2 = "esPropietario"

/**
 * A simple [Fragment] subclass.
 * Use the [cesta.newInstance] factory method to
 * create an instance of this fragment.
 */
class cesta : Fragment() {
    private var idCesta: String? = null
    private var esPropietario: Boolean? = null
    private var adapterCesta=adapter_cesta(mutableListOf(),true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idCesta = it.getString(ARG_PARAM1)
            esPropietario = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cesta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toast.makeText(context, "Id cesta-> $idCesta  esPropietario-> $esPropietario", Toast.LENGTH_SHORT).show()
        if(esPropietario!!)
            fab_add_article.setOnClickListener {
                createAddArticleDialog()
            }
        else
            fab_add_article.visibility=View.GONE
        rv_articulos.layoutManager=LinearLayoutManager(context)
        rv_articulos.adapter=adapterCesta
        descargarDatosRecyclerView()
    }

    private fun descargarDatosRecyclerView(){
        val db= Firebase.firestore
        db.collection("articulos")
            .whereEqualTo("idCesta",idCesta)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.documents.isNotEmpty())
                        formatearDatos(task.result?.documents!!)
                }
            }
    }

    private fun formatearDatos(documents: MutableList<DocumentSnapshot>) {
        val listaArticulos:MutableList<Articulo> = mutableListOf()
        documents.forEach {
            listaArticulos.add((Articulo(it.id
                ,it["nombre"].toString()
                ,it["categoria"].toString()
                ,it["comprado"] as Boolean
            )))
        }
        anclarDatosRecyclerView(listaArticulos)
        if(esPropietario!!)
            anclarClickListener()
    }

    private fun anclarDatosRecyclerView(listaArticulos: MutableList<Articulo>) {
        rv_articulos.layoutManager=LinearLayoutManager(context)
        adapterCesta=adapter_cesta(listaArticulos,esPropietario!!)
        rv_articulos.adapter=adapterCesta
    }

    private fun anclarClickListener() {
        val db= Firebase.firestore
        adapterCesta.setOnItemClickListener(object : adapter_cesta.ClickListener {
            override fun onItemClick(v: View, position: Int) {
                //Toast.makeText(context, "Clicked: ${adapterCesta.getItem(position)?.nombre.toString()}", Toast.LENGTH_SHORT).show()
            }
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT) ) {
            override fun onMove(v: RecyclerView, h: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(h: RecyclerView.ViewHolder, dir: Int) {
                val articuloParaBorrar: Articulo? =adapterCesta.getItem(h.adapterPosition)
                db.collection("articulos").document(articuloParaBorrar!!.id_articulo)
                    .delete()
                    .addOnSuccessListener {
                        adapterCesta.removeAt(h.adapterPosition)
                        Toast.makeText(context, "Se ha borrado ${articuloParaBorrar.nombre}", Toast.LENGTH_SHORT).show()
                    }
            }
        }).attachToRecyclerView(rv_articulos)

    }

    private fun createAddArticleDialog(){
        val customDialog= Dialog(requireContext())
        customDialog.setContentView(R.layout.custom_dialog_add_article)
        customDialog.window?.setBackgroundDrawable(getDrawable(requireContext(),R.drawable.background_dialog_logout))
        customDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        customDialog.findViewById<Button>(R.id.bt_dialog_add_article).setOnClickListener {
            val etNombreArticulo=customDialog.findViewById<EditText>(R.id.et_dialog_idcesta_name)
            val etCategoriaArticulo=customDialog.findViewById<EditText>(R.id.et_dialog_article_category)
            if(etCategoriaArticulo.text.isNotEmpty()&&etNombreArticulo.text.isNotEmpty()){
                addNewArticle(etNombreArticulo.text.toString(),etCategoriaArticulo.text.toString())
                customDialog.dismiss()
            }
        }

        customDialog.findViewById<Button>(R.id.bt_dialog_cancel_own_cesta).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun addNewArticle(nombreArticulo:String,categoriaArticulo:String){
        val db= Firebase.firestore
        val articulo= hashMapOf(
            "idCesta" to idCesta,
            "nombre" to nombreArticulo,
            "categoria" to categoriaArticulo,
            "comprado" to false
        )
        db.collection("articulos").add(articulo)
            .addOnSuccessListener {
                   descargarDatosRecyclerView()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param id Parameter 1.
         * @param mio Parameter 2.
         * @return A new instance of fragment cesta.
         */
        @JvmStatic
        fun newInstance(id: String, mio: Boolean) =
            cesta().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                    putBoolean(ARG_PARAM2, mio)
                }
            }
    }
}