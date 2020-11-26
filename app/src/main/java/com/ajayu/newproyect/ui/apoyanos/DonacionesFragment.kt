package com.ajayu.newproyect.ui.apoyanos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ajayu.newproyect.R
import com.ajayu.newproyect.otros.AnimateView
import kotlinx.android.synthetic.main.fragment_apoyo.*

class DonacionesFragment : Fragment() {

    private lateinit var donacionesViewModel: DonacionesViewModel
    private val animateView =AnimateView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        donacionesViewModel =
                ViewModelProvider(this).get(DonacionesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_apoyo, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        donacionesViewModel.text.observe(viewLifecycleOwner,{
            textView.text = it
        })
        return root


        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_paypal.setOnClickListener {

            animateView.scaleView(btn_paypal,1f,1.15f)
            animateView.scaleView(btn_paypal,1.15f,1f)

            val url = "http://www.ajayufest.com"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }
}
