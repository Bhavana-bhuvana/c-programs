
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
}

