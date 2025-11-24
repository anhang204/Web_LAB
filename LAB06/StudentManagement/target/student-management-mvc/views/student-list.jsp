<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-style: italic;
        }
        
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .btn {
            display: inline-block;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        
        tbody tr {
            transition: background-color 0.2s;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <div>
                <h1>📚 Student Management System</h1>
                <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
            </div>
            <div>
                <span style="margin-right: 10px; color: #666;">Welcome, <strong>${sessionScope.user.username}</strong></span>
                <a href="logout" class="btn btn-danger" style="padding: 8px 15px; font-size: 14px;">Logout</a>
            </div>
        </div>
        
        <!-- Success Message -->
        <c:if test="${not empty param.message}">
            <div class="message success">
                ✅ ${param.message}
            </div>
        </c:if>
        
        <!-- Error Message -->
        <c:if test="${not empty param.error}">
            <div class="message error">
                ❌ ${param.error}
            </div>
        </c:if>
        
        <!-- Search and Filter Form -->
        <div style="background: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
            <form action="student" method="GET" style="display: flex; gap: 15px; align-items: center; flex-wrap: wrap;">
                <input type="hidden" name="action" value="list">
                
                <div style="flex: 1; min-width: 200px;">
                    <input type="text" name="search" value="${search}" placeholder="Search by Name or Code..." 
                           style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                </div>
                
                <div style="min-width: 150px;">
                    <select name="major" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
                        <option value="">-- All Majors --</option>
                        <c:forEach var="m" items="${majors}">
                            <option value="${m}" ${currentMajor == m ? 'selected' : ''}>${m}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="btn btn-primary">🔍 Search</button>
                <a href="student?action=list" class="btn btn-secondary">Reset</a>
            </form>
        </div>

        <!-- Add New Student Button -->
        <div style="margin-bottom: 20px;">
            <a href="student?action=new" class="btn btn-primary">
                ➕ Add New Student
            </a>
        </div>
        
        <!-- Student Table -->
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <th>
                                <a href="student?action=list&search=${search}&major=${currentMajor}&sort=id&order=${sortBy == 'id' && sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: white; text-decoration: none;">
                                    ID ${sortBy == 'id' ? (sortOrder == 'ASC' ? '▲' : '▼') : ''}
                                </a>
                            </th>
                            <th>
                                <a href="student?action=list&search=${search}&major=${currentMajor}&sort=student_code&order=${sortBy == 'student_code' && sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: white; text-decoration: none;">
                                    Student Code ${sortBy == 'student_code' ? (sortOrder == 'ASC' ? '▲' : '▼') : ''}
                                </a>
                            </th>
                            <th>
                                <a href="student?action=list&search=${search}&major=${currentMajor}&sort=full_name&order=${sortBy == 'full_name' && sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: white; text-decoration: none;">
                                    Full Name ${sortBy == 'full_name' ? (sortOrder == 'ASC' ? '▲' : '▼') : ''}
                                </a>
                            </th>
                            <th>
                                <a href="student?action=list&search=${search}&major=${currentMajor}&sort=email&order=${sortBy == 'email' && sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: white; text-decoration: none;">
                                    Email ${sortBy == 'email' ? (sortOrder == 'ASC' ? '▲' : '▼') : ''}
                                </a>
                            </th>
                            <th>
                                <a href="student?action=list&search=${search}&major=${currentMajor}&sort=major&order=${sortBy == 'major' && sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: white; text-decoration: none;">
                                    Major ${sortBy == 'major' ? (sortOrder == 'ASC' ? '▲' : '▼') : ''}
                                </a>
                            </th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                <td>
                                    <div class="actions">
                                        <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">
                                            ✏️ Edit
                                        </a>
                                        <a href="student?action=delete&id=${student.id}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this student?')">
                                            🗑️ Delete
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <!-- Pagination -->
                <div style="margin-top: 20px; display: flex; justify-content: center; gap: 5px;">
                    <c:if test="${currentPage > 1}">
                        <a href="student?action=list&page=${currentPage - 1}&search=${search}&major=${currentMajor}&sort=${sortBy}&order=${sortOrder}" class="btn btn-secondary">Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="student?action=list&page=${i}&search=${search}&major=${currentMajor}&sort=${sortBy}&order=${sortOrder}" 
                           class="btn ${currentPage == i ? 'btn-primary' : 'btn-secondary'}"
                           style="padding: 8px 12px;">
                            ${i}
                        </a>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="student?action=list&page=${currentPage + 1}&search=${search}&major=${currentMajor}&sort=${sortBy}&order=${sortOrder}" class="btn btn-secondary">Next</a>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <h3>No students found</h3>
                    <p>Start by adding a new student</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
