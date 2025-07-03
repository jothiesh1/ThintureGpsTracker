package com.GpsTracker.Thinture.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public abstract Long getAdmin_id();
    public abstract void setAdmin_id(Long admin_id);

    public abstract Long getDealer_id();
    public abstract void setDealer_id(Long dealer_id);

    public abstract Long getClient_id();
    public abstract void setClient_id(Long client_id);

    public abstract Long getDriver_id();
    public abstract void setDriver_id(Long driver_id);

    public abstract Long getUser_id();
    public abstract void setUser_id(Long user_id);

    public abstract Long getSuperadmin_id();
    public abstract void setSuperadmin_id(Long superadmin_id);
}

