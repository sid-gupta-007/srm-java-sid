package com.example.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> 
{

}
