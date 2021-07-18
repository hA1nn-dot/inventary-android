package com.example.inventariofsico

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.AsyncTaskLoader
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.lang.Exception
import java.sql.SQLException


class MainActivity : AppCompatActivity(), View.OnClickListener{

    private var username: String = ""
    private var password: String = ""
    private var usuario: Usuario = Usuario()
    private var exit: Int = 0
    private var carga: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(SQLiteFunction.getMainCodigos(this) != "0"){
            SQLiteFunction.loadUserData(this,usuario)
            changeActivity()
        }

        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        version()
        val btnLogin: Button = findViewById(R.id.btn_login)
        carga = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            getRequest().execute()
        }
    }

    internal inner class getRequest : AsyncTask<Void,Void,String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            carga!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Void?): String? {

            val resultMessage = login()
            return resultMessage
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            carga!!.visibility = View.GONE
            if(result != "Completado"){
                Toast.makeText(this@MainActivity,result,Toast.LENGTH_SHORT).show()
            }else{
                changeActivity()
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_login -> {
                getRequest().execute()
            }
        }
    }

    private fun version(){
        try {
            val versionTxt = findViewById<TextView>(R.id.version)
            var pInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
            var version: String = pInfo.versionName
            versionTxt.text = "Made with Kotlin v$version"
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private  fun login(): String{
            var usuariotxt = findViewById<EditText>(R.id.userTxt)
            var passwordtxt = findViewById<EditText>(R.id.passwordTxt)

            username = usuariotxt.text.toString()
            password = passwordtxt.text.toString()

            try {
                if(username.isNotEmpty() && password.isNotEmpty()){
                        if(SQLfunction.verifyUser(username,password,this)){
                            usuario.setUsuario(username,password,true)
                        }else
                            throw Exception("Usuario o contraseña errónea, favor de verificar.")

                }else throw Exception("Favor de llenar los espacios correspondientes.")

            }catch (p1: SQLException){
                return "SQLException: " + p1.message.toString()
            }catch (p2: ClassNotFoundException) {
                return "Class not found: " + p2.message.toString()
            }catch (p4: InstantiationException){
                return "Problemas de instanciación: " + p4.message.toString()
            }catch (p5: Exception){
                return p5.message.toString()
            }catch (p6: FileNotFoundException){
                return "Error en el archivo de configuración"+ p6.message.toString()
            }
        return "Completado"
    }

    private fun changeActivity(){
        try {
            val intentvar = Intent(this, LoadProductsActivity::class.java)
            intentvar.putExtra("usuarioEnviado",usuario)

            startActivity(intentvar)
        }catch (exp: InstantiationException){
            throw InstantiationException("Error al cargar la nueva ventana")
        }

    }


    override fun onBackPressed() {
        if (exit == 0){
            Toast.makeText(this,"Presione de nuevo para salir",Toast.LENGTH_SHORT).show()
            exit++
        }else{
            super.onBackPressed()
        }

        object: CountDownTimer(3000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                exit = 0
            }
        }.start()
    }

}