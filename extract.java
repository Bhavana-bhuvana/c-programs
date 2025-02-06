
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter// .
package org.example;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class Main{

    // Function to extract text from an image
    public static String extractTextFromImage(String imagePath) {
        try {
            // Convert WebP to PNG (if needed)
            String convertedImagePath = imagePath;
            if (imagePath.toLowerCase().endsWith(".webp")) {
                convertedImagePath = imagePath.replace(".webp", ".png");
                BufferedImage image = ImageIO.read(new File(imagePath));
                ImageIO.write(image, "png", new File(convertedImagePath));

            }

            // Process the (possibly converted) image
            File imageFile = new File(convertedImagePath);
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");  // Set Tesseract path
            tesseract.setLanguage("eng");  // Set OCR language
            return tesseract.doOCR(imageFile).toLowerCase();

        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return "Error extracting text from image.";
        }
    }

    // Function to extract text from a PDF
    public static String extractTextFromPDF(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            return new PDFTextStripper().getText(document).toLowerCase();
        } catch (IOException e) {
            System.out.println("Error extracting text from PDF: " + e.getMessage());
            return "";
        }
    }

    // Function to extract text from CSV
    public static String extractTextFromCSV(String csvPath) {
        StringBuilder text = new StringBuilder();
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                text.append(String.join(" ", row)).append("\n");
            }
        } catch (Exception e) {
            System.out.println("Error extracting text from CSV: " + e.getMessage());
        }
        return text.toString().toLowerCase();
    }

    // Function to extract text from Excel
    public static String extractTextFromExcel(String excelPath) {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        text.append(cell.toString()).append(" ");
                    }
                    text.append("\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Error extracting text from Excel: " + e.getMessage());
        }
        return text.toString().toLowerCase();
    }

    // Function to extract test results using regex
    public static void extractTestResults(String text) {
        String regex = "([a-z\\s/()-]*\\b(?:creatinine|sodium|potassium|chloride|electrolytes|blood urea nitrogen|bun|glomerular filtration rate|gfr)\\b[a-z\\s]*)\\s+(\\d+\\.?\\d*)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            System.out.println("Test Name: " + matcher.group(1).trim() + " | Result: " + matcher.group(2));
        }
    }

    // Main function
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the file path (Image, PDF, CSV, or Excel): ");

        try {
            String filePath = reader.readLine();
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }

            String extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
            String text = "";

            if (extension.matches("\\.(jpg|jpeg|png|webp)")) {
                text = extractTextFromImage(filePath);
            } else if (extension.equals(".pdf")) {
                text = extractTextFromPDF(filePath);
            } else if (extension.equals(".csv")) {
                text = extractTextFromCSV(filePath);
            } else if (extension.matches("\\.(xls|xlsx)")) {
                text = extractTextFromExcel(filePath);
            } else {
                System.out.println("Unsupported file type.");
                return;
            }

            extractTestResults(text);

        } catch (IOException e) {
            System.out.println("Error reading file path: " + e.getMessage());
        }
    }
   // pom.xml
    <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.text.extractor</groupId>
    <artifactId>text-extraction</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>


    <dependencies>
        <!-- Tess4J for OCR (Image to Text) -->
        <dependency>
            <groupId>net.sourceforge.tess4j</groupId>
            <artifactId>tess4j</artifactId>
            <version>4.5.5</version>
        </dependency>
        <dependency>
            <groupId>com.github.jai-imageio</groupId>
            <artifactId>jai-imageio-core</artifactId>
            <version>1.4.0</version>
        </dependency>
        <dependency>
            <groupId>net.coobird</groupId>
            <artifactId>thumbnailator</artifactId>
            <version>0.4.18</version>
        </dependency>


        <!-- Apache PDFBox for PDF Text Extraction -->
        <dependency>
            <groupId>org.apache.pdfbox</groupId>
            <artifactId>pdfbox</artifactId>
            <version>2.0.30</version>
        </dependency>

        <!-- Apache POI for Excel Handling -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.4.0</version>
        </dependency>
        <dependency>
            <groupId>com.twelvemonkeys.imageio</groupId>
            <artifactId>imageio-webp</artifactId>
            <version>3.9.4</version>
        </dependency>
        <!-- OpenCSV for CSV Handling -->
        <dependency>
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>5.7.1</version>
        </dependency>
    </dependencies>
</project>

    
}

