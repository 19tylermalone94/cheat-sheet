package com.slideshow.app;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class App implements Runnable {

    static final String INPUT_DIRECTORY = "input_files/";
    static final String OUTPUT_DIRECTORY = "output_files/";

    @Override
public void run() {
    // List<BufferedImage> images = readAllInputPDFs();
    List<BufferedImage> images = readOutputFolder();
    System.out.println(images.size());

    // Create the collage
    BufferedImage collage1 = createCollage(images.subList(0, images.size() / 2), 2550, 3300);
    BufferedImage collage2 = createCollage(images.subList(images.size() / 2, images.size()), 2550, 3300);
    saveCollage(collage1, "output_files/collage_side1.png");
    saveCollage(collage2, "output_files/collage_side2.png");

    // Create PDF document with these collages
    try {
        createPDFWithCollages("output_files/collage_side1.png", "output_files/collage_side2.png", "output_files/collage_document.pdf");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

/**
 * Creates a PDF with two pages from the collages.
 *
 * @param collage1Path Path to the first collage image.
 * @param collage2Path Path to the second collage image.
 * @param outputPdfPath Path to save the generated PDF document.
 */
private void createPDFWithCollages(String collage1Path, String collage2Path, String outputPdfPath) throws IOException {
    // Create a new PDF document
    PDDocument document = new PDDocument();

    // Add the first collage image as a page
    addImageToPDF(document, collage1Path);

    // Add the second collage image as another page
    addImageToPDF(document, collage2Path);

    // Save the PDF to file
    document.save(outputPdfPath);
    System.out.println("PDF saved successfully at " + outputPdfPath);

    // Close the document
    document.close();
}

/**
 * Adds a given image as a full-page image in the PDF document.
 *
 * @param document The PDF document.
 * @param imagePath Path to the image.
 * @throws IOException If the image or PDF writing fails.
 */
private void addImageToPDF(PDDocument document, String imagePath) throws IOException {
    // Load the image as a PDImageXObject
    PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);

    // Create a new page based on the image size
    PDRectangle pageSize = new PDRectangle(pdImage.getWidth(), pdImage.getHeight());
    PDPage page = new PDPage(pageSize);
    document.addPage(page);

    // Create a content stream to write the image to the PDF page
    PDPageContentStream contentStream = new PDPageContentStream(document, page);

    // Draw the image, starting from the bottom-left corner
    contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());

    // Close the content stream
    contentStream.close();
}

    private List<BufferedImage> readOutputFolder() {
        List<BufferedImage> images = new ArrayList<>();
    
        File outputDir = new File(OUTPUT_DIRECTORY);
        
        // Check if directory exists and list all files
        if (outputDir.exists() && outputDir.isDirectory()) {
            File[] pngFiles = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
    
            if (pngFiles != null) {
                // Read each PNG file into a BufferedImage
                for (File file : pngFiles) {
                    try {
                        BufferedImage image = ImageIO.read(file);
                        images.add(image);
                    } catch (IOException e) {
                        System.err.println("Failed to read image file: " + file.getName());
                    }
                }
            }
        } else {
            System.err.println("Output directory does not exist or is not a directory");
        }
    
        return images;
    }
    private BufferedImage createCollage(List<BufferedImage> images, int width, int height) {
        int padding = 10; // Padding between images in pixels
        int cols = (int) Math.ceil(Math.sqrt(images.size())); // Number of columns based on square root of image count
        int rows = (int) Math.ceil((double) images.size() / cols); // Number of rows
    
        int imageWidth = (width - (cols + 1) * padding) / cols; // Adjust for padding
        int imageHeight = (height - (rows + 1) * padding) / rows; // Adjust for padding
    
        // Create an empty canvas for the collage
        BufferedImage collage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = collage.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, width, height); // Fill background with white
    
        int x = padding, y = padding; // Start position with padding
        for (int i = 0; i < images.size(); i++) {
            BufferedImage image = images.get(i);
            BufferedImage resizedImage = resizeImage(image, imageWidth, imageHeight);
    
            g2d.drawImage(resizedImage, x, y, null); // Draw image at (x, y)
    
            x += imageWidth + padding; // Move to the next column
            if (x + imageWidth + padding > width) { // If we're past the width, move to next row
                x = padding;
                y += imageHeight + padding;
            }
        }
    
        g2d.dispose(); // Clean up resources
        return collage;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    private void saveCollage(BufferedImage collage, String outputPath) {
        try {
            File outputFile = new File(outputPath);
            ImageIO.write(collage, "png", outputFile);
            System.out.println("Collage saved successfully at " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save the collage image", e);
        }
    }

    private List<BufferedImage> readPDF(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(document);
            List<BufferedImage> images = new ArrayList<>();            
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 200);
                images.add(image);
            }
            return images;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process PDF: " + file.getName(), e);
        }
    }

    private List<BufferedImage> readAllInputPDFs() {
        return Arrays.stream(new File(INPUT_DIRECTORY).listFiles())
            .map(this::readPDF)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private void writeFiles(List<BufferedImage> images) {
        try {
            for (int i = 0; i < images.size(); ++i) {
                File outputFile = new File(OUTPUT_DIRECTORY, "output" + i + ".png");
                ImageIO.write(images.get(i), "png", outputFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write images to output directory");
        }
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.run();
    }

}
