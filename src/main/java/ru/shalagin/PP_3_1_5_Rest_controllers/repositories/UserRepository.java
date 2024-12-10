package ru.shalagin.PP_3_1_5_Rest_controllers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    @Query("Select u from User u left join fetch u.roles where u.email=:userName")
    Optional<User> findByUserName (@Param("userName") String userName);
}
