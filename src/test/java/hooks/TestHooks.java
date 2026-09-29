package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import utils.SharedTestData;
import utils.TestDataStore;
import utils.TokenGenerator;

import java.io.FileInputStream;
import java.io.IOException;

public class TestHooks {

    private static final String FILE_PATH =
            "src/test/resources/Team01_RestAssured_Rebels_TestDataSheet.xlsx";

    @Before(order = 1)
    public void generateTokenBeforeScenario() {

        if (SharedTestData.token != null
                && !SharedTestData.token.isBlank()) {
            return;
        }

        String token = TokenGenerator.generateToken();

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Token generation failed. Token is null or blank."
            );
        }

        SharedTestData.token = token;
    }

    @Before(order = 2)
    public void openExcelBeforeScenario() throws IOException {

        FileInputStream inputStream = new FileInputStream(FILE_PATH);

        Workbook workbook = WorkbookFactory.create(inputStream);

        TestDataStore.setWorkbook(workbook);
    }

    @After(order = 1)
    public void closeExcelAfterScenario() {

        Workbook workbook = TestDataStore.getWorkbook();

        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Failed to close Excel workbook.",
                        e
                );
            }
        }

        TestDataStore.clearWorkbook();
    }
}