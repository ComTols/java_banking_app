package Data;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!§$%&()=?_-.: ";

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Generiere den Großbuchstaben
        password.append(CHARACTERS.charAt(random.nextInt(26)));

        // Generiere den Kleinbuchstaben
        password.append(CHARACTERS.charAt(random.nextInt(26) + 26));

        // Generiere die Zahl
        password.append(CHARACTERS.charAt(random.nextInt(10) + 52));

        // Generiere das Sonderzeichen
        password.append(CHARACTERS.charAt(random.nextInt(15) + 62));

        // Generiere die restlichen Zeichen
        for (int i = 4; i < 10; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }
}
