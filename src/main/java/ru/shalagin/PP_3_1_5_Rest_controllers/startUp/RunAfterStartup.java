package ru.shalagin.PP_3_1_5_Rest_controllers.startUp;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.RoleService;
import ru.shalagin.PP_3_1_5_Rest_controllers.services.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class RunAfterStartup {

    private final UserService userService;
    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {

        System.out.println("The application is running!");

        // если база пуста, то создаем для теста базу из 4 пользователей и таблицу ролей
        if (userService.getCountUsers() == 0) {
            // если база пуста, то создаем для теста базу из 4 пользователей и таблицу ролей

            // создаем роли
            roleService.create("ROLE_ADMIN");
            roleService.create("ROLE_USER");

            // admin/user
            User user1 = new User("Рустам", "Башаев", 20, "kata@mail.ru", "1234");
            user1.setRoles(new HashSet<>(Arrays.asList(roleService.get("ROLE_ADMIN"), roleService.get("ROLE_USER"))));

            // user
            User user2 = new User("Mike", "Tyson", 30, "mikeTyson@gmail.ru", "1234");
            user2.setRoles(Collections.singleton(roleService.get("ROLE_USER")));

            // admin/user
            User user3 = new User("Jon", "Smith", 40, "smith@list.ru", "1234");
            user3.setRoles(new HashSet<>(Arrays.asList(roleService.get("ROLE_ADMIN"), roleService.get("ROLE_USER"))));

            // admin
            User user4 = new User("Олег", "Иванов", 50, "oleg-ivanov@gmail.com", "1234");
            user4.setRoles(Collections.singleton(roleService.get("ROLE_ADMIN")));

            userService.save(user1);
            userService.save(user2);
            userService.save(user3);
            userService.save(user4);

            System.out.println("A new database has been created!");
        } else {
            System.out.println("We use the existing database!");
        }
    }
}
