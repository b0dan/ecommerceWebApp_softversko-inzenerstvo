package com.fict.eCommerceWebApp.controllers;

import com.fict.eCommerceWebApp.entities.ImageEntity;
import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.ImageService;
import com.fict.eCommerceWebApp.services.ListingService;
import com.fict.eCommerceWebApp.services.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private UserService userService;
    private ListingService listingService;

    @Autowired
    public ProfileController(UserService userService, ListingService listingService) {
        this.userService = userService;
        this.listingService = listingService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewProfilePage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("userProfile", user);
            model.addAttribute("listingsByUser", listingService.findAllNonPurchasedListingsByUser(user));
        }
        return "profiles/profile_view";
    }

    @RequestMapping(value = "/{id}/purchased", method = RequestMethod.GET)
    public String viewPurchasedListingsPage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("userProfile", user);
            model.addAttribute("purchasedListings", listingService.findAllPurchasedListingsByUser(user));
        }
        return "profiles/profile_purchased";
    }


    @RequestMapping(value = "/{id}/listings", method = RequestMethod.GET)
    public String viewCreatedListingsPage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("userProfile", user);
            model.addAttribute("createdListings_nonPurchased", listingService.findAllNonPurchasedListingsByUser(user));
            model.addAttribute("createdListings_purchased", listingService.findAllPurchasedListingsCreatedBy(user));
        }
        return "profiles/profile_listings";
    }

    @RequestMapping(value = "/{id}/balance", method = RequestMethod.GET)
    public String viewProfileBalancePage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("userProfile", user);
        }
        return "profiles/profile_balance";
    }

    @RequestMapping(value = "/{id}/balance", method = RequestMethod.POST)
    public String addBalance(@PathVariable Long id, @RequestParam(value = "amount") double amount, RedirectAttributes redirectAttributes) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            user.setBalance(user.getBalance() + amount);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.now();
            Map<String, Double> balanceHistory = user.getBalanceHistory();
            balanceHistory.put(dateTimeFormatter.format(localDateTime), amount);
            user.setBalanceHistory(balanceHistory);

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("added", true);

            return "redirect:/profile/" + user.getId() + "/balance";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{id}/settings", method = RequestMethod.GET)
    public String viewSettingsPage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("userProfile", user);
        }
        return "profiles/profile_settings";
    }

    @RequestMapping(value = "/{id}/settings", method = RequestMethod.POST)
    public String updateSettings(@PathVariable Long id, @RequestParam(value = "realName", required = false) String realName, @RequestParam(value = "showName", required = false) String showRealName, @RequestParam(value = "location", required = false) String location, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "email", required = false) String email, @RequestParam(value = "oldPassword", required = false) String oldPassword, @RequestParam(value = "newPassword", required = false) String newPassword, RedirectAttributes redirectAttributes) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            if (realName != null || showRealName != null || location != null) {
                if (realName != null) {
                    user.setFullName(realName);
                }

                user.setShowUserRealName(showRealName != null);

                if (location != null) {
                    user.setLocation(location);
                }
                redirectAttributes.addFlashAttribute("updated", true);
            }
            if (username != null || email != null) {
                if (username != null) {
                    user.setUsername(username);
                }
                if (email != null) {
                    user.setEmail(email);
                }
                redirectAttributes.addFlashAttribute("updated", true);
            }
            if ((oldPassword != null && newPassword != null) && oldPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                redirectAttributes.addFlashAttribute("updated", true);
            } else if ((oldPassword != null && newPassword != null) && !oldPassword.equals(user.getPassword())) {
                redirectAttributes.addFlashAttribute("passwordError", true);
            }
            userService.saveUser(user);

            return "redirect:/profile/" + user.getId() + "/settings";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
    public String viewDeleteProfilePage(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            model.addAttribute("user", user);
            model.addAttribute("userListings", listingService.findAllNonPurchasedListingsByUser(user).size());

            return "profiles/profile_delete";
        } else {
            return "error";
        }
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String deleteProfile(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if(userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            List<ListingEntity> allListingsByUser = listingService.findAllNonPurchasedListingsByUser(user);
            if (userService.deleteUser(user)) {
                if (!allListingsByUser.isEmpty()) {
                    for (ListingEntity listing : allListingsByUser) {
                        if (listingService.deleteListing(listing)) {
                            if (!listing.getImages().isEmpty()) {
                                for (ImageEntity image : listing.getImages()) {
                                    File file = new File(ImageService.IMAGES_DIRECTORY + image.getImageName());
                                    if (file.exists() && file.isFile()) {
                                        FileUtils.delete(file);
                                    }
                                }
                            }
                        } else {
                            return "error";
                        }
                    }
                }
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                SecurityContextHolder.clearContext();

                redirectAttributes.addFlashAttribute("deleted", true);
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("notDeleted", true);
                return "redirect:/profile/" + user.getId() + "/settings";
            }
        } else {
            return "error";
        }
    }
}