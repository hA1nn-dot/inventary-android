package com.example.inventariofsico
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.lang.NullPointerException

class ScannerActivity : AppCompatActivity(), View.OnKeyListener {
    private var text_codigo: EditText? = null
    private var text_cantidad: EditText? = null
    private var btnGuardar: Button? = null
    private var btnCleanBoxes: Button? = null
    private var text_descripcion: TextView? = null
    private var text_IDUnidad: TextView? = null
    private var text_IDProducto: TextView? = null
    private var spinnerUnidades: Spinner? = null
    private var unidadSeleccionada: String? = null
    private var ubicaciontxt: String? = null
    private var borrar: Boolean = false
    private var id_producto: Int = 0
    private var id_unidad: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaner)
        text_codigo = findViewById(R.id.codigoBarrasTxt)
        text_cantidad = findViewById(R.id.textCantidad)
        text_descripcion = findViewById(R.id.descripcionText)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnCleanBoxes = findViewById(R.id.btn_clearBoxes)
        spinnerUnidades = findViewById(R.id.spinner_Unidad)
        text_codigo!!.setOnKeyListener(this)
        text_cantidad!!.setOnKeyListener(this)
        text_IDUnidad = findViewById(R.id.id_unidadTxt)
        text_IDProducto = findViewById(R.id.id_productoTxt)

        text_codigo!!.requestFocus()

        text_codigo!!.filters += InputFilter.AllCaps()
        setUbicacion()
        val listCleaned = listOf("<Cargue producto>")
        val  adapterUnidad = ArrayAdapter(
            this@ScannerActivity,
            android.R.layout.simple_spinner_item,
            listCleaned
        )
        spinnerUnidades!!.adapter = adapterUnidad

        btnGuardar!!.setOnClickListener {
            if(NullFieldsExists())
                return@setOnClickListener
            guardarCodigo()
            cleanBoxes()
        }
        btnCleanBoxes!!.setOnClickListener {
            cleanBoxes()
        }
        spinnerUnidades!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(text_codigo!!.text.isNotEmpty()) {

                    val barcode = text_codigo!!.text.toString()
                    if(unidadSeleccionada != spinnerUnidades!!.selectedItem.toString()){
                        if(id_producto != 0 && id_unidad != 0)
                            changeAmount(barcode)
                        unidadSeleccionada = spinnerUnidades!!.selectedItem.toString()
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun NullFieldsExists(): Boolean{
        val barcode = text_codigo!!.text.toString()
        val amount = text_cantidad!!.text.toString()
        if(barcode.isNullOrEmpty()){
            printMessageByToast("Ingrese un código de barras o la clave del producto")
            return true
        }else if(amount.isNullOrEmpty()){
            printMessageByToast("Ingrese una cantidad")
            return true
        }

        return false
    }

    private fun changeAmount(barcode: String) {
        var cantidadFound = "1"
        var errorMessage = ""
        id_unidad = SQLiteFunction.getIDUnidad(this,spinnerUnidades!!.selectedItem.toString())
        id_producto = SQLiteFunction.getIDProduct(this,barcode)

        try {
            if(SQLiteFunction.isCodeExistsinCodesTableById(this,id_unidad,id_producto)){
                cantidadFound = SQLiteFunction.getCantidad(this,id_producto.toString(),id_unidad.toString())
            }
            text_cantidad!!.setText(cantidadFound)

        }catch (liteX: SQLiteException){
            errorMessage = liteX.message.toString()
        }catch (nullvar: NullPointerException){
            errorMessage = nullvar.message.toString()
        }
        if(errorMessage != "")
            Toast.makeText(this@ScannerActivity, errorMessage, Toast.LENGTH_SHORT).show()
        text_cantidad!!.requestFocus(cantidadFound.length)

    }

    private fun setUbicacion() {
        val bundle = this.intent.extras
        val ubicacionSeleccionada = bundle?.getString("ubicacionSeleccionada")
        val title: TextView = findViewById(R.id.txt_ubicacion)
        title.text = ubicacionSeleccionada
        ubicaciontxt = ubicacionSeleccionada
    }

    override fun onKey(view: View?, keycode: Int, evento: KeyEvent?): Boolean {
        if (evento != null) {
            if(evento.action == KeyEvent.ACTION_DOWN){
                when(view?.id){
                    R.id.codigoBarrasTxt -> {
                        if(keycode == KeyEvent.KEYCODE_ENTER){
                            val barcode = text_codigo!!.text.toString()
                            text_codigo!!.requestFocus()
                            if(barcode.isNotEmpty()){
                                searchCode(barcode)
                                borrar = true
                            }
                        }
                        if(keycode == KeyEvent.KEYCODE_FORWARD_DEL && borrar){
                            cleanBoxes()
                            borrar = false
                        }
                    }
                    R.id.textCantidad -> {
                        if(keycode == KeyEvent.KEYCODE_ENTER){
                            if(text_cantidad!!.text.toString() != "0"){
                                guardarCodigo()
                                cleanBoxes()
                            }

                        }

                    }
                }
            }
        }
        return false
    }

    private fun searchCode(barcode: String){
        var errorMessage = ""
        var cantidadFound = "1"
        loadUnitsProduct(barcode)
        val productoDescrition = SQLiteFunction.buscaNombreCodigo(this,barcode)
        if(productoDescrition != "Producto no encontrado"){
            id_unidad = SQLiteFunction.getIDUnidad(this,spinnerUnidades!!.selectedItem.toString())
            id_producto = SQLiteFunction.getIDProduct(this,barcode)
            try {
                if(SQLiteFunction.isCodeExistsinCodesTableById(this,id_unidad,id_producto))
                    cantidadFound = SQLiteFunction.getCantidad(this,id_producto.toString(),id_unidad.toString())
            }catch (liteX: SQLiteException){
                errorMessage = liteX.message.toString()
            }catch (nullvar: NullPointerException){
                errorMessage = nullvar.message.toString()
            }
            if(errorMessage != "")
                printMessageByToast(errorMessage)
        }else if(SQLiteFunction.isCodeExistsInCodesTableByBarcode(this,barcode)){

            cantidadFound = searchUnknownProductInCodesTable()
        }

        text_cantidad!!.setText(cantidadFound)
        text_cantidad!!.requestFocus(1)
        text_descripcion!!.text = productoDescrition
    }

    private fun searchUnknownProductInCodesTable():String{
        val barcode = text_codigo!!.text.toString()
        val cantidad = SQLiteFunction.getCantidad(this,barcode)
        return cantidad.toString()
    }

    private fun showAlertDialogIfAddUnknownProduct(product: Product){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setMessage("¿Seguro que desea guardar este producto?").setCancelable(false)
            .setPositiveButton("Guardar")
            { _, _ ->
                SQLiteFunction.insertProduct(this,product)
                printMessageByToast("Producto Guardado")
                cleanBoxes()
            }
            .setNegativeButton("No")
            { _, _ ->
                printMessageByToast("Producto Cancelado")
                cleanBoxes()
            }
        val title = alert.create()
        title.setTitle("Producto no encontrado")
        title.show()
    }

    fun printMessageByToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadUnitsProduct(barcode: String) {
        val spinnerUnidad: Spinner = findViewById(R.id.spinner_Unidad)
        var listUnidades = SQLiteFunction.getUnidades(this,barcode)
        if(listUnidades.count() == 0){
            listUnidades = listOf("<Cargue producto>")
        }
        val  adapterUnidad = ArrayAdapter(              //Load list of unidades
            this@ScannerActivity,
            android.R.layout.simple_spinner_item,
            listUnidades
        )
        spinnerUnidad.adapter = adapterUnidad
        unidadSeleccionada = spinnerUnidad.selectedItem.toString()
    }

    private fun guardarCodigo(){
        val context = this
        val product = Product()
        product.setBarCode(text_codigo!!.text.toString())
        product.setAmount(text_cantidad!!.text.toString())
        product.setUnitText(spinnerUnidades!!.selectedItem.toString())
        product.setIdsProduct(context)
        product.setDate(context)

        if(product.isInCodesTable(context)){
            if(product.getCantidad() == 0){
                SQLiteFunction.deleteProductFromCodesTable(context,product)
                printMessageByToast("Producto Eliminado")
                return
            }
            SQLiteFunction.updateProduct(this,product)
            printMessageByToast("Producto Actualizado")
            return
        }
        else if(product.isExistsInMainCodesTable(context)){
            SQLiteFunction.insertProduct(context,product)
            printMessageByToast("Producto Guardado")
            return
        }else{
            showAlertDialogIfAddUnknownProduct(product)
        }
    }

    private fun cleanBoxes(){
        val spinnerUnidad: Spinner = findViewById(R.id.spinner_Unidad)
        val listCleaned = listOf("<Cargue producto>")
        val  adapterUnidad = ArrayAdapter(
            this@ScannerActivity,
            android.R.layout.simple_spinner_item,
            listCleaned
        )
        spinnerUnidad.adapter = adapterUnidad
        text_codigo!!.setText("")
        text_codigo!!.requestFocus()
        text_cantidad!!.setText("")
        text_descripcion!!.text = ""
        text_IDProducto!!.text = ""
        text_IDUnidad!!.text = ""

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

}