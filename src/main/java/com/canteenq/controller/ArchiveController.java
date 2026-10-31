package com.canteenq.controller;

import com.canteenq.io.ArchiveService;
import com.canteenq.io.Bill;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/archive")
public class ArchiveController {

    private final ArchiveService archive;

    public ArchiveController(ArchiveService archive) {
        this.archive = archive;
    }

    @PostMapping("/export")
    public Map<String, Object> export() throws IOException {
        int count = archive.exportBills();
        return Map.of("billsSaved", count, "files", List.of("bills.ser", "day-sheet.txt"));
    }

    @GetMapping("/bills")
    public List<Bill> bills() throws IOException, ClassNotFoundException {
        return archive.importBills();
    }

    @GetMapping("/reprint/{orderId}")
    public Bill reprint(@PathVariable Long orderId) throws IOException, ClassNotFoundException {
        return archive.reprint(orderId);
    }

    @PostMapping("/backup")
    public Map<String, Object> backup() throws IOException {
        return Map.of("bytesCopied", archive.backup(), "file", "bills-backup.ser");
    }

    @GetMapping(value = "/sheet", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sheet() throws IOException {
        return archive.readDaySheet();
    }
}
