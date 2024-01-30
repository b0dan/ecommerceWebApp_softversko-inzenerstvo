package com.fict.eCommerceWebApp.repositories;

import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<ListingEntity, Long> {
    List<ListingEntity> findAllByPurchasedIsFalse();

    List<ListingEntity> findByTitleContainingAndPurchasedIsFalse(String searchedTitle);

    List<ListingEntity> findAllByCategoryAndPurchasedIsFalse(String category);

    @Query("SELECT category FROM ListingEntity")
    List<String> findAllCategories();

    List<ListingEntity> findAllByPurchasedIsFalseOrderByCreationDateDesc();

    List<ListingEntity> findAllByPurchasedIsFalseOrderByCreationDateAsc();

    List<ListingEntity> findAllByPurchasedIsFalseOrderByPriceDesc();

    List<ListingEntity> findAllByPurchasedIsFalseOrderByPriceAsc();

    List<ListingEntity> findAllByCreatedBy(UserEntity user);

    List<ListingEntity> findAllByCreatedByAndPurchasedIsFalse(UserEntity user);

    List<ListingEntity> findAllByPurchasedIsTrueAndPurchasedBy(UserEntity user);

    List<ListingEntity> findAllByPurchasedIsTrueAndCreatedBy(UserEntity user);
}