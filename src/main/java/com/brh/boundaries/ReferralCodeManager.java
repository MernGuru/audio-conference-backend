package com.brh.boundaries;


public class ReferralCodeManager {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Generate a referral code from a user ID
    public String generateReferralCode(int userId) {
        // Generate 3-digit number from user ID
        int numericPart = userId % 1000; // Ensures a 3-digit number

        // Generate letters from user ID
        String lettersPart = generateLettersFromUserId(userId);

        return String.format("%03d%s", numericPart, lettersPart); // Ensures the numeric part is 3 digits
    }

    // Generate letter part of the referral code from user ID
    private static String generateLettersFromUserId(int userId) {
        int base = userId % ALPHABET.length();
        char letter1 = ALPHABET.charAt((base) % ALPHABET.length());
        char letter2 = ALPHABET.charAt((base + 1) % ALPHABET.length());
        char letter3 = ALPHABET.charAt((base + 2) % ALPHABET.length());

        return "" + letter1 + letter2 + letter3;
    }

    // Extract the user ID from a referral code
    public int extractUserIdFromReferralCode(String referralCode) {
        if (referralCode == null || referralCode.length() < 6) {
            return -1;
        }

        // Extract the numeric part (last 3 digits of user ID)
        int numericPart = Integer.parseInt(referralCode.substring(0, 3));

        // The letter part can be used for further validation or estimation of the user ID range if necessary
        // For simplicity, this example returns the numeric part directly
        // You might need a more complex algorithm to map letters back to the user ID

        return numericPart;
    }
}
