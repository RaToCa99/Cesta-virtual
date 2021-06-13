package com.example.cestavirtual.register.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cestavirtual.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [register.newInstance] factory method to
 * create an instance of this fragment.
 */
class register : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boton_registrarse.setOnClickListener {
            if(comprobarCampos())
                comprobarUsuario()
        }
    }

    private fun comprobarUsuario() {
        val db = Firebase.firestore
        db.collection("usuario")
            .whereEqualTo("nombreUsuario",et_username.text.toString())
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    hacerRegistro()
                }

            }
    }

    private fun hacerRegistro() {
        val db = Firebase.firestore
        db.collection("usuario")
            .add(
                hashMapOf(
                    "nombreUsuario" to et_username.text.toString(),
                    "contrasenna" to et_password_register.text.toString(),
                    "correoElectronico" to et_email.text.toString(),
                    "nombreApellidos" to et_name_lastname.text.toString()
                )
            )
            .addOnSuccessListener {
                Toast.makeText(context, "Se ha registrado satisfactoriamente", Toast.LENGTH_LONG)
                    .show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se ha podido registrar", Toast.LENGTH_LONG).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    private fun comprobarCampos() :Boolean{
        return if(et_username.text.isNullOrEmpty()
            || et_name_lastname.text.isNullOrEmpty()
            || et_email.text.isNullOrEmpty()
            ||et_password_register.text.isNullOrEmpty()) {
            tv_caution.text="Debe rellenar todos los campos"
            tv_caution.visibility=View.VISIBLE
            false
        }else if(et_password_register.text.toString()!= et_password_register2.text.toString()){
            tv_caution.text="Las contraseñas deben coincidir"
            tv_caution.visibility=View.VISIBLE
            false
        }else{
            tv_caution.visibility=View.GONE

            true
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment register.
         */
        @JvmStatic
        fun newInstance() =
            register().apply {
                arguments = Bundle().apply {

                }
            }
    }
}