package com.example.inventariofsico

import android.content.Context
import android.os.AsyncTask
import java.sql.ResultSet
import java.sql.Statement

class BackgroundTask {
    companion object{
        internal class BackUpTableTasks(registers:MutableList<RegisterData>,context: Context)
            : AsyncTask<Void,Void,String>(){

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: Void?): String {
                return ""
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }

        }
    }

}