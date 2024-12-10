package ru.shalagin.PP_3_1_5_Rest_controllers.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shalagin.PP_3_1_5_Rest_controllers.model.Role;
import ru.shalagin.PP_3_1_5_Rest_controllers.repositories.RoleRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role get(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль " + roleName + " не найдена!\n"));
    }
    public boolean create(String roleName) {
        Optional<Role> roleFromBD = roleRepository.findByName(roleName);
        if (!roleFromBD.isEmpty()) {
            return false;
        }
        roleRepository.save(new Role(roleName));
        return true;
    }

}
