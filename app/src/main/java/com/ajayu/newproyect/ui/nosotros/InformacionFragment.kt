package com.ajayu.newproyect.ui.nosotros

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ajayu.newproyect.R
import com.ajayu.newproyect.otros.AnimateView
import kotlinx.android.synthetic.main.fragment_informacion.*

class InformacionFragment : Fragment() {

    private lateinit var informacionViewModel: InformacionViewModel
    private val animateView = AnimateView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        informacionViewModel = ViewModelProvider(this).get(InformacionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_informacion, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        informacionViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_info.setOnClickListener {
            animateView.scaleView(btn_info,1f,1.15f)
            animateView.scaleView(btn_info,1.15f,1f)

            val url = "http://www.ajayufest.com"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        btn_manual2.setOnClickListener {
            animateView.scaleView(btn_manual2,1f,1.15f)
            animateView.scaleView(btn_manual2,1.15f,1f)

            val url = "http://www.ajayufest.com/p/manual-de-uso-ajayukuna.html?m=1"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

    }
}
