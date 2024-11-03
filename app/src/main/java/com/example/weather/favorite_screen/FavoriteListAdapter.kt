package com.example.weather.favorite_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.WeatherDisplayable
import com.example.weather.databinding.FavoriteCardBinding

class FavoriteListAdapter(private val listener: (WeatherDisplayable) -> Unit):
    ListAdapter<WeatherDisplayable, FavoriteListAdapter.viewHolder>(MyDiffUtilFavoriteListAdapter()){
        class viewHolder(var binding:FavoriteCardBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater:LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = FavoriteCardBinding.inflate(inflater,parent,false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentObject = getItem(position)
        holder.binding.apply {
            placeNameText.text = currentObject.location
            deleteBtn.setOnClickListener{
                listener.invoke(currentObject)
            }
        }
    }
}

class MyDiffUtilFavoriteListAdapter: DiffUtil.ItemCallback<WeatherDisplayable>(){
    override fun areItemsTheSame(oldItem: WeatherDisplayable, newItem: WeatherDisplayable): Boolean {
        return  oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: WeatherDisplayable, newItem: WeatherDisplayable): Boolean {
        return oldItem.equals(newItem)
    }
}