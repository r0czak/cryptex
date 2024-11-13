package org.atonic.cryptexsimple.model.repository;

import org.atonic.cryptexsimple.model.entity.Role;
import org.atonic.cryptexsimple.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
