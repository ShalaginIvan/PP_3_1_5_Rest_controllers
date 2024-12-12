package ru.shalagin.PP_3_1_5_Rest_controllers.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.RoleService;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.UserService;
import ru.shalagin.PP_3_1_5_Rest_controllers.util.UserNotCreatedException;

import java.util.Collections;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/registration")
    public String registrationPage(@ModelAttribute("user") User user) {
        return "/auth/registration";
    }

    @PostMapping("/auth/registration")
    public String performRegistration(@Valid @ModelAttribute("user") User user, BindingResult result) {

        if (result.hasErrors()) {
            return "/auth/registration";
        }

        // Проверяем Password and Confirm_Password
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.user", "Password and Confirm_Password do not match");
            return "/auth/registration";
        }

        // при регистрации даем роль -  ROLE_USER
        user.setRoles(Collections.singleton(roleService.get("ROLE_USER")));

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userService.save(user);
        } catch (UserNotCreatedException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            return "/auth/registration";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/admin")
    public String index(Model model) {

        addLoggedInfo(model);
        return "/admin/index";
    }

    @GetMapping("/user")
    public String user(Model model) {

        addLoggedInfo(model);
        return "/user/user";
    }

    // метод загружает в модель данные пользователя, который ввел успешно логин и пароль
    private void addLoggedInfo (Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userAuth = (User) authentication.getPrincipal();

        // Преобразуем Set ролей в строку, разделенную запятой
        String rolesString = userAuth.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(", "));

        // Добавление текущего пользователя и его роли в модель
        model.addAttribute("loggedInRoles", rolesString);
        model.addAttribute("loggedInUser", userAuth);
    }
}