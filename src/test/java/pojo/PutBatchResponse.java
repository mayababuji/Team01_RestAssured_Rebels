package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutBatchResponse {

    @JsonProperty("batchId")
    private int batchId;

    @JsonProperty("batchDescription")
    private String batchDescription;

    @JsonProperty("batchName")
    private String batchName;

    @JsonProperty("batchNoOfClasses")
    private int batchNoOfClasses;

    @JsonProperty("batchStatus")
    private String batchStatus;

    @JsonProperty("programId")
    private int programId;

    @JsonProperty("programName")
    private String programName;

    @JsonProperty("batchId")
    public int getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    @JsonProperty("batchDescription")
    public String getBatchDescription() {
        return batchDescription;
    }

    @JsonProperty("batchDescription")
    public void setBatchDescription(String batchDescription) {
        this.batchDescription = batchDescription;
    }

    @JsonProperty("batchName")
    public String getBatchName() {
        return batchName;
    }

    @JsonProperty("batchName")
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @JsonProperty("batchNoOfClasses")
    public int getBatchNoOfClasses() {
        return batchNoOfClasses;
    }

    @JsonProperty("batchNoOfClasses")
    public void setBatchNoOfClasses(int batchNoOfClasses) {
        this.batchNoOfClasses = batchNoOfClasses;
    }

    @JsonProperty("batchStatus")
    public String getBatchStatus() {
        return batchStatus;
    }

    @JsonProperty("batchStatus")
    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    @JsonProperty("programId")
    public int getProgramId() {
        return programId;
    }

    @JsonProperty("programId")
    public void setProgramId(int programId) {
        this.programId = programId;
    }

    @JsonProperty("programName")
    public String getProgramName() {
        return programName;
    }

    @JsonProperty("programName")
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    //to get a readable summary while printing insteadf of ClassName@hashcode
    @Override
    public String toString() {
        return "PutBatchResponse{" +
                "batchId=" + batchId +
                ", batchDescription='" + batchDescription + '\'' +
                ", batchName='" + batchName + '\'' +
                ", batchNoOfClasses=" + batchNoOfClasses +
                ", batchStatus='" + batchStatus + '\'' +
                ", programId=" + programId +
                ", programName='" + programName + '\'' +
                '}';
    }
}