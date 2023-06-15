package com.sakeman.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.employee.Employee;
import com.sakeman.entity.employee.EmployeeService;
import com.sakeman.form.EmployeeForm;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @Autowired
    private ModelMapper modelMapper;

    /** employeeの一覧を取得 */
    @GetMapping("/list")
    public String index(Model model) {
        model.addAttribute("employeeList", service.findAll());
        return "employee/employeeList";
    }

    /** employeeを1件取得 */
    @GetMapping("/detail/{id}")
    public String getEmployeeOne(EmployeeForm form, Model model,
            @PathVariable("id") String id) {
        Employee employee = service.findById(id);
        form = modelMapper.map(employee, EmployeeForm.class);
        model.addAttribute("employeeForm", form);
        return "employee/employeeDetail";
    }

    /** employeeをupdate */
    @PostMapping(value = "/detail", params = "update")
    public String updateOne(@ModelAttribute EmployeeForm form, Model model) {
        Employee employee = modelMapper.map(form, Employee.class);
        service.update(employee);
        return "redirect:/employee/list";
    }

    /** employeeをdelete */
    @PostMapping(value = "/detail", params = "delete")
    public String deleteOne(@ModelAttribute EmployeeForm form, Model model) {
        service.deleteById(form.getId());
        return "redirect:/employee/list";
    }
}