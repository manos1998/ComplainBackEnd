package com.abctelecom.complain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abctelecom.complain.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	@Query(value ="SELECT * FROM complain.users u RIGHT JOIN complain.user_roles ur ON u.id = ur.user_id WHERE ur.role_id = 3", nativeQuery = true)
	List<User> findByRolesEngineer();

	@Query(value ="SELECT * FROM complain.users u RIGHT JOIN complain.user_roles ur ON u.id = ur.user_id WHERE ur.role_id = 4", nativeQuery = true)
	List<User> findByRolesFieldWorker();

	@Query(value ="SELECT * FROM complain.users u RIGHT JOIN complain.user_roles ur ON u.id = ur.user_id WHERE ur.role_id = 5", nativeQuery = true)
	List<User> findByRolesUsers();
	
	@Query(value = "SELECT * FROM complain.users u inner JOIN complain.complains c ON c.user_id = u.id", nativeQuery = true)
//	@Query(value = "SELECT * complain.users u FROM INNER JOIN  complain.complains c  ON c.user_id = u.id", nativeQuery = true)
	List<User> findComplainsCreater();
}