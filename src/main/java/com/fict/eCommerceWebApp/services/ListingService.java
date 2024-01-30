package com.fict.eCommerceWebApp.services;

import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ListingService {
    private ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<ListingEntity> findAllNonPurchasedListings() {
        return listingRepository.findAllByPurchasedIsFalse();
    }

    public Optional<ListingEntity> findListingById(Long id) {
        return listingRepository.findById(id);
    }

    public List<ListingEntity> findNonPurchasedSearchedListings(String searchedListing) {
        return listingRepository.findByTitleContainingAndPurchasedIsFalse(searchedListing);
    }

    public List<ListingEntity> findNonPurchasedListingsByCategory(String category) {
        return listingRepository.findAllByCategoryAndPurchasedIsFalse(category);
    }

    public List<String> findAllCategories() {
        return listingRepository.findAllCategories();
    }

    public List<ListingEntity> orderNonPurchasedListingsByDateDescending() {
        return listingRepository.findAllByPurchasedIsFalseOrderByCreationDateDesc();
    }

    public List<ListingEntity> orderNonPurchasedListingsByDateAscending() {
        return listingRepository.findAllByPurchasedIsFalseOrderByCreationDateAsc();
    }

    public List<ListingEntity> orderNonPurchasedListingsByPriceDescending() {
        return listingRepository.findAllByPurchasedIsFalseOrderByPriceDesc();
    }

    public List<ListingEntity> orderNonPurchasedListingsByPriceAscending() {
        return listingRepository.findAllByPurchasedIsFalseOrderByPriceAsc();
    }

    public List<ListingEntity> findAllListingsByUser(UserEntity user) {
        return listingRepository.findAllByCreatedBy(user);
    }

    public List<ListingEntity> findAllNonPurchasedListingsByUser(UserEntity user) {
        return listingRepository.findAllByCreatedByAndPurchasedIsFalse(user);
    }

    public List<ListingEntity> findAllPurchasedListingsByUser(UserEntity user) {
        return listingRepository.findAllByPurchasedIsTrueAndPurchasedBy(user);
    }

    public List<ListingEntity> findAllPurchasedListingsCreatedBy(UserEntity user) {
        return listingRepository.findAllByPurchasedIsTrueAndCreatedBy(user);
    }

    public boolean saveListing(ListingEntity listing) {
        listingRepository.save(listing);
        return true;
    }

    @Transactional
    public boolean deleteListing(ListingEntity listing) {
        listingRepository.delete(listing);
        return true;
    }
}