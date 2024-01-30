package com.fict.eCommerceWebApp.controllers;

import com.fict.eCommerceWebApp.entities.ImageEntity;
import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.ImageService;
import com.fict.eCommerceWebApp.services.ListingService;
import com.fict.eCommerceWebApp.services.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/listings")
public class ListingController {
    private ListingService listingService;
    private ImageService imageService;
    private UserService userService;

    @Autowired
    public ListingController(ListingService listingService, ImageService imageService, UserService userService) {
        this.listingService = listingService;
        this.imageService = imageService;
        this.userService = userService;
    }

    @RequestMapping(value = "/browse", method = RequestMethod.GET)
    public String viewBrowseListingsPage(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "search", required = false) String searchedListing, @RequestParam(value = "category", required = false) String categoryName, @RequestParam(value = "sort", required = false) String sortBy, Model model) {
        List<ListingEntity> listingsList;
        if (searchedListing == null) {
            listingsList = listingService.findAllNonPurchasedListings();
        } else {
            listingsList = listingService.findNonPurchasedSearchedListings(searchedListing);
        }

        if (categoryName != null) {
            listingsList = listingService.findNonPurchasedListingsByCategory(categoryName);
            model.addAttribute("selectedCategory", categoryName);
        }

        model.addAttribute("sorted", "Sort By");
        if (sortBy != null) {
            switch (sortBy) {
                case "latest" -> {
                    listingsList = listingService.orderNonPurchasedListingsByDateDescending();
                    model.addAttribute("sorted", "Latest First");
                }

                case "oldest" -> {
                    listingsList = listingService.orderNonPurchasedListingsByDateAscending();
                    model.addAttribute("sorted", "Oldest First");
                }

                case "lowest" -> {
                    listingsList = listingService.orderNonPurchasedListingsByPriceAscending();
                    model.addAttribute("sorted", "Lowest Price");
                }

                case "highest" -> {
                    listingsList = listingService.orderNonPurchasedListingsByPriceDescending();
                    model.addAttribute("sorted", "Highest Price");
                }

                default -> {
                    model.addAttribute("sortingError", true);
                    model.addAttribute("sortingArgument", sortBy);
                }
            }
        }
        model.addAttribute("listings", listingsList);

        int start = (page - 1) * 10;
        int end = Math.min(start + 10, listingsList.size());
        List<ListingEntity> paginatedList = listingsList.subList(start, end);

        Pageable pageable = PageRequest.of((page - 1), 10);
        Page<ListingEntity> pageList = new PageImpl<>(paginatedList, pageable, listingsList.size());

        model.addAttribute("pageList", pageList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageList.getTotalPages());

        String queryString = ServletUriComponentsBuilder.fromCurrentRequest().build().getQuery();
        if (queryString == null || queryString.isEmpty()) {
            queryString = "?page=";
        } else {
            if (queryString.contains("page=")) {
                queryString = "?" + queryString.replaceAll("page=\\d+", "page=");
            } else {
                queryString = ("?" + queryString + "&page=");
            }
        }
        model.addAttribute("query", queryString);

        return "listings/listing_browse";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String viewCreateListingPage(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(currentUser.getId());
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("listing", new ListingEntity());

        return "listings/listing_create";
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String createListing(@Valid ListingEntity listing, BindingResult bindingResult, @RequestParam(value = "uploadedImages", required = false) MultipartFile[] images, @RequestParam(value = "showPhone", required = false) String showPhone, @RequestParam(value = "showEmail", required = false) String showEmail, @RequestParam(value = "showName", required = false) String showRealName, Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("notSaved", true);
            model.addAttribute("listing", listing);
            model.addAttribute("errors", bindingResult.getAllErrors());

            return "listings/listing_create";
        } else {
            if (showPhone == null) {
                listing.setPhone(null);
            }
            if (showEmail == null) {
                listing.setEmail(null);
            }
            if (showRealName == null) {
                listing.setShowUserRealName(false);
            } else {
                listing.setShowUserRealName(showRealName.equals("showName"));
            }

            if (listingService.saveListing(listing)) {
                if (!images[0].isEmpty()) {
                    imageService.saveImage(listing, images);
                }
                redirectAttributes.addFlashAttribute("success", true);
            } else {
                model.addAttribute("notSaved", true);
                model.addAttribute("listing", listing);

                return "listings/listing_create";
            }
            return "redirect:/listings/" + listing.getId();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewListingPage(@PathVariable Long id, Model model) {
        Optional<ListingEntity> listingOpt = listingService.findListingById(id);
        if (listingOpt.isPresent()) {
            ListingEntity listing = listingOpt.get();
            model.addAttribute("listing", listing);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity currentUser) {
                if (currentUser.getId().equals(listing.getCreatedBy().getId())) {
                    model.addAttribute("userCanModify", true);
                }
            }
        }
        return "listings/listing_view";
    }

    @RequestMapping(value = "/purchase/{id}", method = RequestMethod.GET)
    public String viewPurchaseListingPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity currentUser) {
            Optional<UserEntity> userOpt = userService.findUserById(currentUser.getId());
            Optional<ListingEntity> listingOpt = listingService.findListingById(id);
            if (userOpt.isPresent() && listingOpt.isPresent()) {
                UserEntity user = userOpt.get();
                ListingEntity listing = listingOpt.get();

                if (user.getId().equals(listing.getCreatedBy().getId())) {
                    redirectAttributes.addFlashAttribute("sameUser", true);
                    return "redirect:/listings/" + listing.getId();
                } else if (user.getBalance() < listing.getPrice()) {
                    redirectAttributes.addFlashAttribute("lowBalance", true);
                    return "redirect:/listings/" + listing.getId();
                } else {
                    model.addAttribute("user", user);
                    model.addAttribute("listing", listing);

                    return "listings/listing_purchase";
                }
            } else {
                return "error";
            }
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/purchase/{id}", method = RequestMethod.POST)
    public String purchaseListing(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity currentUser) {
            Optional<UserEntity> userOpt = userService.findUserById(currentUser.getId());
            Optional<ListingEntity> listingOpt = listingService.findListingById(id);
            if (userOpt.isPresent() && listingOpt.isPresent()) {
                UserEntity user = userOpt.get();
                ListingEntity listing = listingOpt.get();

                if (user.getId().equals(listing.getCreatedBy().getId())) {
                    return "error";
                } else {
                    user.setBalance(user.getBalance() - listing.getPrice());
                    userService.saveUser(user);

                    UserEntity listingUser = listing.getCreatedBy();
                    listingUser.setBalance(listingUser.getBalance() + listing.getPrice());
                    userService.saveUser(listingUser);

                    listing.setPurchased(true);
                    listing.setPurchasedBy(user);
                    listingService.saveListing(listing);

                    redirectAttributes.addFlashAttribute("purchased", true);
                    return "redirect:/listings/browse";
                }
            } else {
                return "error";
            }
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String viewEditListingPage(@PathVariable Long id, Model model) {
        Optional<ListingEntity> listingOpt = listingService.findListingById(id);
        if (listingOpt.isPresent()) {
            ListingEntity listing = listingOpt.get();
            model.addAttribute("listing", listing);

            return "listings/listing_edit";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String editListing(@PathVariable Long id, @Valid ListingEntity listing, BindingResult bindingResult, @RequestParam(value = "uploadedImages", required = false) MultipartFile[] images, @RequestParam(value = "showPhone", required = false) String showPhone, @RequestParam(value = "showEmail", required = false) String showEmail, @RequestParam(value = "showName", required = false) String showRealName, Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("notSaved", true);
            model.addAttribute("listing", listing);
            model.addAttribute("errors", bindingResult.getAllErrors());

            return "listings/listing_edit";
        } else {
            if (showPhone == null) {
                listing.setPhone(null);
            }
            if (showEmail == null) {
                listing.setEmail(null);
            }
            if (showRealName == null) {
                listing.setShowUserRealName(false);
            } else {
                listing.setShowUserRealName(showRealName.equals("showName"));
            }

            if (listingService.saveListing(listing)) {
                if (!images[0].isEmpty()) {
                    imageService.saveImage(listing, images);
                }
                redirectAttributes.addFlashAttribute("success", true);
            } else {
                model.addAttribute("notSaved", true);
                model.addAttribute("listing", listing);

                return "listings/listing_edit";
            }
            return "redirect:/listings/" + listing.getId();
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String viewDeleteListingPage(@PathVariable Long id, Model model) {
        Optional<ListingEntity> listingOpt = listingService.findListingById(id);
        if (listingOpt.isPresent()) {
            ListingEntity listing = listingOpt.get();
            model.addAttribute("listing", listing);

            return "listings/listing_delete";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteListing(@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {
        Optional<ListingEntity> listingOpt = listingService.findListingById(id);
        if(listingOpt.isPresent()) {
            ListingEntity listing = listingOpt.get();
            if (listingService.deleteListing(listing)) {
                if (!listing.getImages().isEmpty()) {
                    for (ImageEntity image : listing.getImages()) {
                        File file = new File(ImageService.IMAGES_DIRECTORY + image.getImageName());
                        if (file.exists() && file.isFile()) {
                            FileUtils.delete(file);
                        }
                    }
                }
                redirectAttributes.addFlashAttribute("deleted", true);
                return "redirect:/listings/browse";
            } else {
                redirectAttributes.addFlashAttribute("notDeleted", true);
                return "redirect:/listings/" + listing.getId();
            }
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{id}/images", method = RequestMethod.GET)
    public String viewImagesAssociatedWithListing(@PathVariable Long id, Model model) {
        Optional<ListingEntity> listingOpt = listingService.findListingById(id);
        if (listingOpt.isPresent()) {
            ListingEntity listing = listingOpt.get();
            model.addAttribute("listing", listing);

            return "images/image_list";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{listingId}/images/delete/{id}", method = RequestMethod.GET)
    public String viewDeleteImageAssociatedWithListing(@PathVariable Long id, Model model) {
        Optional<ImageEntity> imageOpt = imageService.findImageById(id);
        if (imageOpt.isPresent()) {
            ImageEntity image = imageOpt.get();
            model.addAttribute("image", image);

            return "/images/image_delete";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{listingId}/images/delete/{imageId}", method = RequestMethod.POST)
    public String deleteImageAssociatedWithListing(@PathVariable Long listingId, @PathVariable Long imageId, RedirectAttributes redirectAttributes) throws IOException {
        Optional<ImageEntity> imageOpt = imageService.findImageById(imageId);
        if (imageOpt.isPresent()) {
            ImageEntity image = imageOpt.get();

            Optional<ListingEntity> listingOpt = listingService.findListingById(listingId);
            if (listingOpt.isPresent()) {
                ListingEntity listing = listingOpt.get();

                if (imageService.deleteImage(image)) {
                    redirectAttributes.addFlashAttribute("imageDeleted", true);

                    if (listing.getImages().isEmpty()) {
                        return "redirect:/listings/" + listingId;
                    } else {
                        return "redirect:/listings/" + listingId + "/images";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("imageNotDeleted", true);
                    return "redirect:/listings/" + listingId + "/images";
                }
            } else {
                return "error";
            }
        } else {
            return "error";
        }
    }
}