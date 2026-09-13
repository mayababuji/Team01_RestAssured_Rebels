package httpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.CreateUserRequest;
public class UserRequestParser {
	private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CreateUserRequest createUserParseData(String body) throws JsonProcessingException {
        return objectMapper.readValue(body, CreateUserRequest.class);
    }

}
