package utils;

import java.util.ArrayList;
import java.util.List;

public class SharedTestData {

    // Lists to store multiple IDs and names across scenarios
    protected static final List<Integer> batchIds = new ArrayList<>();
    protected static final List<Integer> programIdList = new ArrayList<>();
    protected static final List<String> programNameList = new ArrayList<>();

    // Single-value shared fields
    protected static int batchId;
    protected static String batchName;
    protected static int programId;
    protected static String programName;

    // Auth token shared across tests
    public static String token;

    public SharedTestData() {

    }



}
