package com.electricity.controller;

import com.electricity.model.MeterReading;
import com.electricity.service.ElectricityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/electricity")
@CrossOrigin(origins = "*")
public class ElectricityController {
    private final ElectricityService service;
    public ElectricityController(ElectricityService service) { this.service = service; }

    @PostMapping("/reading")
    public MeterReading addReading(@RequestBody MeterReading reading) {
        try { return service.addReading(reading); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage()); }
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() { return service.getDashboard(); }

    @GetMapping("/history")
    public List<MeterReading> history() { return service.getHistory(); }
}
