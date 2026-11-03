package com.example.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.EmployeeRequest;
import com.example.model.Department;
import com.example.model.Employee;
import com.example.service.DepartmentService;
import com.example.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeApiController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public EmployeeApiController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(employeeService.search(search));
        }

        if (page != null && size != null) {
            return ResponseEntity.ok(employeeService.getEmployees(page, size, sortBy, direction));
        }

        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequest request) {
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentService.getById(request.getDepartmentId());
            if (department == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Department with ID " + request.getDepartmentId() + " not found");
                return ResponseEntity.badRequest().body(error);
            }
        }

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setSalary(request.getSalary() != null ? request.getSalary() : 0.0);
        employee.setDepartment(department);

        Employee saved = employeeService.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest request) {
        Employee existing = employeeService.getById(id);
        if (existing == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getEmail() != null) existing.setEmail(request.getEmail());
        if (request.getSalary() != null) existing.setSalary(request.getSalary());

        if (request.getDepartmentId() != null) {
            Department dept = departmentService.getById(request.getDepartmentId());
            if (dept != null) {
                existing.setDepartment(dept);
            }
        }

        Employee updated = employeeService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.delete(id);
        if (!deleted) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Employee with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee with ID " + id + " deleted successfully");
        response.put("deletedId", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", employeeService.count());
        stats.put("averageSalary", Math.round(employeeService.getAverageSalary() * 100.0) / 100.0);
        stats.put("totalDepartments", departmentService.count());
        stats.put("departmentBreakdown", employeeService.getCountByDepartment());
        return ResponseEntity.ok(stats);
    }
}

