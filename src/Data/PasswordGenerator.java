package Data;

import java.security.SecureRandom;

/**
 * Generates passwords
 *
 * @author Joscha Dierks
 * @version v1.0_stable_alpha
 */
public class PasswordGenerator {
    /**
     * Charset to used in password
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!§$%&()=?_-.: ";

    /**
     * Generates a password with minimum one uppercase letter, one lowercase letter, one digit and one special letter
     * @return generated password
     */
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
