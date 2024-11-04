package com.example.weather.favorite_screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.WeatherDisplayable
import com.example.weather.data.db.WeatherDataBase
import com.example.weather.data.db.WeatherLocalDataSource
import com.example.weather.data.remote_network.WeatherRemoteDataSource
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.home_screen.HomeFragment
import com.example.weather.map_ui.MapActivity
import com.example.weather.util.SettingsPreferencesHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {
    lateinit var favAdapter: FavoriteListAdapter
    lateinit var binding:FragmentFavoriteBinding
    lateinit var favroiteViewModel: FavroiteViewModel
    lateinit var factory: FavroiteViewModelFactory
    private lateinit var sharedViewModel: SharedHomeViewModel
    val MAP_PICKER_REQUEST_CODE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        factory = FavroiteViewModelFactory(
            WeatherRepository.getRepository(
                WeatherRemoteDataSource,
                SettingsPreferencesHelper(requireContext()),
                WeatherLocalDataSource.getInstance(WeatherDataBase.getInstance(requireContext()).getWeatherDao(),
                    WeatherDataBase.getInstance(requireContext()).getAlarmDao() )
            ))
        favroiteViewModel = ViewModelProvider(this,factory).get(FavroiteViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedHomeViewModel::class.java)
        setupFavAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch { favroiteViewModel.listDataFlow.collect{ favAdapter.submitList(it) } }

        setButtonClickListener()

    }
    private fun setupFavAdapter() {
        favAdapter = FavoriteListAdapter( requireContext(),
            onItemClicked = { selectedLocation ->
                // Update shared ViewModel with selected location’s coordinates
                sharedViewModel.setSelectedLocation(selectedLocation)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, HomeFragment())
                    .addToBackStack(null)
                    .commit()
//                (requireActivity() as? MainActivity)?.bottomNavFav(R.id.navHome)
            },
            onDeleteClick = { rmvedLocation ->
                favroiteViewModel.deleteLocation(rmvedLocation)
                getView()?.let { showSnackbarWithAction(it,rmvedLocation) }
            }
        )
        binding.favListRecycler.apply {
            adapter = favAdapter
            layoutManager = LinearLayoutManager(context).apply { orientation = RecyclerView.VERTICAL }
        }
    }

    private fun setButtonClickListener(){
        binding.floatingActionButton2.setOnClickListener{
            val intent = Intent(requireContext(), MapActivity::class.java)
            startActivityForResult(intent, MAP_PICKER_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MAP_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            favroiteViewModel.addLocationFav(latitude!!,longitude!!)
        }
    }

    private fun showSnackbarWithAction(view: View, location: WeatherDisplayable) {
        val snackbar = Snackbar.make(view, "Location is removed from Favorite", Snackbar.LENGTH_LONG)
            .setAction("UNDO") { v: View? ->
                favroiteViewModel.insertLocation(location)
            }
        snackbar.show()
    }

}