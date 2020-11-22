package com.fei.pavement

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_see_all_keng.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Double.parseDouble


class SeeAllKeng : AppCompatActivity() {
    var allSee=ArrayList<See>()
    val me_context=baseContext
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_all_keng)
        allSee.add(See(28.07684, 113.021069))
        allSee.add(See(28.077677, 113.020791))
        allSee.add(See(28.074226, 113.022039))
        allSee.add(See(28.070027, 113.023647))
        allSee.add(See(28.068082, 113.018235))
        allSee.add(See(28.066735, 113.014013))
        allSee.add(See(28.065301, 113.015226))
        Log.e("feifei?","??????")
        val retrofit = Retrofit.Builder()
            .baseUrl("http://223.4.183.204:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val appService = retrofit.create(ThingsService::class.java)
        appService.searchPage("1","200").enqueue(object : Callback<SearchPage> {
            override fun onFailure(call: Call<SearchPage>, t: Throwable) {
                Log.e("feifei",t.message)
            }

            override fun onResponse(call: Call<SearchPage>, response: Response<SearchPage>) {
                val things1 = response.body()
                if (things1 != null) {

                    Log.e("feifei",allSee.size.toString())
                    try {
                    for(things in things1.list){
                        allSee.add(See(parseDouble(things.longitude), parseDouble(things.latitude)))
                    }
                    Log.e("feifei",allSee.size.toString())

//            val list=intent.getSerializableExtra("allSee")
                        val layoutMessage = LinearLayoutManager(me_context)
                        recycler_view.layoutManager = layoutMessage
                        val adapter =SeeAdapter (allSee)
                        recycler_view?.adapter = adapter
                    }catch (e:Exception){
                        Log.e("feifei",e.message)
                    }
                } else {
                    Log.e("feifei2","shibai")
                }
            }

        })



    }
}