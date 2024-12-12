package ru.shalagin.PP_3_1_5_Rest_controllers.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.Role;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.RoleService;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.UserService;
import ru.shalagin.PP_3_1_5_Rest_controllers.util.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final Check check;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/user") // пример запроса для получения user с id=7 -  http://localhost:8080/user?id=7
    public ResponseEntity<User> getUser(@RequestParam("id") Long id) {

        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersList() {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // Возвращаем 204 - No Content
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid User user, BindingResult bindingResult) {

        check.all(user, bindingResult); // выполняем проверки валидации, ролей и пароля

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user); // Сохраняем пользователя с проверкой уникальности

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/users/update")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid User user, BindingResult bindingResult) {

        check.all(user, bindingResult); // выполняем проверки валидации, ролей и пароля

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.update(user); // Сохраняем пользователя без проверки уникальности

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/user/delete") // пример запроса для удаления user с id=6 - http://localhost:8080/user/delete?id=6
    public ResponseEntity<HttpStatus> delete(@RequestParam("id") Long id) {

        userService.delete(id);
        // отправляем HTTP ответ с пустым телом и статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(
                "User with this id wasn't found!",
                System.currentTimeMillis()
        );
        // В HTTP ответе тело ответа (userErrorResponse) и статус в заголовке
        return new ResponseEntity<>(userErrorResponse, HttpStatus.NOT_FOUND); // - 404 статус
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        // В HTTP ответе тело ответа (userErrorResponse) и статус в заголовке
        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST); // - 404 статус
    }

}
