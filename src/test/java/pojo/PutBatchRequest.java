package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutBatchRequest {

    @JsonProperty("batchDescription")
    private String batchDescription;

    @JsonProperty("batchId")
    private int batchId;

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

    @JsonProperty("batchId")
    public int getBatchId() {
        return batchId;
    }

    @JsonProperty("batchId")
    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        return "PutBatchRequest{" +
                "batchDescription='" + batchDescription + '\'' +
                ", batchId=" + batchId +
                ", batchName='" + batchName + '\'' +
                ", batchNoOfClasses=" + batchNoOfClasses +
                ", batchStatus='" + batchStatus + '\'' +
                ", programId=" + programId +
                ", programName='" + programName + '\'' +
                '}';
    }
}