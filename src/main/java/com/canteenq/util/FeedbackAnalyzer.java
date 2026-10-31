package com.canteenq.util;

import com.canteenq.dto.FeedbackStats;

import java.util.Arrays;

public final class FeedbackAnalyzer {

    private static final String VOWELS = "aeiouAEIOU";
    private static final String[] GOOD_WORDS = {"tasty", "good", "great", "awesome", "fresh", "love", "nice", "yummy", "hot"};
    private static final String[] BAD_WORDS = {"cold", "bad", "late", "stale", "oily", "worst", "salty", "slow", "costly"};

    private FeedbackAnalyzer() {
    }

    public static FeedbackStats analyse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Feedback cannot be empty");
        }
        int vowels = 0, consonants = 0, digits = 0, specials = 0;
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                if (VOWELS.indexOf(ch) >= 0) {
                    vowels++;
                } else {
                    consonants++;
                }
            } else if (Character.isDigit(ch)) {
                digits++;
            } else if (!Character.isWhitespace(ch)) {
                specials++;
            }
        }
        int words = text.trim().split("\\s+").length;
        return new FeedbackStats(text.length(), words, vowels, consonants, digits, specials, mood(text.toLowerCase()));
    }

    private static String mood(String text) {
        long good = Arrays.stream(GOOD_WORDS).filter(text::contains).count();
        long bad = Arrays.stream(BAD_WORDS).filter(text::contains).count();
        if (good > bad) {
            return "Happy";
        }
        return good < bad ? "Unhappy" : "Neutral";
    }
}
