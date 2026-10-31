package com.canteenq.dto;

import java.util.List;

public record RushReport(String dish, int plates, List<String> served, List<String> missed, List<String> log) {
}
