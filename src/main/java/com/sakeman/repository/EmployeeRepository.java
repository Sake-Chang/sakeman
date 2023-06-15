package com.sakeman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.employee.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
