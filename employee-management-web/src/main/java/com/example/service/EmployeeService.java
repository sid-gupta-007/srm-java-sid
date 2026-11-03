package com.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.model.Department;
import com.example.model.Employee;

@Service
public class EmployeeService {
    private final List<Employee> employees = new ArrayList<>();
    private long nextId = 1;

    public EmployeeService(DepartmentService departmentService) {
        // Seed initial employees for testing & demo
        Department it = departmentService.getById(1L);
        Department hr = departmentService.getById(2L);
        Department finance = departmentService.getById(3L);

        if (it != null && hr != null && finance != null) {
            create(new Employee(null, "Alice Smith", "alice.smith@example.com", 85000.00, it));
            create(new Employee(null, "Bob Johnson", "bob.johnson@example.com", 65000.00, hr));
            create(new Employee(null, "Charlie Brown", "charlie.b@example.com", 72000.00, finance));
            create(new Employee(null, "Diana Prince", "diana.p@example.com", 94000.00, it));
            create(new Employee(null, "Evan Wright", "evan.w@example.com", 58000.00, finance));
            create(new Employee(null, "Fiona Gallagher", "fiona.g@example.com", 67000.00, hr));
            create(new Employee(null, "George Clark", "george.c@example.com", 79000.00, it));
        }
    }

    public List<Employee> getAll() {
        return new ArrayList<>(employees);
    }

    public Employee create(Employee employee) {
        employee.setId(nextId++);
        employees.add(employee);
        return employee;
    }

    public Employee getById(Long id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Employee> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAll();
        }
        String q = query.toLowerCase().trim();
        return employees.stream()
                .filter(e -> (e.getName() != null && e.getName().toLowerCase().contains(q))
                        || (e.getEmail() != null && e.getEmail().toLowerCase().contains(q))
                        || (e.getDepartment() != null && e.getDepartment().getName().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployees(int page, int size, String sortBy, String direction) {
        List<Employee> result = new ArrayList<>(employees);

        Comparator<Employee> comparator;
        if ("salary".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingDouble(Employee::getSalary);
        } else if ("email".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Employee::getEmail, String.CASE_INSENSITIVE_ORDER);
        } else if ("id".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Employee::getId);
        } else {
            comparator = Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER);
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        result.sort(comparator);

        int start = page * size;
        if (start >= result.size()) {
            return new ArrayList<>();
        }

        int end = Math.min(start + size, result.size());
        return result.subList(start, end);
    }

    public int getTotalPages(int size) {
        if (employees.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) employees.size() / size);
    }

    public Employee update(Long id, Employee updated) {
        Employee existing = getById(id);
        if (existing == null) {
            return null;
        }

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setSalary(updated.getSalary());
        if (updated.getDepartment() != null) {
            existing.setDepartment(updated.getDepartment());
        }

        return existing;
    }

    public boolean delete(Long id) {
        return employees.removeIf(e -> e.getId().equals(id));
    }

    public int count() {
        return employees.size();
    }

    public double getAverageSalary() {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Map<String, Long> getCountByDepartment() {
        return employees.stream()
                .filter(e -> e.getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getDepartment().getName(), Collectors.counting()));
    }
}
