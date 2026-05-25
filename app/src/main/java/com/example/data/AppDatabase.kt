package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "profiles")
data class ChildProfile(
    @PrimaryKey val id: String = "default_child",
    val name: String = "גיבור/ת מרוצים",
    val level: Int = 1,
    val coins: Int = 0,
    val currentCarId: String = "basic_car",
    val currentTrackId: String = "city_track",
    val totalPoints: Int = 0
)

@Entity(tableName = "track_progress")
data class TrackProgress(
    @PrimaryKey val trackId: String = "city_track",
    val currentPoints: Int = 0,
    val targetPoints: Int = 500,
    val claimedRewardsStr: String = "" // Comma separated for MVP
)

@Entity(tableName = "point_events")
data class PointEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMs: Long = System.currentTimeMillis(),
    val points: Int,
    val note: String
)

@Entity(tableName = "owned_items")
data class OwnedItem(
    @PrimaryKey val itemId: String
)

@Dao
interface KidsDao {
    @Query("SELECT * FROM profiles LIMIT 1")
    fun getProfileFlow(): Flow<ChildProfile?>

    @Query("SELECT * FROM profiles LIMIT 1")
    suspend fun getProfile(): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfile)

    @Query("SELECT * FROM track_progress WHERE trackId = :trackId")
    fun getTrackProgressFlow(trackId: String): Flow<TrackProgress?>

    @Query("SELECT * FROM track_progress WHERE trackId = :trackId")
    suspend fun getTrackProgress(trackId: String): TrackProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackProgress(progress: TrackProgress)

    @Insert
    suspend fun insertPointEvent(event: PointEvent)

    @Query("SELECT * FROM point_events ORDER BY dateMs DESC LIMIT 50")
    fun getRecentEventsFlow(): Flow<List<PointEvent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwnedItem(item: OwnedItem)

    @Query("SELECT * FROM owned_items")
    fun getOwnedItemsFlow(): Flow<List<OwnedItem>>
}

@Database(
    entities = [ChildProfile::class, TrackProgress::class, PointEvent::class, OwnedItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kidsDao(): KidsDao
}
