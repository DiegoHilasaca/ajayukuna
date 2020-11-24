package com.ajayu.newproyect.ui.nosotros

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InformacionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "\n" +
                "INFORMACIÓN \n" +
                "\n" +
                "\n" +
                "Stop Motion AJAYUKUNA, es un aplicativo para dispositivos móviles, que permite crear películas animadas de manera fácil, rápida y divertida.\n" +
                "\n" +
                "Nace como iniciativa del Festival Internacional de Animación Ajayu, elaborado por Sapa Inti y la Asociación Cinematográfica Ajayu, organizaciones que colaboran conjuntamente en reforzar la identidad cultural a través de la producción, difusión, promoción cinematográfica, gestión cultural y otras actividades educativas relacionadas a la innovación y experimentación audiovisual, sobre todo de cine animado.\n" +
                "\n" +
                "Descarga Stop Motion Ajayu y dale vida a historias increíbles. \n" +
                "\n" +
                "ajayufest@gmail.com - animacionajayu@gmail.com \n" +
                "\n" +
                "+51 989551618"+
                "\n" +
                "\n" +
                "Este Aplicativo fue elaborado gracias al apoyo del Ministerio de Cultura del Perú.\n" +
                "\n" +
                "El software que se utilizó para el desarrollo de este aplicativo fue Android Studio\n" +
                "\n" +
                "Desarrollado por: Diego Hilasaca Quico\n" +
                "\n" +
                "Agradecemos a los organizadores del Festival Internacional de Animación Ajayu por el compromiso y dedicación con este proyecto, bajo la dirección de Henry Ticona Huaquisto y al equipo de producción: Ruth Fiorela Flores Jove, Brigitte Antonella Tapia Figueroa, Gimena Ancasi Caceres, Yanira Caceres Calvo y Wilson Xavier Ponce Vilca a quien se lo dedicamos con especial cariño.\n" +
                "\n" +
                "Sapa Inti - Asociación Cinematográfica Ajayu"+
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n"
    }
    val text: LiveData<String> = _text
}