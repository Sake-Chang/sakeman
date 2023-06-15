package com.sakeman.entity.employee;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Employee {
    @Id
    private String id;
    private String name;
    private int age;
}