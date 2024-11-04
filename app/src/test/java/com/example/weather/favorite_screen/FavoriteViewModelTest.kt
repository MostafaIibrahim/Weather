package com.example.weather.favorite_screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.data.WeatherDisplayable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    private lateinit var fakeRepo: FakeWeatherRepositoryTest
    private lateinit var viewModel: FavroiteViewModel
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fakeRepo = FakeWeatherRepositoryTest()
        viewModel = FavroiteViewModel(fakeRepo)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
     fun insertNewLocation_NewFavLocationEqualTrue() = runTest{
        //Given: an object of Favorite View Model
        val favroiteViewModel = FavroiteViewModel(FakeWeatherRepositoryTest())

        val location = WeatherDisplayable(
            id = 1,
            location = "Cairo, EG",
            currentDay = "10-01", // MM-dd format for current day
            currentTime = "14:00", // HH:mm format for current time
            weatherIconUrl = "01d",
            weatherDescription = "Clear sky",
            temperature = "22",
            tempUnit = "°C",
            currentMaxTemp = "25",
            currentMinTemp = "20",
            sunrise = "06:30",
            sunset = "18:45",
            feelLike = "21",
            pressure = "1013",
            windSpeed = "3.5",
            speedUnit = "m/sec",
            humidity = "60",
            cloudCoverage = "10",
            hourlyForecast = listOf(), // Replace with mock data if needed
            dailyForecast = listOf() // Replace with mock data if needed
        )
        //When: Add a new favorite Location
        favroiteViewModel.insertLocation(location)
        val favouritesList = favroiteViewModel.listDataFlow.first()
        assertThat(favouritesList.contains(location), `is`(true))
    }

    @Test
    fun deleteNewLocation_NewFavLocationEqualFalse() = runTest{
        //Given: an object of Favorite View Model
        val favroiteViewModel = FavroiteViewModel(FakeWeatherRepositoryTest())

        val location = WeatherDisplayable(
            id = 1,
            location = "Cairo, EG",
            currentDay = "10-01", // MM-dd format for current day
            currentTime = "14:00", // HH:mm format for current time
            weatherIconUrl = "01d",
            weatherDescription = "Clear sky",
            temperature = "22",
            tempUnit = "°C",
            currentMaxTemp = "25",
            currentMinTemp = "20",
            sunrise = "06:30",
            sunset = "18:45",
            feelLike = "21",
            pressure = "1013",
            windSpeed = "3.5",
            speedUnit = "m/sec",
            humidity = "60",
            cloudCoverage = "10",
            hourlyForecast = listOf(), // Replace with mock data if needed
            dailyForecast = listOf() // Replace with mock data if needed
        )
        //When: Add a new favorite Location
        favroiteViewModel.deleteLocation(location)
        val favouritesList = favroiteViewModel.listDataFlow.first()
        assertThat(favouritesList.contains(location), `is`(false))
    }
}