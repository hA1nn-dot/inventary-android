package com.example.inventariofsico

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import java.sql.SQLException

open class SQLiteFunction {
    companion object{
        /*fun getCantidad(context: Context, barcode: String): Int{
            try {
                var cantidad = 1
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT cantidad FROM codigos where codigo = '$barcode'",null)
                if(fila.moveToFirst())
                    cantidad = fila.getInt(fila.getColumnIndex("cantidad"))
                fila.close()
                db.close()
                return cantidad
            }catch (sqlexc: SQLiteException){
                throw SQLiteException("Problemas con SQLite: "+sqlexc.message.toString())
            }catch (nullvar: NullPointerException){
                throw NullPointerException("Null exception: "+nullvar.message.toString())
            }
        }*/

        fun getCantidad(context: Context, id_producto: String,id_unidad: String): String{
            try {
                var cantidad = "1"
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT cantidad FROM codigos where id_producto = $id_producto AND id_unidad = $id_unidad",null)
                if(fila.moveToFirst())
                   cantidad = fila.getString(fila.getColumnIndex("cantidad"))
                fila.close()
                db.close()
                return cantidad
            }catch (sqlexc: SQLiteException){
                throw SQLiteException("Problemas con SQLite: "+sqlexc.message.toString())
            }catch (nullvar: NullPointerException){
                throw NullPointerException("Null exception: "+nullvar.message.toString())
            }
        }

        fun getCantidad(context: Context, barcode: String): String{
            try {
                var cantidad = "1"
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT cantidad FROM codigos WHERE codigo ) $barcode",null)
                if(fila.moveToFirst())
                    cantidad = fila.getString(fila.getColumnIndex("cantidad"))
                fila.close()
                db.close()
                return cantidad
            }catch (sqlexc: SQLiteException){
                throw SQLiteException("Problemas con SQLite: "+sqlexc.message.toString())
            }catch (nullvar: NullPointerException){
                throw NullPointerException("Null exception: "+nullvar.message.toString())
            }
        }
        fun getUbicacion(context: Context,id_producto: Int,id_unidad: Int): Int{

            try {
                var id_ubicacion = 0
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val cursor: Cursor = db.rawQuery("SELECT id_ubicacion FROM mainCodigos where id_producto = $id_producto AND id_unidad = $id_unidad",null)
                if(cursor.moveToFirst())
                    id_ubicacion = cursor.getInt(0)
                cursor.close()
                db.close()
                return id_ubicacion
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }
        fun _clearLectorDataBase(context: Context){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                db.execSQL("DELETE FROM codigos")
                db.close()
            }catch (sqlexc: SQLiteException){
                throw SQLiteException("Error: "+sqlexc.message.toString())
            }
        }

        fun isCodeExistsinCodesTableById(context: Context, id_unidad: Int,id_producto: Int): Boolean{
            var status = false
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT * FROM codigos where id_producto = $id_producto AND id_unidad = $id_unidad",null)
                if(fila.count != 0)
                    status = true
                fila.close()
                db.close()
                return status
            }catch (liteX: SQLiteException){
                throw SQLiteException("Error SQLite: "+liteX.message.toString())
            }
        }

        fun isCodeExistsInMainCodesTableById(context: Context, id_unidad: Int,id_producto: Int): Boolean{
            var status = false
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT * FROM mainCodigos where id_producto = $id_producto AND id_unidad = $id_unidad",null)
                if(fila.count != 0)
                    status = true
                fila.close()
                db.close()
                return status
            }catch (liteX: SQLiteException){
                throw SQLiteException("Error SQLite: "+liteX.message.toString())
            }
        }

        fun isCodeExistsInCodesTableByBarcode(context: Context, barcode: String): Boolean{
            var status = false
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT * FROM codigos where codigo = '$barcode'",null)
                if(fila.count != 0)
                    status = true
                fila.close()
                db.close()
                return status
            }catch (liteX: SQLiteException){
                throw SQLiteException("Error SQLite: "+liteX.message.toString())
            }
        }

        fun isCodeExists(context: Context, barcode: String): Boolean{
            var status = false
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val fila = db.rawQuery("SELECT * FROM codigos where codigo = '$barcode'",null)
                if(fila.moveToFirst())
                    status = true
                fila.close()
                db.close()
                return status
            }catch (liteX: SQLiteException){
                throw SQLiteException("Error SQLite: "+liteX.message.toString())
            }
        }


        fun _updateProduct(context: Context,cantidad: String,id_producto: Int,id_unidad: Int){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val values = ContentValues()
                values.put("cantidad",cantidad.toInt())
                val args = arrayOf(id_producto.toString(),id_unidad.toString())
                db.update("codigos",values,"id_producto=? AND id_unidad=?",args)

            }catch (liteX: SQLiteException){
                throw liteX
            }
        }

        fun _updateProduct(context: Context, barcode: String, cantidad: String){
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val values = ContentValues()
                values.put("cantidad",cantidad.toInt())
                val args = arrayOf(barcode)
                db.update("codigos",values,"codigo=?",args)
        }

        fun getMainCodigos(context:Context): String{
            var contador = 0
            val admin = SQLiteConnection(context,"administracion",null,1)
            val bd = admin.readableDatabase
            val c: Cursor = bd.rawQuery("select * from mainCodigos", null)
            while(c.moveToNext())
                contador++
            c.close()
            bd.close()
            return contador.toString()
        }

        fun _sendRegisterstoServer(context:Context,conteo: String,user: Usuario): MutableList<RegisterData>{
            val registers : MutableList<RegisterData> = mutableListOf()

            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val bd = admin.readableDatabase
                val c: Cursor = bd.rawQuery("select * from codigos where subido = 1", null)
                while(c.moveToNext()){
                    val register = RegisterData()
                    register.setBarcode(c.getString(c.getColumnIndex("codigo")))
                    register.set_Id_Producto(c.getInt(c.getColumnIndex("id_producto")))
                    register.set_Id_Unidad( c.getInt(c.getColumnIndex("id_unidad")))
                    register.set_Id_Ubicacion(c.getInt(c.getColumnIndex("id_ubicacion")))
                    register.set_Cantidad(c.getInt(c.getColumnIndex("cantidad")))
                    register.set_Fecha(c.getString(c.getColumnIndex("fecha_cap")))
                    register.setUserName(user.getUserName())
                    register.set_Conteo(conteo)

                    registers.add(register)
                }
                return registers
                c.close()
                bd.close()
            }catch (liteX: SQLiteException){
                throw SQLiteException("SQlite error cause: ${liteX.cause} \nMessage: "+liteX.message.toString())
            }catch (nullable: NullPointerException){
                throw NullPointerException("Null Element "+nullable.cause+"\nMessage: "+nullable.message.toString())
            }catch (sqlX : SQLException){
                throw SQLException("Server Unstable casue: ${sqlX.cause} \n" +
                        "Error Code: ${sqlX.errorCode}\nMessage: ${sqlX.message.toString()}}")
            }catch (timeout: RuntimeException){
                throw RuntimeException("Server connection time out, cause: ${timeout.cause}\nMessage: ${timeout.message.toString()}")
            }
        }


        fun guardarCodigo(context: Context, product: Product){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val registro = ContentValues()
                registro.put("codigo",product.getBarcode())
                registro.put("cantidad",product.getCantidad())
                registro.put("fecha_cap",product.getDate())
                registro.put("id_producto",product.getIdProduct())
                registro.put("id_ubicacion",product.getIdUbication())
                registro.put("id_unidad", product.getIdUnit())
                registro.put("subido", 1)
                db.insert("codigos",null,registro)
                db.close()
            }catch (liteX: SQLiteException){
                throw  liteX
            }catch (nullex: NullPointerException){
                throw nullex
            }catch (algo: Exception){
                throw algo
            }

        }


        fun getFecha(context: Context): String{
            var fechaget = ""
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT fecha FROM personal",null)
                if(fila.moveToFirst()){
                    fechaget = fila.getString(0)
                }
                fila.close()
                db.close()
            }catch (sqlexc: SQLException){
                throw SQLException("Error SQLite: "+sqlexc.message.toString())
            }
            return fechaget
        }

        fun countCodigos(context:Context): Int{
            var contador = 0
            val admin = SQLiteConnection(context,"administracion",null,1)
            val bd = admin.readableDatabase
            val c: Cursor = bd.rawQuery("select * from codigos where subido = 1", null)
            while(c.moveToNext())
                contador++
            c.close()
            bd.close()
            return contador
        }

        fun _deleteMainCodigos(context: Context): String{
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                db.execSQL("DELETE FROM mainCodigos")
                db.close()
            }catch (sqlexc: SQLException){
                return "Error: "+sqlexc.message.toString()
            }
            return "Completado"
        }

        fun _deleteCodes(context: Context): String{
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                db.execSQL("DELETE FROM codigos")
                db.close()
            }catch (sqlexc: SQLException){
                return "Error: "+sqlexc.message.toString()
            }
            return "Completado"
        }

        fun _updateUserNametoNull(context: Context){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val values = ContentValues()
                values.put("usuario","")
                db.update("personal",values,null,null)
                db.close()
            }catch (liteX: SQLiteException){
                throw liteX
            }
        }

        fun setUserName(context: Context, user: Usuario){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT * FROM personal",null)
                if(fila.moveToFirst())
                    user.setUserName(fila.getString(1))
                fila.close()
                db.close()
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }

        fun getSQLiteData(context: Context, column : String): List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT $column FROM personal",null)
                if(fila.moveToFirst()){
                    if(fila.getString(0).isNotEmpty())
                        list.add(fila.getString(0))
                }

                fila.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }

        }

        fun buscaNombreCodigo(context: Context, codigo_leido: String): String{
            var descripcion = "Producto no encontrado"
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT nombre FROM mainCodigos where barcode = '$codigo_leido' OR codigo_producto = '$codigo_leido'",null)
                if(fila.moveToFirst())
                    descripcion = fila.getString(fila.getColumnIndex("nombre"))
                fila.close()
                db.close()
            }catch (sqlexc: SQLException){
                return "Error SQLite: "+sqlexc.message.toString()
            }
            return descripcion
        }


        fun getUnidades(context: Context, barcode: String): List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val cursor: Cursor = db.rawQuery("SELECT unidad FROM mainCodigos where barcode = '$barcode' or codigo_producto = '$barcode'",null)
                while(cursor.moveToNext())
                    list.add(cursor.getString(cursor.getColumnIndex("unidad")))
                cursor.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }

        fun _deleteUsuario(context: Context): String{
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                db.execSQL("DELETE FROM personal")
                db.close()
            }catch (sqlexc: SQLException){
                return "Error: "+sqlexc.message.toString()
            }
            return "Completado"
        }



        fun getIDUnidad(context: Context,unidad: String): Int {
            var id_unidad = 0
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val cursor: Cursor = db.rawQuery("SELECT DISTINCT id_unidad FROM mainCodigos where unidad = '$unidad'",null)
                if(cursor.moveToFirst())
                    id_unidad = cursor.getInt(cursor.getColumnIndex("id_unidad"))
                cursor.close()
                db.close()
                return id_unidad
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }



        fun getIDProduct(context: Context, barcode: String): Int {
            try {
                var id_producto = 0
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.readableDatabase
                val cursor: Cursor = db.rawQuery("SELECT DISTINCT id_producto FROM mainCodigos where barcode = '$barcode' OR codigo_producto = '$barcode'",null)
                if(cursor.moveToFirst())
                    id_producto = cursor.getInt(cursor.getColumnIndex("id_producto"))
                cursor.close()
                db.close()
                return id_producto
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }


        fun _deleteProductFromSQLite(product: RegisterData, context: Context){
            try{
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val values = ContentValues()
                values.put("subido",0)
                val args = arrayOf(product.get_Id_Producto().toString(),
                    product.get_Id_Unidad().toString(),
                    product.get_Id_Ubicacion().toString())
                if(product.get_Id_Producto() == 0 && product.get_Id_Unidad() == 0){
                    db.update("codigos",values,"codigo=?", arrayOf(product.getBarcode()))
                }else {
                    db.update("codigos",values,"id_producto=? AND id_unidad=? AND id_ubicacion =?",args)
                    println("Producto ${product.get_Id_Producto()} borrado, cantidad: ${product.get_CantidadProducto()}")
                }
            }catch (liteError: SQLiteException){
                throw liteError
            }
        }

        fun isUserExistsInDataBase(context: Context):Boolean{
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val c = db.rawQuery("select * from personal",null)
            if(c.count == 0){
                db.close()
                c.close()
                return false
            }
            db.close()
            c.close()
            return true
        }

        fun isUserNullInDataBase(context: Context):Boolean{
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val c = db.rawQuery("select * from personal",null)
            if(c.moveToFirst()){
                if(c.getString(c.getColumnIndex("usuario")) == ""){
                    db.close()
                    c.close()
                    return true
                }
            }
            db.close()
            c.close()
            return false
        }

        fun createUser(context: Context,user: Usuario){
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val registro = ContentValues()
            registro.put("usuario",user.getUserName())
            db.insert("personal",null,registro)
            db.close()
        }

        fun updateUserName(context: Context,user: Usuario){
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val registro = ContentValues()
            registro.put("usuario",user.getUserName())
            db.update("personal",registro,null,null)
            db.close()
        }

        fun insertProduct(context: Context,product: Product){
            try {

                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val row = ContentValues()
                row.put("codigo",product.getBarcode())
                row.put("cantidad",product.getCantidad())
                row.put("fecha_cap",product.getDate())
                row.put("id_producto",product.getIdProduct())
                row.put("id_ubicacion",product.getIdUbication())
                row.put("id_unidad", product.getIdUnit())
                row.put("subido", 1)
                db.insert("codigos",null,row)
                db.close()
            }catch (liteX: SQLiteException){
                throw  liteX
            }catch (nullex: NullPointerException){
                throw nullex
            }catch (algo: Exception){
                throw algo
            }
        }

        fun updateProduct(context: Context,product: Product){
            try {
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val values = ContentValues()
                values.put("cantidad",product.getCantidad())
                if(product.getIdProduct() != 0 && product.getIdUnit() != 0){
                    val args = arrayOf(product.getIdProduct().toString(),product.getIdUnit().toString())
                    db.update("codigos",values,"id_producto=? AND id_unidad=?",args)
                }else{
                    val args = arrayOf(product.getBarcode())
                    db.update("codigos",values,"codigo=?",args)
                }


            }catch (liteX: SQLiteException){
                throw liteX
            }
        }
        fun deleteProductFromCodesTable(context: Context,product: Product){
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            if(product.getIdProduct() == 0 && product.getIdUnit() == 0){
                db.execSQL("DELETE FROM codigos WHERE codigo = '${product.getBarcode()}'")
            }else
                db.execSQL("DELETE FROM codigos WHERE id_producto = ${product.getIdProduct()} AND id_unidad = ${product.getIdUnit()}")
            db.close()
        }
        fun updateProductIds(product: RegisterData, context: Context){
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val values = ContentValues()
            val args = arrayOf(product.getBarcode())
            values.put("id_producto",product.get_Id_Producto())
            values.put("id_unidad",product.get_Id_Unidad())
            values.put("id_ubicacion",product.get_Id_Ubicacion())
            db.update("codigos",values,"codigo=?",args)
        }
    }
}