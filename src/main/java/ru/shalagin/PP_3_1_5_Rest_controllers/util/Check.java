package ru.shalagin.PP_3_1_5_Rest_controllers.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;

import java.util.List;

@Component
public class Check {

    // Метод проверки на ошибки валидации и что введенный пароль и его подтверждение совпадают
    public void hasErrors (BindingResult bindingResult) {

        StringBuilder errorMsg = new StringBuilder();
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError e : errors) {
                errorMsg.append(e.getDefaultMessage())
                        .append(";\n");
            }
            throw new UserNotCreatedException(errorMsg.toString());
        }
    }

    // Метод проверки, что выбрана хотя бы одна роль
    public void selectedRoles(User user) {

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
           throw new UserNotCreatedException("Please select at least one role\n");
        }
    }

    public void password (User user) {

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            throw new UserNotCreatedException("Password and Confirm_Password do not match\n");
        }
    }

    public void all (User user, BindingResult bindingResult) {

        hasErrors(bindingResult);
        selectedRoles(user);
        password(user);
    }

}
