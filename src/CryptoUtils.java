import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static IvParameterSpec generateIv() throws NoSuchProviderException, NoSuchAlgorithmException {

        byte iv[] = new byte[16];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
                                 File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ivSpec = generateIv();
            cipher.init(cipherMode, secretKey, ivSpec);

            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            if (cipherMode == Cipher.DECRYPT_MODE) {
                byte[] outputBytes = cipher.doFinal(inputBytes);

                // remove the iv from the start of the message
                byte[] plainOutputBytes = new byte[outputBytes.length - ivSpec.getIV().length];

                System.arraycopy(outputBytes, ivSpec.getIV().length, plainOutputBytes, 0, plainOutputBytes.length);
                outputStream.write(plainOutputBytes);
            } else {
                byte[] iv = ivSpec.getIV();
                byte[] outputBytes = new byte[cipher.getOutputSize(iv.length + inputBytes.length)];
                int ctLength = cipher.update(iv, 0, iv.length, outputBytes, 0);
                ctLength += cipher.update(inputBytes, 0, inputBytes.length, outputBytes, ctLength);
                cipher.doFinal(outputBytes, ctLength);

                outputStream.write(outputBytes);
            }

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException
                | InvalidAlgorithmParameterException | NoSuchProviderException
                | ShortBufferException ex) {
            throw new CryptoException("Erro cifrando/decifrando o arquivo", ex);
        }
    }
}
