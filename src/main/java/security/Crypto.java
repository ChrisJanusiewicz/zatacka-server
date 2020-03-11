package security;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;


public class Crypto {

    private final int passwordHashIterations;
    private final int passwordKeyLength;
    private final String keyStoreLocation;
    public KeyManagerFactory kmf;
    public TrustManagerFactory tmf;
    private KeyStore myKeyStore;
    private SecureRandom secureRandom;
    private MessageDigest digest;
    private SecretKeyFactory skf;

    public Crypto(String keyStoreLocation, int passwordHashIterations, int passwordKeyLength) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {

        this.keyStoreLocation = keyStoreLocation;
        this.passwordHashIterations = passwordHashIterations;
        this.passwordKeyLength = passwordKeyLength;

        this.skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        this.digest = MessageDigest.getInstance("SHA-256");


        secureRandom = new SecureRandom();

        getTrustStore();

    }

    private void getTrustStore() {

        try (InputStream keyStoreData = new FileInputStream(keyStoreLocation)) {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());


            System.out.println("[Crypto]: Loading keystore...");
            myKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            myKeyStore.load(keyStoreData, "password".toCharArray());

            kmf.init(myKeyStore, "password".toCharArray());
            tmf.init(myKeyStore);
            System.out.println("[Crypto]: Keystore initialiased");

            /*System.out.println("kmf:" + kmf.toString());
            System.out.println("algorithm:" + kmf.getAlgorithm());
            System.out.println("key managers:" + Arrays.toString(kmf.getKeyManagers()));

            System.out.println("tmf:" + tmf.toString());
            System.out.println("algorithm:" + tmf.getAlgorithm());
            System.out.println("trust managers:" + Arrays.toString(tmf.getTrustManagers()));*/

        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }

    }

    private void loadKeyStore() {
        myKeyStore = null;
        char[] keyStorePassword = "password".toCharArray();


        try (InputStream keyStoreData = new FileInputStream("keystore.jks")) {
            myKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            myKeyStore.load(keyStoreData, keyStorePassword);


            Certificate certificate = myKeyStore.getCertificate("selfsigned");
            PrivateKey pKey = (PrivateKey) myKeyStore.getKey("selfsigned", "password".toCharArray());
            System.out.println(pKey.toString());
            kmf.init(myKeyStore, "password".toCharArray());

        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }

    }

    private void exportCertificate() {
        KeyStore keyStore = null;
        char[] keyStorePassword = "password".toCharArray();


        try (InputStream keyStoreData = new FileInputStream("keystore.jks")) {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreData, keyStorePassword);


            Certificate certificate = keyStore.getCertificate("selfsigned");

            byte[] certificateBytes = Base64.getEncoder().encode(certificate.getEncoded());


            try (FileOutputStream stream = new FileOutputStream("cert.x509")) {
                stream.write(certificateBytes);
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public byte[] genSalt() {
        byte[] salt = new byte[passwordKeyLength];
        secureRandom.nextBytes(salt);
        return salt;
    }


    //TODO: init secretkey factory and keep it as a static member of crypto class
    public byte[] hashPassword(final String password, final byte[] salt) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, passwordHashIterations, passwordKeyLength);
        SecretKey key = skf.generateSecret(spec);
        byte[] res = key.getEncoded();
        return res;
    }

    public byte[] getMessageDigest(byte[] messageBytes) {
        return digest.digest(messageBytes);
    }

}
