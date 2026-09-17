package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import utils.TestDataStore;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelHooks {

    private static final String FILE_PATH = "src/test/resources/Team01_RestAssured_Rebels_TestDataSheet.xlsx";

    @Before
    public void beforeScenario() throws IOException {
        FileInputStream fis = new FileInputStream(FILE_PATH);
        Workbook workbook = WorkbookFactory.create(fis);
        TestDataStore.setWorkbook(workbook);
    }

    @After
    public void afterScenario() {
        Workbook workbook = TestDataStore.getWorkbook();
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                // Optionally log
            }
        }
        TestDataStore.clearWorkbook();
    }
}