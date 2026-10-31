package com.canteenq.dto;

import java.util.List;

public record OrderRequest(Long memberId, String offer, List<LineRequest> items) {
}
