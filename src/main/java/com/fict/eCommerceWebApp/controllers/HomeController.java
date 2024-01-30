package com.fict.eCommerceWebApp.controllers;

import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.services.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {
    private ListingService listingService;

    @Autowired
    public HomeController(ListingService listingService) {
        this.listingService = listingService;
    }

    @RequestMapping(value = {"/home", "/", ""}, method = RequestMethod.GET)
    public String viewHomePage(Model model) {
        List<ListingEntity> allListings = listingService.findAllNonPurchasedListings();

        if (!allListings.isEmpty()) {
            Collections.shuffle(allListings);
            List<ListingEntity> someListings = allListings.subList(0, Math.min(allListings.size(), 10));
            model.addAttribute("someListings", someListings);

            List<ListingEntity> latestListings = listingService.orderNonPurchasedListingsByDateDescending();
            latestListings = latestListings.subList(0, Math.min(latestListings.size(), 10));
            model.addAttribute("latestListings", latestListings);

            List<ListingEntity> cheapestListings = listingService.orderNonPurchasedListingsByPriceAscending();
            cheapestListings = cheapestListings.subList(0, Math.min(cheapestListings.size(), 10));
            model.addAttribute("cheapestListings", cheapestListings);

            List<String> allCategories = listingService.findAllCategories();
            Collections.shuffle(allCategories);
            List<ListingEntity> categoryListings = listingService.findNonPurchasedListingsByCategory(allCategories.get(0));
            Collections.shuffle(categoryListings);
            categoryListings = categoryListings.subList(0, Math.min(categoryListings.size(), 10));
            model.addAttribute("categoryListings", categoryListings);
        } else {
            model.addAttribute("noListings", true);
        }
        return "home";
    }
}