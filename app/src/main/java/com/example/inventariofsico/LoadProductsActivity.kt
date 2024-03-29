package com.example.inventariofsico

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.IOException
import java.lang.NullPointerException
import java.lang.NumberFormatException
import java.sql.SQLException
import java.sql.Statement

class LoadProductsActivity : AppCompatActivity() {

    private var spinAlmacenes: Spinner? = null
    private var spin_Ubicaciones: Spinner? = null
    private var spin_Conteo: Spinner? = null
    private var btnLoadProducts: Button? = null
    private var btn_Send: Button? = null
    private var btnScan: Button?= null
    private var btnCloseSession: Button? = null
    private var fecha: EditText? = null
    private var loading: ProgressBar? = null
    private var usuario: Usuario? = null
    private var productos_cantidad: String = "0"
    private var productos_lector: Int = 0
    private var cantidadTxtView: TextView? = null
    private var almacenSeleccionado: String? = null
    private var dateEditText: EditText? = null

    private var almacenList: List<String>? = null
    private var ubicacionList: List<String>? = null
    private var ubicationRioVerdeList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_products)
        supportActionBar!!.hide()
        val bundle = this.intent.extras
        val bundleUsuario = bundle?.getSerializable("usuarioEnviado") as Usuario

        val title: TextView = findViewById(R.id.titulo)

        //Spinners and EditText date
        spinAlmacenes = findViewById(R.id.spinnerAlmacenes)
        spin_Ubicaciones = findViewById(R.id.spinnerUbicaciones)
        spin_Conteo = findViewById(R.id.spinnerConteo)
        fecha = findViewById(R.id.eDate)
        loading = findViewById(R.id.progressBar2)
        cantidadTxtView = findViewById(R.id.cantidadTextView)
        dateEditText = findViewById(R.id.eDate)

        //Buttons
        btnLoadProducts = findViewById(R.id.btn_CargarDatos)
        btnScan = findViewById(R.id.btn_scan)
        btn_Send = findViewById(R.id.btn_sendData)
        btnCloseSession = findViewById(R.id.btn_CloseSession)
        val btn_Reset = findViewById<Button>(R.id.btn_Reset)

        //usuario = bundleUsuario
        //title.text = "Bienvenido ${usuario!!.getUserName()}"

        refreshCantidad()
        if(SQLiteFunction.isUserExistsInDataBase(this)){
            if(productos_cantidad != "0"){
                putSQLiteUserData()
                buttonsBehaviour(false)
            }else{
                loadConteo()
                getRequest().execute()
            }
        }else{
            finish()
        }
        btnLoadProducts!!.setOnClickListener {
            setUserInfo()
            loadDataSQLite().execute()
        }
        btnScan!!.setOnClickListener {
            changeActivity()
        }
        btnCloseSession!!.setOnClickListener {
            closeSession()
        }
        dateEditText!!.setOnClickListener{
            showDatePickerDialog()
        }
        btn_Send!!.setOnClickListener {
            showAlertDialogToSendAllProductsToServer()
            refreshCantidad()
        }
        btn_Reset.setOnClickListener {
            createResetDialog()
        }
        spinAlmacenes!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                if(almacenSeleccionado == null){
                    almacenSeleccionado = spinAlmacenes!!.selectedItem.toString()
                }else if(almacenSeleccionado != spinAlmacenes!!.selectedItem.toString()){
                    almacenSeleccionado = spinAlmacenes!!.selectedItem.toString()
                    getRequest().execute()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    private fun createResetDialog() {
        val dialogBuilder = AlertDialog.Builder(this,R.style.AlertDialog_AppCompat)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_action_reset,null)
        dialogBuilder.setView(dialogView)

        val editText: EditText = dialogView.findViewById(R.id.password)
        val btn_AcceptReset : Button = dialogView.findViewById(R.id.accept_button)
        val btn_CancelReset : Button = dialogView.findViewById(R.id.cancel_button)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        btn_AcceptReset.setOnClickListener {
            if(editText.text.toString() == "Ibague2021"){
                SQLiteFunction._deleteMainCodigos(this)
                SQLiteFunction._deleteCodes(this)
                buttonsBehaviour(true)
                refreshCantidad()
                printMessageByToast("Lector reseteado")
                alertDialog.dismiss()
            }else printMessageByToast("Contraseña incorrecta")
        }
        btn_CancelReset.setOnClickListener {
            Toast.makeText(this,"Cancelado",Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

    }

    private fun deleteProducts(){
        val context = this
        if(SQLiteFunction.countCodigos(context) == 0){
            buttonsBehaviour(true)
            SQLiteFunction._deleteMainCodigos(context)
            SQLiteFunction._deleteCodes(context)
            almacenList = null
            ubicacionList = null
            almacenSeleccionado = null
        }else{
            Toast.makeText(context,"No todos los productos se han subido, vuelva a subir de nuevo",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialogToSendAllProductsToServer(){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        productos_lector = SQLiteFunction.countCodigos(this)
        alert.setMessage("Esta seguro de enviar los $productos_lector productos al sistema?").setCancelable(false)
            .setPositiveButton("Enviar")
            { _, _ ->
                sendData_SQliteToSQLServer()

            }
            .setNegativeButton("No")
            { _, _ ->
                //Close AlertDialog
            }
        val title = alert.create()
        title.setTitle("Envio de datos")
        title.show()
    }

    private fun setUserInfo(){
        try {
            val almacenSelected = spinAlmacenes!!.selectedItem.toString()
            val ubicationSelected = spin_Ubicaciones!!.selectedItem.toString()
            val fechaEditText: EditText = findViewById(R.id.eDate)
            val date: String = fechaEditText.text.toString()
            val conteo = spin_Conteo!!.selectedItem.toString()
            val userInfo = mapOf<String,String>(
                Pair("almacen",almacenSelected),
                Pair("ubicacion",ubicationSelected),
                Pair("fecha",date),
                Pair("conteo",conteo)
            )
            UserInfo.setUserInfoIntoLocalDataBase(this,userInfo)
        }catch (pointerError: NullPointerException){
            Toast.makeText(this,pointerError.message.toString(),Toast.LENGTH_LONG).show()
        }

    }


    internal inner class TaskSendingSQL( _registers:MutableList<RegisterData>)
        : AsyncTask<Void,Void,String>(){
        val context = this@LoadProductsActivity
        val registers : MutableList<RegisterData> = _registers
        override fun onPreExecute()  {
            super.onPreExecute()
            btn_Send!!.isEnabled = false
            loading!!.visibility = View.VISIBLE
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            btn_Send!!.isEnabled = true
            if(result == "Descarga completada"){
                if(SQLiteFunction.countCodigos(context) == 0){
                    deleteProducts()
                    getRequest().execute()
                    loadConteo()
                }else{
                    refreshCantidad()
                    loading!!.visibility = View.INVISIBLE
                    Toast.makeText(context,"No se han cargado todos los codigos, favor de reenviar de nuevo",Toast.LENGTH_SHORT).show()
                    return
                }

            }
            refreshCantidad()
            loading!!.visibility = View.INVISIBLE
            Toast.makeText(context,result,Toast.LENGTH_SHORT).show()

        }

        override fun doInBackground(vararg p0: Void?): String {
            val context = this@LoadProductsActivity
            var message = "Descarga completada"

            try {
                registers.forEach {
                    println(it.printRegisterInfo())
                    if(it.get_Id_Producto() == 0 || it.get_Id_Unidad() == 0) {
                        SQLfunction.searchProductIds(it, context)
                        SQLiteFunction.updateProductIds(it, context)
                    }
                    if (SQLfunction.isProductExistsInBackUpTable(it, context))
                        SQLfunction.updateProductInBackUpTable(it, context)
                    else
                        SQLfunction.insertProductInBackUpTable(it, context)
                    SQLfunction.updateProductInCONTEOS_IFTable(it, context)
                    
                    SQLiteFunction._deleteProductFromSQLite(it, context)
                }
            }catch (sqlX: SQLException){
                message = "SQL Error: \n"+sqlX.message.toString()
            }catch (liteError: SQLiteException){
                message = "SQLite Error: \n"+liteError.message.toString()
            }catch (runtimeError: RuntimeException){
                message = "Run time Error: \n"+runtimeError.message.toString()
            }catch (error: Exception){
                message = "Exception: \n" + error.message.toString()
                println(message)
            }

            return message
        }
    }





    private fun sendData_SQliteToSQLServer() {
        val conteo = spin_Conteo!!.selectedItem.toString()
        var message = ""
        if(usuario != null){
            try {
                val registers = SQLiteFunction._sendRegisterstoServer(this,conteo,usuario!!)
                if(!registers.isNullOrEmpty()){
                    TaskSendingSQL(registers).execute()
                }else message = "No hay datos registrados en el lector"

            }catch (SQLX: SQLException){
                message = SQLX.message.toString()
            }catch (liteX: SQLiteException){
                message = liteX.message.toString()
            }catch (nullable: NullPointerException){
                message = nullable.message.toString()
            }catch (timeout: RuntimeException){
                message = timeout.message.toString()
            }
        }
        if(!message.isNullOrEmpty())
            Toast.makeText(this@LoadProductsActivity,message,Toast.LENGTH_SHORT).show()
    }

    private fun buttonsBehaviour(status: Boolean){
        spinAlmacenes!!.isEnabled = status
        spin_Ubicaciones!!.isEnabled = status
        spin_Conteo!!.isEnabled = status
        fecha!!.isEnabled = status
        btnLoadProducts!!.isEnabled = status
    }
    private fun putSQLiteUserData(){
        try {
            val almacen = UserInfo.getAlmacen(this)
            val ubicacion = UserInfo.getUbication(this)
            val date = UserInfo.getFecha(this)
            val conteo = UserInfo.getConteo(this)

            val  adaptadorAlmacen = ArrayAdapter(this, android.R.layout.simple_spinner_item, almacen)
            spinAlmacenes!!.adapter = adaptadorAlmacen
            val  adaptadorUbicacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, ubicacion)
            spin_Ubicaciones!!.adapter = adaptadorUbicacion
            val  adaptadorConteo = ArrayAdapter(this, android.R.layout.simple_spinner_item, conteo)
            spin_Conteo!!.adapter = adaptadorConteo
            val fechaVar: EditText = findViewById(R.id.eDate)
            fechaVar.setText(date[0])
        }catch (pointError: NullPointerException){
            Toast.makeText(this,pointError.message.toString(),Toast.LENGTH_LONG).show()
        }


    }

    private fun changeActivity(){
        try {
            val intentvar = Intent(this@LoadProductsActivity, ScannerActivity::class.java)
            intentvar.putExtra("ubicacionSeleccionada",spin_Ubicaciones!!.selectedItem.toString())
            startActivity(intentvar)
        }catch (exp: InstantiationException){
            throw InstantiationException("Error al cargar la nueva ventana")
        }

    }
    private fun loadUser(): String{
        val admin = SQLiteConnection(this,"administracion",null,1)
        val db = admin.writableDatabase
        val registroUsuario = ContentValues()

        registroUsuario.put("usuario", usuario!!.getUserName())
        registroUsuario.put("almacen", spinAlmacenes!!.selectedItem.toString())
        registroUsuario.put("ubicacion", spin_Ubicaciones!!.selectedItem.toString())
        registroUsuario.put("conteo", spin_Conteo!!.selectedItem.toString())
        registroUsuario.put("fecha", fecha!!.text.toString())
        db.insert("personal",null,registroUsuario)

        return "Completado"
    }



    private fun loadConteo(){
        spin_Conteo = findViewById(R.id.spinnerConteo)
        val list =  listOf("Conteo 1","Conteo 2")
        val  adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        spin_Conteo!!.adapter = adaptador
    }

    private fun closeSession(){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)

        alert.setMessage("Está seguro desea salir del sistema?").setCancelable(false)
            .setPositiveButton("Salir")
            { _, _ ->
                try {
                    //deleteMainCodigos().execute()
                    SQLiteFunction._updateUserNametoNull(this)
                    finish()
                }catch (liteX : SQLiteException){
                    Toast.makeText(this@LoadProductsActivity,liteX.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No")
            { _, _ ->
                //Just Close AlertDialog
            }
        val title = alert.create()
        title.setTitle("Cerrar sesión")
        title.show()
    }

    internal inner class getRequest : AsyncTask<Void, Void, String>(){
        var context: Context = this@LoadProductsActivity
        override fun onPreExecute() {
            super.onPreExecute()
            loading!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Void?): String? {

            try {

                almacenList = SQLfunction.getAlmacenes(context).sorted()
                if(almacenList == null){
                    this.cancel(true)
                    finish()
                }
                if(almacenList != null && almacenSeleccionado != null){
                    val nombreAlmacen: String = almacenSeleccionado as String
                    val id_almacen: String = SQLfunction.getIDAlmacen(nombreAlmacen,context)
                    if(nombreAlmacen == "B4 RIO VERDE"){
                        val ubicacionRioverde: List<String> = listOf("Todas las áreas")
                        ubicacionList = ubicacionRioverde
                        ubicationRioVerdeList = SQLfunction.getUbicaciones(id_almacen.toInt(),context)
                        ubicationRioVerdeList?.forEach {
                            println(it)
                        }
                    }else{
                        ubicacionList =  SQLfunction.getUbicaciones(id_almacen.toInt(),context)
                    }

                }else if (almacenSeleccionado == null){
                    val nombreAlmacen: String = almacenList!!.first().toString()
                    if(nombreAlmacen == "B4 RIO VERDE"){
                        val ubicacionRioverde: List<String> = listOf("Todas las áreas")
                        ubicacionList = ubicacionRioverde
                    }else{
                        val id_almacen: String = SQLfunction.getIDAlmacen(nombreAlmacen,context)
                        ubicacionList =  SQLfunction.getUbicaciones(id_almacen.toInt(),context)
                    }
                }
            } catch (p1: SQLException) {
                return p1.message.toString()
            } catch (p2: ClassNotFoundException) {
                return p2.message.toString()
            } catch (ex: NullPointerException) {
                return "Error NullPointer " + ex.message.toString()
            } catch (def: NumberFormatException) {
                return "Error de formato " + def.message.toString()
            } catch (netException: NetworkOnMainThreadException) {
                return "Error de red, reinicie la aplicación"
            } catch (notElement: NoSuchElementException){
                return "Error al cargar las ubicaciones, reinicie la aplicación"
            } catch (timeEx: RuntimeException){
                return "Tiempo de la petición agotada, favor de intentar de nuevo: "+timeEx.message.toString()
            }catch(io_ex: IOException){
                return "Error de I/O: "+io_ex.message.toString()+ " "+io_ex.cause.toString()
            }


            return "Completado"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result != "Completado"){
                Toast.makeText(this@LoadProductsActivity,result,Toast.LENGTH_SHORT).show()
                finish()
            }
            if(almacenList != null && almacenSeleccionado == null){
                val  adaptadorAlmacenes = ArrayAdapter(
                    this@LoadProductsActivity,
                    android.R.layout.simple_spinner_item,
                    almacenList!!.sorted()
                )
                spinAlmacenes!!.adapter = adaptadorAlmacenes
            }

            if(ubicacionList != null){
                val  adaptadorUbicaciones = ArrayAdapter(
                    this@LoadProductsActivity,
                    android.R.layout.simple_spinner_item,
                    ubicacionList!!
                )
                spin_Ubicaciones!!.adapter = adaptadorUbicaciones
            }else{
                Toast.makeText(this@LoadProductsActivity,"No hay elementos el Ubicaciones",Toast.LENGTH_SHORT).show()
            }
            loading!!.visibility = View.INVISIBLE
        }
    }

    internal inner class loadDataSQLite : AsyncTask<Void, Void, String>(){
        private val context: Context = this@LoadProductsActivity
        private val ubicationName: String =  spin_Ubicaciones!!.selectedItem.toString()
        private val dateSelected: String = fecha!!.text.toString()
        override fun onPreExecute() {
            super.onPreExecute()
            loading!!.visibility = View.VISIBLE
            btnLoadProducts!!.isEnabled = false
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try{
                var resultCodigos = ""
                if(ubicationName == "Todas las áreas"){
                    if(dateSelected.isNullOrEmpty()) return "Seleccione una fecha"
                    if (!SQLfunction.isInventaryExist(dateSelected, context))
                        return "No hay conteos en la fecha $dateSelected"
                    ubicationRioVerdeList!!.forEach{
                        val ubicationID = SQLfunction.getIDUbicacion(it,context)
                        resultCodigos = SQLfunction.getBarcodesRioVerde(ubicationID,dateSelected,context)
                        if (resultCodigos != "Completado") return resultCodigos
                    }
                    val resultUsuario = loadUser()
                    println("Proceso terminado")
                    productos_cantidad = SQLiteFunction.getMainCodigos(context)
                    if (resultCodigos != "Completado") return resultCodigos
                    if (resultUsuario != "Completado") return resultUsuario
                }else {

                    val id_ubicacion = SQLfunction.getIDUbicacion(ubicationName, context)

                    if (id_ubicacion == 0) {
                        return "Error al obtener id_ubicacion"
                    }
                    if (!dateSelected.isNullOrEmpty()) {
                        if (SQLfunction.isInventaryExist(dateSelected, context)) {
                            resultCodigos = SQLfunction.getBarcodes(id_ubicacion, dateSelected, context)
                            val resultUsuario = loadUser()
                            productos_cantidad = SQLiteFunction.getMainCodigos(context)
                            if (resultCodigos != "Completado") return resultCodigos
                            if (resultUsuario != "Completado") return resultUsuario
                            return "Completado"
                        } else return "No hay conteos en la fecha $dateSelected"
                    } else {
                        return "Seleccione una fecha"
                    }
                }
                return "Completado"
            }catch (sqlEx: SQLException){
                return sqlEx.message.toString()
            }catch (timeEx: RuntimeException){
                return "EL tiempo de espera ha finalizado, intente de nuevo"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result == "Completado"){
                Toast.makeText(context,"Carga Completada",Toast.LENGTH_SHORT).show()
                buttonsBehaviour(false)
                refreshCantidad()
            }else{
                Toast.makeText(context,result,Toast.LENGTH_SHORT).show()
                btnLoadProducts!!.isEnabled = true
            }
            loading!!.visibility = View.INVISIBLE
        }
    }
    private fun refreshCantidad(){
        productos_cantidad = SQLiteFunction.getMainCodigos(this@LoadProductsActivity)
        cantidadTxtView!!.text = "Cantidad: $productos_cantidad"

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDateSelected(day: Int, _month: Int, year: Int){
        var formatDate = ""
        var month = _month
        month++
        val fechaVar: EditText = findViewById(R.id.eDate)
        if(month < 10 && day < 10){
            formatDate = "$year-0$month-0$day"
        }else if(month < 10)
            formatDate = "$year-0$month-$day"
        else if(day < 10)
            formatDate = "$year-$month-0$day"
        fechaVar.setText(formatDate)
    }
    fun printMessageByToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
