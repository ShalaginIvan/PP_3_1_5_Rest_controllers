package ru.shalagin.PP_3_1_5_Rest_controllers.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName (@Param("name") String name);
}

