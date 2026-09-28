package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GuardBookViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "guardbook_db"
    ).fallbackToDestructiveMigration().build()

    val repository = SecurityRepository(db.securityDao())

    // ------------------ UI & NAVIGATION STATES ------------------
    val currentScreen = MutableStateFlow("welcome") // welcome, sign_in, dashboard, visitors, vehicles, incidents, residents, services, reports, extra_income, settings
    val selectedLanguage = MutableStateFlow("EN") // EN or HI
    val currentProperty = MutableStateFlow("ABC Residency")
    val selectedGate = MutableStateFlow("Gate 1 — Main Gate")
    val selectedShift = MutableStateFlow("Morning Shift")

    // Active Guard state
    val loggedInGuard = MutableStateFlow<Guard?>(null)
    val activeDutySession = MutableStateFlow<DutySession?>(null)
    
    // System Connection State
    val isOnline = MutableStateFlow(true)
    val lastSyncTime = MutableStateFlow("Just now")
    val isSyncing = MutableStateFlow(false)

    // Pro / Ads State
    val isProUser = MutableStateFlow(false)

    // Extra Income Balance State
    val earningsBalance = MutableStateFlow(0.0)
    val pendingEarnings = MutableStateFlow(0.0)
    val completedTasksCount = MutableStateFlow(0)

    // Search and Input Filter state
    val universalSearchQuery = MutableStateFlow("")
    val searchResults = MutableStateFlow<List<Visitor>>(emptyList())

    // ------------------ DATA FLOWS FROM ROOM ------------------
    val allGuards: StateFlow<List<Guard>> = repository.allGuards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDutySessions: StateFlow<List<DutySession>> = repository.allDutySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisitors: StateFlow<List<Visitor>> = repository.allVisitors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visitorsCurrentlyInside: StateFlow<List<Visitor>> = repository.visitorsCurrentlyInside
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisitorRecords: StateFlow<List<VisitorRecord>> = repository.allVisitorRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResidents: StateFlow<List<Resident>> = repository.allResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChallans: StateFlow<List<Challan>> = repository.allChallans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncidents: StateFlow<List<Incident>> = repository.allIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLostFound: StateFlow<List<LostFound>> = repository.allLostFound
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaintenanceEntries: StateFlow<List<MaintenanceEntry>> = repository.allMaintenanceEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed default data if database is empty
        viewModelScope.launch {
            allGuards.first()
            val guardsCount = db.securityDao().getAllGuardsFlow().first().size
            if (guardsCount == 0) {
                seedInitialData()
            }
        }

        // Setup reactive search
        viewModelScope.launch {
            universalSearchQuery.debounce(300).collect { query ->
                if (query.trim().isEmpty()) {
                    searchResults.value = emptyList()
                } else {
                    repository.searchVisitors(query).collect { list ->
                        searchResults.value = list
                    }
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        // 1. Seed Residents
        repository.insertResident(Resident(flat = "B-204", name = "Sanjay Sharma", mobile = "9876543211", familyInfo = "4 Family Members"))
        repository.insertResident(Resident(flat = "C-101", name = "Amit Verma", mobile = "9876543212", familyInfo = "2 Family Members"))
        repository.insertResident(Resident(flat = "A-305", name = "Rajesh Patel", mobile = "9876543213", familyInfo = "3 Family Members"))
        repository.insertResident(Resident(flat = "D-402", name = "Vikram Singh", mobile = "9876543214", familyInfo = "5 Family Members"))

        // 2. Seed Announcements
        repository.insertAnnouncement(Announcement(title = "Water Maintenance", description = "Water maintenance scheduled for tomorrow 10 AM to 1 PM.", date = getFormattedDate(), priority = "High", expiryDate = getFormattedDate()))
        repository.insertAnnouncement(Announcement(title = "Security Drill", description = "Annual security drill at 6 PM near Main Gate.", date = getFormattedDate(), priority = "Medium", expiryDate = getFormattedDate()))

        // 3. Seed Audit Log
        logAudit("System", "System", "Database initialized. Secure environment ready.")
    }

    // ------------------ ACTION METHODS ------------------

    fun createGuardAccount(
        name: String,
        mobile: String,
        guardId: String,
        pin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val exists = allGuards.value.any { it.guardId.equals(guardId, ignoreCase = true) }
            if (exists) {
                onError(if (selectedLanguage.value == "EN") "Guard ID already exists. Please choose another ID." else "Guard ID पहले से मौजूद है। कृपया दूसरा चुनें।")
            } else {
                val newGuard = Guard(
                    guardId = guardId.trim().uppercase(),
                    name = name.trim(),
                    mobile = mobile.trim(),
                    pin = pin.trim(),
                    assignedGate = "Gate 1 — Main Gate",
                    assignedShift = "Morning Shift"
                )
                repository.insertGuard(newGuard)
                logAudit(newGuard.name, "Guard", "Guard account created successfully.")
                onSuccess()
            }
        }
    }

    fun loginGuard(guardId: String, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val guard = repository.getGuardByIdAndPin(guardId, pin)
            if (guard != null) {
                loggedInGuard.value = guard
                selectedGate.value = guard.assignedGate
                selectedShift.value = guard.assignedShift
                // Check if active duty session exists
                val activeSession = repository.getActiveDutySession(guardId)
                if (activeSession != null) {
                    activeDutySession.value = activeSession
                }
                logAudit(guard.name, "Guard", "Guard signed in successfully.")
                onSuccess()
            } else {
                onError(if (selectedLanguage.value == "EN") "Invalid Guard ID or PIN!" else "अमान्य Guard ID या PIN!")
            }
        }
    }

    fun startDuty() {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val session = DutySession(
                guardId = guard.guardId,
                guardName = guard.name,
                gate = selectedGate.value,
                shiftName = selectedShift.value,
                startDate = getFormattedDate(),
                startTime = getFormattedTime()
            )
            val sessionId = repository.insertDutySession(session)
            activeDutySession.value = session.copy(id = sessionId.toInt())
            
            // Update Guard status
            repository.updateGuard(guard.copy(status = "ON DUTY", assignedGate = selectedGate.value, assignedShift = selectedShift.value))
            logAudit(guard.name, "Guard", "Duty started at ${selectedGate.value}")
        }
    }

    fun endDuty(onComplete: () -> Unit) {
        val session = activeDutySession.value ?: return
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val endTime = getFormattedTime()
            val totalMin = calculateDurationMinutes(session.startTime, endTime)
            val updatedSession = session.copy(
                endDate = getFormattedDate(),
                endTime = endTime,
                durationMinutes = totalMin,
                status = "OFF DUTY"
            )
            repository.updateDutySession(updatedSession)
            activeDutySession.value = null
            
            // Update Guard status
            repository.updateGuard(guard.copy(status = "OFF DUTY"))
            logAudit(guard.name, "Guard", "Duty ended. Total duration: ${totalMin}m")
            onComplete()
        }
    }

    // Handover Duty
    fun performHandover(incomingGuardName: String, notes: String, onComplete: () -> Unit) {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            logAudit(guard.name, "Guard", "Handover completed to $incomingGuardName with notes: $notes")
            endDuty {
                loggedInGuard.value = null
                onComplete()
            }
        }
    }

    // Visitor Entry
    fun recordVisitorEntry(
        name: String,
        mobile: String,
        flat: String,
        vehicleNumber: String = "",
        vehicleType: String = "",
        purpose: String = "Personal",
        notes: String = "",
        onComplete: () -> Unit
    ) {
        val guard = loggedInGuard.value ?: return
        val currentGate = selectedGate.value
        viewModelScope.launch {
            // Check if visitor profile already exists
            val existingVisitor = repository.getVisitorByMobile(mobile)
            val currentTime = getFormattedDate() + " " + getFormattedTime()

            if (existingVisitor != null) {
                // Update existing visitor visits
                val updatedVisitor = existingVisitor.copy(
                    vehicleNumber = vehicleNumber,
                    vehicleType = vehicleType,
                    flatNumber = flat,
                    purpose = purpose,
                    notes = notes,
                    lastVisit = currentTime,
                    totalVisits = existingVisitor.totalVisits + 1,
                    currentStatus = "INSIDE",
                    entryTime = currentTime,
                    guardName = guard.name,
                    gateName = currentGate
                )
                repository.updateVisitor(updatedVisitor)
            } else {
                // Create new visitor
                val newVisitor = Visitor(
                    name = name,
                    mobile = mobile,
                    vehicleNumber = vehicleNumber,
                    vehicleType = vehicleType,
                    flatNumber = flat,
                    purpose = purpose,
                    notes = notes,
                    firstVisit = currentTime,
                    lastVisit = currentTime,
                    currentStatus = "INSIDE",
                    entryTime = currentTime,
                    guardName = guard.name,
                    gateName = currentGate
                )
                repository.insertVisitor(newVisitor)
            }

            // Log Visitor Record for Audit Trail / Reports
            repository.insertVisitorRecord(
                VisitorRecord(
                    visitorMobile = mobile,
                    visitorName = name,
                    flatNumber = flat,
                    vehicleNumber = vehicleNumber,
                    purpose = purpose,
                    entryTime = System.currentTimeMillis(),
                    guardId = guard.guardId,
                    guardName = guard.name,
                    gate = currentGate
                )
            )

            // Update session activity count
            activeDutySession.value?.let { session ->
                repository.updateDutySession(session.copy(activityCount = session.activityCount + 1))
            }

            logAudit(guard.name, "Guard", "Recorded entry for visitor: $name to flat $flat")
            onComplete()
        }
    }

    // Visitor Exit
    fun recordVisitorExit(visitor: Visitor, onComplete: () -> Unit) {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val currentTime = getFormattedDate() + " " + getFormattedTime()
            val updatedVisitor = visitor.copy(
                currentStatus = "EXITED",
                exitTime = currentTime
            )
            repository.updateVisitor(updatedVisitor)

            // Update corresponding VisitorRecord in history
            val records = repository.allVisitorRecords.first()
            val matchingRecord = records.find { it.visitorMobile == visitor.mobile && it.status == "INSIDE" }
            if (matchingRecord != null) {
                repository.updateVisitorRecord(
                    matchingRecord.copy(
                        exitTime = System.currentTimeMillis(),
                        status = "EXITED"
                    )
                )
            }

            logAudit(guard.name, "Guard", "Recorded exit for visitor: ${visitor.name}")
            onComplete()
        }
    }

    // Add Resident (Admin operation)
    fun addResident(flat: String, name: String, mobile: String, familyInfo: String) {
        viewModelScope.launch {
            repository.insertResident(Resident(flat = flat, name = name, mobile = mobile, familyInfo = familyInfo))
            logAudit("Admin", "Admin", "Added resident: $name in flat $flat")
        }
    }

    // Deactivate Resident
    fun toggleResidentStatus(resident: Resident) {
        viewModelScope.launch {
            val nextStatus = if (resident.status == "Active") "Inactive" else "Active"
            repository.updateResident(resident.copy(status = nextStatus))
            logAudit("Admin", "Admin", "Updated resident ${resident.name} status to $nextStatus")
        }
    }

    // Challan Operations
    fun createChallan(
        vehicleNumber: String,
        violation: String,
        amount: Double,
        location: String = "Gate 1",
        notes: String = ""
    ) {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val challanNo = "CH" + (100000 + Random().nextInt(900000))
            val dueDate = getFutureDate(7)
            val challan = Challan(
                vehicleNumber = vehicleNumber.uppercase(),
                challanNumber = challanNo,
                date = getFormattedDate(),
                time = getFormattedTime(),
                location = location,
                violation = violation,
                amount = amount,
                dueDate = dueDate,
                notes = notes,
                documentPath = "Challan_Notice_${challanNo}.pdf"
            )
            repository.insertChallan(challan)
            logAudit(guard.name, "Guard", "Issued challan $challanNo to Vehicle $vehicleNumber (₹$amount)")
        }
    }

    fun payChallan(challan: Challan, paymentMethod: String, referenceNo: String) {
        viewModelScope.launch {
            val updated = challan.copy(
                status = "PAID",
                paymentDate = getFormattedDate(),
                paymentMethod = paymentMethod,
                paymentReference = referenceNo,
                receiptDocumentPath = "Receipt_${challan.challanNumber}.png"
            )
            repository.updateChallan(updated)
            logAudit("System", "System", "Challan ${challan.challanNumber} payment recorded via $paymentMethod")
        }
    }

    // Incidents
    fun reportIncident(type: String, description: String, severity: String, people: String = "", vehicle: String = "") {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val incident = Incident(
                type = type,
                date = getFormattedDate(),
                time = getFormattedTime(),
                gate = selectedGate.value,
                description = description,
                reporter = guard.name,
                severity = severity,
                peopleInvolved = people,
                vehicleInvolved = vehicle
            )
            repository.insertIncident(incident)
            logAudit(guard.name, "Guard", "Reported $severity Incident: $type")
        }
    }

    fun updateIncidentStatus(incident: Incident, nextStatus: String) {
        viewModelScope.launch {
            repository.updateIncident(incident.copy(status = nextStatus))
            logAudit("Admin", "Admin", "Incident ${incident.id} status set to $nextStatus")
        }
    }

    // Lost & Found
    fun reportLostFound(itemName: String, foundLocation: String, foundBy: String, description: String) {
        viewModelScope.launch {
            val item = LostFound(
                itemName = itemName,
                foundLocation = foundLocation,
                date = getFormattedDate(),
                time = getFormattedTime(),
                foundBy = foundBy,
                description = description
            )
            repository.insertLostFound(item)
            logAudit("System", "System", "Lost & Found registered item: $itemName")
        }
    }

    fun claimLostFound(item: LostFound, claimant: String) {
        viewModelScope.launch {
            repository.updateLostFound(
                item.copy(
                    status = "CLAIMED",
                    claimantInfo = claimant
                )
            )
            logAudit("Admin", "Admin", "Lost & Found item ${item.itemName} claimed by $claimant")
        }
    }

    // Service Entries
    fun recordServiceEntry(name: String, company: String, mobile: String, purpose: String, flat: String, notes: String = "") {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            val entry = MaintenanceEntry(
                personName = name,
                company = company,
                mobile = mobile,
                purpose = purpose,
                flatNumber = flat,
                entryTime = System.currentTimeMillis(),
                notes = notes
            )
            repository.insertMaintenanceEntry(entry)
            logAudit(guard.name, "Guard", "Recorded service entry for $name ($company)")
        }
    }

    fun recordServiceExit(entry: MaintenanceEntry) {
        val guard = loggedInGuard.value ?: return
        viewModelScope.launch {
            repository.updateMaintenanceEntry(
                entry.copy(
                    exitTime = System.currentTimeMillis(),
                    status = "EXITED"
                )
            )
            logAudit(guard.name, "Guard", "Recorded service exit for ${entry.personName}")
        }
    }

    // Extra Income Completion simulation
    fun completeTask(reward: Double) {
        viewModelScope.launch {
            earningsBalance.value = earningsBalance.value + reward
            completedTasksCount.value = completedTasksCount.value + 1
            logAudit("Guards", "Earning", "Completed micro-task. Earned ₹$reward")
        }
    }

    fun requestSync() {
        viewModelScope.launch {
            isSyncing.value = true
            kotlinx.coroutines.delay(1500) // Simulation
            isSyncing.value = false
            lastSyncTime.value = getFormattedTime()
            logAudit("System", "Sync", "Manual synchronization completed successfully.")
        }
    }

    // Logging helpers
    private suspend fun logAudit(user: String, role: String, action: String) {
        repository.insertAuditLog(
            AuditLog(
                username = user,
                role = role,
                date = getFormattedDate(),
                time = getFormattedTime(),
                gate = selectedGate.value,
                action = action
            )
        )
    }

    // Date/Time utilities
    fun getFormattedDate(): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    fun getFormattedTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    private fun getFutureDate(daysAhead: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAhead)
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
    }

    private fun calculateDurationMinutes(start: String, end: String): Long {
        try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateStart = sdf.parse(start) ?: return 0
            val dateEnd = sdf.parse(end) ?: return 0
            val diffMs = dateEnd.time - dateStart.time
            val diffMins = diffMs / (1000 * 60)
            return if (diffMins < 0) diffMins + 1440 else diffMins // handle overnight
        } catch (e: Exception) {
            return 0
        }
    }
}
