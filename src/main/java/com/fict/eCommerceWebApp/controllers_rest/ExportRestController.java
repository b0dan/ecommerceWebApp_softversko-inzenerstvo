package com.fict.eCommerceWebApp.controllers_rest;

import com.fict.eCommerceWebApp.entities.ListingEntity;
import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.ListingService;
import com.fict.eCommerceWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/export")
public class ExportRestController {
    private ListingService listingService;
    private UserService userService;

    @Autowired
    public ExportRestController(ListingService listingService, UserService userService) {
        this.listingService = listingService;
        this.userService = userService;
    }

    @RequestMapping(value = "/{id}", produces = "text/plain", method = RequestMethod.GET)
    public ResponseEntity<String> exportUserData(@AuthenticationPrincipal UserEntity currentlyLoggedInUser, @PathVariable Long id, @RequestParam(name = "type", required = false) String type) {
        Optional<UserEntity> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            if (!user.getId().equals(currentlyLoggedInUser.getId())) {
                return ResponseEntity.status(403).build();
            }

            if (type == null) {
                type = "json";

            }

            switch(type) {
                case "json" -> {
                    StringBuilder json = new StringBuilder();

                    json.append("{\n");
                    json.append("\t\"userId\": ").append(user.getId()).append(",\n");
                    json.append("\t\"joinDate\": \"").append(user.getCreationDate()).append("\",\n");
                    json.append("\t\"email\": \"").append(user.getEmail()).append("\",\n");
                    json.append("\t\"username\": \"").append(user.getUsername()).append("\",\n");
                    json.append("\t\"name\": \"").append(user.getFullName()).append("\",\n");
                    json.append("\t\"location\": \"").append(user.getLocation()).append("\",\n");
                    json.append("\t\"balance\": ").append(user.getBalance()).append(",\n");

                    json.append("\t\"createdListingsCount\": ").append(listingService.findAllListingsByUser(user).size()).append(",\n");
                    if (!listingService.findAllListingsByUser(user).isEmpty()) {
                        json.append("\t\"createdListings\": [\n");

                        int k1 = 0;
                        for (ListingEntity listing : listingService.findAllListingsByUser(user)) {
                            k1++;

                            json.append("\t\t{\n");
                            json.append("\t\t\t\"listingId\": ").append(listing.getId()).append(",\n");
                            json.append("\t\t\t\"createdOn\": \"").append(listing.getCreationDate()).append("\",\n");
                            if (listing.getLastModifiedDate() != null) {
                                json.append("\t\t\t\"isModified\": true").append(",\n");
                                json.append("\t\t\t\"modifiedOn\": ").append(listing.getLastModifiedDate()).append(",\n");
                            } else {
                                json.append("\t\t\t\"isModified\": false").append(",\n");
                            }
                            json.append("\t\t\t\"title\": \"").append(listing.getTitle()).append("\",\n");
                            json.append("\t\t\t\"category\": \"").append(listing.getCategory()).append("\",\n");
                            json.append("\t\t\t\"description\": \"").append(listing.getDescription()).append("\",\n");
                            if (!listing.getImages().isEmpty()) {
                                json.append("\t\t\t\"hasImages\": true").append(",\n");
                                json.append("\t\t\t\"images\": {\n");
                                json.append("\t\t\t\t\"imageCount\": ").append(listing.getImages().size()).append(",\n");
                                for (int i = 0; i < listing.getImages().size(); i++) {
                                    json.append("\t\t\t\t\"image").append((i + 1)).append("\": \"").append(listing.getImages().get(i).getImageName());
                                    if (i != (listing.getImages().size() - 1)) {
                                        json.append("\",\n");
                                    } else {
                                        json.append("\"\n");
                                    }
                                }
                                json.append("\t\t\t},\n");
                            } else {
                                json.append("\t\t\t\"hasImages\": false").append(",\n");
                            }

                            json.append("\t\t\t\"price\": ").append(listing.getPrice()).append(",\n");
                            if (listing.getLocation() != null) {
                                json.append("\t\t\t\"contactLocation\": \"").append(listing.getLocation()).append("\",\n");
                            }
                            if (listing.getEmail() != null) {
                                json.append("\t\t\t\"contactEmail\": \"").append(listing.getEmail()).append("\",\n");
                            }
                            if (listing.getPhone() != null) {
                                json.append("\t\t\t\"contactPhone\": \"").append(listing.getPhone()).append("\",\n");
                            }
                            json.append("\t\t\t\"isPurchased\": ").append(listing.isPurchased()).append(",\n");
                            if (listing.isPurchased()) {
                                json.append("\t\t\t\"purchasedBy\": \"").append(listing.getPurchasedBy().getUsername()).append("\"\n");
                            }
                            json.append("\t\t}");
                            if (k1 != listingService.findAllListingsByUser(user).size()) {
                                json.append(",\n");
                            } else {
                                json.append("\n");
                            }
                        }
                        json.append("\t],\n");
                    }

                    json.append("\t\"purchasedListingsCount\": ").append(listingService.findAllPurchasedListingsByUser(user).size());
                    if (!listingService.findAllPurchasedListingsByUser(user).isEmpty()) {
                        json.append(",\n");
                        json.append("\t\"purchasedListings\": [\n");

                        int k2 = 0;
                        for (ListingEntity listing : listingService.findAllPurchasedListingsByUser(user)) {
                            k2++;

                            json.append("\t\t{\n");
                            json.append("\t\t\t\"listingId\": ").append(listing.getId()).append(",\n");
                            json.append("\t\t\t\"createdBy_username\": \"").append(listing.getCreatedBy().getUsername()).append("\",\n");
                            if (listing.isShowUserRealName()) {
                                json.append("\t\t\t\"createdBy_name\": \"").append(listing.getCreatedBy().getFullName()).append("\",\n");
                            }
                            json.append("\t\t\t\"createdOn\": \"").append(listing.getCreationDate()).append("\",\n");
                            if (listing.getLastModifiedDate() != null) {
                                json.append("\t\t\t\"isModified\": true").append(",\n");
                                json.append("\t\t\t\"modifiedOn\": ").append(listing.getLastModifiedDate()).append(",\n");
                            } else {
                                json.append("\t\t\t\"isModified\": false").append(",\n");
                            }
                            json.append("\t\t\t\"title\": \"").append(listing.getTitle()).append("\",\n");
                            json.append("\t\t\t\"category\": \"").append(listing.getCategory()).append("\",\n");
                            json.append("\t\t\t\"description\": \"").append(listing.getDescription()).append("\",\n");
                            if (!listing.getImages().isEmpty()) {
                                json.append("\t\t\t\"hasImages\": true").append(",\n");
                                json.append("\t\t\t\"images\": {\n");
                                json.append("\t\t\t\t\"imageCount\": ").append(listing.getImages().size()).append(",\n");
                                for (int i = 0; i < listing.getImages().size(); i++) {
                                    json.append("\t\t\t\t\"image").append((i + 1)).append("\": \"").append(listing.getImages().get(i).getImageName());
                                    if (i != (listing.getImages().size() - 1)) {
                                        json.append("\",\n");
                                    } else {
                                        json.append("\"\n");
                                    }
                                }
                                json.append("\t\t\t},\n");
                            } else {
                                json.append("\t\t\t\"hasImages\": false").append(",\n");
                            }

                            json.append("\t\t\t\"price\": ").append(listing.getPrice()).append(",\n");
                            if (listing.getLocation() != null) {
                                json.append("\t\t\t\"contactLocation\": \"").append(listing.getLocation()).append("\",\n");
                            }
                            if (listing.getEmail() != null) {
                                json.append("\t\t\t\"contactEmail\": \"").append(listing.getEmail()).append("\",\n");
                            }
                            if (listing.getPhone() != null) {
                                json.append("\t\t\t\"contactPhone\": \"").append(listing.getPhone()).append("\",\n");
                            }
                            json.append("\t\t},\n");
                        }
                        json.append("\t]\n");
                    } else {
                        json.append("\n");
                    }
                    json.append("}");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    return new ResponseEntity<>(json.toString(), headers, HttpStatus.OK);
                }

                case "xml" -> {
                    StringBuilder xml = new StringBuilder();

                    xml.append("<user>\n");
                    xml.append("\t<userId>").append(user.getId()).append("</userId>\n");
                    xml.append("\t<joinDate>").append(user.getCreationDate()).append("</joinDate>\n");
                    xml.append("\t<email>").append(user.getEmail()).append("</email>\n");
                    xml.append("\t<username>").append(user.getUsername()).append("</username>\n");
                    xml.append("\t<name>").append(user.getFullName()).append("</name>\n");
                    xml.append("\t<location>").append(user.getLocation()).append("</location>\n");
                    xml.append("\t<balance>").append(user.getBalance()).append("</balance>\n");

                    if (!listingService.findAllListingsByUser(user).isEmpty()) {
                        xml.append("\t<createdListings amount=\"").append(listingService.findAllListingsByUser(user).size()).append("\">\n");

                        for (ListingEntity listing : listingService.findAllListingsByUser(user)) {
                            xml.append("\t\t<listing>\n");
                            xml.append("\t\t\t<listingId>").append(listing.getId()).append("</listingId>\n");
                            xml.append("\t\t\t<createdOn>").append(listing.getCreationDate()).append("</createdOn>\n");
                            if (listing.getLastModifiedDate() != null) {
                                xml.append("\t\t\t<isModified>true</isModified>\n");
                                xml.append("\t\t\t<modifiedOn>").append(listing.getLastModifiedDate()).append("</modifiedOn>\n");
                            } else {
                                xml.append("\t\t\t<isModified>false</isModified>\n");
                            }
                            xml.append("\t\t\t<title>").append(listing.getTitle()).append("</title>\n");
                            xml.append("\t\t\t<category>").append(listing.getCategory().replace("&", "&amp;")).append("</category>\n");
                            xml.append("\t\t\t<description>").append(listing.getDescription()).append("</description>\n");
                            if (!listing.getImages().isEmpty()) {
                                xml.append("\t\t\t<hasImages>true</hasImages>\n");
                                xml.append("\t\t\t<images amount=\"").append(listing.getImages().size()).append("\">\n");
                                for (int i = 0; i < listing.getImages().size(); i++) {
                                    xml.append("\t\t\t\t<image").append((i + 1)).append(">").append(listing.getImages().get(i).getImageName()).append("</image").append((i + 1)).append(">\n");
                                }
                                xml.append("\t\t\t</images>\n");
                            } else {
                                xml.append("\t\t\t<hasImages>false</hasImages>\n");
                            }

                            xml.append("\t\t\t<price>").append(listing.getPrice()).append("</price>\n");
                            if (listing.getLocation() != null) {
                                xml.append("\t\t\t<contactLocation>").append(listing.getLocation()).append("</contactLocation>\n");
                            }
                            if (listing.getEmail() != null) {
                                xml.append("\t\t\t<contactEmail>").append(listing.getEmail()).append("</contactEmail>\n");
                            }
                            if (listing.getPhone() != null) {
                                xml.append("\t\t\t<contactPhone>").append(listing.getPhone()).append("</contactPhone>\n");
                            }
                            xml.append("\t\t\t<isPurchased>").append(listing.isPurchased()).append("</isPurchased>\n");
                            if (listing.isPurchased()) {
                                xml.append("\t\t\t<purchasedBy>").append(listing.getPurchasedBy().getUsername()).append("</purchasedBy>\n");
                            }
                            xml.append("\t\t</listing>\n");
                        }
                        xml.append("\t</createdListings>\n");
                    }

                    if (!listingService.findAllPurchasedListingsByUser(user).isEmpty()) {
                        xml.append("\t<purchasedListings amount=\"").append(listingService.findAllPurchasedListingsByUser(user).size()).append("\">\n");

                        for (ListingEntity listing : listingService.findAllPurchasedListingsByUser(user)) {
                            xml.append("\t\t<listing>\n");
                            xml.append("\t\t\t<listingId>").append(listing.getId()).append("</listingId>\n");
                            xml.append("\t\t\t<createdBy_username>").append(listing.getCreatedBy().getUsername()).append("</createdBy_username>\n");
                            if (listing.isShowUserRealName()) {
                                xml.append("\t\t\t<createdBy_name>").append(listing.getCreatedBy().getFullName()).append("</createdBy_name>\n");
                            }
                            xml.append("\t\t\t<createdOn>").append(listing.getCreationDate()).append("</createdOn>\n");
                            if (listing.getLastModifiedDate() != null) {
                                xml.append("\t\t\t<isModified>true</isModified>\n");
                                xml.append("\t\t\t<modifiedOn>").append(listing.getLastModifiedDate()).append("</modifiedOn>\n");
                            } else {
                                xml.append("\t\t\t<isModified>false</isModified>\n");
                            }
                            xml.append("\t\t\t<title>").append(listing.getTitle()).append("</title>\n");
                            xml.append("\t\t\t<category>").append(listing.getCategory().replace("&", "&amp;")).append("</category>\n");
                            xml.append("\t\t\t<description>").append(listing.getDescription()).append("</description>\n");
                            if (!listing.getImages().isEmpty()) {
                                xml.append("\t\t\t<hasImages>true</hasImages>\n");
                                xml.append("\t\t\t<images amount=\"").append(listing.getImages().size()).append("\">\n");
                                for (int i = 0; i < listing.getImages().size(); i++) {
                                    xml.append("\t\t\t\t<image").append((i + 1)).append(">").append(listing.getImages().get(i).getImageName()).append("</image").append((i + 1)).append(">\n");
                                }
                                xml.append("\t\t\t</images>\n");
                            } else {
                                xml.append("\t\t\t<hasImages>false</hasImages>\n");
                            }

                            xml.append("\t\t\t<price>").append(listing.getPrice()).append("</price>\n");
                            if (listing.getLocation() != null) {
                                xml.append("\t\t\t<contactLocation>").append(listing.getLocation()).append("</contactLocation>\n");
                            }
                            if (listing.getEmail() != null) {
                                xml.append("\t\t\t<contactEmail>").append(listing.getEmail()).append("</contactEmail>\n");
                            }
                            if (listing.getPhone() != null) {
                                xml.append("\t\t\t<contactPhone>").append(listing.getPhone()).append("</contactPhone>\n");
                            }
                            xml.append("\t\t</listing>\n");
                        }
                        xml.append("\t</purchasedListings>\n");
                    } else {
                        xml.append("\n");
                    }
                    xml.append("</user>");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_XML);

                    return new ResponseEntity<>(xml.toString(), headers, HttpStatus.OK);
                }

                default -> {
                    return ResponseEntity.badRequest().body("Invalid type!\nOnly JSON and XML available.");
                }
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}