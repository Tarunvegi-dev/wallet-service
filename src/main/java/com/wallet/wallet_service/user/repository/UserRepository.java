package com.wallet.wallet_service.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wallet.wallet_service.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
