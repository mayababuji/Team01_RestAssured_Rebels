package httpRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.CreateProgramRequest;

public class ProgramRequestParser {

    public static CreateProgramRequest createProgramParseData(String body) throws JsonProcessingException {
        return new ObjectMapper().readValue(body, CreateProgramRequest.class);
    }
}
