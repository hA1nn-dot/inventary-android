package com.example.inventariofsico

class RegisterData : Usuario() {
    private var id_unidad: Int? = null
    private var id_producto: Int? = null
    private var id_ubicacion: Int? = null
    private var cant: Int? = null
    private var fecha: String? = null
    private var conteo: String? = null
    fun set_Id_Unidad(_id_unidad: Int){
        id_unidad = _id_unidad
    }
    fun set_Id_Producto(_id_producto: Int){
        id_producto = _id_producto
    }
    fun set_Id_Ubicacion(_id_ubicacion: Int){
        id_ubicacion = _id_ubicacion
    }
    fun set_Cantidad(_cantidad: Int){
        cant = _cantidad
    }
    fun set_Fecha(_fechaCaptura: String){
        fecha = _fechaCaptura
    }
    fun set_Conteo(_conteo: String){
        conteo = _conteo
    }

    fun get_Id_Unidad(): Int? {
        return id_unidad
    }
    fun get_Id_Producto(): Int? {
        return id_producto
    }
    fun get_Id_Ubicacion(): Int? {
        return id_ubicacion
    }
    fun get_CantidadProducto(): Int? {
        return cant
    }
    fun get_FechaCaptura(): String? {
        return fecha
    }
    fun get_Conteo(): String?{
        return conteo
    }

}