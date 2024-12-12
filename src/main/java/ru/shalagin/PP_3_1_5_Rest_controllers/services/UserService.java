package ru.shalagin.PP_3_1_5_Rest_controllers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.Role;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;
import ru.shalagin.PP_3_1_5_Rest_controllers.repositories.UserRepository;
import ru.shalagin.PP_3_1_5_Rest_controllers.util.UserNotCreatedException;
import ru.shalagin.PP_3_1_5_Rest_controllers.util.UserNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found;\n"));
    }

    @Transactional
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user) {
        // Если такой пользователь уже существует
        // выбрасываем исключение, чтобы потом отправить ответ с сообщением и статусом ошибки
        userRepository.findByUserName(user.getEmail()).ifPresent(existUser -> {
            throw new UserNotCreatedException("User with that email already exists;\n");
        });

        // Получаем роли из базы данных
        Set<Role> roles = user.getRoles().stream()
                .map(role -> roleService.get(role.getName()))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        userRepository.save(user);
    }

    @Transactional
    public void update(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public Long getCountUsers() {
        return userRepository.count();
    }

}
