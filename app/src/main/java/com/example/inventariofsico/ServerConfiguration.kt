package com.example.inventariofsico


import android.os.Environment
import java.io.File
import java.io.FileNotFoundException

class ServerConfiguration {
    private val pathName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "test.txt"
    private var IP : String? = null
    private var DATABASE : String? = null
    private var INSTANCE : String? = null
    private var USER : String? = null
    private var PASSWORD : String? = null

    fun getIP():String{
        return IP.toString()
    }
    fun getDataBase():String{
        return DATABASE.toString()
    }
    fun getInstance():String{
        return INSTANCE.toString()
    }
    fun getUser():String{
        return USER.toString()
    }
    fun getPassword():String{
        return PASSWORD.toString()
    }
    fun setServerConfiguration(){
        try {
            val myFile = File(pathName)
            val lineas = myFile.readLines()
            lineas.forEach { println(it) }

            val lineasLista = mutableListOf<String>()
            myFile.useLines { lines -> lines.forEach { lineasLista.add(it) } }
            lineasLista.forEachIndexed { i, line ->
                when(i){
                    0 -> IP = line
                    1 -> DATABASE = line
                    2 -> INSTANCE = line
                    3 -> USER = line
                    4 -> PASSWORD = line
                }
            }
        }catch (fileX: FileNotFoundException){
            throw fileX
        }
     }
}