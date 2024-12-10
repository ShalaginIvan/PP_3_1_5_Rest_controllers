package ru.shalagin.PP_3_1_5_Rest_controllers.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found;\n");
        }
        return user.get();
    }

    @Transactional
    public User getById(Long id) {
        Optional<User> userFromDb = userRepository.findById(id);
        return userFromDb.orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user) {
        Optional<User> userFromBD = userRepository.findByUserName(user.getEmail());
        if (!userFromBD.isEmpty()) {
            // Если такой пользователь уже существует
            // выбрасываем исключение, чтобы потом отправить ответ с сообщением и статусом ошибки
            throw new UserNotCreatedException("User with that email already exists;\n");
        }

        userRepository.save(user);
    }

    @Transactional
    public void update(User user) {
        entityManager.merge(user);
    }

    @Transactional
    public void delete(Long id) {
        entityManager.createQuery("delete from User where id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Transactional
    public Long getCountUsers() {
        return (Long) entityManager.createQuery("SELECT COUNT(u) FROM User u")
                .getSingleResult();
    }

}
