package com.example.data

import kotlinx.coroutines.flow.Flow

class SecurityRepository(private val securityDao: SecurityDao) {

    // Guards
    val allGuards: Flow<List<Guard>> = securityDao.getAllGuardsFlow()

    suspend fun getGuardByIdAndPin(guardId: String, pin: String): Guard? {
        return securityDao.getGuardByIdAndPin(guardId, pin)
    }

    suspend fun insertGuard(guard: Guard) {
        securityDao.insertGuard(guard)
    }

    suspend fun updateGuard(guard: Guard) {
        securityDao.updateGuard(guard)
    }

    // Duty Sessions
    val allDutySessions: Flow<List<DutySession>> = securityDao.getAllDutySessionsFlow()

    fun getDutySessionsByGuard(guardId: String): Flow<List<DutySession>> {
        return securityDao.getDutySessionsByGuardFlow(guardId)
    }

    suspend fun getActiveDutySession(guardId: String): DutySession? {
        return securityDao.getActiveDutySession(guardId)
    }

    suspend fun insertDutySession(session: DutySession): Long {
        return securityDao.insertDutySession(session)
    }

    suspend fun updateDutySession(session: DutySession) {
        securityDao.updateDutySession(session)
    }

    // Visitors
    val allVisitors: Flow<List<Visitor>> = securityDao.getAllVisitorsFlow()
    val visitorsCurrentlyInside: Flow<List<Visitor>> = securityDao.getVisitorsCurrentlyInsideFlow()

    suspend fun getVisitorByMobile(mobile: String): Visitor? {
        return securityDao.getVisitorByMobile(mobile)
    }

    suspend fun getVisitorByVehicleNumber(vehicleNumber: String): Visitor? {
        return securityDao.getVisitorByVehicleNumber(vehicleNumber)
    }

    fun searchVisitors(query: String): Flow<List<Visitor>> {
        return securityDao.searchVisitorsFlow(query)
    }

    suspend fun insertVisitor(visitor: Visitor): Long {
        return securityDao.insertVisitor(visitor)
    }

    suspend fun updateVisitor(visitor: Visitor) {
        securityDao.updateVisitor(visitor)
    }

    // Visitor Records
    val allVisitorRecords: Flow<List<VisitorRecord>> = securityDao.getAllVisitorRecordsFlow()

    suspend fun insertVisitorRecord(record: VisitorRecord): Long {
        return securityDao.insertVisitorRecord(record)
    }

    suspend fun updateVisitorRecord(record: VisitorRecord) {
        securityDao.updateVisitorRecord(record)
    }

    // Residents
    val allResidents: Flow<List<Resident>> = securityDao.getAllResidentsFlow()

    suspend fun insertResident(resident: Resident) {
        securityDao.insertResident(resident)
    }

    suspend fun updateResident(resident: Resident) {
        securityDao.updateResident(resident)
    }

    suspend fun deleteResident(resident: Resident) {
        securityDao.deleteResident(resident)
    }

    // Challans
    val allChallans: Flow<List<Challan>> = securityDao.getAllChallansFlow()

    fun getChallansByVehicle(vehicleNumber: String): Flow<List<Challan>> {
        return securityDao.getChallansByVehicleFlow(vehicleNumber)
    }

    suspend fun insertChallan(challan: Challan): Long {
        return securityDao.insertChallan(challan)
    }

    suspend fun updateChallan(challan: Challan) {
        securityDao.updateChallan(challan)
    }

    // Incidents
    val allIncidents: Flow<List<Incident>> = securityDao.getAllIncidentsFlow()

    suspend fun insertIncident(incident: Incident): Long {
        return securityDao.insertIncident(incident)
    }

    suspend fun updateIncident(incident: Incident) {
        securityDao.updateIncident(incident)
    }

    // Lost & Found
    val allLostFound: Flow<List<LostFound>> = securityDao.getAllLostFoundFlow()

    suspend fun insertLostFound(item: LostFound) {
        securityDao.insertLostFound(item)
    }

    suspend fun updateLostFound(item: LostFound) {
        securityDao.updateLostFound(item)
    }

    // Maintenance
    val allMaintenanceEntries: Flow<List<MaintenanceEntry>> = securityDao.getAllMaintenanceEntriesFlow()
    val maintenanceCurrentlyInside: Flow<List<MaintenanceEntry>> = securityDao.getMaintenanceCurrentlyInsideFlow()

    suspend fun insertMaintenanceEntry(entry: MaintenanceEntry): Long {
        return securityDao.insertMaintenanceEntry(entry)
    }

    suspend fun updateMaintenanceEntry(entry: MaintenanceEntry) {
        securityDao.updateMaintenanceEntry(entry)
    }

    // Announcements
    val allAnnouncements: Flow<List<Announcement>> = securityDao.getAllAnnouncementsFlow()

    suspend fun insertAnnouncement(announcement: Announcement) {
        securityDao.insertAnnouncement(announcement)
    }

    // Audit Logs
    val allAuditLogs: Flow<List<AuditLog>> = securityDao.getAllAuditLogsFlow()

    suspend fun insertAuditLog(log: AuditLog) {
        securityDao.insertAuditLog(log)
    }
}
