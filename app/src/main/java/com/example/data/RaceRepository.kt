package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RaceRepository(private val context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "homework_race_db"
    ).fallbackToDestructiveMigration().build()
    
    private val dao = db.kidsDao()

    init {
        // Profile is now created via setup flow.
    }

    val profileFlow = dao.getProfileFlow()
    val trackProgressFlow = dao.getTrackProgressFlow("city_track")
    val recentEventsFlow = dao.getRecentEventsFlow()
    val ownedItemsFlow = dao.getOwnedItemsFlow()
    val carUpgradesFlow = dao.getAllCarUpgradesFlow()

    // Combining data for the UI
    val uiStateFlow = combine(profileFlow, trackProgressFlow) { profile, track ->
        if (profile == null) return@combine RaceUiState.NeedsSetup
        if (track == null) return@combine RaceUiState.Loading
        RaceUiState.Success(profile, track)
    }
    
    suspend fun createProfile(name: String) = withContext(Dispatchers.IO) {
        dao.insertProfile(ChildProfile(name = name))
        dao.insertTrackProgress(TrackProgress(trackId = "city_track", targetPoints = 500))
        dao.insertOwnedItem(OwnedItem("basic_car"))
    }

    suspend fun addPoints(points: Int, note: String) = withContext(Dispatchers.IO) {
        val profile = dao.getProfile() ?: return@withContext
        val track = dao.getTrackProgress(profile.currentTrackId) ?: return@withContext

        val currentPts = track.currentPoints
        val newTarget = minOf(currentPts + points, track.targetPoints)
        
        dao.insertTrackProgress(track.copy(currentPoints = newTarget))
        dao.insertProfile(profile.copy(
            totalPoints = profile.totalPoints + points,
            coins = profile.coins + points
        ))
        
        dao.insertPointEvent(PointEvent(points = points, note = note))

        // Level up logic (every 500 points)
        val newLevel = 1 + ((profile.totalPoints + points) / 500)
        if (newLevel > profile.level) {
            val updatedProfile = dao.getProfile() ?: return@withContext
            dao.insertProfile(updatedProfile.copy(level = newLevel))
        }
        
        // Handle track completion
        if (newTarget >= track.targetPoints) {
            // Reward bonus coins and reset track for MVP
            val pt = dao.getProfile() ?: return@withContext
            dao.insertProfile(pt.copy(coins = pt.coins + 200))
            dao.insertTrackProgress(track.copy(currentPoints = 0, claimedRewardsStr = ""))
        }
    }

    suspend fun buyItem(itemId: String, price: Int): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getProfile() ?: return@withContext false
        if (profile.coins >= price) {
            dao.insertProfile(profile.copy(coins = profile.coins - price))
            dao.insertOwnedItem(OwnedItem(itemId))
            return@withContext true
        }
        return@withContext false
    }
    
    suspend fun equipCar(carId: String) = withContext(Dispatchers.IO) {
        val profile = dao.getProfile() ?: return@withContext
        dao.insertProfile(profile.copy(currentCarId = carId))
    }

    suspend fun loadCarUpgrade(carId: String): CarUpgrade = withContext(Dispatchers.IO) {
        return@withContext dao.getCarUpgrade(carId) ?: CarUpgrade(carId = carId)
    }

    suspend fun saveCarUpgrade(upgrade: CarUpgrade) = withContext(Dispatchers.IO) {
        dao.insertCarUpgrade(upgrade)
    }

    suspend fun buyCarUpgrade(carId: String, category: String, price: Int): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getProfile() ?: return@withContext false
        if (profile.coins >= price) {
            val current = dao.getCarUpgrade(carId) ?: CarUpgrade(carId = carId)
            val updated = when (category) {
                "engine" -> current.copy(engineLevel = minOf(5, current.engineLevel + 1))
                "turbo" -> current.copy(turboLevel = minOf(5, current.turboLevel + 1))
                "wheels" -> current.copy(wheelsLevel = minOf(5, current.wheelsLevel + 1))
                else -> current
            }
            dao.insertProfile(profile.copy(coins = profile.coins - price))
            dao.insertCarUpgrade(updated)
            return@withContext true
        }
        return@withContext false
    }

    suspend fun buyCarPaint(carId: String, colorValue: Int, price: Int): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getProfile() ?: return@withContext false
        if (profile.coins >= price) {
            val current = dao.getCarUpgrade(carId) ?: CarUpgrade(carId = carId)
            val updated = current.copy(colorArgb = colorValue)
            dao.insertProfile(profile.copy(coins = profile.coins - price))
            dao.insertCarUpgrade(updated)
            return@withContext true
        }
        return@withContext false
    }
}

sealed class RaceUiState {
    object Loading : RaceUiState()
    object NeedsSetup : RaceUiState()
    data class Success(
        val profile: ChildProfile,
        val trackProgress: TrackProgress
    ) : RaceUiState()
}
