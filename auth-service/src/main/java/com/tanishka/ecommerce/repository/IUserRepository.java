package com.tanishka.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanishka.ecommerce.entity.User;
@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByMobileNo(String mobileNo);
	
}
