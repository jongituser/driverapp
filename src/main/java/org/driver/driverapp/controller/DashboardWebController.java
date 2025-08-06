package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.DashboardDTO;
import org.driver.driverapp.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardWebController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        DashboardDTO dashboard = dashboardService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        return "dashboard";
    }
}
