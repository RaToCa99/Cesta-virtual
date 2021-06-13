package com.example.cestavirtual.cestas.child_view

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cestavirtual.R
import com.example.cestavirtual.cestas.adapter.adapter_cestas
import com.example.cestavirtual.data.Cesta
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_cestas_hijo.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "idUsuario"
private const val ARG_PARAM2 = "esPropietario"

/**
 * A simple [Fragment] subclass.
 * Use the [Cestas_hijo.newInstance] factory method to
 * create an instance of this fragment.
 */
class Cestas_hijo : Fragment() {
    private var idUsuario: String? = null
    private var esPropietario: Boolean = true
    private var adapterCesta = adapter_cestas(mutableListOf(), false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getString(ARG_PARAM1)
            esPropietario = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cestas_hijo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_cestas.layoutManager = LinearLayoutManager(context)
        rv_cestas.adapter = adapterCesta
        fab_add_cesta.setOnClickListener {
            if (esPropietario)
                showAddOwnCestaDialog()
            else
                showAddForeignCestaDialog()
        }
        cargarDatos()
    }

    private fun showAddOwnCestaDialog() {
        val customDialog = Dialog(requireContext())
        customDialog.setContentView(R.layout.custom_dialog_add_own_cesta)
        customDialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.background_dialog_logout
            )
        )
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customDialog.findViewById<Button>(R.id.bt_dialog_save_own_cesta).setOnClickListener {
            val etNombreCesta = customDialog.findViewById<EditText>(R.id.et_dialog_cesta_name)
            if (etNombreCesta.text.isNotEmpty()) {
                addOwnCesta(etNombreCesta.text.toString())
                customDialog.dismiss()
            }

        }

        customDialog.findViewById<Button>(R.id.bt_dialog_cancel_own_cesta).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun showAddForeignCestaDialog() {
        val customDialog = Dialog(requireContext())
        customDialog.setContentView(R.layout.custom_dialog_add_foreign_cesta)
        customDialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.background_dialog_logout
            )
        )
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customDialog.findViewById<Button>(R.id.bt_dialog_save_foreign_cesta).setOnClickListener {
            val etIdCesta = customDialog.findViewById<EditText>(R.id.et_dialog_idcesta_name)
            if (etIdCesta.text.isNotEmpty()) {
                addRelationCesta(etIdCesta.text.toString())
                customDialog.dismiss()
            }

        }

        customDialog.findViewById<Button>(R.id.bt_dialog_cancel_foreign_cesta).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun addOwnCesta(nombreCesta: String) {
        val db = Firebase.firestore
        db.collection("cestas")
            .add(hashMapOf("nombre" to nombreCesta))
            .addOnSuccessListener {
                addRelationCesta(it.id)
            }
    }

    private fun addRelationCesta(idCesta: String) {
        val db = Firebase.firestore
        val nuevaRelacion = hashMapOf(
            "idCesta" to idCesta,
            "idUsuario" to db.document("usuario/$idUsuario"),
            "esPropietario" to esPropietario
        )

        db.collection("tabla_usuarios_cesta")
            .add(nuevaRelacion)
            .addOnSuccessListener {
                cargarDatos()
            }
    }

    fun cargarDatos() {
        val db = Firebase.firestore
        val usuario = Firebase.firestore.collection("usuario").document(idUsuario!!)

        db.collection("tabla_usuarios_cesta")
            .whereEqualTo("idUsuario", usuario)
            .whereEqualTo("esPropietario", esPropietario)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.documents.isNotEmpty())
                        cargarCestas(task.result?.documents!!)
                }
            }
    }

    private fun cargarCestas(documents: MutableList<DocumentSnapshot>) {
        val db = Firebase.firestore
        val listaCestas: MutableList<Cesta> = mutableListOf()
        val listaTareas: MutableList<Task<DocumentSnapshot>> = mutableListOf()

        documents.forEach {
            val task = db.collection("cestas").document(it["idCesta"].toString()).get()
            listaTareas.add(task)
        }
        val allTasks: Task<List<DocumentSnapshot>> = Tasks.whenAllSuccess(listaTareas)

        allTasks.addOnSuccessListener {
            for (doc in it) {
                listaCestas.add(Cesta(doc.id, doc["nombre"].toString()))
            }
            //   Toast.makeText(context, it[0]["nombre"].toString(), Toast.LENGTH_LONG).show()

            rv_cestas.layoutManager = LinearLayoutManager(context)
            if (listaCestas.isNotEmpty()) {
                adapterCesta = adapter_cestas(listaCestas, esPropietario)
                rv_cestas.adapter = adapterCesta
                anclarClickListener()
            }

        }
    }

    private fun anclarClickListener() {
        adapterCesta.setOnItemClickListener(object : adapter_cestas.ClickListener {
            override fun onItemClick(v: View, position: Int) {
                // Toast.makeText(context, "Clicked: ${adapterCesta.getItem(position)?.nombre.toString()}", Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                bundle.putString("idCesta", adapterCesta.getItem(position).idCesta)
                bundle.putBoolean(ARG_PARAM2, esPropietario)
                findNavController().navigate(R.id.navigate_cestas_to_cesta, bundle)
            }

            override fun deleteClick(cestaPos: Int) {
                showDialogDeleteConfirmation(adapterCesta.getItem(cestaPos), cestaPos)
            }

            override fun editClick(cesta: Cesta) {
                showDialogEditCesta(cesta)
            }

        })

        adapterCesta.setItemLongClickListener(object : adapter_cestas.ItemLongClickListener {
            override fun onItemLongClick(v: View, position: Int) {
                Toast.makeText(
                    context,
                    "El ID de la cesta se ha copiado en el portapapeles",
                    Toast.LENGTH_SHORT
                ).show()
                if (Build.VERSION.SDK_INT >= 29)
                    vibrar()
                copiarPortapapeles(adapterCesta.getItem(position).idCesta)
            }
        })

    }

    fun vibrar() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_DOUBLE_CLICK))

    }

    fun copiarPortapapeles(idCesta: String) {
        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("idCesta", idCesta))
    }

    fun showDialogDeleteConfirmation(cesta: Cesta, cestaPos: Int) {
        val customDialog = Dialog(requireContext())
        customDialog.setContentView(R.layout.cesta_delete_confirmation)
        customDialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.background_dialog_logout
            )
        )
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customDialog.findViewById<TextView>(R.id.tv_dialog_delete_confirmation).text =
            "¿Esta seguro que quiere borrar la cesta: ${cesta.nombre}?"
        customDialog.findViewById<Button>(R.id.bt_dialog_cesta_confirmation_yes)
            .setOnClickListener {
                if (esPropietario) borrarCestaPropia(cesta.idCesta, cestaPos)
                else borrarCestaAjena(cesta, cestaPos)
                customDialog.dismiss()
            }

        customDialog.findViewById<Button>(R.id.bt_dialog_cesta_confirmation_no).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()

    }

    fun showDialogEditCesta(cesta: Cesta) {
        val customDialog = Dialog(requireContext())
        customDialog.setContentView(R.layout.custom_dialog_edit_cesta)
        customDialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.background_dialog_logout
            )
        )
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customDialog.findViewById<EditText>(R.id.et_dialog_cesta_name_editar).setText(cesta.nombre)
        customDialog.findViewById<Button>(R.id.bt_dialog_save_own_edit_cesta).setOnClickListener {
            val etEditCestaName =
                customDialog.findViewById<EditText>(R.id.et_dialog_cesta_name_editar)
            if (etEditCestaName.text.isNotEmpty()) {
                actualizarCesta(cesta.idCesta, etEditCestaName.text.toString())
                customDialog.dismiss()
            }
        }

        customDialog.findViewById<Button>(R.id.bt_dialog_cancel_edit_cesta).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun actualizarCesta(idCesta: String, nuevoNombre: String) {
        val db = Firebase.firestore
        db.collection("cestas").document(idCesta)
            .update("nombre", nuevoNombre).addOnSuccessListener {
                cargarDatos()
            }
    }

    private fun borrarCestaAjena(cesta: Cesta, cestaPos: Int) {
        val db = Firebase.firestore

        db.collection("tabla_usuarios_cesta")
            .whereEqualTo("esPropietario", false)
            .whereEqualTo("idCesta", cesta.idCesta)
            .whereEqualTo("idUsuario", db.document("usuario/$idUsuario"))
            .get()
            .addOnSuccessListener {
                db.collection("tabla_usuarios_cesta").document(it.documents[0].id)
                    .delete().addOnSuccessListener {
                        adapterCesta.removeAt(cestaPos)
                    }
            }


    }

    private fun borrarCestaPropia(idCesta: String, cestaPos: Int) {
        val db = Firebase.firestore
        db.collection("cestas")
            .document(idCesta).delete().addOnSuccessListener {
                db.collection("tabla_usuarios_cesta")
                    .whereEqualTo("idCesta", idCesta)
                    .get()
                    .addOnSuccessListener { documentos ->
                        documentos.forEach {
                            it.reference.delete()
                        }
                        borrarArticulos(idCesta)
                        adapterCesta.removeAt(cestaPos)
                    }
            }
    }

    private fun borrarArticulos(idCesta: String) {
        val db = Firebase.firestore
        db.collection("articulos")
            .whereEqualTo("idCesta", idCesta)
            .get()
            .addOnSuccessListener { documentos ->
                documentos.forEach {
                    it.reference.delete()
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param idUsuario Parameter 1.
         * @return A new instance of fragment Cestas_hijo.
         */
        @JvmStatic
        fun newInstance(idUsuario: String, esPropietario: Boolean) =
            Cestas_hijo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, idUsuario)
                    putBoolean(ARG_PARAM2, esPropietario)
                }
            }
    }
}