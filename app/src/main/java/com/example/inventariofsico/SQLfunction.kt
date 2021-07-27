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

                var connection = SQLServerConnection.getConnection(context)
                if (connection != null) {
                    var stat: Statement = connection.createStatement()
                    var resultSet: ResultSet = stat.executeQuery(
                        "SELECT DISTINCT P.ID,P.PRODUCTO,P.NOMBRE,PP.PRODUCTO,PP.UNIDAD_MEDIDA_EQUIVALENCIA,PP.CODIGO_BARRAS,PU.UBICACION,U.UNIDAD from PRODUCTOS as P "
                                + "INNER JOIN PRODUCTOS_PRECIOS as PP on P.ID = PP.PRODUCTO AND P.STATUS = 1 "
                                + "INNER JOIN PRODUCTOS_UBICACIONES as PU on PU.PRODUCTO = PP.PRODUCTO "
                                + "INNER JOIN UNIDADES as U on U.ID = PP.UNIDAD_MEDIDA_EQUIVALENCIA "
                                + "INNER JOIN CONTEOS_IF as CI on CI.PRODUCTO = P.ID and CI.FECHA = '$fecha' "
                                + "AND PU.UBICACION = $id_ubicacion"
                    )

                    while (resultSet.next()) {
                        registroCodigo.put("id_producto", resultSet.getInt(1))
                        registroCodigo.put("codigo_producto", resultSet.getString(2))
                        registroCodigo.put("nombre", resultSet.getString(3))
                        registroCodigo.put("id_unidad", resultSet.getInt(5))
                        registroCodigo.put("barcode", resultSet.getString(6))
                        registroCodigo.put("id_ubicacion", resultSet.getInt(7))
                        registroCodigo.put("unidad", resultSet.getString(8))
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

        fun getCountProductFromServer(context: Context, date: String, id_ubicacion: Int): Int {
            var connection = SQLServerConnection.getConnection(context)
            var count = 0
            if (connection != null) {
                var stat: Statement = connection.createStatement()
                var resultSet: ResultSet = stat.executeQuery(
                    "Select distinct CI.FECHA,CI.PRODUCTO,PP.CODIGO_BARRAS,PP.UNIDAD_MEDIDA_EQUIVALENCIA from CONTEOS_IF as CI" +
                            "Inner join PRODUCTOS as P on P.ID = CI.PRODUCTO and P.STATUS = 1" +
                            "Inner join PRODUCTOS_PRECIOS as PP on PP.PRODUCTO = CI.PRODUCTO" +
                            "inner join PRODUCTOS_UBICACIONES as PU on PU.PRODUCTO = CI.PRODUCTO" +
                            "and PU.UBICACION = $id_ubicacion and CI.FECHA = '$date'"
                )

                while (resultSet.next()) {
                    count++
                }
            }
            return count
        }
    }

}