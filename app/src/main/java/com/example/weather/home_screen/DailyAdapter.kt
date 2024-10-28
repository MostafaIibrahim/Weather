package com.example.weather.home_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.data.util_pojo.List
import com.example.weather.databinding.FivedayCardHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyAdapter ( private val context: Context):
    ListAdapter<List, DailyAdapter.viewHolder>(MyDiffUtilDailyAdapter()){
    class viewHolder(var binding: FivedayCardHomeBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = FivedayCardHomeBinding.inflate(inflater,parent,false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentDay = getItem(position)
        val iconName = currentDay.weather.get(0).icon // Example: "10d" or "01n"
        val baseUrl = "https://openweathermap.org/img/wn/"
        val fullIconUrl = "$baseUrl${iconName?.dropLast(1)}d@2x.png"
        holder.binding.apply {
            dateText.text = diplayedDate(currentDay.dtTxt)
            weatherDescription.text = currentDay.weather.get(0).description
            temperatureText.text= displayTemp(currentDay.main?.tempMax?.toInt().toString(),
                currentDay.main?.tempMin?.toInt().toString()
            )
            Glide.with(context)
                .load(fullIconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                .into(weatherIcon)
        }
    }
    private fun diplayedDate(dtText:String?): String? {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dtText)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val dayOfWeak = when(calendar.get(Calendar.DAY_OF_WEEK)){
            1->"Sun"
            2->"Mon"
            3->"Tue"
            4->"Wed"
            5->"Thu"
            6->"Fri"
            7->"Sat"
            else -> {}
        }.toString()



        return  dayOfWeak
    }
    private fun displayTemp(maxTemp:String,minTemp:String):String = maxTemp+"/"+minTemp

}

class MyDiffUtilDailyAdapter: DiffUtil.ItemCallback<List>(){
    override fun areItemsTheSame(oldItem: List, newItem: List): Boolean {
        return  oldItem.dtTxt == newItem.dtTxt
    }
    override fun areContentsTheSame(oldItem: List, newItem: List): Boolean {
        return oldItem.equals(newItem)
    }
}