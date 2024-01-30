package com.fict.eCommerceWebApp.controllers;

import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {
    private UserService userService;

    @Autowired
    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void globalAttribute(@AuthenticationPrincipal UserEntity loggedInUser, Model model) {
        if (loggedInUser != null) {
            Optional<UserEntity> userOpt = userService.findUserById(loggedInUser.getId());
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                model.addAttribute("loggedInUser", user);
            }
        }
    }
}