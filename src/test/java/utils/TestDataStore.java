package utils;

import org.apache.poi.ss.usermodel.Workbook;

public class TestDataStore {

    private static final ThreadLocal<Workbook> WORKBOOK = new ThreadLocal<>();

    public static Workbook getWorkbook() {
        return WORKBOOK.get();
    }

    public static void setWorkbook(Workbook workbook) {
        WORKBOOK.set(workbook);
    }

    public static void clearWorkbook() {
        WORKBOOK.remove();
    }
}