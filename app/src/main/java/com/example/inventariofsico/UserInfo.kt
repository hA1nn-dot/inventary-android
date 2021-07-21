package com.example.inventariofsico

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class UserInfo {
    companion object{
        fun getAlmacen(context: Context): List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT almacen FROM personal",null)
                if(fila.moveToFirst()){
                    list.add(fila.getString(fila.getColumnIndex("almacen")))
                }

                fila.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }

        fun getUbication(context: Context) :List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT ubicacion FROM personal",null)
                if(fila.moveToFirst()){
                    list.add(fila.getString(fila.getColumnIndex("ubicacion")))
                }

                fila.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }

        fun getFecha(context: Context): List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT fecha FROM personal",null)
                if(fila.moveToFirst()){
                    list.add(fila.getString(fila.getColumnIndex("fecha")))
                }

                fila.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }
        }

        fun getConteo(context: Context): List<String>{
            try {
                val list: MutableList<String> = mutableListOf()
                val admin = SQLiteConnection(context,"administracion",null,1)
                val db = admin.writableDatabase
                val fila = db.rawQuery("SELECT conteo FROM personal",null)
                if(fila.moveToFirst()){
                    list.add(fila.getString(fila.getColumnIndex("conteo")))
                }

                fila.close()
                db.close()
                return list
            }catch (sqlexc: SQLiteException){
                throw sqlexc
            }

        }

        fun setUserInfoIntoLocalDataBase(context: Context,userInfo: Map<String,String>){
            val admin = SQLiteConnection(context,"administracion",null,1)
            val db = admin.writableDatabase
            val registro = ContentValues()
            registro.put("almacen",userInfo["almacen"])
            registro.put("ubicacion",userInfo["ubicacion"])
            registro.put("fecha",userInfo["fecha"])
            registro.put("conteo",userInfo["conteo"])
            db.update("personal",registro,null,null)
            db.close()

        }
    }
}