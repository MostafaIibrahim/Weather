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
import com.example.weather.databinding.HourlyCardHomeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyAdapter( private val context: Context):
    ListAdapter<List, HourlyAdapter.viewHolder>(MyDiffUtilHourAdapter()) {

    class viewHolder(var binding:HourlyCardHomeBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater:LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = HourlyCardHomeBinding.inflate(inflater,parent,false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentHour = getItem(position)
//        val modeSuffix = if (isDarkModeEnabled(context)) "n" else "d"
//        val fullIconUrl = "$baseUrl${iconName?.dropLast(1)}$modeSuffix@2x.png"
        val iconName = currentHour.weather.get(0).icon // Example: "10d" or "01n"
        val baseUrl = "https://openweathermap.org/img/wn/"
        val fullIconUrl = "$baseUrl$iconName@2x.png"

        holder.binding.apply {
            homeFragmentHourlyHourTextView.text = diplayedHour(currentHour.dtTxt)
            homeFragmentHourlyTempDegree.text = currentHour.main?.temp?.toInt().toString()
            Glide.with(context)
                .load(fullIconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                .into(HomeFragmentHourlyWeatherIcon)
        }
    }

    private fun diplayedHour(dtText:String?): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh a", Locale.getDefault())
        val date = dtText?.let { inputFormat.parse(it) }
        val output = date?.let { outputFormat.format(it) }
        return output
    }

}

class MyDiffUtilHourAdapter: DiffUtil.ItemCallback<List>(){
    override fun areItemsTheSame(oldItem: List, newItem: List): Boolean {
        return  oldItem.dtTxt == newItem.dtTxt
    }
    override fun areContentsTheSame(oldItem: List, newItem: List): Boolean {
        return oldItem.equals(newItem)
    }
}

