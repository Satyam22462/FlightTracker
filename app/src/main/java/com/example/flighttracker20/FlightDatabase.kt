package com.example.flighttracker20

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(tableName = "flights")
data class FlightRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: String,
    val arrivalTime: String,
    val delayMinutes: Int,
    val actualDurationMinutes: Int,
    val date: String
)

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: FlightRecord)

    @Query("SELECT * FROM flights")
    suspend fun getAllFlights(): List<FlightRecord>

    @Query("""
        SELECT AVG(actualDurationMinutes) 
        FROM flights 
        WHERE departureAirport = :from AND arrivalAirport = :to
    """)
    suspend fun getAverageFlightTime(from: String, to: String): Double?
}

@Database(entities = [FlightRecord::class], version = 1)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile private var INSTANCE: FlightDatabase? = null

        fun getInstance(context: Context): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun insertSampleFlight(context: Context) {
            val dao = getInstance(context).flightDao()
            withContext(Dispatchers.IO) {
                dao.insertFlight(
                    FlightRecord(
                        flightNumber = "EK215",
                        departureAirport = "DXB",
                        arrivalAirport = "LAX",
                        departureTime = "2025-04-07T12:00:00",
                        arrivalTime = "2025-04-07T20:30:00",
                        delayMinutes = 15,
                        actualDurationMinutes = 510,
                        date = "2025-04-07"
                    )
                )
            }
        }
    }
}
