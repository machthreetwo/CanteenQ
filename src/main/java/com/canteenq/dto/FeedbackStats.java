package com.canteenq.dto;

public record FeedbackStats(int length, int words, int vowels, int consonants,
                            int digits, int specialChars, String mood) {
}
