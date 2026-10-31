package com.canteenq.controller;

import com.canteenq.jdbc.JdbcLedger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final JdbcLedger ledger;

    public LedgerController(JdbcLedger ledger) {
        this.ledger = ledger;
    }

    @PostMapping("/close")
    public Map<String, Object> close(@RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws SQLException {
        return ledger.closeDay(date);
    }

    @GetMapping("/closings")
    public List<Map<String, Object>> closings() throws SQLException {
        return ledger.closings();
    }

    @GetMapping("/low-stock")
    public List<String> lowStock(@RequestParam(defaultValue = "10") int below) throws SQLException {
        return ledger.lowStock(below);
    }
}
