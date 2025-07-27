package com.tanishka.ecommerce.dto;

import java.time.LocalDateTime;

import com.tanishka.ecommerce.entity.Role;
import com.tanishka.ecommerce.entity.User;

import lombok.Data;

@Data

public class UserResponseDTO {
	 private  Long userId;
	    private  String name;
	    private  String email;
	    private  String mobileNo;
	    private  Role role;
	    private  LocalDateTime createdAt;
	    
	    public UserResponseDTO(User user) {
	        this.userId = user.getUserId();
	        this.name = user.getName();
	        this.email = user.getEmail();
	        this.mobileNo = user.getMobileNo();
	        this.role = user.getRole();
	        this.createdAt = user.getCreatedAt();
	    }

}
