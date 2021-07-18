package com.example.inventariofsico

import android.content.Context
import android.os.StrictMode
import java.io.FileNotFoundException
import java.lang.Exception
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException

class SQLServerConnection {

    companion object{
        private var ip: String? = null
        private var instanceName: String? = null
        private var dataBaseName: String? = null
        private var user: String? = null
        private var pass: String? = null
        private var URL: String? = null

        private var connection: Connection? = null
        fun getConnection(context: Context): Connection? {
            if(connection == null || connection!!.isClosed)
                connection = connectSQLServer(context)
            return connection
        }

        private fun connectSQLServer(context: Context): Connection?{
            try{
                var conn: Connection? = null
                var policy: StrictMode.ThreadPolicy? = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
                if(ip.isNullOrEmpty() || dataBaseName.isNullOrEmpty() || instanceName.isNullOrEmpty() || user.isNullOrEmpty() || pass.isNullOrEmpty())
                    URL = getURLConnection()
                conn = DriverManager.getConnection(URL,user,pass)
                return conn
            }catch (exception: SQLException){
                throw SQLException("Error en la conexión, vuelva a intentarlo\n"+exception.message.toString())
            }catch (exceptionClass: ClassNotFoundException){
                throw Exception("Error al cargar la libreria jtds.jar")
            }catch (fileX: FileNotFoundException){
                throw FileNotFoundException("Error en el archivo de configuración: " + fileX.message.toString())
            }

        }

        private fun getURLConnection(): String{
            try{
                val configuration = ServerConfiguration()
                configuration.setServerConfiguration()

                setIP(configuration.getIP())
                setInstance(configuration.getInstance())
                setDataBaseName(configuration.getDataBase())
                setUserName(configuration.getUser())
                setPassword(configuration.getPassword())

            }catch (fileX: FileNotFoundException){
                throw fileX
            }
            return "jdbc:jtds:sqlserver://$ip;instance=$instanceName;databasename=$dataBaseName;"
        }

        private fun setIP(_ip: String){
            ip = _ip
        }
        private fun setInstance(_instance: String){
            instanceName = _instance
        }
        private fun setDataBaseName(_databaseName: String){
            dataBaseName = _databaseName
        }
        private  fun setUserName(_user: String){
            user = _user
        }
        private fun setPassword(_pass: String){
            pass = _pass
        }
    }
}