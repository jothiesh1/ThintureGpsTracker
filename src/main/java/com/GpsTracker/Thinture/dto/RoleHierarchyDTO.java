package com.GpsTracker.Thinture.dto;

public class RoleHierarchyDTO {

    private Long superadmin_id;
    private Long admin_id;
    private Long dealer_id;
    private Long client_id;
    private Long user_id;

    // Optional - name, email, phone, etc. for different roles
    private String name;
    private String email;
    private String phone;
    private String password;
	public Long getSuperadmin_id() {
		return superadmin_id;
	}
	public void setSuperadmin_id(Long superadmin_id) {
		this.superadmin_id = superadmin_id;
	}
	public Long getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(Long admin_id) {
		this.admin_id = admin_id;
	}
	public Long getDealer_id() {
		return dealer_id;
	}
	public void setDealer_id(Long dealer_id) {
		this.dealer_id = dealer_id;
	}
	public Long getClient_id() {
		return client_id;
	}
	public void setClient_id(Long client_id) {
		this.client_id = client_id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

    // Add additional fields as needed per role
}
