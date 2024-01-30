package com.fict.eCommerceWebApp.entities;

import com.fict.eCommerceWebApp.entities.audit.Auditable;
import javax.persistence.*;
import java.util.List;

@Entity
public class ListingEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String category;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @OneToMany(mappedBy = "listingEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImageEntity> images;

    @Column(nullable = false)
    private double price;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String location;

    @Column(columnDefinition = "LONGTEXT")
    private String email;

    @Column(columnDefinition = "LONGTEXT")
    private String phone;

    @Column(nullable = false)
    private boolean showUserRealName;

    @Column(nullable = false)
    private boolean purchased;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity purchasedBy;

    public ListingEntity() {
        title = "";
        category = "";
        description = "";
        price = 0.00;
        location = "";
        email = "";
        phone = "";
        showUserRealName = false;
        purchased = false;
    }

    public ListingEntity(String title, String category, String description, double price, String location, String email, String phone, boolean showUserRealName, boolean purchased) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.location = location;
        this.email = email;
        this.phone = phone;
        this.showUserRealName = showUserRealName;
        this.purchased = purchased;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
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

    public List<ImageEntity> getImages() {
        return images;
    }
    public void setImages(List<ImageEntity> images) {
        this.images = images;
    }

    public boolean isShowUserRealName() {
        return showUserRealName;
    }
    public void setShowUserRealName(boolean showUserRealName) {
        this.showUserRealName = showUserRealName;
    }

    public boolean isPurchased() {
        return purchased;
    }
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public UserEntity getPurchasedBy() {
        return purchasedBy;
    }
    public void setPurchasedBy(UserEntity purchasedBy) {
        this.purchasedBy = purchasedBy;
    }
}