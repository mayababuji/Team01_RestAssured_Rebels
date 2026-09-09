package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateProgramRequest {

    @JsonProperty("programDescription")
    private String programDescription;

    @JsonProperty("programName")
    private String programName;

    @JsonProperty("programStatus")
    private String programStatus;

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramStatus() {
        return programStatus;
    }

    public void setProgramStatus(String programStatus) {
        this.programStatus = programStatus;
    }
}
