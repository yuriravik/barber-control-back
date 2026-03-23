package br.com.ravikyu.barbercontrol.infrastructure.web.controller;

import br.com.ravikyu.barbercontrol.application.dashboard.dto.DashboardResponse;
import br.com.ravikyu.barbercontrol.application.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse obter() {
        return dashboardService.obter();
    }
}
