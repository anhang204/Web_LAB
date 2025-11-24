package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // List all students (delegates to searchStudents)
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        searchStudents(request, response);
    }

    // Search students (Exercise 5)
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get parameters
        String keyword = request.getParameter("search");
        String major = request.getParameter("major");
        String sortBy = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        String pageParam = request.getParameter("page");
        
        // 2. Pagination defaults
        int page = 1;
        int limit = 5; // Students per page
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offset = (page - 1) * limit;
        
        // 3. Get data from DAO
        List<Student> students = studentDAO.getAllStudents(keyword, major, sortBy, sortOrder, limit, offset);
        int totalStudents = studentDAO.countStudents(keyword, major);
        List<String> majors = studentDAO.getMajors();
        
        int totalPages = (int) Math.ceil((double) totalStudents / limit);
        
        // 4. Set attributes for JSP
        request.setAttribute("students", students);
        request.setAttribute("majors", majors);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalStudents", totalStudents);
        
        // Preserve filter/sort parameters
        request.setAttribute("search", keyword);
        request.setAttribute("currentMajor", major);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        // 5. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Insert new student with validation
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        // Validation
        String error = null;
        if (studentCode == null || studentCode.trim().isEmpty()) {
            error = "Student Code is required";
        } else if (fullName == null || fullName.trim().isEmpty()) {
            error = "Full Name is required";
        } else if (email == null || email.trim().isEmpty()) {
            error = "Email is required";
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            error = "Invalid Email format";
        } else if (major == null || major.trim().isEmpty()) {
            error = "Major is required";
        }
        
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("student", new Student(studentCode, fullName, email, major));
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        
        if (studentDAO.addStudent(newStudent)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            request.setAttribute("error", "Failed to add student (Duplicate code or DB error)");
            request.setAttribute("student", newStudent);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    // Update student with validation
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        // Validation
        String error = null;
        if (studentCode == null || studentCode.trim().isEmpty()) {
            error = "Student Code is required";
        } else if (fullName == null || fullName.trim().isEmpty()) {
            error = "Full Name is required";
        } else if (email == null || email.trim().isEmpty()) {
            error = "Email is required";
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            error = "Invalid Email format";
        } else if (major == null || major.trim().isEmpty()) {
            error = "Major is required";
        }
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);
        
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            request.setAttribute("error", "Failed to update student (Duplicate code or DB error)");
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
}
