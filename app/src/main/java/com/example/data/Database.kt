package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ------------------ ENTITIES ------------------

@Entity(tableName = "guards")
data class Guard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guardId: String,
    val name: String,
    val mobile: String,
    val pin: String,
    val assignedGate: String,
    val assignedShift: String,
    val status: String = "OFF DUTY", // ON DUTY, OFF DUTY
    val isActive: Boolean = true
)

@Entity(tableName = "duty_sessions")
data class DutySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guardId: String,
    val guardName: String,
    val gate: String,
    val shiftName: String,
    val startDate: String,
    val startTime: String,
    val endDate: String = "",
    val endTime: String = "",
    val durationMinutes: Long = 0,
    val status: String = "ON DUTY", // ON DUTY, OFF DUTY
    val activityCount: Int = 0
)

@Entity(tableName = "visitors")
data class Visitor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val mobile: String,
    val vehicleNumber: String = "",
    val vehicleType: String = "", // 2-Wheeler, 4-Wheeler, None
    val flatNumber: String,
    val residentName: String = "",
    val purpose: String, // Personal, Delivery, Service, Staff, Other
    val notes: String = "",
    val firstVisit: String,
    val lastVisit: String,
    val totalVisits: Int = 1,
    val currentStatus: String = "INSIDE", // INSIDE, EXITED
    val entryTime: String,
    val exitTime: String = "",
    val guardName: String = "",
    val gateName: String = ""
)

@Entity(tableName = "visitor_records")
data class VisitorRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitorMobile: String,
    val visitorName: String,
    val flatNumber: String,
    val vehicleNumber: String = "",
    val purpose: String,
    val entryTime: Long, // timestamp
    val exitTime: Long? = null, // timestamp
    val guardId: String,
    val guardName: String,
    val gate: String,
    val status: String = "INSIDE" // INSIDE, EXITED
)

@Entity(tableName = "residents")
data class Resident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flat: String,
    val name: String,
    val mobile: String,
    val familyInfo: String = "",
    val status: String = "Active" // Active, Inactive
)

@Entity(tableName = "challans")
data class Challan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vehicleNumber: String,
    val challanNumber: String,
    val date: String,
    val time: String,
    val location: String,
    val violation: String,
    val amount: Double,
    val status: String = "PENDING", // PENDING, PAID, DISPUTED, CANCELLED
    val dueDate: String,
    val notes: String = "",
    val documentPath: String = "", // File path / URI representation
    val paymentDate: String = "",
    val paymentMethod: String = "",
    val paymentReference: String = "",
    val receiptDocumentPath: String = ""
)

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val date: String,
    val time: String,
    val gate: String,
    val description: String,
    val reporter: String,
    val severity: String, // Low, Medium, High
    val status: String = "OPEN", // OPEN, UNDER REVIEW, RESOLVED
    val peopleInvolved: String = "",
    val vehicleInvolved: String = ""
)

@Entity(tableName = "lost_found")
data class LostFound(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val foundLocation: String,
    val date: String,
    val time: String,
    val foundBy: String,
    val description: String,
    val status: String = "FOUND", // FOUND, CLAIMED, RETURNED
    val claimantInfo: String = ""
)

@Entity(tableName = "maintenance_entries")
data class MaintenanceEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val company: String,
    val mobile: String,
    val purpose: String, // Electrician, Plumber, etc.
    val flatNumber: String,
    val entryTime: Long,
    val exitTime: Long? = null,
    val notes: String = "",
    val status: String = "INSIDE" // INSIDE, EXITED
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val priority: String, // Low, Medium, High
    val expiryDate: String
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val role: String,
    val date: String,
    val time: String,
    val gate: String,
    val action: String
)

// ------------------ DAO ------------------

@Dao
interface SecurityDao {

    // Guards
    @Query("SELECT * FROM guards WHERE guardId = :guardId AND pin = :pin AND isActive = 1 LIMIT 1")
    suspend fun getGuardByIdAndPin(guardId: String, pin: String): Guard?

    @Query("SELECT * FROM guards WHERE isActive = 1")
    fun getAllGuardsFlow(): Flow<List<Guard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuard(guard: Guard)

    @Update
    suspend fun updateGuard(guard: Guard)

    // Duty Sessions
    @Query("SELECT * FROM duty_sessions WHERE guardId = :guardId AND status = 'ON DUTY' LIMIT 1")
    suspend fun getActiveDutySession(guardId: String): DutySession?

    @Query("SELECT * FROM duty_sessions ORDER BY id DESC")
    fun getAllDutySessionsFlow(): Flow<List<DutySession>>

    @Query("SELECT * FROM duty_sessions WHERE guardId = :guardId ORDER BY id DESC")
    fun getDutySessionsByGuardFlow(guardId: String): Flow<List<DutySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDutySession(session: DutySession): Long

    @Update
    suspend fun updateDutySession(session: DutySession)

    // Visitors
    @Query("SELECT * FROM visitors ORDER BY id DESC")
    fun getAllVisitorsFlow(): Flow<List<Visitor>>

    @Query("SELECT * FROM visitors WHERE currentStatus = 'INSIDE' ORDER BY id DESC")
    fun getVisitorsCurrentlyInsideFlow(): Flow<List<Visitor>>

    @Query("SELECT * FROM visitors WHERE mobile = :mobile LIMIT 1")
    suspend fun getVisitorByMobile(mobile: String): Visitor?

    @Query("SELECT * FROM visitors WHERE vehicleNumber = :vehicleNumber LIMIT 1")
    suspend fun getVisitorByVehicleNumber(vehicleNumber: String): Visitor?

    @Query("SELECT * FROM visitors WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' OR vehicleNumber LIKE '%' || :query || '%' OR flatNumber LIKE '%' || :query || '%'")
    fun searchVisitorsFlow(query: String): Flow<List<Visitor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: Visitor): Long

    @Update
    suspend fun updateVisitor(visitor: Visitor)

    // Visitor Records (History Log)
    @Query("SELECT * FROM visitor_records ORDER BY entryTime DESC")
    fun getAllVisitorRecordsFlow(): Flow<List<VisitorRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitorRecord(record: VisitorRecord): Long

    @Update
    suspend fun updateVisitorRecord(record: VisitorRecord)

    // Residents
    @Query("SELECT * FROM residents ORDER BY flat ASC")
    fun getAllResidentsFlow(): Flow<List<Resident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResident(resident: Resident)

    @Update
    suspend fun updateResident(resident: Resident)

    @Delete
    suspend fun deleteResident(resident: Resident)

    // Challans
    @Query("SELECT * FROM challans ORDER BY id DESC")
    fun getAllChallansFlow(): Flow<List<Challan>>

    @Query("SELECT * FROM challans WHERE vehicleNumber = :vehicleNumber ORDER BY id DESC")
    fun getChallansByVehicleFlow(vehicleNumber: String): Flow<List<Challan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallan(challan: Challan): Long

    @Update
    suspend fun updateChallan(challan: Challan)

    // Incidents
    @Query("SELECT * FROM incidents ORDER BY id DESC")
    fun getAllIncidentsFlow(): Flow<List<Incident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident): Long

    @Update
    suspend fun updateIncident(incident: Incident)

    // Lost & Found
    @Query("SELECT * FROM lost_found ORDER BY id DESC")
    fun getAllLostFoundFlow(): Flow<List<LostFound>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLostFound(item: LostFound)

    @Update
    suspend fun updateLostFound(item: LostFound)

    // Maintenance / Service Entries
    @Query("SELECT * FROM maintenance_entries ORDER BY entryTime DESC")
    fun getAllMaintenanceEntriesFlow(): Flow<List<MaintenanceEntry>>

    @Query("SELECT * FROM maintenance_entries WHERE status = 'INSIDE' ORDER BY entryTime DESC")
    fun getMaintenanceCurrentlyInsideFlow(): Flow<List<MaintenanceEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceEntry(entry: MaintenanceEntry): Long

    @Update
    suspend fun updateMaintenanceEntry(entry: MaintenanceEntry)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncementsFlow(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllAuditLogsFlow(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)
}

// ------------------ DATABASE ------------------

@Database(
    entities = [
        Guard::class,
        DutySession::class,
        Visitor::class,
        VisitorRecord::class,
        Resident::class,
        Challan::class,
        Incident::class,
        LostFound::class,
        MaintenanceEntry::class,
        Announcement::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun securityDao(): SecurityDao
}
