package pe.vichaval.gestionriego.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.vichaval.gestionriego.dashboard.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({
            "/",
            "/dashboard"
    })
    public String mostrarDashboard(Model model) {

        model.addAttribute(
                "resumen",
                dashboardService.obtenerResumen()
        );

        return "dashboard/index";
    }
}