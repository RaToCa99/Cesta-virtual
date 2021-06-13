package com.example.cestavirtual.cestas.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cestavirtual.PageController
import com.example.cestavirtual.R
import com.example.cestavirtual.cestas.child_view.Cestas_hijo
import kotlinx.android.synthetic.main.fragment_cestas.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "idUsuario"

/**
 * A simple [Fragment] subclass.
 * Use the [cestas.newInstance] factory method to
 * create an instance of this fragment.
 */
class cestas : Fragment() {
    private var idUsuario: String? = null
    private var fragmentCestasPropia=Cestas_hijo()
    private var fragmentCestasAjenas=Cestas_hijo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idUsuario = it.getString(ARG_PARAM1)
        }
     //   Toast.makeText(context, "En cestas se encuentra el usuario $idUsuario", Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cestas, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpTabs()
    }

    private fun setUpTabs(){
        val adapter=PageController(childFragmentManager)
        val bundle1=Bundle()
        val bundle2=Bundle()
        bundle1.putString("idUsuario",idUsuario)
        bundle2.putString("idUsuario",idUsuario)
        bundle1.putBoolean("esPropietario",true)
        bundle2.putBoolean("esPropietario",false)

        fragmentCestasPropia.arguments=bundle1
        fragmentCestasAjenas.arguments=bundle2

        adapter.addFragment(fragmentCestasPropia,"Mis cestas")
        adapter.addFragment(fragmentCestasAjenas,"Cestas compartidas")
        vp_cestas.adapter=adapter
        tabsCestas.setupWithViewPager(vp_cestas)

    }

    fun recargarCestas(){
        fragmentCestasAjenas.cargarDatos()
        fragmentCestasPropia.cargarDatos()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param idUsuario Parameter 1.
         * @return A new instance of fragment cestas.
         */
        @JvmStatic
        fun newInstance(idUsuario: String) =
            cestas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, idUsuario)
                }
            }
    }
}