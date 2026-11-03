package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Department;
import com.example.service.DepartmentService;
import com.example.service.EmployeeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String departments(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null) {
            return "redirect:/login";
        }

        model.addAttribute("departments", departmentService.getAll());
        model.addAttribute("role", role);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("departmentCounts", employeeService.getCountByDepartment());

        return "departments";
    }

    @PostMapping("/save")
    public String save(@RequestParam String name, HttpSession session) {
        String role = (String) session.getAttribute("role");

        if ("HR".equals(role) && name != null && !name.trim().isEmpty()) {
            departmentService.create(new Department(null, name.trim()));
        }

        return "redirect:/departments";
    }
}
