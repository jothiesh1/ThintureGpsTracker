package com.GpsTracker.Thinture.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;


import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class SupportTicket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String type; // Support / Feedback
	@Column
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String message;

	@Column
	private String status = "Pending";
	@Column
	private String response;
	@Column
	private String role; // USER, ADMIN, etc.

	@Column
	private String priority; // High, Medium, Low
	@Column
	private String issueType; // Bug, Complaint, Feature
	@Column
	private String snapshotUrl; // file upload path

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime submittedAt = LocalDateTime.now();

	@Column
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime resolvedAt;

	// Role ID Fields
	@Column(name = "superadmin_id")
	private Long superadminId;
	@Column(name = "admin_id")
	private Long adminId;
	@Column(name = "dealer_id")
	private Long dealerId;
	@Column(name = "client_id")
	private Long clientId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "driver_id")
	private Long driverId;
	
	
	@Column(name = "`read`") // ← use backticks to escape it in SQL
	private boolean read = false;


	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", insertable = false, updatable = false)
	private Admin admin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dealer_id", insertable = false, updatable = false)
	private Dealer dealer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id", insertable = false, updatable = false)
	private Client client;

	
	@Column private String userName;
	@Column private String userEmail;
	@Column private String dealerClientName;
	@Column private String vehicleNumber;
	@Column private String imeiNumber;
	@Column private String complaintType;
	@Column private String complaintDescription;
	
	@Column
	private String escalationLevel; // Client, Dealer, Admin, SuperAdmin

	@Column
	private String escalationStatus; // Escalated, Closed, etc.

	// Getters and Setters here (or use Lombok @Getter/@Setter)

	// Getters & Setters (you can generate them with Lombok if you prefer)

	public Long getId() {
		return id;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getSnapshotUrl() {
		return snapshotUrl;
	}

	public void setSnapshotUrl(String snapshotUrl) {
		this.snapshotUrl = snapshotUrl;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

	public void setResolvedAt(LocalDateTime resolvedAt) {
		this.resolvedAt = resolvedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public Long getSuperadminId() {
		return superadminId;
	}

	public void setSuperadminId(Long superadminId) {
		this.superadminId = superadminId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	
	//newcode
	
	


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getDealerClientName() {
		return dealerClientName;
	}

	public void setDealerClientName(String dealerClientName) {
		this.dealerClientName = dealerClientName;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getComplaintType() {
		return complaintType;
	}

	public void setComplaintType(String complaintType) {
		this.complaintType = complaintType;
	}

	public String getComplaintDescription() {
		return complaintDescription;
	}

	public void setComplaintDescription(String complaintDescription) {
		this.complaintDescription = complaintDescription;
	}
	public String getEscalationLevel() {
	    return escalationLevel;
	}

	public void setEscalationLevel(String escalationLevel) {
	    this.escalationLevel = escalationLevel;
	}

	public String getEscalationStatus() {
	    return escalationStatus;
	}

	public void setEscalationStatus(String escalationStatus) {
	    this.escalationStatus = escalationStatus;
	}

}
