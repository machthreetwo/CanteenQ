package com.canteenq.service;

import com.canteenq.exception.InvalidOfferException;
import com.canteenq.pattern.OfferPolicy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class OfferService {

    private final Map<String, OfferPolicy> offers;

    public OfferService(Map<String, OfferPolicy> offers) {
        this.offers = offers;
    }

    public OfferPolicy pick(String code) {
        String key = (code == null || code.isBlank()) ? "NONE" : code.trim().toUpperCase();
        OfferPolicy policy = offers.get(key);
        if (policy == null) {
            throw new InvalidOfferException(code);
        }
        return policy;
    }

    public Map<String, String> available() {
        Map<String, String> list = new TreeMap<>();
        offers.forEach((code, policy) -> list.put(code, policy.label()));
        return list;
    }
}
