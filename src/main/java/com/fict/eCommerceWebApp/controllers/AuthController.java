package com.fict.eCommerceWebApp.controllers;

import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
public class AuthController {
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String viewLoginPage() {
        return "auth/auth_login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String viewRegisterPage(Model model) {
        model.addAttribute("user", new UserEntity());
        return "auth/auth_register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerNewUser(@Valid UserEntity user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("errors", bindingResult.getAllErrors());

            return "auth/auth_register";
        } else {
            if (userService.userExistsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("emailExists", true);

                return "auth/auth_register";
            } else {
                if (userService.userExistsByUsername(user.getUsername())) {
                    model.addAttribute("user", user);
                    model.addAttribute("usernameExists", true);

                    return "auth/auth_register";
                } else {
                    if (user.getPassword().length() >= 8) {
                        if (user.getPassword().equals(user.getConfirmPassword())) {
                            UserEntity newUser = userService.register(user);
                            redirectAttributes.addAttribute("id", newUser.getId()).addFlashAttribute("success", true);
                        } else {
                            model.addAttribute("user", user);
                            model.addAttribute("passwordsNoMatch", true);

                            return "auth/auth_register";
                        }
                    } else {
                        model.addAttribute("user", user);
                        model.addAttribute("shortPassword", true);

                        return "auth/auth_register";
                    }

                    return "redirect:/login";
                }
            }
        }
    }
}