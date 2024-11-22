package com.app.ecommerce.repository;

import com.app.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    User getByUsername(String username);

    User findByUsername(String username);

    List<User> findByUserFullNameContainingOrUsernameContaining(String userFullName, String username);
}
