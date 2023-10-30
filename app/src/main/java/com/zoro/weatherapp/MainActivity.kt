package com.zoro.weatherapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.zoro.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition


//37d018edde47a04b1f777cbbfb889f18
class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)
        fetchWeatherData("jaipur")
        SearchCity()
    }

    private fun SearchCity() {
         val searchView =  binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if(query != null)
                    fetchWeatherData( query)
                    return true

            }

            override fun onQueryTextChange(p0: String?): Boolean {
               return true
            }

        })

    }



    private fun fetchWeatherData(cityName: String ) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherDate(cityName,"jaipur", "37d018edde47a04b1f777cbbfb889f18", "metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val wind = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.pressure
                    val max = responseBody.main.temp_max
                    val min = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main ?: "Unknown"


                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.max.text = "max $max °C"
                    binding.min.text = "min $max °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$wind"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$sea hpa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding cityName.text = "$cityName"


                    //Log.d("TAG","onResponse:$temperature")
                    changeImageAccordingToWeatherCondition(condition)
                }

            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun changeImageAccordingToWeatherCondition(conditions: String) {
     when (conditions){
         "clear sky", "sunny"->{
             binding.root.setBackgroundResource(R.drawable.sunny_background)
             binding.lottieAnimationView.setAnimation(R.raw.sun)
         }
         "partly clouds"," clouds"," mist"->{
             binding.root.setBackgroundResource(R.drawable.colud_background)
             binding.lottieAnimationView.setAnimation(R.raw.cloud)
         }
         "  light rain", "heavy rain", " drizzle"->{
             binding.root.setBackgroundResource(R.drawable.rain_background)

             binding.lottieAnimationView.setAnimation(R.raw.rain)
         }
         "light snow", " heavy snow"->{
             binding.root.setBackgroundResource(R.drawable.snow_background)
             binding.lottieAnimationView.setAnimation(R.raw.snow)
         }
         else ->{

                 binding.root.setBackgroundResource(R.drawable.sunny_background)
                 binding.lottieAnimationView.setAnimation(R.raw.sun)

         }
     }
        binding.lottieAnimationView.playAnimation()
    }


    private fun date(): String {
            val sdf = SimpleDateFormat("dd mm yyyy", Locale.getDefault())
            return sdf.format((Date()))
        }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp: Long): String
    {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
            }