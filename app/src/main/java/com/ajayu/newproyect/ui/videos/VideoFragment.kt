package com.ajayu.newproyect.ui.videos

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajayu.newproyect.ExoActivity
import com.ajayu.newproyect.model.VideoModel
import com.ajayu.newproyect.R
import com.ajayu.newproyect.adapters.VideoViewAdapter
import com.ajayu.newproyect.crud.CrudPhoto
import com.ajayu.newproyect.crud.CrudVideo
import com.ajayu.newproyect.otros.AnimateView
import kotlinx.android.synthetic.main.fragment_animaciones.*
import kotlinx.android.synthetic.main.fragment_videos.*
import java.io.File

@Suppress("DEPRECATION")
class VideoFragment : Fragment(), VideoViewAdapter.OnVideoClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var videoList = ArrayList<VideoModel>()

    private var videoPath=""
    private var APP_NAME="Ajayukuna"
    private var APP_PATH = Environment.getExternalStorageDirectory().absolutePath
    private val VIDEOS= "Videos"
    private val ANIMACIONES = "Animaciones"

    val animateView= AnimateView()
    val crud = CrudPhoto()
    val crudVideo =  CrudVideo()

    private var deletePath =""
    private var deletePosition=0
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =inflater.inflate(R.layout.fragment_videos, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        videoPath = APP_PATH + File.separator + APP_NAME + File.separator + VIDEOS

        loadRecycleViewVideo()

        delete_video.setOnClickListener {
            animateView.scaleView(delete_video,1f,1.25f)
            animateView.scaleView(delete_video,1.25f,1f)
            Handler().postDelayed({
                if (deletePath=="") Toast.makeText(context,"Seleccione una Animacion",Toast.LENGTH_LONG).show()
                else showDialogDelete()
            },200)

        }
        delete_select_video.setOnClickListener {
            select_video.text = ""
            deletePath=""
            delete_select_video.visibility=View.INVISIBLE
            Toast.makeText(context,"Has eliminado la seleccion",Toast.LENGTH_SHORT).show()
        }

        crudVideo.deleteEmptyVideo(this.videoPath)
    }

    private fun loadRecycleViewVideo(){
        videoList = ArrayList()

        crudVideo.addVideo(videoPath,videoList)

        viewManager = GridLayoutManager(context, 4)
        viewAdapter = VideoViewAdapter(
            this.requireContext(),
            this,
            videoList
        )

        recyclerView = recycle_video.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        recycle_video.itemAnimator
    }



    override fun onItemClick(item: VideoModel, position: Int, itemView: View){
        Toast.makeText(context,item.path, Toast.LENGTH_SHORT).show()

        if (itemView.isPressed) {
            animateView.scaleView(itemView, 1f, 1.25f)
            animateView.scaleView(itemView, 1.25f, 1f)
        }

        Handler().postDelayed({
                val intent = Intent(context, ExoActivity::class.java)
                intent.putExtra("videoPath", item.path)
                Log.e("path",item.path)
                startActivity(intent)
                //activity?.finish()

            navController.popBackStack(R.id.nav_videos,true)

            /*
            val bundle = bundleOf("videoPath" to item.path)
                navController.navigate(R.id.action_nav_videos_to_exoActivity2,bundle)
            */
            activity?.overridePendingTransition(R.animator.slide_left,R.animator.slide_right)
        },200)

    }

    override fun onItemLongClick(item: VideoModel, position: Int, itemView: View) {
        deletePath=(videoList[position].path)
        deletePosition=position
        if (itemView.isPressed) {
            animateView.scaleView(itemView, 1f, 1.25f)
            animateView.scaleView(itemView, 1.25f, 1f)
        }
        Toast.makeText(context,"$deletePath ha sido seleccionado", Toast.LENGTH_LONG).show()
        delete_select_video.visibility=View.VISIBLE
        select_video.text=item.path.substringAfterLast("/")
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
            val deleteFile= File(deletePath)
            Log.e("deleteFile",deleteFile.absolutePath+"//"+ deletePosition.toString())
            deleteFile.delete()
            videoList.removeAll(videoList)
            crudVideo.addVideo(videoPath,videoList)
            viewAdapter.notifyItemRemoved(deletePosition)
            dialog.dismiss()

            //limpia la seleccion
            select_video.text = ""
            deletePath=""
            delete_select_video.visibility=View.INVISIBLE

            Toast.makeText(context,"$deletePath ha sido borrado", Toast.LENGTH_LONG).show()
        }
        cancelDelete?.setOnClickListener{

            dialog.dismiss()
            Toast.makeText(context,"Borrado cancelado", Toast.LENGTH_LONG).show()
            deletePath=""
        }

    }

}