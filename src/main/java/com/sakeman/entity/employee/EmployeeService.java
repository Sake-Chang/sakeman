package com.sakeman.entity.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.repository.EmployeeRepository;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Transactional
    public Employee update(Employee employee) {
        return repository.save(employee);
    }

    @Transactional
    public Employee findById(String id) {
        Optional<Employee> employee = repository.findById(id);
        return employee.get();
    }

    @Transactional
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}