package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {

	public static String filePath = "src/test/resources/Team01_RestAssured_Rebles_TestDataSheet.xlsx";

	public static List<Map<String, String>> getAllSheetData(String sheetName) throws IOException {
		List<Map<String, String>> listData = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = null;

			// 1. Try exact match
			sheet = workbook.getSheet(sheetName);

			// 2. Fallback: Search case-insensitively and trim spaces
			if (sheet == null) {
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					Sheet s = workbook.getSheetAt(i);
					if (s.getSheetName().trim().equalsIgnoreCase(sheetName.trim())) {
						sheet = s;
						break;
					}
				}
			}

			// 3. Throw a helpful error if still not found
			if (sheet == null) {
				List<String> availableSheets = new ArrayList<>();
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					availableSheets.add("'" + workbook.getSheetAt(i).getSheetName() + "'");
				}
				throw new RuntimeException("Sheet '" + sheetName + "' was NOT found in " + filePath
						+ ". Available sheets in workbook are: " + availableSheets);
			}

			DataFormatter formatter = new DataFormatter();
			Row header = sheet.getRow(0);

			if (header == null)
				return listData;

			for (int r = 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null)
					continue;

				Map<String, String> rowData = new HashMap<>();

				for (int c = 0; c < row.getLastCellNum(); c++) {

					String key = formatter.formatCellValue(header.getCell(c)).replace("\u00A0", " ").trim();
					String value = formatter.formatCellValue(row.getCell(c)).replace("\u00A0", " ").trim();

					if (!key.isEmpty()) {
						rowData.put(key, value);
					}

				}

				listData.add(rowData);
			}

		}

		return listData;

	}

	public static Map<String, String> readExcelData(String sheetName, String scenarioName) throws IOException {
		List<Map<String, String>> data = getAllSheetData(sheetName);

		// Normalize target scenario string
		String targetScenario = (scenarioName != null) ? scenarioName.replace("\u00A0", " ").trim() : "";

		for (Map<String, String> row : data) {
			// Try multiple column names safely
			String scenarioInExcel = row.get("ScenarioName");
			if (scenarioInExcel == null)
				scenarioInExcel = row.get("scenario");
			if (scenarioInExcel == null)
				scenarioInExcel = row.get("Scenario");

			// ONLY clean and compare if the value extracted is NOT null
			if (scenarioInExcel != null) {
				String cleanExcelValue = scenarioInExcel.replace("\u00A0", " ").trim();

				if (cleanExcelValue.equalsIgnoreCase(targetScenario)) {
					return row;
				}
			}
		}

		throw new RuntimeException("scenario '" + scenarioName + "' was not found in sheet '" + sheetName + "'!");
	}
}
