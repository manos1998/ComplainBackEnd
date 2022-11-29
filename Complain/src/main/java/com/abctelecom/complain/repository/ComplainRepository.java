package com.abctelecom.complain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abctelecom.complain.models.Complain;

public interface ComplainRepository extends JpaRepository<Complain, Long> {

	List<Complain> findByActive(boolean active);

	List<Complain> findByTypeContaining(String type);

	List<Complain> findByUserId(Long userId);

//	List<Complain> findByComplainId(Long complainId);

}
