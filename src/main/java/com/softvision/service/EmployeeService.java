package com.softvision.service;

import com.softvision.model.Employee;
import com.softvision.model.EmployeeType;
import com.softvision.model.TechnologyCommunity;

import java.util.List;
import java.util.Optional;

public interface EmployeeService<T extends Employee> {

    Optional<T> getInterviewerById(String id);

    Optional<List<Employee>> getAllEmployers();

    Optional<T> getEmployeeById(String id);

    List<T> search(String str);

    Optional<T> addEmployee(T employee);

    Optional<T> updateEmployee(T employee, String id);

    void deleteEmployee(String id);

    void deleteAllEmployees();

    Optional<List<Employee>> getAllEmployeesByBandExp(int expInmonths, String technicalCommunity);

    Optional<List<TechnologyCommunity>> getTechStack();

    Optional<List<EmployeeType>> getEmployeeType();
}
