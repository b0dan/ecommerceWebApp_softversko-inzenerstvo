package com.fict.eCommerceWebApp.entities;

import com.fict.eCommerceWebApp.entities.audit.Auditable;
import javax.persistence.*;

@Entity
public class ImageEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ListingEntity listingEntity;

    public ImageEntity() {
        imageName = "";
    }

    public ImageEntity(String imageName, ListingEntity listingEntity) {
        this.imageName = imageName;
        this.listingEntity = listingEntity;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public ListingEntity getListing() {
        return listingEntity;
    }
    public void setListing(ListingEntity listingEntity) {
        this.listingEntity = listingEntity;
    }
}