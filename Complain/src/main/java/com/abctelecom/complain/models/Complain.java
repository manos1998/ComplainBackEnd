package com.abctelecom.complain.models;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "complains")
public class Complain {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idC;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private Long uId ;

	private String pincode;
	
	private String type;

	private String details;

	private boolean active;

	private String status;
	
	private String feedback;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
	
	@ManyToMany(mappedBy = "complains", fetch = FetchType.LAZY)
	private Set<User> workers;

	public Complain(boolean active, String details, String type, String status) {
		this.createdOn = LocalDateTime.now();
		this.type = type;
		this.details = details;
		this.active = active;
		this.status = status;
	}

	public Complain() {
	}
	
	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public Long getuId() {
		return uId;
	}

	public void setuId(Long uId) {
		this.uId = uId;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}


	public Long getIdC() {
		return idC;
	}

	public void setIdC(Long idC) {
		this.idC = idC;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void addWorkers(User user) {
		this.workers.add(user);
	}

	public Set<User> getWorkers() {
		return workers;
	}

	public void setWorkers(Set<User> workers) {
		this.workers = workers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idC == null) ? 0 : idC.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Complain other = (Complain) obj;
		if (idC == null) {
			if (other.idC != null)
				return false;
		} else if (!idC.equals(other.idC))
			return false;
		return true;
	}

}
