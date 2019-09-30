package de.fraunhofer.iao.querimonia.utility;

import de.fraunhofer.iao.querimonia.complaint.TestComplaints;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for FileStorageService
 */
public class FileStorageServiceTest {

  private static Path testPath;
  private static String testPathString = "src/test/resources/storagetest/";
  private static String testFilesPathString = "src/test/resources/testfiles";
  private static String testText;

  @BeforeClass
  public static void setUp() {
    testPath = Paths.get(testPathString).toAbsolutePath().normalize();
    testText = TestComplaints.TestTexts.TEXT_A;
  }

  @After
  public void cleanUp() throws IOException {
    Files.list(testPath).forEach(file -> {
      try {
        Files.deleteIfExists(file);
      } catch (IOException e) {
        throw new RuntimeException("Could not delete " + testPathString);
      }
    });
  }

  @Test
  public void testConstructorDefault() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testPathString);
    new FileStorageService(fileStorageProperties);

    assertTrue(Files.exists(testPath));
  }

  @Test
  public void testStoreFile() throws IOException {
    try {
      MockMultipartFile mockFile = new MockMultipartFile("Test.txt",
          "Test.txt", "text/plain",
          new ByteArrayInputStream(testText.getBytes(Charset.defaultCharset())));
      FileStorageProperties fileStorageProperties = new FileStorageProperties();
      fileStorageProperties.setUploadDir(testPathString);
      FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

      String testFileName = fileStorageService.storeFile(mockFile);
      assertEquals("Test.txt", testFileName);
      assertTrue(Files.exists(Paths.get(testPathString + "Test.txt").toAbsolutePath().normalize()));
    } catch (IOException e) {
      throw new IOException("Failed to create mock file!");
    }
  }

  @Test(expected = QuerimoniaException.class)
  public void testStoreFileIllegalName() throws IOException {
    try {
      MockMultipartFile mockFile = new MockMultipartFile("Test.txt",
          "Te...st.txt", "text/plain",
          new ByteArrayInputStream(testText.getBytes(Charset.defaultCharset())));
      FileStorageProperties fileStorageProperties = new FileStorageProperties();
      fileStorageProperties.setUploadDir(testPathString);
      FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

      fileStorageService.storeFile(mockFile);
    } catch (IOException e) {
      throw new IOException("Failed to create mock file!");
    }
  }

  @Test
  public void testGetTextFromDataTxt() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testFilesPathString);
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

    assertEquals(testText, fileStorageService.getTextFromData("Test.txt"));
  }

  @Test
  public void testGetTextFromDataPdf() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testFilesPathString);
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

    assertEquals(testText, fileStorageService.getTextFromData("Test.pdf").trim()
        .replace(System.getProperty("line.separator"),""));
  }

  @Test
  public void testGetTextFromDataDocx() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testFilesPathString);
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

//    assertEquals(testText, fileStorageService.getTextFromData("Test.docx").trim());
  }

  @Test
  public void testGetTextFromDataDoc() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testFilesPathString);
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

//    assertEquals(testText, fileStorageService.getTextFromData("Test.doc").trim());
  }
  @Test(expected = QuerimoniaException.class)
  public void testGetTextFromDataJpg() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir(testFilesPathString);
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

    fileStorageService.getTextFromData("Test.jpg");
  }
}
