package com.example.inventariofsico

import android.content.Context

class Product : SQLiteFunction() {

    private var barcode = ""
    private var unitText = ""
    private var cantidad = 0
    private var date = ""

    private var almacenName = ""
    private var ubicationName = ""

    private var idProduct = 0
    private var idUnit = 0
    private var idUbication = 0

    private var product: Product? = null

    fun setBarCode(_barcode: String){
        barcode = cleanField(_barcode)
    }
    fun setAmount(_amount: String){
        cantidad = if(_amount.isNullOrEmpty()) 0
        else _amount.toInt()
    }
    fun setUnitText(_unit: String){
        unitText = _unit ?: ""
    }

    fun getBarcode(): String{
        return barcode
    }

    fun getCantidad(): Int{
        return cantidad
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

    fun getDate(): String{
        return date
    }
    fun getIdUbication(): Int{
        return idUbication
    }

    private fun setIdProduct(_idProduct: Int){
         idProduct = _idProduct
    }
    private fun setIdUnit(_idUnit: Int){
        idUnit = _idUnit
    }
    private fun setIdUbication(_idUbication: Int){
        idUbication = _idUbication
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


    fun setIdsProduct(context: Context){
        setIdProduct(getIDProduct(context,getBarcode()))
        setIdUnit(getIDUnidad(context,getUnitText()))
        setIdUbication(getUbicacion(context,getIdProduct(),getIdUnit()))
    }

    fun setDate(context: Context){
        date = getFecha(context)
    }

    fun isExistsInMainCodesTable(context: Context):Boolean{
        if(isCodeExistsInMainCodesTableById(context,getIdUnit(),getIdProduct()))
            return true
        return false
    }

    fun isInCodesTable(context: Context):Boolean{
        if(getIdProduct() != 0 && getIdUnit() != 0){
            if(isCodeExistsinCodesTableById(context, getIdUnit(),getIdProduct()))
                return true
            return false
        }
        if(isCodeExistsInCodesTableByBarcode(context,getBarcode()))
            return true
        return false
    }

}