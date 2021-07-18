package com.example.inventariofsico

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteConnection(context: Context,name: String, factory: SQLiteDatabase.CursorFactory?,version: Int):
    SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS personal(id integer primary key AUTOINCREMENT,usuario text, almacen text, ubicacion text, conteo text, fecha text)")

        db.execSQL("CREATE TABLE IF NOT EXISTS codigos(id integer primary key AUTOINCREMENT, codigo text, cantidad integer, fecha_cap text,id_producto integer, id_ubicacion integer, id_unidad integer,  subido integer)")

        db.execSQL("CREATE TABLE IF NOT EXISTS mainCodigos(id integer primary key AUTOINCREMENT, barcode text,codigo_producto text,nombre text, id_producto integer, id_ubicacion integer,id_unidad integer, unidad text)")
        /*db.execSQL("CREATE TABLE IF NOT EXISTS referencesSQL(id integer primary key AUTOINCREMENT, ip text, user text, ip text, instance text, databaseName text)")*/
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

}