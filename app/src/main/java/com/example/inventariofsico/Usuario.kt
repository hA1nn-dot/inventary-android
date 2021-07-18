package com.example.inventariofsico

import java.io.Serializable

open class Usuario : Serializable{
    private var enable = false
    private var name = ""
    private var password = ""

    constructor(){
        enable = false
        name = ""
        password = ""
    }

    fun setUsuario(_name: String, _password: String, _enable: Boolean){
        name = _name
        password = _password
        enable = _enable
        if(_name.isEmpty() || _password.isEmpty()){
            throw Exception("Alguno de los campos están vacios.")
        }
    }

    fun isAvailable(): Boolean{
        return enable
    }
    fun getUserName(): String{
        return name
    }
    fun getPassword(): String{
        return password
    }
    fun setUserName(_name: String){
        name = _name
    }
}