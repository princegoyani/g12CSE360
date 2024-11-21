package com.educationCenter.Encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionHelperTest {

    private EncryptionHelper encryptionHelper;

    @BeforeEach
    void setUp() throws Exception {
        encryptionHelper = new EncryptionHelper();
    }

    @Test
    void encryptAndDecrypt_shouldReturnOriginalText() throws Exception {
        String originalText = "Hello, Encryption!";
        byte[] plainText = originalText.getBytes(StandardCharsets.UTF_8);

        // Create an example initialization vector
        byte[] initializationVector = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };

        // Encrypt the text
        byte[] encryptedText = encryptionHelper.encrypt(plainText, initializationVector);
        assertNotNull(encryptedText, "Encryption failed; result is null.");

        // Decrypt the text
        byte[] decryptedText = encryptionHelper.decrypt(encryptedText, initializationVector);
        assertNotNull(decryptedText, "Decryption failed; result is null.");

        // Verify the decrypted text matches the original
        String decryptedString = new String(decryptedText, StandardCharsets.UTF_8);
        assertEquals(originalText, decryptedString, "Decrypted text does not match the original.");
    }

    @Test
    void encrypt_shouldThrowExceptionForInvalidIV() {
        String text = "Invalid IV Test";
        byte[] plainText = text.getBytes(StandardCharsets.UTF_8);

        // Invalid initialization vector (not 16 bytes)
        byte[] invalidIV = new byte[]{0x01, 0x02};

        Exception exception = assertThrows(Exception.class, () -> {
            encryptionHelper.encrypt(plainText, invalidIV);
        });

        String expectedMessage = "Invalid IV";
        exception.getMessage().contains(expectedMessage);
    }

    @Test
    void decrypt_shouldThrowExceptionForTamperedCipherText() throws Exception {
        String text = "Tampering Test";
        byte[] plainText = text.getBytes(StandardCharsets.UTF_8);

        // Create a valid initialization vector
        byte[] initializationVector = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };

        // Encrypt the text
        byte[] encryptedText = encryptionHelper.encrypt(plainText, initializationVector);

        // Tamper with the encrypted text
        encryptedText[0] = (byte) (encryptedText[0] + 1);

        // Try to decrypt the tampered text
        Exception exception = assertThrows(Exception.class, () -> {
            encryptionHelper.decrypt(encryptedText, initializationVector);
        });

        String expectedMessage = "pad block corrupted"; // BouncyCastle-specific error
        assertTrue(exception.getMessage().contains(expectedMessage), "Expected an error due to tampering.");
    }

    @Test
    void encryptAndDecrypt_shouldHandleEmptyString() throws Exception {
        String originalText = "";
        byte[] plainText = originalText.getBytes(StandardCharsets.UTF_8);

        // Create an example initialization vector
        byte[] initializationVector = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };

        // Encrypt the text
        byte[] encryptedText = encryptionHelper.encrypt(plainText, initializationVector);
        assertNotNull(encryptedText, "Encryption failed; result is null.");

        // Decrypt the text
        byte[] decryptedText = encryptionHelper.decrypt(encryptedText, initializationVector);
        assertNotNull(decryptedText, "Decryption failed; result is null.");

        // Verify the decrypted text matches the original
        String decryptedString = new String(decryptedText, StandardCharsets.UTF_8);
        assertEquals(originalText, decryptedString, "Decrypted text does not match the original.");
    }

    @Test
    void encrypt_shouldReturnDifferentCipherTextForSamePlainTextWithDifferentIVs() throws Exception {
        String text = "Same Text, Different IVs";
        byte[] plainText = text.getBytes(StandardCharsets.UTF_8);

        // Create two different initialization vectors
        byte[] iv1 = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };
        byte[] iv2 = new byte[]{
                0x0f, 0x0e, 0x0d, 0x0c, 0x0b, 0x0a, 0x09, 0x08,
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00
        };

        // Encrypt the text with two different IVs
        byte[] encryptedText1 = encryptionHelper.encrypt(plainText, iv1);
        byte[] encryptedText2 = encryptionHelper.encrypt(plainText, iv2);

        // Ensure the cipher texts are different
        assertNotEquals(Base64.getEncoder().encodeToString(encryptedText1),
                Base64.getEncoder().encodeToString(encryptedText2),
                "Cipher text should differ for different IVs.");
    }
}
