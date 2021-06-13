package com.example.cestavirtual.login.details

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cestavirtual.MainActivity
import com.example.cestavirtual.R
import com.example.cestavirtual.crypto.SecurePreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "leerPreferencias"

/**
 * A simple [Fragment] subclass.
 * Use the [login.newInstance] factory method to
 * create an instance of this fragment.
 */
class login : Fragment() {
    private var leerPreferencias: Boolean? = null
    private val USERNAME = "username"
    private val PASSWORD = "password"
    private val IDUSER="idUsuario"
    private val db=Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            leerPreferencias = it.getBoolean(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val preferences=SecurePreferences(context,"almacenamientoLogin","cestaVirtual",true)

        if(leerPreferencias!=false){
            if( preferences.containsKey(USERNAME)&&preferences.containsKey(PASSWORD)&&preferences.containsKey(IDUSER)){
                et_username.setText(preferences.getString(USERNAME))
                et_password.setText(preferences.getString(PASSWORD))
                chb_remember.isChecked=true
                val bundle=Bundle()
                bundle.putString("idUsuario",preferences.getString(IDUSER))
               // Toast.makeText(context, "En el login cuando se lee el IdUser->${preferences.getString(IDUSER)}", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.navigate_login_to_cestas,bundle)
            }
        }else
            deletePreferences(preferences)

        btn_registrarse_login.setOnClickListener{
            findNavController().navigate(R.id.navigate_login_register)
        }

        bt_login.setOnClickListener {
                if (comprobarCampos()) {
                    hacerConsulta(et_username.text.toString(), et_password.text.toString())
                }
        }

        chb_remember.setOnClickListener {
            if(!chb_remember.isChecked){
                deletePreferences(preferences)
            }
        }
    }

    private fun deletePreferences(preferences: SecurePreferences) {
        preferences.removeValue(USERNAME)
        preferences.removeValue(PASSWORD)
        preferences.removeValue(IDUSER)
    }

    private fun comprobarCampos() :Boolean{
        return if(et_username.text.isNullOrEmpty()|| et_password.text.isNullOrEmpty()){
            tv_advertise.text="Debe rellenar todos los campos"
            tv_advertise.visibility=View.VISIBLE
            false
        }else{
            tv_advertise.visibility=View.GONE
            true
        }
    }

    private fun guardarPrecerences(idUsuario:String){
        val preferences=SecurePreferences(context,"almacenamientoLogin","cestaVirtual",true)
        preferences.put(USERNAME, et_username.text.toString())
        preferences.put(PASSWORD, et_password.text.toString())
        preferences.put(IDUSER,idUsuario)

    }

    private fun hacerConsulta(usuario:String,contrasenna:String){
        db.collection("usuario")
            .whereEqualTo("nombreUsuario",usuario)
            .whereEqualTo("contrasenna",contrasenna)
            .get()
            .addOnCompleteListener {
                task->
               if(task.isSuccessful){
                    if(task.result!!.isEmpty){
                        Toast.makeText(context, "No se encuentra el usuario", Toast.LENGTH_LONG).show()
                        mostrarErrorContrasenna()
                    }else{
                        if (chb_remember.isChecked) {
                            guardarPrecerences(task.result!!.documents[0].id)
                          //  Toast.makeText(context, "Usuario-> ${et_username.text} Contraseña-> ${et_password.text}", Toast.LENGTH_LONG).show()
                        }
                        val bundle=Bundle()
                        bundle.putString("idUsuario",task.result!!.documents[0].id)
                        findNavController().navigate(R.id.navigate_login_to_cestas,bundle)
                    }
                } else{
                    Toast.makeText(context, "Error en el login", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun mostrarErrorContrasenna(){
        tv_advertise.text="La contraseña o el usuario es incorrecta"
        tv_advertise.visibility=View.VISIBLE

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param leerPreferencias Parameter 1.
         * @return A new instance of fragment login.
         */
        @JvmStatic
        fun newInstance(leerPreferencias: Boolean) =
            login().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, leerPreferencias)
                }
            }
    }
}