import java.io.File;
import java.util.Scanner;

/**
 * Created by vitor on 4/9/16.
 */
public class EncryptFile {
    public static void main(String[] args) {
        String key;

        Scanner input = new Scanner(System.in);
        System.out.println("Digite a chave que você deseja usar para cifrar o arquivo: ");
        key = input.nextLine();
        while (key.length() != 16) {
            System.out.println("A chave deve ter exatamente 16 caractéres, por favor digite novamente: ");
            key = input.nextLine();
        }

        String fileName;
        System.out.println("Digite o arquivo que deve ser cifrado: ");
        fileName = input.nextLine();

        String fileExtension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        File inputFile = new File(fileName);
        File encryptedFile = new File(fileNameWithoutExtension + ".encrypted" + fileExtension);
        File decryptedFile = new File(fileNameWithoutExtension + ".decrypted" + fileExtension);

        try {
            CryptoUtils.encrypt(key, inputFile, encryptedFile);
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
