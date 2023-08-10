package com.blackdiamondstudios.android.simpleapicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.sql.Connection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsynctask("Denis", "123456").execute()
    }

    private inner class CallAPILoginAsynctask(val username: String, val password: String): AsyncTask <Any, Void , String>(){
        lateinit var customProgressDialog : Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null
//launching a connection request to the server
            try{
                val url = URL("https://run.mocky.io/v3/52bc8949-610c-47df-850a-281fd1ad3f56")
            connection = url.openConnection() as HttpURLConnection
               //useful to test the data transfer property of the input and out put

                connection.doInput = true
                connection.doOutput = true


                //sending a response to the connection server

                connection.instanceFollowRedirects = false
                //http rtequest type
                connection.requestMethod = "POST"
                //setting the request properties
                connection.setRequestProperty("Content-Type","Application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")
                //tunnel through caches for an internet connection
                connection.useCaches = false
                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("Username",username)
                jsonRequest.put("Password",password)
                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()



                val httpResult: Int = connection.responseCode
//to test if the connection went well with respect to its connection status JSON code
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try{

                        //to go through the lines in thw url website link to test
                        while(reader.readLine().also { line = it}!= null ){
                            stringBuilder.append(line + "\n") //appends the lines of strings from the website
                        }

                    }catch(e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch(e:IOException){
                            e.printStackTrace()
                        }

                    }
            result = stringBuilder.toString()

                }else {
                    result = connection.responseMessage
                }

            }
            catch (e: SocketTimeoutException) //PoorInternet timeout error catch
            {
        result = "connection timeout "
            }catch(e:Exception){
                result = "Error: " + e.message
            }finally{
                connection?.disconnect()
            }
            return result
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            Log.i("JSON RESPONSE RESULT ", result!!)

            //alternative approach of listing the JSON objects using the third party library GSON
            val responseData = Gson().fromJson(result, ResponseData::class.java)
            Log.i("Message",responseData.message)
            Log.i("User_id","${responseData.user_id}")
            Log.i("my_matter","${responseData.myMatter}")

            Log.i("data_list", "${responseData.profile_details.profile_completed}")
            Log.i("data_list", "${responseData.profile_details.rating}")

            for(item in responseData.data_list.indices){
                Log.i("Value $item", "${responseData.data_list[item]}")

                Log.i("user_id $item", "${responseData.data_list[item].user_id}")
                Log.i("my_matter $item", "${responseData.data_list[item].my_matter}")
            }

/*      (normal way to implement JSON object calls without the third party library GSON

            //to test the access of objects via our JSON object
            //direct objects first (match the type of variable to every JSON declaration line
            val jsonObject = JSONObject(result)
            val message = jsonObject.optString("message")
            Log.i("Message", message)
            val userId = jsonObject.optInt("User_id")
            Log.i("The User Id", "$userId")
            val myMatter= jsonObject.optString("My matter")
            Log.i("The My matter", myMatter)

            //for a JSON object in another JSON object we use this approach
            val profileDetails= jsonObject.optJSONObject("Profile details")
            val isProfileCompleted = profileDetails.optBoolean("profile_completed")
            Log.i("Is Profile Completed", "$isProfileCompleted")

            //for a list of JSON objects , we use this approach
            val dataListArray = jsonObject.optJSONArray("data_list")
            //amount of entries in the list
            Log.i("Data list size", "${dataListArray.length()}")
            //to display every element of the list
            for (item in 0 until dataListArray.length()){
                Log.i("Value $item", "${dataListArray[item]}")

                val dataItemObject: JSONObject = dataListArray[item] as JSONObject
                // now we can declare each object in the list as normally
                val userIde = dataItemObject.optInt("user_id")
                Log.i("user_id","$userIde")
                val my_matter = dataItemObject.optString("my_matter")
                Log.i("my_matter",my_matter)
            }
*/
        }


        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }
    }
}