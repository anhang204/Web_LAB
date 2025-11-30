package com.student.controller;

import com.student.dao.UserDAO;
import com.student.model.User;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation
        if (currentPassword == null || currentPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {

            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirm password do not match");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 8) {
            request.setAttribute("error", "New password must be at least 8 characters long");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        // Verify current password
        // Note: We need to fetch the latest password hash from DB or use the one in
        // session if it's up to date.
        // Ideally, we should fetch from DB to be sure.
        User currentUserFromDb = userDAO.getUserById(user.getId());
        if (currentUserFromDb == null || !BCrypt.checkpw(currentPassword, currentUserFromDb.getPassword())) {
            request.setAttribute("error", "Incorrect current password");
            request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
            return;
        }

        // Update password
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        boolean success = userDAO.updatePassword(user.getId(), newHashedPassword);

        if (success) {
            request.setAttribute("success", "Password changed successfully");
            // Update session user object if needed, or just let them login again next time.
            // For better UX, we can update the session user's password field if we want,
            // but strictly speaking we don't store the password in the session user object
            // usually,
            // or if we do it's the hash.
            currentUserFromDb.setPassword(newHashedPassword);
            session.setAttribute("user", currentUserFromDb);
        } else {
            request.setAttribute("error", "Failed to update password. Please try again.");
        }

        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }
}
