package CIelectronica;


import java.util.Base64;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

/**
 *
 * @author gdotta
 */
public class SmartcardTests {
    //add BC provider for validation to work
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private static final String minucia_3CQ = "761460F21474971A9C6B1C81801D5F641F638220BD4F2243D22299EE229578297EF92A55AF2D5ABD2E79A933BBB4399BC93E591F404A78437E2544699B459A4D4546C34979AA49B9BA4F598051607451406652664F5469B0547990557A6E5B82E25C59595C48AC5D586E63698264B75D666B1B674B9769B5AD6976316DAB4A6E6A9771B2D071583E778B6A784BB27A75997D6FAE7E731B854BA8876F5C8B8B698B89B68E73B491B254938AA9966B309B498B9B68B19B6CE69D5C50A049C7A19BAEA34CD4A55C43A68954A78868AA86A5AA4989AB8958AE46B5AF43CEAF6094B5476AB6449BB7452CB98887BE8637C3454FC44598C44456C6A44BCE4659CE4477CF4433D2848BD5429AD7832FDBA558DB8432E2A670E4845BE54229E9872FECA563EEA477F064";
    private static final String minucia_3CQ_cortada = "761460F21474971A9C6B1C81801D5F641F638220BD4F2243D22299EE229578297EF92A55AF2D5ABD2E79A933BBB4399BC93E591F404A78437E2544699B459A4D4546C34979AA49B9BA4F598051607451406652664F5469B0547990557A6E5B82E25C59595C48AC5D586E63698264B75D666B1B674B9769B5AD6976316DAB4A6E6A9771B2D071583E778B6A784BB27A75997D6FAE7E731B854BA8876F5C8B8B698B89B68E73B491B254938AA9966B309B498B9B68B19B6CE69D5C50A049C7A19B";
    private static final String minucia_3CQ_cortada_2 = "761460F21474971A9C6B1C81801D5F641F638220BD4F2243D22299EE229578297EF92A55AF2D5ABD2E79A933BBB4399BC93E591F404A78437E2544699B459A4D4546C34979AA49B9BA4F598051607451406652664F5469B0547990557A6E5B82E25C59595C48AC5D586E63698264B75D666B1B674B9769B5AD6976316DAB4A6E6A9771B2D071583E778B6A784BB27A75997D6FAE7E731B854BA8876F5C8B8B698B89B68E73B491B254938AA9966B309B498B9B68B19B6CE69D5C50A049C7A19BAEA34CD4A55C43A68954A78868AA86A5AA4989AB8958AE46B5AF43CEAF6094B5476AB6449BB7452CB98887BE8637C3454FC44598C44456C6A4";

    private static final String minucia_3CR = "A401B8A3115A7F115D4B15805916608917BD78177DD91955E419B4C51B76BC1F993C22649B275BCF2956E9295568317EF73195B237B795385AE13B576D415F7E427C8C429C5345A4684A7D284D69404E858F4F5B684F62D44F975E58413F60AB74609D2766AB8F66B8AE67977A685C79739C38768C9C769665806F7A81756E83B350856D5A87AE218C6C66907156978E8598962B994C6A9A72429CAA589C6B62A1ACB0A49938A76AD0A79B4AAA8C75AD5A5BAF8CD1B45EB0B49C8FB65C62B747A5BB5C7FBE5F67BF8150C189B3C55F74C7A2A0CC5F52CE472FD0899DD4403CDD879EDD5F4DE0457AE56339E6A697E7A184F0423FF24597F3A336F76891F783";
    private static final String minucia_3CR_cortada = "A401B8A3115A7F115D4B15805916608917BD78177DD91955E419B4C51B76BC1F993C22649B275BCF2956E9295568317EF73195B237B795385AE13B576D415F7E427C8C429C5345A4684A7D284D69404E858F4F5B684F62D44F975E58413F60AB74609D2766AB8F66B8AE67977A685C79739C38768C9C769665806F7A81756E83B350856D5A87AE218C6C66907156978E8598962B994C6A9A72429CAA589C6B62A1ACB0A49938A76AD0A79B4AAA8C75AD5A5BAF8CD1B45EB0B49C8FB65C62B747A5BB5C7FBE5F67BF8150C189B3C55F74C7A2A0CC5F52CE472FD0899DD4403CDD879EDD5F4DE0457AE56339E6A697E7A184F0423FF24597F3A3";
    private static final String minucia_3CR_cortada_2 = "A401B8A3115A7F115D4B15805916608917BD78177DD91955E419B4C51B76BC1F993C22649B275BCF2956E9295568317EF73195B237B795385AE13B576D415F7E427C8C429C5345A4684A7D284D69404E858F4F5B684F62D44F975E58413F60AB74609D2766AB8F66B8AE67977A685C79739C38768C9C769665806F7A81756E83B350856D5A87AE218C6C66907156978E8598962B994C6A9A72429CAA589C6B62A1ACB0A49938A76AD0A79B4AAA8C75AD5A5BAF8CD1B45EB0B49C8FB65C62B747";

    private static final String pulgarDer_3CR_1 = "A3085C99087D860C9BA00E7B1E12434113605415602218634A184095197AB8199A731C9C6D1DBD071F4568265DC9305966337D7E339BAC395B263B65A73CBA393F62FC3F9672419DDD46995C485F69497D3C4B42455064B2505A5D547F65569E8B589B6A5D7C0D5F4AD867997B695B3E6A67266B69DF6CBA696D7CDC6F779F759BBA767969795D987ABB727D797D7D5AB47DB9557E66E27E97AE845A8D8C7C568EAA708F9D68916350924949946C709663B196598D9A77339A6D6F9C811B9EAE859FB784A475B3A6B6E2A6B76CAE7373AF95D8AF9942B2AF7AB2B228B650BDBA976EBC5330BD7038BE4F5BC3B61CC46EDCC65D64C85558C854";
    private static final String pulgarDer_3CR_2 = "A3085C860C9B1E12435415602218634A184095197AB8199A731C9C6D1DBD071F4568265DC930597E339BAC395B263B65393F62FC3F9672419DDD469969497D3C4B42B2505A5D547F65569E8B589BD867997B695B3E6A67266B69DF6CBADC6F779F759B69795D987ABB7D7D5A557E66E27E97AE845A8D8C7C568EAA708F9D50924949946C709663B196598D9A77339A6D6F9C811B9EAE859FB7B3A6B6E2A6B76CAE7373AF9542B2AF7AB2B2BDBA976EBC5330BD7038BE4F5BC3B61CC46EDCC65D64C85558C854A3CB57E5CBA067D298D2D241F3D49FB9DB9B94DC586EDE78C3E37E65E859A1E85ADFEA604DEC59BAEE7D98EF9CB2F35E81F75D";
    
    // Certificado extraido del eID
    private static String certificate_HEX_DER_encoded = "";

    // Certificado del ministerio del interior, el que firma los del eID
    private static final String MiCA = "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";

    private static final String HASH = "4D7CCCBD17064A12DD43021668679F7B488AFD55AAB1502E0CA8A55F5A8E2C0B";

    private static String HASH_Signature = "";

    private static String PIN_ASCII = "";
    //Test 2

    /**
     * @param args the command line arguments
     * @throws javax.smartcardio.CardException
     * @throws java.io.IOException
     * @throws Base64DecodingException
     * @throws CertificateException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     */
    public static void main(String[] args) throws CardException, IOException,
            
        CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, Exception {
        // TODO code application logic here
        // show the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        // System.out.println("Terminals: " + terminals);
        // get the first terminal
        CardTerminal terminal = terminals.get(0);
        // establish a connection with the card
        Card card = terminal.connect("T=0");
        // System.out.println("card ATR: " +
        // byteArrayToHex(card.getATR().getBytes()));
        CardChannel channel = card.getBasicChannel();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //Log Configuration
        System.out.println("Input the log path or press enter to default location.");
        String logPath = br.readLine();

        if (logPath.equals("")) {
            logPath = System.getProperty("user.dir");
        }

        System.out.println("Input \"C\" for APDU Commands only, \"R\" for APDU Responses only, blank for both \n");

        String logOption = br.readLine();

        LogUtils logUtils = LogUtils.getInstance();
        logUtils.configure(logPath,logOption);

        System.out.println("Path configured: "+logPath);
        
        
        //Log Configuration done
        
        int swValue = 0;

        while (swValue != 9) {

            // Display menu graphics
            System.out.println("\n");
            System.out.println("================================");
            System.out.println("|   SELECCION DE CASO DE USO   |");
            System.out.println("================================");
            System.out.println("| Opciones                     |");
            System.out.println("|                              |");
            System.out.println("|  1. Card authentication PIN  |");
            System.out.println("|  2. Card authentication FP   |");
            System.out.println("|  3. User authentication PIN  |");
            System.out.println("|  4. User identification      |");
            System.out.println("|  5. PKI signature            |");
            System.out.println("|  6. Card CPLC INFO           |");
            System.out.println("|  7. Read File                |");
            System.out.println("|  8. Read Card Certificate    |");
            System.out.println("|  9. Exit                     |");
            System.out.println("============================");

            System.out.println("Seleccione una opcion");

            swValue = Integer.parseInt(br.readLine());

            switch (swValue) {
                case 1:
                    System.out.println("Card authentication PIN\n");

                    //request PIN and transform to ASCII
                    System.out.println("Insert PIN");
                    String pin = br.readLine();
                    PIN_ASCII = Utils.PinToAsciiHex(pin);

                    selectIAS(channel);

                    verifyPIN(channel);

                    readCertificate(channel);

                    MSE_SET_DST(channel);

                    PSO_HASH(channel);

                    PSO_CDS(channel);

                    if (validateHashSignature()) {
                        System.out.println("HASH Validado\n\n");
                    } else {

                        System.out.println("HASH Invalido\n\n");
                    }

                    break;

                case 2:
                    System.out.println("Card authentication FP\n\n");
                    selectIAS(channel);

                    if (verifyFP(channel, minucia_3CQ)) {

                        MSE_SET_DST(channel);

                        PSO_HASH(channel);

                        PSO_CDS(channel);

                        if (validateHashSignature()) {
                            System.out.println("HASH Validado\n\n");
                        } else {
                            System.out.println("HASH Invalido\n\n");
                        }
                    }

                    break;
                case 3:
                    System.out.println("User authentication PIN");
                    break;
                case 4:
                    System.out.println("User identification");

                    selectIAS(channel);
                    FCITemplate fcit7000 = selectFile(channel, "7000");
                    //if (fcit7000.isIsDF()) {
                        //System.out.println(fcit7000.getFileName());
                    //}

                    FCITemplate fcit7001 = selectFile(channel, "7001");
                    String cardNumberFile = readBinary(channel, fcit7001.getFileSize());
                    System.out.println("Binary7001: " + cardNumberFile);

                    FCITemplate fcit7002 = selectFile(channel, "7002");
                    String bioFile =  readBinary(channel, fcit7002.getFileSize());
                    System.out.println("Binary7002: " + bioFile);

                    FCITemplate fcit7004 = selectFile(channel, "7004");
                    String imgFile = readBinary(channel, fcit7004.getFileSize());
//                    String result2 = result.substring(10);
//                    DataOutputStream os = new DataOutputStream(new FileOutputStream("/Users/gdotta/Desktop/a.jpg"));
//                    os.write(Utils.hexStringToByteArray(result2));
//                    os.close();
                    System.out.println("Binary7004: " + imgFile);

                    FCITemplate fcit700B = selectFile(channel, "700B");
                    String mrzFile = readBinary(channel, fcit700B.getFileSize());
                    System.out.println("Binary700B :" + mrzFile);
                    
                    //VARIABLE - not all eID Cards have the SOD in the chip
                    //in the example validation is done if and only if sod present
                    FCITemplate fcit711D = selectFile(channel, "711D");
                    if (fcit711D != null) {
                        String sodFile = readBinary(channel, fcit711D.getFileSize());
                        System.out.println("Binary711D :" + sodFile);
                        
                        if (verifySodSignature(cardNumberFile, bioFile, imgFile, mrzFile, sodFile)) {
                            System.out.println("SIGNATURE CORRECT");
                        } else {
                            System.out.println("SIGNATURE INCORRECT");
                        }
                    } else {
                        System.out.println("DATA ON CARD IS UNSIGNED");
                    }

                    break;
                case 5:
                    System.out.println("PKI signature");
                    break;
                case 6:
                    System.out.println("Card CPLC INFO");
                    break;
                case 7:
                    System.out.println("Read File");

                    System.out.println("Insert File ID");
                    String fileID = br.readLine();

                    selectIAS(channel);
                    FCITemplate fcit = selectFile(channel, fileID);
                    System.out.println(readBinary(channel, fcit.getFileSize()));

                    break;
                case 8:
                    System.out.println("Read Card Certificate");
                    selectIAS(channel);
                    readCardCertificate(channel);
                    System.exit(0);
                    break;
                case 9:
                    System.out.println("Exit selected");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid selection");
                    System.exit(1);
            }

        }

        // disconnect
        card.disconnect(false);
    }

    /*
	 * if (isVerifiedPIN(channel)) { System.out.println("PIN Verificado"); }
	 * else { System.out.println("PIN sin Verificar"); }
	 * 
	 * 
	 * 
	 * 
	 * if (unverifyPIN(channel)) { System.out.println("PIN Desverificado"); }
	 * else { System.out.println("PIN NO Desverificado"); } if
	 * (isVerifiedPIN(channel)) { System.out.println("PIN Verificado"); } else {
	 * System.out.println("PIN sin Verificar"); }
	 * 
	 * // if (selectIAS2(channel)) { //
	 * System.out.println("IAS Select 2 Success"); // } else { //
	 * System.out.println("IAS Select 2 ERROR"); // }
	 * 
	 * // disconnect card.disconnect(false); } }
     */
    public static boolean getCPLCData(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "80";
        String INSTRUCTION = "CA";
        String PARAM1 = "9F";
        String PARAM2 = "7F";

        String dataIN = "";

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // TODO si se codifica esta operacion, se puede armar una clase que
        // parsee las cosas y luego permita imprimir o tomar los datos en forma
        // facil
        // System.out.println("Tag + Length: " + subBytes(response, 0, 2));
        // System.out.println("IC Fabricator: " + subBytes(response, 3, 4));
        // System.out.println("IC Type: " + subBytes(response, 5, 6));
        // System.out.println("OS id: " + subBytes(response, 7, 8));
        // System.out.println("OS release date: " + subBytes(response, 9, 10));
        // System.out.println("OS release level: " + subBytes(response, 11,
        // 12));
        // System.out.println("**** Complete Serial Number: " +
        // subBytes(response, 13, 20));
        // System.out.println("******* IC Fabrication Date: " +
        // subBytes(response, 13, 14));
        // System.out.println("******* IC Serial Number: " + subBytes(response,
        // 15, 18));
        // System.out.println("******* IC Batch ID: " + subBytes(response, 19,
        // 20));
        // System.out.println("IC Module Fabricator: " + subBytes(response, 21,
        // 22));
        // System.out.println("IC Module Packaging Date: " + subBytes(response,
        // 23, 24));
        // System.out.println("ICC Manufacturer: " + subBytes(response, 25,
        // 26));
        // System.out.println("IC Embedding Date: " + subBytes(response, 27,
        // 28));
        // System.out.println("IC Pre-Personalizer: " + subBytes(response, 29,
        // 30));
        // System.out.println("IC Pre-Personalization Date: " +
        // subBytes(response, 31, 32));
        // System.out.println("IC Pre-Personalization Equipment Id: " +
        // subBytes(response, 33, 36));
        // System.out.println("IC Personalizer: " + subBytes(response, 37, 38));
        // System.out.println("IC Personalization Date: " + subBytes(response,
        // 39, 40));
        // System.out.println("IC Personalization Equipment Identifier: " +
        // subBytes(response, 41, 44));
    }

    // Retorna true si pudo seleccionar la aplicacion IAS
    public static boolean selectIAS(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "A4";
        String PARAM1 = "04";
        String PARAM2 = "00";

        String dataIN = "A00000001840000001634200"; //IAS AID
        // String dataIN = "A0000000180C000001634200";

        // ESTO DEBE SER EL APP ID de la IAS
        // REVISAR LA DOCUMENTACION DE GEMALTO PARA CONFIRMARLO
        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);
    }

    private static boolean verifyFP(CardChannel channel, String minutiae) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "21";
        String PARAM1 = "00";
        String PARAM2 = "21";

        String dataIN = "";
        dataIN += "7F2E"; // TAG del TLV para llevar las minucias
        // en la documentacion de gemalto y de la IAS figura como mandar
        // estatico el 8180 en el length
        // pero no funciona si se hace asi.
        // encontre que funciona si se pone el length como 81XX, donde XX es el
        // largo de lo que sigue
        // que si bien es otro TLV, para este es value.
        // Como el TLV hijo es minucias + length + value, entonces el largo de
        // este
        // es largo(minucias) + 2
        int largo_value = 2 + minutiae.length() / 2;
        if (largo_value > 0x7F) {
            dataIN += "81";
            //Esto es porque dice que es una estructura BER-TLV, y se codifica con el 81 adelante
        }
        dataIN += Utils.byteArrayToHex(Utils.intToByteArray(largo_value)); // Length
        // del
        // FP
        // container

        // 81 aca es el tag del proximo TLV, y el length es el largo de las
        // minucias, que es el value
        // hay un ejemplo confuso en la documentacion, que pone como que habria
        // que poner el length
        // como 81XX igual que en el TLV padre, pero esto no es asi. Se pone el
        // length normal
        dataIN += "81"
                + Utils.byteArrayToHex(Utils.intToByteArray(minutiae.length() / 2));
        
        //hay que ordenar las minucias explicitamente
        byte[] bMinutiae = Utils.hexStringToByteArray(minutiae);
        Utils.sortMinutiae(bMinutiae);
        dataIN += Utils.byteArrayToHex(bMinutiae);

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN),0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
    }

    private static boolean verifyPIN(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "20";
        String PARAM1 = "00";
        String PARAM2 = "11";

        String dataIN = PIN_ASCII; // 1234

        // Aparentemente el Reference PIN es de 12 bytes de largo,
        // por lo que el DATA tiene que ser siempre de 12 bytes.
        // se pone con padding de ceros para completar 12.
        // Los caracteres van codificados en ascii, hay que ver si se hace
        // enforcement de eso
        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
        // 9000 es SW de exito
    }

    private static boolean isVerifiedPIN(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "20";
        String PARAM1 = "00";
        String PARAM2 = "11";

        String dataIN = "";
        // el buffer vacio y el LC 0 indica que se pregunta si esta verificado

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
    }

    private static boolean unverifyPIN(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "20";
        String PARAM1 = "FF";
        String PARAM2 = "11";

        String dataIN = "";
        // el buffer vacio, el modo FF y el LC 0 indica que se quiere
        // "desverificar"

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
    }

    // Precondicion: Verify PIN
    public static boolean MSE_SET_DST(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {

        String CLASS = "00";
        String INSTRUCTION = "22";
        String PARAM1 = "41";
        String PARAM2 = "B6";

        // mse-set, en el documento de IAS esta al reves la especificacion.
        // Revisar bien los parámetros, el iso.
        String dataIN = "840101800102"; // Select the key pair (RSA/ECC) and the
        // signature ALGO

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
        // 9000 es SW de exito
    }

    // Precondicion: Verify PIN
    public static boolean PSO_HASH(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {

        String CLASS = "00";
        String INSTRUCTION = "2A";
        String PARAM1 = "90";
        String PARAM2 = "A0";

        String hash_external = HASH;
        String length = Utils.byteArrayToHex(Utils.intToByteArray(hash_external.length() / 2));

        String dataIN = "90"; // Select the key pair (RSA/ECC) and the signature
        // ALGO
        dataIN += length;
        dataIN += hash_external;

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
        // 9000 es SW de exito
    }

    // Precondicion: PSO_HASH
    public static boolean PSO_CDS(CardChannel channel) throws CardException, FileNotFoundException, UnsupportedEncodingException {

        String CLASS = "00";
        String INSTRUCTION = "2A";
        String PARAM1 = "9E";
        String PARAM2 = "9A";

        String dataIN = "";

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);

        HASH_Signature = Utils.byteArrayToHex(r.getData());

        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
        // 9000 es SW de exito
    }

    public static UserId getUserId(CardChannel channel) {
        //FCITemplate fcit = selectFile(channel, "7001");

        return null;
    }

    public static boolean readCertificate(CardChannel channel) throws CardException, Exception {

        FCITemplate fcit = selectFile(channel, "B001");
        certificate_HEX_DER_encoded = readBinary(channel, fcit.getFileSize());

        return true;
    }

    public static boolean readCardCertificate(CardChannel channel) throws CertificateException, Exception {

        // Read card certificate
        readCertificate(channel);

        // Create X509 Certificate from previously read card certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream b64eIDCertificate = new ByteArrayInputStream(Utils.hexStringToByteArray(certificate_HEX_DER_encoded));
        X509Certificate eIDCertificate = (X509Certificate) cf.generateCertificate(b64eIDCertificate);

        String certSerialNumber = Utils.formatHexaString(eIDCertificate.getSerialNumber().toString(16));

        System.out.println("Certificate Serial Number");
        System.out.println(certSerialNumber);
        System.out.println("CA Information");
        System.out.println(eIDCertificate.getIssuerDN());
        System.out.println("Certificate creation date");
        System.out.println(eIDCertificate.getNotBefore());
        System.out.println("Certificate expiration date");
        System.out.println(eIDCertificate.getNotAfter());
        System.out.println("Certificate owner information");
        System.out.println(eIDCertificate.getSubjectDN());

        return true;
    }

    //POR AHORA ESTA SOPORTA SOLO LECTURA DE EF
    //CAPAZ SOPORTA DF TAMBIEN, PERO NO FUE HECHA PARA ESO
    public static FCITemplate selectFile(CardChannel channel, String fileID) throws CardException, Exception {

        String CLASS = "00";
        String INSTRUCTION = "A4";
        String PARAM1 = "00";
        String PARAM2 = "00";

        String dataIN = fileID;

        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];

        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);

        // Si la lectura del archivo es exitosa debo construir el fci template
        if (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00) {

            FCITemplate fcit = new FCITemplate();
            fcit.buildFromBuffer(r.getData(), 0, r.getData().length);
            return fcit;

        } else {

            return null;
        }

    }

    public static String readBinary(CardChannel channel, int fileSize) throws CardException, FileNotFoundException, UnsupportedEncodingException {

        // Construyo el Read Binary, lo que cambia en cada read son P1 y P2
        // porque van variando los offset para ir leyendo el binario hasta llegar al tamaño total
        // en cada read leo FF
        String CLASS = "00";
        String INSTRUCTION = "B0";
        String dataIN = "";
        String PARAM1;
        String PARAM2;

        int FF_int = Integer.parseInt("FF", 16);

        int cantBytes = 0;
        int dataOUTLength = 0; //le

        String binaryHexString = "";

        while (cantBytes < fileSize) {

            // Calculo el LE
            // Si la cantidad de Bytes que me quedan por obtener es mayor a
            // FF entonces me traigo FF. Sino me traigo los Bytes que me quedan.
            if (cantBytes + FF_int <= fileSize) {
                dataOUTLength = FF_int;
            } else {
                dataOUTLength = fileSize - cantBytes;
            }

            // Param1 y param2 comienzan en 00 00, voy incrementando FF
            // bytes hasta leer el total del binario.
            String PARAM1_PARAM2 = Utils.byteArrayToHex(Utils.intToByteArray(cantBytes));

            //uso solo p2 porque la cantidad de bytes que voy leyendo es menor a FF
            if (cantBytes <= 255) {
                PARAM1 = "00";
                PARAM2 = PARAM1_PARAM2.substring(0, 2);
            } else {
                PARAM1 = PARAM1_PARAM2.substring(0, 2);
                PARAM2 = PARAM1_PARAM2.substring(2, 4);
            }
            byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
            byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
            byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
            byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];

            ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), dataOUTLength);

            binaryHexString += Utils.byteArrayToHex(r.getData());

            if (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00) {

                cantBytes += dataOUTLength;

            } else {
                // Fallo algun read binary
                return "";
            }

        }
        return binaryHexString;
    }

    public static boolean validateHashSignature() throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {

        //The terminal can now perform:
        //1) Verify the card certificate signature (signed by the C.A.)
        //2) Extract the card public RSA key from the card certificate
        //3) Perform a signature verification (HASH, public key, card signature)
        //4) If sig is OK  the card is genuine.
        //parseo la representacion B64 del certificado de la MiCA a un objeto x509 
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certificado_b64_MiCA = new ByteArrayInputStream(Base64.getDecoder().decode(MiCA));
        X509Certificate certificado_MiCA = (X509Certificate) cf.generateCertificate(certificado_b64_MiCA);
        PublicKey pubKeyMiCA = certificado_MiCA.getPublicKey();

        //creo el objeto x509 certificate a partir del certificado extraido del eID, extraigo la clave publica para validar el hash
        InputStream certificado_b64_eID = new ByteArrayInputStream(Utils.hexStringToByteArray(certificate_HEX_DER_encoded));
        X509Certificate certificado_eID = (X509Certificate) cf.generateCertificate(certificado_b64_eID);

        //1) Verify the card certificate signature (signed by the C.A.)
        certificado_eID.checkValidity();
        certificado_eID.verify(pubKeyMiCA);

        //2) Extract the card public RSA key from the card certificate
        PublicKey pubKeyeID = certificado_eID.getPublicKey();

        //3) Perform a signature verification (HASH, public key, card signature)
        Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, pubKeyeID);
        String decryptedMessage = Utils.byteArrayToHex(decrypt.doFinal(Utils.hexStringToByteArray(HASH_Signature)));

        //System.out.println(decryptedMessage);
        //4) If sig is OK  the card is genuine.
        return decryptedMessage.equals(HASH);
    }

    private static boolean verifySodSignature(String cardNumberFile, String bioFile, 
            String imgFile, String mrzFile, String sodFile) throws CMSException, CertificateException, OperatorCreationException, IOException, NoSuchAlgorithmException {
        //remove first 4 bytes: 1 for the tag (77h) and 3 for the length (82XXXXh)
        String p7string = sodFile.substring(8);
        CMSSignedData s = new CMSSignedData(Utils.hexStringToByteArray(p7string));
        
        //STEP 1 - Obtain Hashes from SOD
        CMSProcessable signedContent = s.getSignedContent() ;
        byte[] originalContent  = (byte[]) signedContent.getContent();     
        //System.out.println(Utils.byteArrayToHex(originalContent));
        ASN1InputStream input = new ASN1InputStream(originalContent);

        //TODO - Check if this structure is a standard and improve parsing
        ASN1Primitive p;
        //if algOid is not obtained, use SHA-256 as default
        ASN1ObjectIdentifier algOid = new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.1");
        Map<Integer,byte[]> hashes = new HashMap<Integer,byte[]>();
        while ((p = input.readObject()) != null) {
            //outer sequence
            ASN1Sequence outerSeq = ASN1Sequence.getInstance(p);
            
            //FIRST - integer ZERO
            ASN1Integer zero = ASN1Integer.getInstance(outerSeq.getObjectAt(0));
            //System.out.println("Integer: " + zero.getValue());
            
            //SECOND - OID Container Sequence
            ASN1Sequence oidSequence = ASN1Sequence.getInstance(outerSeq.getObjectAt(1));
            algOid = ASN1ObjectIdentifier.getInstance(oidSequence.getObjectAt(0));
            //System.out.println(algOid.getId());
            ASN1Null nul = ASN1Null.getInstance(oidSequence.getObjectAt(1));
            
            //THIRD - File Hashes Sequence
            ASN1Sequence hashesSequence = ASN1Sequence.getInstance(outerSeq.getObjectAt(2));
            ASN1SequenceParser par = hashesSequence.parser();
            ASN1Encodable hashPair;
            while ((hashPair = par.readObject()) != null) {
                //each entry is a pair of (integer,octetString)
                ASN1Sequence hashPairSeq = ASN1Sequence.getInstance(hashPair);
                ASN1Integer fileID = ASN1Integer.getInstance(hashPairSeq.getObjectAt(0));
                ASN1OctetString hashValue = ASN1OctetString.getInstance(hashPairSeq.getObjectAt(1));
                hashes.put(fileID.getValue().intValue(), hashValue.getOctets());
            }
            //System.out.println(ASN1Dump.dumpAsString(p));
        }
        
        //STEP 2 - Compute and compare Hashes
        
        //01 - Card number
        //Data is padded with 76 bytes 3C "<"
        cardNumberFile = cardNumberFile + "3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C3C";
        //new wrapping TLV is 5F1F plus length
        cardNumberFile = "5F1F" + Utils.byteArrayToHex(Utils.intToByteArray(cardNumberFile.length() / 2)) + cardNumberFile;
        //outer wrapping TLV is 61h plus length
        cardNumberFile = "61" + Utils.byteArrayToHex(Utils.intToByteArray(cardNumberFile.length() / 2)) + cardNumberFile;
        
        //Previous algOid holds oid of used hashing algorithm
        MessageDigest md = MessageDigest.getInstance(algOid.getId());
        byte [] hash = md.digest(Utils.hexStringToByteArray(cardNumberFile));
        
        if (Utils.byteArrayToHex(hash).equals(Utils.byteArrayToHex(hashes.get(1)))) {
            System.out.println("Card Number hash CHECKS!!!");
        } else {
            System.out.println("Card Number hash FAILS");
        }
        
        //02 - BIO Information 
        //Data is not padded
        //outer wrapping TLV is 75h plus ber-length
        bioFile = "75" + Utils.berLength(bioFile.length() / 2) + bioFile;
        
        md = MessageDigest.getInstance(algOid.getId());
        hash = md.digest(Utils.hexStringToByteArray(bioFile));
        
        if (Utils.byteArrayToHex(hash).equals(Utils.byteArrayToHex(hashes.get(2)))) {
            System.out.println("BIO File hash CHECKS!!!");
        } else {
            System.out.println("BIO File hash FAILS");
        }

        //04 - Picture 
        //Data is not padded
        //outer wrapping TLV is 76h plus ber-length  
        imgFile = "76" + Utils.berLength(imgFile.length() / 2) + imgFile;
        //System.out.println(imgFile);
        
        md = MessageDigest.getInstance(algOid.getId());
        hash = md.digest(Utils.hexStringToByteArray(imgFile));
        
        if (Utils.byteArrayToHex(hash).equals(Utils.byteArrayToHex(hashes.get(4)))) {
            System.out.println("Image hash CHECKS!!!");
        } else {
            System.out.println("Image hash FAILS");
        }
        
        //0B - MRZ
        //Data is not padded
        //outer wrapping TLV is 76h plus ber-length
        mrzFile = "6B" + Utils.berLength(mrzFile.length() / 2) + mrzFile;
        
        md = MessageDigest.getInstance(algOid.getId());
        hash = md.digest(Utils.hexStringToByteArray(mrzFile));
        
        if (Utils.byteArrayToHex(hash).equals(Utils.byteArrayToHex(hashes.get(11)))) {
            System.out.println("MRZ hash CHECKS!!!");
        } else {
            System.out.println("MRZ hash FAILS");
        }
        
       
        
        //STEP 3 - Verify Signature
        Store store = s.getCertificates(); 
        SignerInformationStore signers = s.getSignerInfos(); 

        Collection c = signers.getSigners(); 
        Iterator it = c.iterator(); 
        
        X509Certificate signerCert = null;
        boolean ret = false;
        while (it.hasNext()) { 
            SignerInformation signer = (SignerInformation)it.next(); 
            //2.16.840.1.101.3.4.2.1 - SHA256
            //System.out.println(signer.getDigestAlgOID());
            //1.2.840.10045.4.3.2 - ecdsa-with-SHA256
            //System.out.println(signer.getEncryptionAlgOID());
            
            Collection certCollection = store.getMatches(signer.getSID()); 
            Iterator certIt = certCollection.iterator(); 

            X509CertificateHolder certHolder = (X509CertificateHolder)certIt.next(); 
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder); 
            
            if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                ret = true; 
                signerCert = cert;
            }
        }
        
        //STEP 4 - Validate Certificate contained in X509Certificate cert.
        
        return ret;
    }
    
    
    /**
     * 
     * CÓDIGO AGREGADO POR NOSOTROS
     * SEGURIDAD 2018
     * 
     */
    
    /**
     * 
     * @param pin Pin a verificar contra la CI electrónica
     * @return el número de CI si el pin es correcto, null si no lo es
     * @throws CardException
     * @throws IOException
     * @throws CertificateException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchProviderException
     * @throws Exception 
     */
    public static String autenticarConPin(String pin) throws CardException, IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, Exception {
        
        // show the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        
        // get the first terminal
        CardTerminal terminal = terminals.get(0);
        // establish a connection with the card
        Card card = terminal.connect("T=0");
        
        // System.out.println("card ATR: " +
        // byteArrayToHex(card.getATR().getBytes()));
        CardChannel channel = card.getBasicChannel();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //Log Configuration
        String logPath = System.getProperty("user.dir");
        
        LogUtils logUtils = LogUtils.getInstance();
        logUtils.configure(logPath,"");
        //Log Configuration done
        
        //AUTENTICACIÓN DE PIN
        String pin_ascii = Utils.PinToAsciiHex(pin);

        selectIAS(channel);

        verifyPIN(channel, pin_ascii);

        readCertificate(channel);

        MSE_SET_DST(channel);

        PSO_HASH(channel);

        PSO_CDS(channel);

        if (validateHashSignature()) {
            //Hash validado = PIN correcto
            
            //Leemos el card certificate
            selectIAS(channel);
            
            // Read card certificate
            readCertificate(channel);

            // Create X509 Certificate from previously read card certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream b64eIDCertificate = new ByteArrayInputStream(Utils.hexStringToByteArray(certificate_HEX_DER_encoded));
            X509Certificate eIDCertificate = (X509Certificate) cf.generateCertificate(b64eIDCertificate);

            //String certSerialNumber = Utils.formatHexaString(eIDCertificate.getSerialNumber().toString(16));
            Principal sujeto = eIDCertificate.getSubjectDN();
            String info = sujeto.toString();
            //INFO EN FORMATO:
            //CN=<nombre y apellido>, SERIALNUMBER=DNI<num de dni>, C=UY
            String[] datos = info.split(",");
            String[] aux = datos[1].split("DNI");
            String ci = aux[1];
            
            return ci;
            
        } else {
            //Hash invalido = PIN incorrecto
            return null;

        }
    }
    
    private static boolean verifyPIN(CardChannel channel, String pin_ascii) throws CardException, FileNotFoundException, UnsupportedEncodingException {
        String CLASS = "00";
        String INSTRUCTION = "20";
        String PARAM1 = "00";
        String PARAM2 = "11";

        String dataIN = pin_ascii; // 1234

        // Aparentemente el Reference PIN es de 12 bytes de largo,
        // por lo que el DATA tiene que ser siempre de 12 bytes.
        // se pone con padding de ceros para completar 12.
        // Los caracteres van codificados en ascii, hay que ver si se hace
        // enforcement de eso
        byte CLASSbyte = Utils.hexStringToByteArray(CLASS)[0];
        byte INSbyte = Utils.hexStringToByteArray(INSTRUCTION)[0];
        byte P1byte = Utils.hexStringToByteArray(PARAM1)[0];
        byte P2byte = Utils.hexStringToByteArray(PARAM2)[0];
        ResponseAPDU r = Utils.sendCommand(channel, CLASSbyte, INSbyte, P1byte, P2byte, Utils.hexStringToByteArray(dataIN), 0);
        return (r.getSW1() == (int) 0x90 && r.getSW2() == (int) 0x00);

        // El SW 6A80 indica error de codificacion, es decir, en los TLV
        // El SW 63Cx indica error de match y que quedan x intentos
        // 9000 es SW de exito
    }

}
