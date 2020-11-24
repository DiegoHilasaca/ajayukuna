package com.ajayu.newproyect.ui.animaciones

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajayu.newproyect.*
import com.ajayu.newproyect.model.AnimationModel
import com.ajayu.newproyect.adapters.AnimationsViewAdapter
import com.ajayu.newproyect.otros.AnimateView
import com.ajayu.newproyect.crud.CrudPhoto
import com.ajayu.newproyect.crud.CrudAnimations
import kotlinx.android.synthetic.main.fragment_animaciones.*
import kotlinx.android.synthetic.main.fragment_animaciones.create_animation
import kotlinx.android.synthetic.main.fragment_animaciones.recycle_animation
import java.io.File
import java.io.File.*

private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)

@Suppress("DEPRECATION")
class AnimacionesFragment : Fragment(),
    AnimationsViewAdapter.OnProyectClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var animationsList = ArrayList<AnimationModel>()

    private var animationsPath=""
    private var APP_NAME="Ajayukuna"
    private val PROYECTS= "Animaciones"

    val animateView= AnimateView()
    val crud = CrudPhoto()
    val crudAnimations = CrudAnimations()

    private var deletePath =""
    private var deletePosition=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =inflater.inflate(R.layout.fragment_animaciones, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()){
            animationsPath = Environment.getExternalStorageDirectory().absolutePath + separator + APP_NAME + separator + PROYECTS
            crudAnimations.createAppPath()
            loadRecycleViewProyect()
            Log.e("onViewCreated","all permisions granted")
        }else{
            requestPermissions(REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        create_animation.setOnClickListener{

            animateView.scaleView(create_animation,1f,1.25f)
            animateView.scaleView(create_animation,1.25f,1f)
            Handler().postDelayed({
                showDialog()
            },200)
        }
        delete_animation.setOnClickListener {
            animateView.scaleView(delete_animation,1f,1.25f)
            animateView.scaleView(delete_animation,1.25f,1f)
            Handler().postDelayed({
                if (deletePath=="") Toast.makeText(context,"Seleccione una Animacion",Toast.LENGTH_LONG).show()
                else showDialogDelete()
            },200)

        }
        delete_select_animation.setOnClickListener {
            select_animation.text = ""
            deletePath=""
            delete_select_animation.visibility=View.INVISIBLE
            Toast.makeText(context,"Has eliminado la seleccion",Toast.LENGTH_SHORT).show()
        }

        crudAnimations.deleteEmptyAnimation(this.animationsPath)
    }


    fun loadRecycleViewProyect(){
        animationsList = ArrayList()

        crudAnimations.addAnimation(animationsPath,animationsList)

        viewManager = GridLayoutManager(context, 4)
        viewAdapter = AnimationsViewAdapter(
            this.requireContext(),
            this,
            animationsList
        )

        recyclerView = recycle_animation.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        recycle_animation.itemAnimator
    }



    override fun onItemClick(item: AnimationModel, position: Int, itemView: View){
        Toast.makeText(context,item.path, Toast.LENGTH_SHORT).show()

        if (itemView.isPressed) {
            animateView.scaleView(itemView, 1f, 1.25f)
            animateView.scaleView(itemView, 1.25f, 1f)
        }
        Handler().postDelayed({
            val intent = Intent(context, CameraActivity::class.java)
            intent.putExtra("path", item.path)
            Log.e("path",item.path)
            intent.putExtra("nombreProyecto",item.path.substring(animationsPath.length+1))
            Log.e("nombreproyecto",item.path.substring(animationsPath.length+1))
            startActivity(intent)
            activity?.finish()
            activity?.overridePendingTransition(R.animator.slide_left,R.animator.slide_right)
        },200)

    }

    override fun onItemLongClick(item: AnimationModel, position: Int, itemView: View) {
        deletePath=(animationsList[position].path)
        deletePosition=position
        if (itemView.isPressed) {
            animateView.scaleView(itemView, 1f, 1.25f)
            animateView.scaleView(itemView, 1.25f, 1f)
        }
        Toast.makeText(context,"$deletePath ha sido seleccionado",Toast.LENGTH_LONG).show()
        delete_select_animation.visibility=View.VISIBLE
        select_animation.text=item.path.substringAfterLast("/")
    }



    fun showDialog(){
        val builder = context?.let { AlertDialog.Builder(it) }
        val inflater = layoutInflater
        val view : View
        view = inflater.inflate(R.layout.new_proyect_dialog,null)

        builder?.setView(view)

        val dialog = builder?.create()
        dialog?.show()

        val aceptProyect1 = dialog?.findViewById<Button>(R.id.acept_proyect)
        val spinnerFps= dialog?.findViewById<Spinner>(R.id.fps)
        val arrayFps= arrayOf("10","12","20","24")
        val arrayAdapter= context?.let { ArrayAdapter(it,R.layout.spinner_items,arrayFps) }
        spinnerFps?.adapter=arrayAdapter

        aceptProyect1?.setOnClickListener(){
            val nameProyect1 = dialog.findViewById<EditText>(R.id.name_proyect)
            val nombreProyect = nameProyect1?.text.toString()
            val selectSpin= spinnerFps?.selectedItem.toString()
            val nombreProyecto =selectSpin+nombreProyect
            Log.e("proyectoCreado",nombreProyecto)
            crudAnimations.createAnimation(animationsPath, nombreProyecto)
            context?.let { it1 -> crudAnimations.afterCreated(it1,nombreProyecto,animationsPath) }
            dialog.dismiss()
            loadRecycleViewProyect()

            Toast.makeText(context,"Animacion $nombreProyecto ha sido creado",Toast.LENGTH_LONG).show()
        }

    }

    fun showDialogDelete(){
        val builder = context?.let { AlertDialog.Builder(it) }
        val inflater = layoutInflater
        val view : View
        view = inflater.inflate(R.layout.delete_proyect_dialog,null)

        builder?.setView(view)

        val dialog = builder?.create()
        dialog?.show()

        val aceptDelete = dialog?.findViewById<Button>(R.id.acept_delete)
        val cancelDelete= dialog?.findViewById<Button>(R.id.cancel_delete)

        aceptDelete?.setOnClickListener{
            val deleteFile=File(deletePath)
            Log.e("deleteFile",deleteFile.absolutePath+"//"+ deletePosition.toString())
            deleteFile.deleteRecursively()
            animationsList.removeAll(animationsList)
            crudAnimations.addAnimation(animationsPath,animationsList)
            viewAdapter.notifyItemRemoved(deletePosition)
            dialog.dismiss()

            //limpia la seleccion
            select_animation.text = ""
            deletePath=""
            delete_select_animation.visibility=View.INVISIBLE

            Toast.makeText(context,"$deletePath ha sido borrado",Toast.LENGTH_LONG).show()
        }
        cancelDelete?.setOnClickListener{

            dialog.dismiss()
            Toast.makeText(context,"Borrado cancelado",Toast.LENGTH_LONG).show()
            deletePath=""
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS){
            Toast.makeText(context,requestCode.toString()+ REQUEST_CODE_PERMISSIONS.toString(),Toast.LENGTH_SHORT)
            Log.e("request",requestCode.toString()+ "_--"+ REQUEST_CODE_PERMISSIONS.toString())
            if (allPermissionsGranted()){
                animationsPath = Environment.getExternalStorageDirectory().absolutePath + separator + APP_NAME + separator + PROYECTS
                crudAnimations.createAppPath()
                loadRecycleViewProyect()
            }else{
                Toast.makeText(context,"Permisions no granted by the user.", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }


    private fun allPermissionsGranted(): Boolean {

        for (permission in REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission( requireContext(), permission ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("permision",
                    context?.let { ActivityCompat.checkSelfPermission(it,permission).toString() } +" holi "+ PackageManager.PERMISSION_GRANTED.toString() )
                return false
            }
        }
        return true
    }

}
