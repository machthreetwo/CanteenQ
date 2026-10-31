package com.canteenq.dto;

import java.time.LocalDateTime;

public record ApiError(LocalDateTime time, int status, String error, String message) {
}
