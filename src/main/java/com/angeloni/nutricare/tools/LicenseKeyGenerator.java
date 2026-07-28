package com.angeloni.nutricare.tools;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * DEVELOPER TOOL — DO NOT DISTRIBUTE WITH THE APPLICATION.
 *
 * Generates a license key for a given machine ID.
 * Usage: java LicenseKeyGenerator <machineId>
 *
 * The machine ID is shown in the app's License Required dialog.
 * The generated key must be sent to the customer and pasted into the activation field.
 */
public class LicenseKeyGenerator {

    // KEEP THIS PRIVATE — never commit to a public repository
    private static final String PRIVATE_KEY_B64 =
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDQvldRIXwz4JUk" +
        "rI2bZPMIZwFIL5fFqP0m5L8H6vK3Em3HoQRr74UsknYHcic9Q4U73GLAJuLl3ir" +
        "IHgzt/ekj9I/aYPvh7i9wQzOJhg6qQMH5igOgu4Icxi6SFU3EGQFc+Z0QLGLxjYk" +
        "glWLY6vAy0KNEh1gxtfIlyx02N5zL9brzjGmliArVi6vjBm/AapZy1H+uSfda7mq" +
        "tBT0HbaJZPX3+77Smb/xb3XoKtz15ccOsKIUHi/7T+Eikmbt0Eqq4caImtunPaxu" +
        "jCb4nl3u9PnXASd5f81OhJRZvM8Y9Bfah07GipybMkp0RoG9R8UgZg49VZIXbjKE" +
        "ns9an/CNTAgMBAAECggEAAP2WPn1RMJHVQ1pKRb2B5b1PZT9MkhSONvwI6Am2YyYE" +
        "PVlHQxfc4uxDASAKjaDfZfxSv6SvmPWn8FEsMIju7UvAzwF80IKn2PmIqhNnyGkM" +
        "QpnkEIQzRt2t5OWS++Y+eml41FXRlmHcj7oFwQ/EWglhbPbTdVDuuD0xg0taRCGB" +
        "MaG6rZjZ/oKIC87I7H4Reip5MWAFYx++4jOOoN2P2ZpuuYptvtr+yU25AqfLrt2Q" +
        "M0ewk51LW/kN7vcDHOxpt7Qr83K8shLOf5gmVx7F8FpLwj+esMrssyl1YXORAcfs" +
        "dbKbye+bNW2cUEX0BUZAXT3D8LhGEBdCFiVEc8tJYQKBgQDX4DwyAGTUYTFD6ncg" +
        "fm68d2hWkfXpo1Zx6GFq1VjrfMYxF6yoVPktGR0wGFN/ZxkT6C74cQPWgIDmgFL7" +
        "7xM871nB6Dsy1G3Wvdu52HFZjpFSQCnrmK8TJJvaNFKI+5BuBvE/9wK4jdquhbs1" +
        "qoxTpI0GkUf0JMM4lKGq+Ynb4wKBgQD3irvLiy8SwvFog8+8gjI0VgKAfUi5oufP" +
        "p3eC00MLgoKN9E5WKv1HMd58q8IgTjvSd4MiJrCDedBcXR5ZgHL3J9jHCg7wwcdL" +
        "Jn+QBcuJy7R0i1ZTkhQajN5ldjC1Jlor5o7nwYrLO5MaROKDjNRgtQ/nB28QLeEi" +
        "RjoDS+IV0QKBgACbKJAbYC0YeMLwDZWRxU3SKADWOBKH2t5HAL8EClPe7c+FeX6f" +
        "fnOZ9nMEHnflGjTsGfWom5ImmjYLdjAu46bwJRheRoxF/Gj173/+E5NkyxOoCnYW" +
        "36b2G1npe7HWwvkMG/FKCD/sZgjHZ1cnO22gsKSsE7+jusiV4j5QpQpxAoGAFCx1" +
        "Q5BTRMcoX2kmm+lMaCF+ULRlIePZ2n1+auYGt8BqHWGEpNcaDxxMUoK47mMR0wQ0" +
        "ZqsahYx+45gfKhIizGut6gKy1wIj2McJbFZckR1N1pWocmUcSFGkqkYokArRHHTM" +
        "vqxyQD/ZrDFi+G+Yu0zmP+DGN15V07I/dhdQFJECgYEAohnUj3T/mDjPMdtjTeaF" +
        "cxQzrRa3Jcx4Jdtj3260CTKMEHT5WA8ExuKq0xWfMVgXhTQlM8bL6mbDif1GE+Xs" +
        "Hpf3jbX3inOi92WEzan07e2Ah9o5nftf8hzQwlBc7xU5KOk3U+7UwXoMaTy+ngnQ" +
        "648DnkYV/1uBGa2IQTNRCko=";

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: LicenseKeyGenerator <machineId>");
            System.exit(1);
        }
        String machineId = args[0];
        byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY_B64.replaceAll("\\s", ""));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(machineId.getBytes(StandardCharsets.UTF_8));
        String licenseKey = Base64.getEncoder().encodeToString(sig.sign());
        System.out.println("License key for machine [" + machineId + "]:");
        System.out.println(licenseKey);
    }
}
