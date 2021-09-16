package com.example.onlineTest

import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiManager {

    private var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        val shared = ApiManager()
    }

    fun fetchAllUsers(query: String, perPage: Int, page: Int, completion: (Boolean, ArrayList<UserModel>) -> Unit) {

        val url = "https://api.github.com/search/users?q=$query in:login&per_page=$perPage&page=$page"
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Content-Type","application/json")
            .build()

        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                val users : ArrayList<UserModel> = ArrayList()
                override fun onFailure(call: Call, e: IOException) {
                    println(e.localizedMessage)
                    completion(false, users)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val data = JSONObject(it.body!!.string())
                        if (data.has("items")) {
                            val resData = data.getJSONArray("items")
                            for (i in 0 until resData.length()) {
                                val item = resData.getJSONObject(i)
                                val gson = Gson()
                                val user = gson.fromJson(item.toString(), UserModel::class.java)
                                users.add(user)
                            }

                            completion(true, users)
                        } else {
                            completion(false, users)
                        }
                    }
                }
            })
    }

}