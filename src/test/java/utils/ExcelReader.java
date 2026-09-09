package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {
    public static String filePath = "src/test/resources/Team03_RestAssured_Rebels_TestDataSheet.xlsx";

    public static List<Map<String, String>> getAllSheetData(String sheetName) throws IOException {

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheet(sheetName);
            DataFormatter formatter = new DataFormatter();
            List<Map<String, String>> listData = new ArrayList<>();

            Row header = sheet.getRow(0);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {

                Row row = sheet.getRow(r);
                if (row == null) continue; // skip empty rows

                Map<String, String> rowData = new HashMap<>();

                for (int c = 0; c < row.getLastCellNum(); c++) {

                    String key = formatter.formatCellValue(header.getCell(c)).trim();
                    String value = formatter.formatCellValue(row.getCell(c)).trim();

                    rowData.put(key, value);
                }

                listData.add(rowData);
            }

            return listData;
        }
    }

    public static Map<String, String> readExcelData(String sheetName, String scenarioName) throws IOException {
        List<Map<String, String>> data = getAllSheetData(sheetName);

        for (Map<String, String> row : data) {
            if (row.get("ScenarioName").equalsIgnoreCase(scenarioName)) {
                return row;
            }
        }

        return null;
    }
}
