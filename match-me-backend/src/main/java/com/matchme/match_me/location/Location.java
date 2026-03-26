package com.matchme.match_me.location;

import com.matchme.match_me.users.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    private Double maxRadiusKm;

    private String city;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ===== Getters & Setters =====
    public Long getId() { return id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getMaxRadiusKm() { return maxRadiusKm; }
    public void setMaxRadiusKm(Double maxRadiusKm) { this.maxRadiusKm = maxRadiusKm; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}