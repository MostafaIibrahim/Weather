package com.example.weather.favorite_screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    @Test
     fun insertNewLocation_NewFavLocationNotNull() = runTest{
        //Given: an object of Favorite View Model
        val favroiteViewModel = FavroiteViewModel(FakeWeatherRepositoryTest())
        val location = WeatherDisplayable(id = 1)
        //When: Add a new favorite Location
        favroiteViewModel.insertLocation(location)
        val result = favroiteViewModel.listDataFlow.collect{}// Collect the current value
        val itemWithId = result.find { it.id == location.id }

        assertThat(itemWithId?.id, `is`(location.id))
    }
}