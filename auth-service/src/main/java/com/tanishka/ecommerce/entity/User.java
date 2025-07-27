package com.tanishka.ecommerce.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

@Column(nullable = false)
private String name;

@Column(unique = true, nullable = false)
private String email;

@Column(nullable = false)
private String password;

@Column(unique = true, nullable = false)
private String mobileNo;

@Enumerated(EnumType.STRING)
private Role role=Role.USER;

@CreationTimestamp
private LocalDateTime createdAt;

@Column(nullable = false)
private Boolean isActive;

}
