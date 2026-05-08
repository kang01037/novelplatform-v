package org.example.novelplatform.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

public class FileValidator {

    private static final Map<String, byte[][]> IMAGE_MAGIC_NUMBERS = Map.of(
            "jpg", new byte[][]{
                    new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
                    new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0},
                    new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1}
            },
            "png", new byte[][]{
                    new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
            },
            "gif", new byte[][]{
                    new byte[]{0x47, 0x49, 0x46, 0x38, 0x37, 0x61},
                    new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61}
            },
            "webp", new byte[][]{
                    new byte[]{0x52, 0x49, 0x46, 0x46}
            }
    );

    public static boolean validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String extension = getExtension(originalFilename).toLowerCase();
        if (!IMAGE_MAGIC_NUMBERS.containsKey(extension)) {
            return false;
        }

        byte[] fileBytes;
        try (InputStream is = file.getInputStream()) {
            fileBytes = is.readAllBytes();
        } catch (IOException e) {
            return false;
        }

        if (fileBytes.length < 3) {
            return false;
        }

        byte[][] magicNumbers = IMAGE_MAGIC_NUMBERS.get(extension);
        for (byte[] magic : magicNumbers) {
            if (startsWith(fileBytes, magic)) {
                return true;
            }
        }

        if ("webp".equals(extension) && fileBytes.length >= 12) {
            byte[] webpMagic = new byte[]{0x57, 0x45, 0x42, 0x50};
            if (Arrays.equals(Arrays.copyOfRange(fileBytes, 8, 12), webpMagic)) {
                return true;
            }
        }

        return false;
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
}
