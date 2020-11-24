package com.ajayu.newproyect

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ajayu.newproyect.model.PhotoModel
import com.ajayu.newproyect.adapters.PhotoViewAdapter
import com.ajayu.newproyect.crud.CrudPhoto
import com.ajayu.newproyect.databinding.ActivityCameraBinding
import com.ajayu.newproyect.otros.AnimateView
import com.ajayu.newproyect.otros.DrawLineCanvas
import com.ajayu.newproyect.otros.Export
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.photo_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)

const val APP_NAME = "Ajayukuna"
const val VIDEO_PATH = "Videos"

class CameraActivity : AppCompatActivity(),
    PhotoViewAdapter.OnPhotoClickListener {
    //Recycleview
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var listPhoto = ArrayList<PhotoModel>()

    //Camerax
    private lateinit var binding : ActivityCameraBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView : PreviewView

    val animateView= AnimateView()
    val crudPhoto = CrudPhoto()

    private var scrollPosition=1
    private var defaultPhotoExtension = ".jpg"
    private var photoExtension = ".jpg"
    var btnChange="AnimPrev"
    //paths
    private var videoPath = ""
    private var proyectName="" //nombre del proyecto
    private var proyectPath="" //path del proyecto
    private var selectedPathParent=""
    private var fps = 0

    private fun loadRecycle(path: String, scrollPosition:Int ){
        listPhoto= ArrayList()
        addPhoto(path)

        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        viewManager.scrollToPosition(scrollPosition-3)

        viewAdapter = PhotoViewAdapter(
            this,
            this,
            listPhoto
        )

        recyclerView_photos.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            recyclerView_photos.setHasFixedSize(true)

        }
    }

    private fun addPhoto(path: String){
        //agrega foto al recycleAdapter
        var num = 0
        File(path).walkTopDown().sortedWith(naturalOrder()).forEach {
            val dir = it.isFile
            when{dir-> {
                num++
                listPhoto.add(
                    PhotoModel(
                        num,
                        it.toString(),
                        false
                    )
                )

                Log.e("listPhoto",listPhoto[num-1].path)

                }
            }
        }

    }
    fun showPrevAnimation(){
        var bitdrawable =  AnimationDrawable()
        var fpsAnimation=0
        when(fps){
            10->{fpsAnimation=100}
            12->{fpsAnimation=83}
            20->{fpsAnimation=50}
            24->{fpsAnimation=40}
        }
        var count=0
        File(proyectPath).walkTopDown().sortedWith(naturalOrder()).forEach {
            val dir = it.isFile
            when{dir->{
                var sample = BitmapFactory.decodeFile(it.toString())
                if (sample != null){
                    sample = Bitmap.createScaledBitmap(sample, sample.width / 2, sample.height / 2, false)
                    bitdrawable.addFrame(BitmapDrawable(resources, sample), fpsAnimation)
                    count++
                }else Log.e("showPrevAnimation","sample is null ")
            }}
        }
        bitdrawable.isOneShot=true
        animPrev.setImageDrawable(bitdrawable)
        val animatable=animPrev.drawable
        if (animatable is Animatable){
            if (animatable.isRunning) {
                animatable.stop()

                bitdrawable.callback=null

            }
            bitdrawable.start()
        }
        val duration=(fpsAnimation*count).toLong()
        Handler().postDelayed({
            animPrev.setImageDrawable(null)
            Toast.makeText(this,"duracion: $duration",Toast.LENGTH_SHORT).show()
        },duration+500)
    }
    private fun showPrev(){

        val timer=Timer()
        var i = -1
        var fpsAnimation=0
        when(fps){
            10->{fpsAnimation=100}
            16->{fpsAnimation=62}
            20->{fpsAnimation=50}
            24->{fpsAnimation=40}
        }
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.e("fpsAnimation",fpsAnimation.toString())
                i++
                if (i < listPhoto.size) {
                    Log.e("list","$i ${listPhoto[i].path}")
                    animPrev.alpha=1f
                    runOnUiThread {
                        val bit=BitmapFactory.decodeFile(listPhoto[i].path)
                        animPrev.setImageBitmap(bit)

                    }

                } else {
                    i = 0
                    timer.cancel()
                    animPrev.alpha=0f
                }
            }

        }, 0, fpsAnimation.toLong())

    }



    private var selectedPath: String=""  // ruta escogida
    //private var photoSize=0
    var itemViewSelected = 0
    override fun onPhotoLongClick(item: PhotoModel, position: Int) {
        btnChange="Delete"
        btn_animPrev.setImageResource(R.drawable.ic_btn_trash)
        animateView.animateCamera(baseContext,take_picture,"green",false)
        selectedPath=item.path
        scrollPosition=position
        selectItem.text=item.path.substringAfterLast("/")
        deleteSelectItem.visibility=View.VISIBLE
        Log.e("onPhotoLongClick","select path-> $selectedPath")
        Toast.makeText(baseContext,"Has seleccionado : $selectedPath",Toast.LENGTH_SHORT).show()
        itemViewSelected=position

    }


    override fun onPhotoClick(item: PhotoModel, position: Int){
        //activityCameraConstraint
        scrollPosition=position

        val aCC = findViewById<ConstraintLayout>(R.id.activity_camera)
        val set = ConstraintSet()
        val imageViewItem = ImageView(this)
        val estado =listPhoto[position].status
        when(estado){

            false->{
                imageViewItem.id=position
                Log.e("id",imageViewItem.id.toString())
                imageViewItem.alpha=0.3f
                imageViewItem.layoutParams=ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)

                Glide.with(this)
                    .load(item.path)
                    .into(imageViewItem)

                aCC.addView(imageViewItem,1)
                set.clone(aCC)
                set.connect(imageViewItem.id,ConstraintSet.TOP,aCC.id,ConstraintSet.TOP)
                set.applyTo(aCC)
                listPhoto[position].status=true

            }
            true->{
                val imageViewDelete = findViewById<ImageView>(position)
                Log.e("imageViewDelete",imageViewDelete?.id.toString())
                aCC.removeView(imageViewDelete)
                listPhoto[position].status=false

            }
        }

    }

    fun traerAlFrente(){
        btn_animPrev.bringToFront()
        delete.bringToFront()
        recyclerView_photos.bringToFront()
        take_picture.bringToFront()
        btn_pen.bringToFront()
        regresar.bringToFront()
        deleteSelectItem.bringToFront()
        btn_options.bringToFront()

    }
    private fun deleteFramesResiduales(){
        val aCC = findViewById<ConstraintLayout>(R.id.activity_camera)

        val photoSize = listPhoto.size
        Log.e("photoSize", photoSize.toString())
        var position=0

        while (photoSize>position) {
            val posStatus=listPhoto[position].status
            if (posStatus) {
                var imageViewDelete = findViewById<ImageView>(position)
                Log.e("imageViewDelete", imageViewDelete?.id.toString())
                aCC.removeView(imageViewDelete)
            }
            position=position.inc()
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //camerax
        binding = DataBindingUtil.setContentView(this,R.layout.activity_camera)
        binding.lifecycleOwner = this
        previewView = binding.previewView
        previewView.post {startCamera()}
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //datos de otro activity
        val objectIntent = intent
        proyectPath = objectIntent.getStringExtra("path")
        proyectName = objectIntent.getStringExtra("nombreProyecto")

        detectFormatImage()
        val fpsProyect=proyectPath.substringAfterLast("/").substring(0,2)
        Log.e("fpsProyect",fpsProyect)
        fps=fpsProyect.toInt()

        nombre_proyecto.text=proyectName.substring(2)
        //tomar foto
        val path= Environment.getExternalStorageDirectory().absolutePath
        videoPath=path+File.separator+APP_NAME+File.separator+ VIDEO_PATH+File.separator
        outputDirectory = getOutputDirectory(this,proyectPath)


        binding.takePicture.setOnClickListener {
            deleteFramesResiduales()
            if(selectedPath==""&&selectItem.text==""){

                animateView.animateCamera(baseContext,take_picture,"white",true)
                Log.e("takePicture","takePicture")
                takePicture()
                Handler().postDelayed({

                    if (listPhoto.size<7){
                        //loadRecycle(path,listPhoto.size)
                        listPhoto.removeAll(listPhoto)
                        addPhoto(proyectPath)

                        //viewAdapter.notifyItemInserted(listPhoto.size)
                        viewAdapter.notifyDataSetChanged()
                        var count = 0
                        while (count < listPhoto.size) {
                            Log.e("notifyitemchanged", count.toString())
                            viewAdapter.notifyItemChanged(count)
                            count++
                        }
                    }else {
                        Log.e("list Antes",listPhoto.size.toString())
                        listPhoto.removeAll(listPhoto)
                        Log.e("list despues",listPhoto.size.toString())
                        addPhoto(proyectPath)
                        Log.e("list ahora",listPhoto.size.toString())
                        viewAdapter.notifyItemInserted(listPhoto.size)
                        viewManager.scrollToPosition(listPhoto.size)
                        var count = 0
                        while (count < listPhoto.size) {
                            Log.e("notifyitemchanged", count.toString())
                            viewAdapter.notifyItemChanged(count)
                            count++
                        }
                    }
                    deleteSelectedItem()

                },2000)

            }else{
                Log.e("addtakepicture","addtakepicture")
                addTakePicture()
                Handler().postDelayed({
                    deleteSelectedItem()
                },2000)
            }
        }
        var estadoDraw=false
        val aCC = findViewById<ConstraintLayout>(R.id.activity_camera)
        val draw= DrawLineCanvas(this)
        draw.id=1000
        aCC.addView(draw)
        draw.visibility= View.INVISIBLE
        btn_pen.alpha=0.5f
        traerAlFrente()
        btn_pen.setOnClickListener {
            when(estadoDraw){
                true->{
                    draw.visibility= View.INVISIBLE
                    btn_pen.alpha=0.5f
                    estadoDraw=false
                }
                false->{
                    draw.visibility= View.VISIBLE
                    estadoDraw=true
                    btn_pen.alpha=1f
                }
            }
        }

        btn_pen.setOnLongClickListener {
            aCC.removeView(draw)
            draw.limpiar()
            aCC.addView(draw)
            traerAlFrente()
            Toast.makeText(this,"La pantalla fue limpiada",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        delete.setOnClickListener {
            if (selectedPath==""&&selectItem.text=="")Toast.makeText(baseContext,"Selecciona un fotograma manteniendolo presionando",Toast.LENGTH_SHORT).show()
            else{
                deletePhoto()
                listPhoto.removeAll(listPhoto)
                addPhoto(proyectPath)
                viewAdapter.notifyItemRemoved(scrollPosition)
                var count=scrollPosition
                while(count<listPhoto.size){
                    viewAdapter.notifyItemChanged(count)
                    count++
                }
                animateView.scaleView(delete,1f,1.25f)
                animateView.scaleView(delete,1.25f,1f)
                //borra la seleccion del item en recyclerview
                deleteSelectedItem()
            }
        }


        binding.btnAnimPrev.setOnClickListener {

            when(btnChange){
                "AnimPrev"->{
                    animateView.scaleView(btn_animPrev,1f,1.25f)
                    showPrevAnimation()
                    Log.e("btnAnimPrev","btnAnimPrev is pressed")
                    animateView.scaleView(btn_animPrev,1.25f,1f)

                }
                "Delete"->{
                    if (selectedPath==""&&selectItem.text=="")Toast.makeText(baseContext,"Selecciona un fotograma manteniendolo presionando",Toast.LENGTH_SHORT).show()
                    else{
                        deletePhoto()
                        listPhoto.removeAll(listPhoto)
                        addPhoto(proyectPath)
                        viewAdapter.notifyItemRemoved(scrollPosition)
                        var count=scrollPosition
                        while(count<listPhoto.size){
                            viewAdapter.notifyItemChanged(count)
                            count++
                        }
                        animateView.scaleView(delete,1f,1.25f)
                        animateView.scaleView(delete,1.25f,1f)
                        //borra la seleccion del item en recyclerview
                        deleteSelectedItem()
                    }
                }
            }


        }

        regresar.setOnClickListener {
            animateView.scaleView(regresar,1f,1.25f)
            animateView.scaleView(regresar,1.25f,1f)
            deleteCache(this)
            Handler().postDelayed({
                val i = Intent(baseContext,DrawerActivity::class.java)
                startActivity(i)
                finish()
            },750)
        }

        deleteSelectItem.setOnClickListener {
            Toast.makeText(baseContext,"Has eliminado la seleccion",Toast.LENGTH_SHORT).show()
            deleteSelectedItem()

        }
        btn_options.setOnClickListener {
            val builder = this?.let{AlertDialog.Builder(it)}

            val inflater= layoutInflater
            val view : View
            view = inflater.inflate(R.layout.fps_dialog,null)

            builder?.setView(view)

            val dialog = builder?.create()
            dialog?.show()

            val render = dialog?.findViewById<ImageButton>(R.id.render)
            val spinnerFps= dialog?.findViewById<Spinner>(R.id.fps_spinner)
            val spinnerImage = dialog?.findViewById<Spinner>(R.id.imageType_spinner)
            val arrayFps= arrayOf("--------","10","12","20","24")
            val arrayImageType = arrayOf("--------","jpg","webp")
            val arrayAdapter2 = baseContext?.let{ArrayAdapter(it,R.layout.spinner_items,arrayImageType)}
            spinnerImage?.adapter=arrayAdapter2
            val arrayAdapter= baseContext?.let { ArrayAdapter(it,R.layout.spinner_items,arrayFps) }
            spinnerFps?.adapter=arrayAdapter

            spinnerFps?.onItemSelectedListener=object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0){
                        var fps=""
                        when(position){
                            1->fps="10"
                            2->fps="12"
                            3->fps="20"
                            4->fps="24"
                        }
                        changefps(fps)
                        Log.e("fpschanged","fps changed to : $fps")
                        simpleToast("fps changed to : $fps")

                    dialog.dismiss()

                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {    }
            }

            spinnerImage?.onItemSelectedListener= object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0){
                        var imageFormat=""
                        when(position){
                            1->imageFormat="jpg"
                            2->imageFormat="webp"
                        }
                        if (photoExtension != ".$imageFormat" ){
                            changeImageFormat(File(proyectPath),imageFormat)
                            photoExtension = ".$imageFormat"
                            Log.e("imageFormat","imageFormat changed to : $imageFormat")
                            simpleToast("imageFormat changed to : $imageFormat")
                            listPhoto.removeAll(listPhoto)
                            loadRecycle(proyectPath,scrollPosition)
                        }else simpleToast("Eligiste el mismo formato")

                        dialog.dismiss()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {   }

            }

            render?.setOnClickListener {
                Toast.makeText(this,"El video se exportara la carpeta Ajayukuna/Videos Espere unos minutos",Toast.LENGTH_LONG).show()
                animateView.animateCamera(baseContext,render,"red",true)
                val inputPath="$proyectPath/${proyectName}_%06d$photoExtension"
                Log.e("inputPath",inputPath+"///"+crudPhoto.outputPath(proyectName,videoPath))
                val exportProyect= Export(
                    inputPath,
                    crudPhoto.outputPath(proyectName,videoPath),
                    fps.toString(),
                    this
                )
                exportProyect.execute2()
                animateView.animateCamera(baseContext,render,"white",false)
                dialog.dismiss()
            }
        }


        loadRecycle(proyectPath,scrollPosition)
        //soy un comentario
    }

    fun changeImageFormat(file: File,imageFormat:String)= runBlocking(Dispatchers.IO){

        var pathList=ArrayList<String>()
        File(file.absolutePath).walkTopDown().sortedWith(naturalOrder()).forEach {
            val dir = it.isFile
            when {dir->{
                Log.e("changeImageFormat","photoPath : ${it.absolutePath}")
                pathList.add(it.absolutePath)
            }}
        }
        pathList.map {
            async {
                Log.e("changeImageFormat","path : $it")
                changeImageFormatAsync(it,imageFormat)
            }
        }.awaitAll()
        Log.e("changeImageFormat","Full changed")

    }
    private fun changeImageFormatAsync(pathFile: String, imageFormat:String){
        val fileParent=File(pathFile).parent
            if (photoExtension != ".$imageFormat"){
                val out = pathFile.substringBeforeLast(".")
                val outFile = File("$out.$imageFormat")
                Log.e("out",out)
                val outputStream = FileOutputStream(outFile)
                val bitmap = BitmapFactory.decodeFile(pathFile)
                when(imageFormat){
                    "webp"->{
                        bitmap.compress(Bitmap.CompressFormat.WEBP,80,outputStream)
                        outputStream.close()
                    }
                    "jpg"->{
                        bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream)
                        outputStream.close()
                    }
                }

                val photo= pathFile.substringAfterLast("/")
                Log.e("photo",photo)
                val fileToDelete=File(fileParent,photo)
                if (fileToDelete.exists()){
                    fileToDelete.delete()
                    if (fileToDelete.exists()) Log.e("estadoFileBorrado","No se ha podido borrar")
                    else Log.e("estadoFileBorrado","Se ha podido borrar")
                }
            }
    }

    fun simpleToast(msg:String){
        Toast.makeText(baseContext,msg,Toast.LENGTH_SHORT).show()
    }
    fun changefps(fps2:String){
        val fpsActual=fps.toString()
        if (fps2!=fpsActual){
            val newProyectName=fps2+(proyectName.substring(2))
            Log.e("proyectname",proyectName)
            Log.e("new_proyectname",newProyectName)
            Log.e("path",proyectPath)
            val file = File(proyectPath)
            Log.e("parentde file",file.parent)
            val fileA = File(file.parent+"/"+proyectName)
            val fileB = File(file.parent+"/"+newProyectName)

            Log.e("fileA",fileA.absolutePath)
            Log.e("fileB",fileB.absolutePath)

            File(proyectPath).walkTopDown().sortedWith(naturalOrder()).forEach {
                val dir=it.isFile
                when{dir->{
                        val photoA = it.toString().substringAfterLast("/")
                        val photoB=fps2+it.toString().substringAfterLast("/").substring(2)
                        Log.e("dir",it.toString())
                        Log.e("path",proyectPath)
                        Log.e("photoA",photoA)
                        Log.e("photoB",photoB)
                        val filephotoA=File(proyectPath,photoA)
                        val filephotoB=File(proyectPath,photoB)
                        filephotoA.renameTo(filephotoB)
                        Log.e("pafilephotoAth",filephotoA.absolutePath)
                }
                }
            }
            fileA.renameTo(fileB)
            proyectPath=file.parent+"/"+newProyectName
            fps=fps2.toInt()
            loadRecycle(proyectPath,scrollPosition)
            proyectName=newProyectName
            outputDirectory = getOutputDirectory(this,proyectPath)
            Log.e("path_changedfps",proyectPath)
            Log.e("fps_changedfps",fps2)
            Log.e("pathParent_changedfps",proyectName)
            nombre_proyecto.text = proyectName.substring(2)
        }else Toast.makeText(baseContext,"Los fps son los mismos",Toast.LENGTH_SHORT)

    }

//elimina seleccion de item del recycleview

    private fun deleteSelectedItem(){
        btnChange="AnimPrev"
        selectItem.text=""
        selectedPath=""
        deleteSelectItem.visibility=View.INVISIBLE
        animateView.animateCamera(baseContext,take_picture,"red",false)
        Log.e("deleteSelectedItem","btn_animPrev ${btn_animPrev.isEnabled}")
        btn_animPrev.setImageResource(R.drawable.ic_btn_play_white)
        loadRecycle(proyectPath,scrollPosition)
    }


    private fun deletePhoto() {
        Log.e("path", proyectPath)

        File(proyectPath).walkTopDown().sortedWith(naturalOrder()).forEach {
            val dir = it.isFile
            when {
                dir -> {
                    selectedPathParent = File(selectedPath).parent
                    var extension = selectedPath.substringAfter("_")
                    extension = extension.substringBefore(".")
                    val pos = Integer.parseInt(extension)
                    Log.e("pos_path", pos.toString())

                    var extension2 = it.toString().substringAfter("_")
                    extension2 = extension2.substringBefore(".")
                    val posA = Integer.parseInt(extension2)
                    Log.e("posA", posA.toString())
                    val posB = posA - 1
                    Log.e("posB", posB.toString())
                    val fmt = Formatter()
                    val formatPos = fmt.format("%06d", posA).toString()
                    val formatPos2 = fmt.format("%06d", posB).toString().substring(6)

                    Log.e("formatPos", formatPos)
                    Log.e("formatPos2", formatPos2)

                    val a = "${proyectName}_${formatPos+photoExtension}"
                    val b = "${proyectName}_${formatPos2+photoExtension}"


                    val fileA = File(selectedPathParent, a)
                    val fileB = File(selectedPathParent, b)

                    if (pos < posA) {
                        val rename=fileA.renameTo(fileB)
                        if (rename)
                        Log.e("rename ", "$fileA fue renombrado por $fileB ")
                        else Log.e("rename", "fileA no puede ser renombrado")
                    }
                    if (selectedPath == it.toString()) {

                        val aBorrar = "${proyectName}_${formatPos+photoExtension}"
                        val fileBorrar = File(selectedPathParent,aBorrar)
                        Log.e("aBorrar","$aBorrar fue borrada")
                        val status = fileBorrar.exists()
                        if(status){
                            fileBorrar.delete()
                            if (fileBorrar.exists())Log.e("estatusBorrado ", "itemView no fue borrado")
                            else  Log.e("estatusBorrado", "itemView si pudo ser borrado")
                        }
                        else Log.e("estatusBorrado", "itemView no existe")
                    }

                }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteCache(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)


        if (hasFocus)  hideSystemUI()
    }

    private fun hideSystemUI(){
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or

                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN

                )
    }

    override fun onRestart() {
        super.onRestart()
        hideSystemUI()
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private lateinit var imageAnalysis: ImageAnalysis

    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var imageCapture: ImageCapture

    private lateinit var outputDirectory: File

    private var nomProye=""

    private fun photoNumber(path:String){

        var pathPhotoNumber=path+"/${proyectName}_000001.webp"
        val pathPhotoNumber2 = path+"/${proyectName}_000001.jpg"
        val file =File(pathPhotoNumber)
        val file2 = File(pathPhotoNumber2)
        if (file.exists()|| file2.exists() ){
            Log.e("photoNumber",": nombre proyectName_000001 existe")
            var photoNumber: Int
            File(path).walkTopDown().forEach {
                val dir = it.isFile
                when {
                    dir -> {
                            var photoNumberString = pathPhotoNumber.substringAfter("_")
                            photoNumberString = photoNumberString.substringBefore(".")
                            Log.e("photoNumberString",photoNumberString)
                            photoNumber= Integer.parseInt(photoNumberString)

                            photoNumber ++
                            val fmt =Formatter()
                            fmt.format("%06d",photoNumber)
                            photoNumberString = fmt.toString()
                            pathPhotoNumber = "${proyectName}_$photoNumberString"
                            Log.e("photoNumber", "pathPhotoNumber: $pathPhotoNumber")
                            nomProye=pathPhotoNumber

                    }

                }
            }
        }
        else {

            val pathPhotoNumber1 = "${proyectName}_000001"
            nomProye=pathPhotoNumber1
            Log.e("Estoy aqui!!",nomProye)
        }

    }

    private fun takePicture() {
        photoNumber(proyectPath)
        val file = createFile(
            outputDirectory,
            nomProye,
            defaultPhotoExtension
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputFileOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                previewView.post {
                    Log.e("takePicture","photoExtension $photoExtension")
                    val msg = "Photo capture succeeded: ${file.absolutePath.substringAfterLast("/")}"
                    when(photoExtension){
                        ".jpg"->{
                            Log.e("takePicture","option jpg")
                            Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                        }

                        ".webp"->{
                            Log.e("takePicture","photoExtension : $photoExtension")
                            decodeWebp(file)
                            Toast.makeText(baseContext, msg.substringBefore(".")+".webp", Toast.LENGTH_LONG).show()
                        }
                    }
                    animateView.animateCamera(baseContext,take_picture,"red",false)
                }

            }

            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                previewView.post {
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    fun detectFormatImage(){
        var jpg = 0
        var webp = 0
        File(proyectPath).walkTopDown().sortedWith(naturalOrder()).forEach {
            val dir = it.isFile
            when{dir->{
                val imagePath="."+it.path.substringAfterLast(".")
                Log.e("detectFormatImage","imagePath $imagePath")
                when(imagePath){
                    ".jpg"->jpg++
                    ".webp"->webp++
                }
            }}
        }
        if (jpg > webp) photoExtension = ".jpg"
        if (jpg < webp) photoExtension = ".webp"
        if (jpg > 1 && photoExtension == ".webp"){
            File(proyectPath).walkTopDown().sortedWith(naturalOrder()).forEach {
                val dir = it.isFile
                when {dir->{
                    val extension="."+it.path.substringAfterLast(".")
                    if (extension == ".jpg"){
                            decodeWebp(it)
                        Log.e("detectFormatImage","repair ${it.absolutePath}")
                    }
                }}
            }
        }
        Log.e("detectFormatImage","photoExtensionFinal : $photoExtension")
        Log.e("formatImage","jpg: $jpg /// webp: $webp")
    }

    fun decodeWebp(file:File)= runBlocking(Dispatchers.Default){
        Log.e("threadCurrentdecode",Thread.currentThread().name)

        val out = file.absolutePath.toString().substringBeforeLast(".")
        Log.e("out",out)
        val outFile = File("$out.webp")
        val outputStream = FileOutputStream(outFile)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream)
        outputStream.close()

        val fileParent=file.parent
        val photo= file.absoluteFile.toString().substringAfterLast("/")
        Log.e("fileParent",fileParent)
        Log.e("photo",photo)
        val fileToDelete=File(fileParent,photo)
        if (fileToDelete.exists()){
            fileToDelete.delete()
            if (fileToDelete.exists()) Log.e("estadoFileBorrado","No se ha podido borrar")
            else Log.e("estadoFileBorrado","Se ha podido borrar")
        }

        if (selectedPath == "" && selectItem.text == "") {
        } else {
            listPhoto.removeAll(listPhoto)
            addPhoto(proyectPath)
            viewAdapter.notifyItemInserted(scrollPosition)

            var count = 0
            while (count < listPhoto.size) {
                Log.e("notifyitemchanged", count.toString())
                viewAdapter.notifyItemChanged(count)
                count++
            }
        }
    }


    private var pathForAdd=""

    private fun addPhotoBtwPhoto(imageFormat: String){
        Log.e("path",proyectPath)

        File(proyectPath).walkTopDown().sortedWith(reverseOrder()).forEach {
            val dir = it.isFile

            when {
                dir -> {
                    selectedPathParent = File(selectedPath).parent
                    var pathNumber = selectedPath.substringAfter("_")
                    pathNumber = pathNumber.substringBefore(".")
                    val pos = Integer.parseInt(pathNumber)
                    Log.e("pos_path", pos.toString())

                    var newPathNumber = it.toString().substringAfter("_")
                    newPathNumber = newPathNumber.substringBefore(".")
                    val number = Integer.parseInt(newPathNumber)
                    Log.e("posA", number.toString())
                    val newNumber =number+1
                    Log.e("posB", newNumber.toString())
                    val fmt =Formatter()
                    val formatNumber=fmt.format("%06d",number).toString()
                    val formatNewNumber=fmt.format("%06d",newNumber).toString().substring(6)

                    Log.e("formatPos", formatNumber)
                    Log.e("formatPos2", formatNewNumber)

                    val a = "${proyectName}_${formatNumber+imageFormat}"
                    val b = "${proyectName}_${formatNewNumber+imageFormat}"


                    val fileA = File(selectedPathParent,a)
                    val fileB = File(selectedPathParent,b)

                    if(pos<newNumber){
                        val rename=fileA.renameTo(fileB)
                        if (rename)Log.e("rename ", "$fileA fue renombrado por $fileB ")
                        else Log.e("rename", "fileA no pudeo ser renombrado")
                    }
                    if (selectedPath==it.toString()){

                        val aBorrar = "${proyectName}_${formatNumber+imageFormat}"
                        val fileBorrar = File(selectedPathParent,aBorrar)
                        Log.e("aBorrar",aBorrar)
                        val status = fileBorrar.exists()
                        if(status){
                            fileBorrar.delete()
                            if (fileBorrar.exists()) Log.e("estatusBorrado ", "fileBorrar no fue borrado")
                            else Log.e("estatusBorrado ", "fileBorrar fue borrado")
                        }
                        else Log.e("estatusBorrado", "fileBorrar no existe")
                        pathForAdd=a
                        pathForAdd=pathForAdd.substringBeforeLast(".")+".jpg"
                        Log.e("parent",selectedPathParent)
                        Log.e("pathForAdd",pathForAdd)
                    }

                }
            }
        }

    }

    private fun addTakePicture() {
        addPhotoBtwPhoto(photoExtension)
        val file = File(selectedPathParent,pathForAdd)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputFileOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val msg = "Photo capture succeeded: ${file.absolutePath}"
                Log.e("msg",msg)
                previewView.post {
                    val msg = "Photo capture succeeded: ${file.absolutePath.substringAfterLast("/")}"
                    when(photoExtension){
                        ".jpg"->
                            Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                        ".webp"->{
                            Log.e("takePicture","photoExtension : $photoExtension")
                            decodeWebp(file)
                            Toast.makeText(baseContext, msg.substringBefore(".")+".webp", Toast.LENGTH_LONG).show()
                        }
                    }
                    animateView.animateCamera(baseContext,take_picture,"red",false)

                }
            }

            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                previewView.post {
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }
            }
        })
        selectedPath=""
        pathForAdd=""
    }

    private fun startCamera() {

        val metrics = DisplayMetrics().also{ previewView.display.getRealMetrics(it)}

        val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(previewView.display.rotation).build()

        preview.setSurfaceProvider(previewView.previewSurfaceProvider)

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(metrics.widthPixels,metrics.heightPixels))

        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        imageAnalysis.setAnalyzer(executor,LuminosityAnalyzer())

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.bindToLifecycle(this, cameraSelector,preview,imageAnalysis,imageCapture)
        }, ContextCompat.getMainExecutor(this))

    }

    companion object {

        fun getOutputDirectory(context: Context,path:String): File {

            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(path).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, nombreProyecto: String, extension: String) =
            File(baseFolder,nombreProyecto+extension)
            /*File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)*/
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        /**
         * Helper extension function used to extract a byte array from an
         * image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            // Calculate the average luma no more often than every second
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)) {
                // Since format in ImageAnalysis is YUV, image.planes[0]
                // contains the Y (luminance) plane
                val buffer = image.planes[0].buffer
                // Extract image data from callback object
                val data = buffer.toByteArray()
                // Convert the data into an array of pixel values
                val pixels = data.map { it.toInt() and 0xFF }
                // Compute average luminance for the image
                val luma = pixels.average()
                // Log the new luma value
                Log.d("CameraXApp", "Average luminosity: $luma")
                // Update timestamp of last analyzed frame
                lastAnalyzedTimestamp = currentTimestamp
            }
            image.close()
        }
    }

    private fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(baseContext,DrawerActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.animator.slide_left,R.animator.slide_right)
        finish()
        deleteCache(this)
    }
}




