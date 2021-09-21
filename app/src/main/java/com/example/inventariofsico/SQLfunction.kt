package com.example.inventariofsico

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.NetworkOnMainThreadException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList

class SQLfunction {
    companion object {
        fun verifyUser(userName: String, password: String, context: Context): Boolean {
            var verified = false
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet =
                    stat.executeQuery("Select USUARIO,PASSWORD From USUARIOS")
                while (resultSet.next()) {
                    if (userName.equals(resultSet.getString("USUARIO"))) {
                        if (password.equals(resultSet.getString("PASSWORD")))
                            verified = true
                    }
                }
            }
            return verified
        }

        fun getAlmacenes(context: Context): List<String> {
            var almacenesList: MutableList<String> = mutableListOf()
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet = stat.executeQuery("Select Id,NOMBRE From ALMACENES")
                while (resultSet.next()) {
                    almacenesList.add(resultSet.getString("NOMBRE"))
                }
            }

            return almacenesList
        }

        fun getIDAlmacen(nombreAlmacen: String, context: Context): String {
            var id_almacen = ""
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet =
                    stat.executeQuery("Select Id From ALMACENES WHERE NOMBRE = '$nombreAlmacen'")
                while (resultSet.next()) {
                    id_almacen = resultSet.getString("Id")
                }
            }
            return id_almacen
        }

        fun getIDUbicacion(nombreUbicacion: String, context: Context): Int {
            var id_ubicacion = 0
            try {
                var connection = SQLServerConnection.getConnection(context)
                if (connection != null) {
                    var stat: Statement = connection.createStatement()
                    var resultSet: ResultSet =
                        stat.executeQuery("Select ID From UBICACIONES WHERE CLAVE_UBICACION = '$nombreUbicacion'")
                    while (resultSet.next()) {
                        id_ubicacion = resultSet.getInt("ID")
                    }
                }
                return id_ubicacion
            } catch (exp: SQLException) {
                throw exp
            } catch (net: NetworkOnMainThreadException) {
                throw net
            }


        }

        fun getUbicaciones(id: Int, context: Context): List<String> {
            var ubicacionesList: MutableList<String> = mutableListOf()
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet =
                    stat.executeQuery("Select CLAVE_UBICACION From UBICACIONES WHERE ALMACEN = $id")
                while (resultSet.next()) {
                    ubicacionesList.add(resultSet.getString("CLAVE_UBICACION"))
                }
            }

            return ubicacionesList
        }

        fun isInventaryExist(fecha: String, context: Context): Boolean {
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet =
                    stat.executeQuery("SELECT DISTINCT FECHA FROM CONTEOS_IF WHERE FECHA = '$fecha'")
                while (resultSet.next()) {
                    return true
                }
            }
            return false
        }


        fun getBarcodes(id_ubicacion: Int, fecha: String, context: Context): String {
            try {
                val admin = SQLiteConnection(context, "administracion", null, 1)
                val db = admin.writableDatabase
                val registroCodigo = ContentValues()
                var comprobarProductos = false
                var connection = SQLServerConnection.getConnection(context)
                if (connection != null) {
                    var stat: Statement = connection.createStatement()
                    var resultSet: ResultSet = stat.executeQuery(
                        "SELECT CI.PRODUCTO,P.PRODUCTO,P.NOMBRE,CI.UNIDAD,PP.CODIGO_BARRAS,U.UNIDAD FROM CONTEOS_IF as CI " +
                                "INNER JOIN PRODUCTOS as P on P.ID = CI.PRODUCTO AND P.STATUS = 1 "+
                                "INNER JOIN PRODUCTOS_PRECIOS as PP on PP.PRODUCTO = CI.PRODUCTO AND PP.UNIDAD_MEDIDA_EQUIVALENCIA = CI.UNIDAD "+
                                "INNER JOIN UNIDADES as U on U.ID = CI.UNIDAD "+
                                "where CI.UBICACION = $id_ubicacion AND CI.FECHA = '$fecha'"
                    )

                    while (resultSet.next()) {
                        registroCodigo.put("id_producto", resultSet.getInt(1))
                        registroCodigo.put("codigo_producto", resultSet.getString(2))
                        registroCodigo.put("nombre", resultSet.getString(3))
                        registroCodigo.put("id_unidad", resultSet.getInt(4))
                        registroCodigo.put("barcode", resultSet.getString(5))
                        registroCodigo.put("id_ubicacion", id_ubicacion)
                        registroCodigo.put("unidad", resultSet.getString(6))
                        db.insert("mainCodigos", null, registroCodigo)
                        comprobarProductos = true
                    }
                    if(!comprobarProductos)
                        return "No hay conteo para ésta área"
                }
            } catch (exp: SQLException) {
                return "Error al cargar los datos " + exp.message.toString()
            } catch (timeEx: RuntimeException) {
                return "El tiempo de carga ha finalizado, favor de intentarlo de nuevo:\n " + timeEx.message.toString()
            }
            return "Completado"
        }

        fun getBarcodesRioVerde(id_ubicacion: Int, fecha: String, context: Context): String {
            try {
                val admin = SQLiteConnection(context, "administracion", null, 1)
                val db = admin.writableDatabase
                val registroCodigo = ContentValues()
                var connection = SQLServerConnection.getConnection(context)
                if (connection != null) {
                    var stat: Statement = connection.createStatement()
                    var resultSet: ResultSet = stat.executeQuery(
                        "SELECT CI.PRODUCTO,P.PRODUCTO,P.NOMBRE,CI.UNIDAD,PP.CODIGO_BARRAS,U.UNIDAD FROM CONTEOS_IF as CI " +
                                "INNER JOIN PRODUCTOS as P on P.ID = CI.PRODUCTO AND P.STATUS = 1 "+
                                "INNER JOIN PRODUCTOS_PRECIOS as PP on PP.PRODUCTO = CI.PRODUCTO AND PP.UNIDAD_MEDIDA_EQUIVALENCIA = CI.UNIDAD "+
                                "INNER JOIN UNIDADES as U on U.ID = CI.UNIDAD "+
                                "where CI.UBICACION = $id_ubicacion AND CI.FECHA = '$fecha'"
                    )

                    while (resultSet.next()) {
                        registroCodigo.put("id_producto", resultSet.getInt(1))
                        registroCodigo.put("codigo_producto", resultSet.getString(2))
                        registroCodigo.put("nombre", resultSet.getString(3))
                        registroCodigo.put("id_unidad", resultSet.getInt(4))
                        registroCodigo.put("barcode", resultSet.getString(5))
                        registroCodigo.put("id_ubicacion", id_ubicacion)
                        registroCodigo.put("unidad", resultSet.getString(6))
                        db.insert("mainCodigos", null, registroCodigo)
                    }
                }
            } catch (exp: SQLException) {
                return "Error al cargar los datos " + exp.message.toString()
            } catch (timeEx: RuntimeException) {
                return "El tiempo de carga ha finalizado, favor de intentarlo de nuevo:\n " + timeEx.message.toString()
            }
            return "Completado"
        }


        fun getCurrentConteo(
            conteo: String?,
            id_producto: Int?,
            id_unidad: Int?,
            id_ubicacion: Int?,
            fecha: String?,
            conn: Connection?
        ): Int {
            var queryString = ""
            var currentConteo = 0
            if (conteo == "Conteo 1") {
                queryString = "SELECT CONTEO1 FROM CONTEOS_IF WHERE FECHA = '$fecha' " +
                        "AND PRODUCTO = $id_producto " +
                        "AND UNIDAD = $id_unidad " +
                        "AND UBICACION = $id_ubicacion"
            } else if (conteo == "Conteo 2") {
                queryString = "SELECT CONTEO2 FROM CONTEOS_IF WHERE FECHA = '$fecha' " +
                        "AND PRODUCTO = $id_producto " +
                        "AND UNIDAD = $id_unidad " +
                        "AND UBICACION = $id_ubicacion"
            }

            var stat: Statement = conn!!.createStatement()
            var resultSet = stat.executeQuery(queryString)
            if (resultSet.next())
                currentConteo = resultSet.getInt(1)
            return currentConteo
        }

        fun getExistenceOfConteo(
            id_producto: Int?,
            id_unidad: Int?,
            id_ubicacion: Int?,
            fecha: String?,
            conn: Connection?
        ): Int {
            var existence = 0
            var queryString = ""
            var stat: Statement = conn!!.createStatement()
            queryString = "SELECT EXISTENCIA FROM CONTEOS_IF WHERE FECHA = '$fecha' " +
                    "AND PRODUCTO = $id_producto " +
                    "AND UNIDAD = $id_unidad " +
                    "AND UBICACION = $id_ubicacion"

            var resultSet: ResultSet = stat.executeQuery(queryString)
            if (resultSet.next())
                existence = resultSet.getInt(1)
            return existence
        }

        fun searchIdProduct(context: Context, product: RegisterData): Map<String, Int> {
            var idProductMap: MutableMap<String, Int> = mutableMapOf()
            val almacenName = UserInfo.getAlmacenName(context)
            var connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                val query =
                    "SELECT PP.PRODUCTO,P.UNIDAD,PU.UBICACION from PRODUCTOS_PRECIOS as PP Inner join PRODUCTOS as P on P.ID = PP.PRODUCTO inner join PRODUCTOS_UBICACIONES as PU on PU.PRODUCTO = P.ID and PU.PRODUCTO = PP.PRODUCTO inner join UBICACIONES as U on U.ID = PU.UBICACION inner join UNIDADES as UN on UN.ID = P.UNIDAD inner join ALMACENES as AL on U.ALMACEN = AL.Id  AND AL.ALMACEN = '$almacenName' where CODIGO_BARRAS = '${product.getBarcode()}'"
                var r = stat.executeQuery(query)
                if (r.isFirst) {
                    idProductMap["idProduct"] = r.getInt(0)
                    idProductMap["idUnit"] = r.getInt(1)
                    idProductMap["idUbication"] = r.getInt(2)
                }
            }
            return idProductMap
        }

        fun updateProductInCONTEOS_IFTable(product: RegisterData,context: Context){
            var queryString = ""
            val connection = SQLServerConnection.getConnection(context)
            if (connection != null) {
                if (product.get_Id_Producto() == 0 && product.get_Id_Ubicacion() == 0 && product.get_Id_Ubicacion() == 0) {
                    val idFoundProduct = searchIdProduct(context, product)
                    if (idFoundProduct.count() != 0) {
                        product.set_Id_Producto(idFoundProduct["idProduct"] ?: 0)
                        product.set_Id_Ubicacion(idFoundProduct["idUbication"] ?: 0)
                        product.set_Id_Unidad(idFoundProduct["idUnit"] ?: 0)
                    }
                }


                val conteoSQL = getCurrentConteo(
                    product.get_Conteo(),
                    product.get_Id_Producto(),
                    product.get_Id_Unidad(),
                    product.get_Id_Ubicacion(),
                    product.get_FechaCaptura(),
                    connection,
                )
                val existenceProduct = getExistenceOfConteo(
                    product.get_Id_Producto(),
                    product.get_Id_Unidad(),
                    product.get_Id_Ubicacion(),
                    product.get_FechaCaptura(),
                    connection
                )
                val currentConteo = conteoSQL + product.get_CantidadProducto()!!
                val conteoDifference = currentConteo - existenceProduct
                if (product.get_Conteo() == "Conteo 1") {
                    queryString = "UPDATE CONTEOS_IF SET CONTEO1 = $currentConteo," +
                            " USUARIO1 = '${product.getUserName()}'," +
                            " DIFERENCIA = $conteoDifference" +
                            " WHERE PRODUCTO = ${product.get_Id_Producto()}" +
                            " AND FECHA = '${product.get_FechaCaptura()}'" +
                            " AND UNIDAD = ${product.get_Id_Unidad()}" +
                            " AND UBICACION = ${product.get_Id_Ubicacion()}"
                } else if (product.get_Conteo() == "Conteo 2") {
                    queryString = "UPDATE CONTEOS_IF SET CONTEO2 = $currentConteo," +
                            " USUARIO2 = '${product.getUserName()}'," +
                            " DIFERENCIA = $conteoDifference" +
                            " WHERE PRODUCTO = ${product.get_Id_Producto()}" +
                            " AND FECHA = '${product.get_FechaCaptura()}'" +
                            " AND UNIDAD = ${product.get_Id_Unidad()}" +
                            " AND UBICACION = ${product.get_Id_Ubicacion()}"
                }

                val stat: Statement = connection.createStatement()
                stat.executeUpdate(queryString)
            }
        }

        fun isProductExistsInBackUpTable(register: RegisterData,context: Context): Boolean{
            var connection = SQLServerConnection.getConnection(context)
            val commandString = "SELECT ID FROM CONTEOS_IF_IMPORTA "+
                    "WHERE UBICACION = ${register.get_Id_Ubicacion()} "+
                    "AND PRODUCTO = ${register.get_Id_Producto()} "+
                    "AND UNIDAD = ${register.get_Id_Unidad()} "+
                    "AND FECHA = CONVERT(datetime,'${register.get_FechaCaptura()}',101)"
            if(connection != null){
                val stat: Statement = connection.createStatement()
                val resultSet = stat.executeQuery(commandString)
                if(resultSet.next())
                    if(resultSet.getInt(1) != 0)
                        return true
            }
            return false
        }

        fun insertProductInBackUpTable(register: RegisterData,context: Context){
            var commandString = ""
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                if(register.get_Conteo() == "Conteo 1"){
                    commandString = "INSERT INTO CONTEOS_IF_IMPORTA "+
                            "(FECHA,UBICACION,PRODUCTO,UNIDAD,CONTEO1,USUARIOC1,STATUS) " +
                            "VALUES ("+
                            "CONVERT(datetime,'${register.get_FechaCaptura()}',101),"+
                            "${register.get_Id_Ubicacion()},"+
                            "${register.get_Id_Producto()},"+
                            "${register.get_Id_Unidad()},"+
                            "${register.get_CantidadProducto()},"+
                            "'${register.getUserName()}',1)"
                }else if(register.get_Conteo() == "Conteo 2"){
                    commandString = "INSERT INTO CONTEOS_IF_IMPORTA "+
                            "(FECHA,UBICACION,PRODUCTO,UNIDAD,CONTEO2,USUARIOC2,STATUS) " +
                            "VALUES ("+
                            "CONVERT(datetime,'${register.get_FechaCaptura()}',101),"+
                            "${register.get_Id_Ubicacion()},"+
                            "${register.get_Id_Producto()},"+
                            "${register.get_Id_Unidad()},"+
                            "${register.get_CantidadProducto()},"+
                            "'${register.getUserName()}',1)"
                }else
                    throw Exception("Conteo vacío")
                if(connection != null && commandString != "") {
                    val stat: Statement = connection.createStatement()
                    stat.execute(commandString)
                    println("Producto ${register.get_Id_Producto()} insertado")
                }

            }else
                throw SQLException("Conexión perdida con el servidor, favor de verificar")

        }

        fun updateProductInBackUpTable(register: RegisterData,context: Context){
            var commandString = ""
            val conteoEnLector: Int = register.get_CantidadProducto()!!
            val conteoEnSistema: Int = getConteoInBackUpTable(register,context)
            val conteoUpdated = conteoEnLector + conteoEnSistema
            if(register.get_Conteo() == "Conteo 1"){
                commandString = "UPDATE CONTEOS_IF_IMPORTA set CONTEO1 = $conteoUpdated"+
                        "WHERE PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()} "+
                        "AND FECHA = CONVERT(datetime,'${register.get_FechaCaptura()}',101) "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()}"
            }else if(register.get_Conteo() == "Conteo 2"){
                commandString = "UPDATE CONTEOS_IF_IMPORTA set CONTEO2 = $conteoUpdated"+
                        "WHERE PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()} "+
                        "AND FECHA = CONVERT(datetime,'${register.get_FechaCaptura()}',101) "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()}"
            }else
                throw Exception("Conteo vacío")
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                val stat: Statement = connection.createStatement()
                stat.executeUpdate(commandString)
            }else
                throw SQLException("Conexión perdida con el servidor, favor de verificar")
        }

        fun getConteoInBackUpTable(register: RegisterData,context: Context): Int{
            var cantidadObtenida = 0
            var commandString = ""
            if(register.get_Conteo() == "Conteo 1"){
                commandString = "SELECT CONTEO1 FROM CONTEOS_IF_IMPORTA "+
                        "where PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()} "+
                        "AND FECHA = CONVERT(datetime,'${register.get_FechaCaptura()}',101) "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()}"
            }else if(register.get_Conteo() == "Conteo 2"){
                commandString = "SELECT CONTEO2 FROM CONTEOS_IF_IMPORTA "+
                        "where PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()} "+
                        "AND FECHA = CONVERT(datetime,'${register.get_FechaCaptura()}',101) "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()}"
            }else
                throw  Exception("Conteo vacío")
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                val stat: Statement = connection.createStatement()
                val resultSet: ResultSet = stat.executeQuery(commandString)
                while (resultSet.next()){
                    cantidadObtenida = resultSet.getInt(1)
                }
            }else
               throw SQLException("Conexión perdida con el servidor, favor de verificar")
            return cantidadObtenida
        }

        fun isThisProductLoadedCorrectly(register: RegisterData,context: Context): Boolean{
            var commandString = ""
            if(register.get_Conteo() == "Conteo 1"){
                commandString = "SELECT * FROM CONTEOS_IF as CI "+
                        "WHERE EXISTS (SELECT * from CONTEOS_IF_IMPORTA as CI2 " +
                        "where CI2.PRODUCTO = CI.PRODUCTO " +
                        "AND CI2.UNIDAD = CI.UNIDAD " +
                        "AND CI2.CONTEO1 = CI.CONTEO1 " +
                        "AND CI2.UBICACION = CI.UBICACION) " +
                        "AND CI.FECHA = '${register.get_FechaCaptura()}' "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()} "+
                        "AND PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()}"
            }else if(register.get_Conteo() == "Conteo 2"){
                commandString = "SELECT * FROM CONTEOS_IF as CI "+
                        "WHERE EXISTS (SELECT * from CONTEOS_IF_IMPORTA as CI2 " +
                        "where CI2.PRODUCTO = CI.PRODUCTO " +
                        "AND CI2.UNIDAD = CI.UNIDAD " +
                        "AND CI2.CONTEO2 = CI.CONTEO2 " +
                        "AND CI2.UBICACION = CI.UBICACION) " +
                        "AND CI.FECHA = '${register.get_FechaCaptura()}' "+
                        "AND UBICACION = ${register.get_Id_Ubicacion()} "+
                        "AND PRODUCTO = ${register.get_Id_Producto()} "+
                        "AND UNIDAD = ${register.get_Id_Unidad()}"
            }else
                throw Exception("No hay Conteo")
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                val stat: Statement = connection.createStatement()
                val resultSet: ResultSet = stat.executeQuery(commandString)
                if (resultSet.next()){
                    return true
                }
            }else
                throw SQLException("Conexión perdida con el servidor, favor de verificar")
            return false
        }
        fun searchProductIds(product: RegisterData,context: Context)
        {
            var commandString = "SELECT PP.PRODUCTO, PP.UNIDAD_MEDIDA_EQUIVALENCIA,CI.UBICACION FROM PRODUCTOS_PRECIOS as PP " +
                    "INNER JOIN CONTEOS_IF as CI on CI.PRODUCTO = PP.PRODUCTO  AND CI.UNIDAD = PP.UNIDAD_MEDIDA_EQUIVALENCIA " +
                    "INNER JOIN UBICACIONES as U on U.ALMACEN = ${getIdAlmacenByName(context)} AND U.ID = CI.UBICACION "+
                    "WHERE PP.CODIGO_BARRAS = '${product.getBarcode()}' AND CI.FECHA = '${product.get_FechaCaptura()}'"
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                val stat: Statement = connection.createStatement()
                val resultSet: ResultSet = stat.executeQuery(commandString)
                if (resultSet.next()){
                    product.set_Id_Producto(resultSet.getInt(1))
                    product.set_Id_Unidad(resultSet.getInt(2))
                    product.set_Id_Ubicacion(resultSet.getInt(3))
                }
            }else
                throw SQLException("Conexión perdida con el servidor, favor de verificar")
        }

        private fun getIdAlmacenByName(context: Context): Int
        {
            val almacen: String = UserInfo.getAlmacenName(context)
            var id = 0;
            var commandString = "SELECT Id FROM ALMACENES where NOMBRE = '${almacen}'"
            val connection = SQLServerConnection.getConnection(context)
            if(connection != null){
                val stat: Statement = connection.createStatement()
                val resultSet: ResultSet = stat.executeQuery(commandString)
                if (resultSet.next()){
                    id = resultSet.getInt(1)
                }
            }else
                throw SQLException("Conexión perdida con el servidor, favor de verificar")
            return id
        }
    }

}