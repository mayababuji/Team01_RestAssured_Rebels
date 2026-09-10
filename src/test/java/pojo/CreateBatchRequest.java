package pojo;

public class CreateBatchRequest {

    private String batchDescription;
    private String batchName;
    private Integer batchNoOfClasses;
    private String batchStatus;
    private Integer programId;
    private String programName;

    public String getBatchDescription() {
        return batchDescription;
    }

    public void setBatchDescription(String batchDescription) {
        this.batchDescription = batchDescription;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public Integer getBatchNoOfClasses() {
        return batchNoOfClasses;
    }

    public void setBatchNoOfClasses(Integer batchNoOfClasses) {
        this.batchNoOfClasses = batchNoOfClasses;
    }

    public String getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getprogramName() {
        return programName;
    }

    public void setprogramName(String programName) {
        this.programName = programName;
    }
}
