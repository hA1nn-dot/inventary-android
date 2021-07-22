package com.example.inventariofsico

import android.content.Context

class Product : SQLiteFunction() {

    private var barcode = ""
    private var unitText = ""
    private var cantidad = 0

    private var almacenName = ""
    private var ubicationName = ""

    private var idProduct = 0
    private var idUnit = 0
    private var idAlmacen = 0

    private var product: Product? = null

    fun setBarCode(_barcode: String){
        barcode = cleanField(_barcode) ?: ""
    }
    fun setAmount(_amount: String){
        cantidad = _amount.toInt() ?: 0
    }
    fun setUnit(_unit: String){
        unitText = _unit ?: ""
    }

    fun getBarcode(): String{
        return barcode
    }

    fun getUnitText(): String{
        return unitText
    }

    fun getIdProduct(): Int{
        return idProduct
    }

    fun getIdUnit(): Int{
        return idUnit
    }

    private fun setIdProduct(_idProduct: Int){
         idProduct = _idProduct
    }
    private fun setIdUnit(_idUnit: Int){
        idUnit = _idUnit
    }

    companion object{


        private fun findIdsProduct(context: Context, product: Product){
            product.setIdProduct(getIDProduct(context,product.getBarcode()))
            product.setIdUnit(getIDUnidad(context,product.getUnitText()))
        }
    }


    private fun cleanField(field: String): String{
        return field.replace(" ","")
    }

}