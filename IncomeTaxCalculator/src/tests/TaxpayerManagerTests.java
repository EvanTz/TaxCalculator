package tests;

import incometaxcalculator.data.management.Receipt;
import incometaxcalculator.data.management.TaxpayerManager;
import incometaxcalculator.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TaxpayerManagerTests {
    private static String txtFile;
    private static String xmlFile;

    private static int taxRegistrationNumber;
    private TaxpayerManager taxpayerManager = new TaxpayerManager();

    @BeforeAll
    static void setUp() {
        txtFile = "Files\\123456789_INFO.txt";
        xmlFile = "Files\\123456789_INFO.xml";
        taxRegistrationNumber = 123456789;
    }

    @BeforeEach
    void setUpBE(){
        taxpayerManager = new TaxpayerManager();
    }

    @Test
    @Order(1)
    void loadTaxpayerFromTxtTest() {
        tryLoadTaxpayer(txtFile);

        assertTrue(taxpayerManager.containsTaxpayer(taxRegistrationNumber));
        assertEquals(taxpayerManager.getTaxpayerName(taxRegistrationNumber),"Apostolos Zarras");
        assertEquals(taxpayerManager.getTaxpayerStatus(taxRegistrationNumber),"Married Filing Jointly");
        assertEquals(taxpayerManager.getTaxpayerIncome(taxRegistrationNumber),"22570.0");
    }
    @Test
    @Order(2)
    void loadTaxpayerFromXmlTest() {
        tryLoadTaxpayer(xmlFile);

        assertTrue(taxpayerManager.containsTaxpayer(taxRegistrationNumber));
        assertEquals(taxpayerManager.getTaxpayerName(taxRegistrationNumber),"Apostolos Zarras");
        assertEquals(taxpayerManager.getTaxpayerStatus(taxRegistrationNumber),"Married Filing Jointly");
        assertEquals(taxpayerManager.getTaxpayerIncome(taxRegistrationNumber),"22570.0");
    }


    @Test
    @Order(3)
    void removeTaxpayerTest() {
        tryLoadTaxpayer(txtFile);

        taxpayerManager.removeTaxpayer(taxRegistrationNumber);
        assertFalse(taxpayerManager.containsTaxpayer(taxRegistrationNumber));
    }

    @Test
    @Order(4)
    void addReceiptTest() {
        tryLoadTaxpayer(txtFile);

        assertEquals(taxpayerManager.getTaxpayer(taxRegistrationNumber).getTotalReceiptsGathered(),5);

        try {
            taxpayerManager.addReceipt(13,"12/12/2121", 15.3f,
                    "Other","addReceiptTest","Greece","Ioannina","Dodonis 10",
                    5, taxRegistrationNumber);
        } catch (IOException | ReceiptAlreadyExistsException | WrongReceiptDateException | WrongReceiptKindException e) {
            throw new RuntimeException(e);
        }
        assertEquals(taxpayerManager.getTaxpayer(taxRegistrationNumber).getTotalReceiptsGathered(),6);

        // Also test exception for when the receipt already exists
        try {
            taxpayerManager.addReceipt(13,"12/12/2121", 15.3f,
                    "Other","addReceiptTest","Greece","Ioannina","Dodonis 10",
                    5, taxRegistrationNumber);
        } catch (IOException | WrongReceiptDateException | WrongReceiptKindException e) {
            throw new RuntimeException(e);
        } catch (ReceiptAlreadyExistsException e){
            assertEquals("Receipt already exists.",e.getMessage());
        }

        // Remove and reload the taxpayer to make sure the new receipt is written to file as well
        taxpayerManager.removeTaxpayer(taxRegistrationNumber);
        tryLoadTaxpayer(txtFile);

        Receipt addedReceipt = taxpayerManager.getTaxpayer(taxRegistrationNumber).getReceiptHashMap().get(13);
        assertNotNull(addedReceipt);
        assertEquals("12/12/2121", addedReceipt.getIssueDate());
        assertEquals(15.3f, addedReceipt.getAmount());
        assertEquals("Other", addedReceipt.getKind());
        assertEquals("Greece", addedReceipt.getCompany().getCountry());
        assertEquals("Ioannina", addedReceipt.getCompany().getCity());
        assertEquals("Dodonis 10", addedReceipt.getCompany().getStreet());
        assertEquals(5, addedReceipt.getCompany().getNumber());

    }

    @Test
    @Order(5)
    void removeReceiptTest() {
        tryLoadTaxpayer(txtFile);

        assertEquals(taxpayerManager.getTaxpayer(taxRegistrationNumber).getTotalReceiptsGathered(),6);
        try {
            taxpayerManager.removeReceipt(13);
        } catch (IOException | WrongReceiptKindException e) {
            throw new RuntimeException(e);
        }
        assertEquals(taxpayerManager.getTaxpayer(taxRegistrationNumber).getTotalReceiptsGathered(),5);

        // Remove and reload the taxpayer to make sure the receipt is deleted from the file as well
        taxpayerManager.removeTaxpayer(taxRegistrationNumber);
        tryLoadTaxpayer(txtFile);

        Receipt addedReceipt = taxpayerManager.getTaxpayer(taxRegistrationNumber).getReceiptHashMap().get(13);
        assertNull(addedReceipt);
    }

    @Test
    @Order(6)
    void saveLogFileTest() {
        // Delete the files before rewriting them to be sure, although saveLogFile overwrites them anyway.
        File existentTxtLogFile = new File(System.getProperty("user.dir")+"\\123456789_LOG.txt");
        File existentXmlLogFile = new File(System.getProperty("user.dir")+"\\123456789_LOG.xml");
        //noinspection ResultOfMethodCallIgnored
        existentTxtLogFile.delete();
        //noinspection ResultOfMethodCallIgnored
        existentXmlLogFile.delete();

        String txtFileWriteExpected = """
                Name: Apostolos Zarras
                AFM: 123456789
                Income: 22570.0
                Basic Tax: 1207.495
                Tax Increase: 48.2998
                Total Tax: 1255.7948
                TotalReceiptsGathered: 5
                Entertainment: 0.0
                Basic: 4801.0
                Travel: 100.0
                Health: 0.0
                Other: 1000.0
                """;

        String xmlFileWriteExpected = """
                <Name> Apostolos Zarras </Name>
                <AFM> 123456789 </AFM>
                <Income> 22570.0 </Income>
                <BasicTax> 1207.495 </BasicTax>
                <TaxIncrease> 48.2998 </TaxIncrease>
                <TotalTax> 1255.7948 </TotalTax>
                <Receipts> 5 </Receipts>
                <Entertainment> 0.0 </Entertainment>
                <Basic> 4801.0 </Basic>
                <Travel> 100.0 </Travel>
                <Health> 0.0 </Health>
                <Other> 1000.0 </Other>
                """;

        tryLoadTaxpayer(txtFile);

        try {
            taxpayerManager.saveLogFile(taxRegistrationNumber, "txt",System.getProperty("user.dir"));
            taxpayerManager.saveLogFile(taxRegistrationNumber, "xml",System.getProperty("user.dir"));
        } catch (IOException | WrongFileFormatException e) {
            throw new RuntimeException(e);
        }

        try {
            String txtContents = Files.readString(Paths.get(System.getProperty("user.dir")+"\\123456789_LOG.txt"),
                    StandardCharsets.UTF_8);

            assertEquals(txtFileWriteExpected, txtContents.replaceAll("\\r\\n?", "\n"));

            String xmlContents = Files.readString(Paths.get(System.getProperty("user.dir")+"\\123456789_LOG.xml"),
                    StandardCharsets.UTF_8);

            assertEquals(xmlFileWriteExpected, xmlContents.replaceAll("\\r\\n?", "\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(7)
    void getTaxpayerTotalTaxTest() {
        tryLoadTaxpayer(txtFile);

        assertEquals(taxpayerManager.getTaxpayerTotalTax(taxRegistrationNumber),1255.7948);
    }

    @Test
    @Order(8)
    void getTaxpayerBasicTaxTest() {
        tryLoadTaxpayer(txtFile);

        assertEquals(taxpayerManager.getTaxpayerBasicTax(taxRegistrationNumber),1207.495);
    }

    @Test
    @Order(9)
    void changeTaxpayerStatusTest(){
        String newStatus = "Married Filing Separately";
        tryLoadTaxpayer(txtFile);
        // Test that the previous status is not the same as the new income
        assertNotEquals(newStatus, taxpayerManager.getTaxpayer(taxRegistrationNumber).getStatus());

        try {
            taxpayerManager.changeStatus(newStatus, taxRegistrationNumber);
        } catch (IOException | WrongTaxpayerStatusException e) {
            throw new RuntimeException(e);
        }

        assertEquals(newStatus, taxpayerManager.getTaxpayer(taxRegistrationNumber).getStatus());

        // Remove and reload the taxpayer to make sure the new status is written to file as well
        taxpayerManager.removeTaxpayer(taxRegistrationNumber);
        tryLoadTaxpayer(txtFile);

        assertEquals(newStatus, taxpayerManager.getTaxpayer(taxRegistrationNumber).getStatus());

        // Change the status to the original "Married Filing Jointly"
        try {
            taxpayerManager.changeStatus("Married Filing Jointly", taxRegistrationNumber);
        } catch (IOException | WrongTaxpayerStatusException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(10)
    void changeTaxpayerIncomeTest(){
        float newIncome = 12345.67f;
        tryLoadTaxpayer(txtFile);
        // Test that the previous income is not the same as the new income
        assertNotEquals(newIncome, taxpayerManager.getTaxpayer(taxRegistrationNumber).getIncome());

        try {
            taxpayerManager.changeIncome(newIncome, taxRegistrationNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(newIncome, taxpayerManager.getTaxpayer(taxRegistrationNumber).getIncome());

        // Remove and reload the taxpayer to make sure the new income is written to file as well
        taxpayerManager.removeTaxpayer(taxRegistrationNumber);
        tryLoadTaxpayer(txtFile);

        assertEquals(newIncome, taxpayerManager.getTaxpayer(taxRegistrationNumber).getIncome());

        // Change the income to the original 22570.0
        try {
            taxpayerManager.changeIncome(22570.0f, taxRegistrationNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryLoadTaxpayer(String fileInput) {
        try {
            taxpayerManager.loadTaxpayer(fileInput);
        } catch (IOException | WrongFileFormatException | WrongFileEndingException | WrongTaxpayerStatusException |
                 WrongReceiptKindException | WrongReceiptDateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(11)
    void loadTaxpayerTestExceptions() {
        String wrongFileEnding = "456456456_INFO.t";
        WrongFileEndingException thrown1 = assertThrows(WrongFileEndingException.class,
                ()->taxpayerManager.loadTaxpayer(wrongFileEnding));
        assertEquals("Please check your file ending and try again!", thrown1.getMessage());

        String emptyFile = "testFiles\\123123123_INFO.txt";
        WrongFileFormatException thrown2 = assertThrows(WrongFileFormatException.class,
                ()->taxpayerManager.loadTaxpayer(emptyFile));
        assertEquals("Please check your file format and try again!", thrown2.getMessage());

        String wrongStatusFile = "testFiles\\321321321_INFO.txt";
        WrongTaxpayerStatusException thrown3 = assertThrows(WrongTaxpayerStatusException.class,
                ()->taxpayerManager.loadTaxpayer(wrongStatusFile));
        assertEquals("Please check taxpayer's status and try again!", thrown3.getMessage());

        String wrongReceiptKindFile = "testFiles\\234234234_INFO.txt";
        WrongReceiptKindException thrown4 = assertThrows(WrongReceiptKindException.class,
                ()->taxpayerManager.loadTaxpayer(wrongReceiptKindFile));
        assertEquals("Please check receipts kind and try again.", thrown4.getMessage());

        String wrongReceiptDateFile = "testFiles\\456456456_INFO.txt";
        WrongReceiptDateException thrown5 = assertThrows(WrongReceiptDateException.class,
                ()->taxpayerManager.loadTaxpayer(wrongReceiptDateFile));
        assertEquals("Please make sure your date is DD/MM/YYYY and try again.", thrown5.getMessage());
    }

}
