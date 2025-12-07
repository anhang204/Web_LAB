package com.example.product_management.controller;

import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showDashboard(Model model) {
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("averagePrice", productService.getAveragePrice());
        model.addAttribute("totalProducts", productService.getAllProducts().size());

        Map<String, Long> categoryCounts = new HashMap<>();
        for (String cat : productService.getAllCategories()) {
            categoryCounts.put(cat, productService.countProductsByCategory(cat));
        }
        model.addAttribute("categoryCounts", categoryCounts);

        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
        model.addAttribute("recentProducts", productService.getRecentProducts());

        return "dashboard";
    }
}
