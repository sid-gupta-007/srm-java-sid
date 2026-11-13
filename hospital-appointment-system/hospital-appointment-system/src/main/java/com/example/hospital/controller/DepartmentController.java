package com.example.hospital.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hospital.entity.Department;
import com.example.hospital.repository.DepartmentRepository;

@Controller
public class DepartmentController 
{

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/departments")
    public String showDepartments(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@ModelAttribute Department department) {
        departmentRepository.save(department);
        return "redirect:/departments";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return "redirect:/departments";
    }

    @GetMapping("/api/departments")
    @ResponseBody
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
