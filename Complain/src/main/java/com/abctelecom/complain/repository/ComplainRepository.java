package com.abctelecom.complain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abctelecom.complain.models.Complain;

public interface ComplainRepository extends JpaRepository<Complain, Long> {

	List<Complain> findByActive(boolean active);

	List<Complain> findByTypeContaining(String type);

	List<Complain> findByUserId(Long userId);
	
//	@Query(value =  "SELECT c complain.complains c FROM INNER JOIN complain.users u  ON c.user_id = u.id", nativeQuery = true)
//	List<?> findComplainsCreater();
	
	@Query(value = "SELECT * FROM COMPLAINS c WHERE c.c_user_id = ?1", nativeQuery = true)
	List<Complain> findAllComplainWithUserId(long id);

	//complain._id is complain id 
	@Query(value =  "SELECT * FROM worker_complain_table wc INNER JOIN complains u ON wc.complain_idc = u.idc WHERE wc.user_id = ?1", nativeQuery = true)
	List<Complain> findAllComplainUser(long id);

}
