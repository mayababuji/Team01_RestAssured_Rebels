
// My working program with 4 errors

package stepDefinitions;

//import httpRequest.ProgramRequestParser; 
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.CreateProgramRequest;
import pojo.CreateProgramResponse;
import specBuilder.RequestSpec;
import utils.ExcelReader;
import utils.ResponseValidator;
import utils.ScenarioContext;
import utils.SharedTestData;
import utils.TestDataUtil;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProgramStepDef extends SharedTestData {

	private Map<String, String> data;
	private Response response;
	private CreateProgramRequest programInput;

	private final ScenarioContext scenarioContext;

	public ProgramStepDef(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	//
	//========================================================================================================= // CREATE
																												// PROGRAM
																												// -
																												// POST
																												// //
	//=========================================================================================================

	@When("Admin sends POST request to create program with different payload for {string} from dataSheet")
	public void adminSendsPostRequest(String scenarioNameFeature) throws IOException {

		System.out.println();System.out.println("==========================================");
		System.out.println("SCENARIO: "+scenarioNameFeature);
		System.out.println("==========================================");

		//  Read Excel
		data=ExcelReader.readExcelData("Program",scenarioNameFeature);

		if(data==null||data.isEmpty()){

		throw new IllegalStateException("Test data not found in Excel for: "+scenarioNameFeature);}

		//
		//----------------------------------------------------------------------------- ----------------- // Validate
																										// ScenarioName
																										// //
		//----------------------------------------------------------------------------- -----------------

		String excelScenarioName=data.get("ScenarioName");

		if(excelScenarioName==null||!scenarioNameFeature.equalsIgnoreCase(excelScenarioName.trim())){

		throw new IllegalStateException("ScenarioName mismatch. "+"Feature=["+scenarioNameFeature+"] "+"Excel=["+excelScenarioName+"]");}

		//
		//----------------------------------------------------------------------------- ----------------- // Expected
																										// Status //
		//----------------------------------------------------------------------------- -----------------

		int expectedStatus=Integer.parseInt(data.get("ExpectedStatusCode").trim());

		//
		//----------------------------------------------------------------------------- ----------------- // Parse Request
																										// Body //
		//----------------------------------------------------------------------------- -----------------

		String body=data.get("Body");

		if(body==null||body.trim().isEmpty()){

		programInput=null;

		}else{

		try{programInput=RequestSpec.parseBody(body,CreateProgramRequest.class);}catch(com.fasterxml.jackson.core.JsonProcessingException e)
		{throw new IllegalStateException("Failed to parse Program request body for scenario: "+scenarioNameFeature,e);}}

		//
		//----------------------------------------------------------------------------- ----------------- // Data Strategy
																										// column //
		//----------------------------------------------------------------------------- -----------------

		String dataStrategy=data.get("Data Strategy");

		if(dataStrategy==null||dataStrategy.trim().isEmpty()){

		throw new IllegalStateException("DataStrategy is missing in Excel for: "+scenarioNameFeature);}

		switch(dataStrategy.trim().toUpperCase()){

		case"UNIQUE":

		if(programInput==null){

		throw new IllegalStateException("Request body is empty for UNIQUE strategy.");}

		String uniqueProgramName=TestDataUtil.generateUniqueProgramName();

		programInput.setProgramName(uniqueProgramName);

		SharedTestData.programName=uniqueProgramName;

		System.out.println("Data Strategy : UNIQUE");System.out.println("Program Name  : "+uniqueProgramName);

		break;

		case"EXCEL":

		System.out.println("Data Strategy : EXCEL");

		if(programInput!=null){

		System.out.println("Program Name  : "+programInput.getProgramName());}

		break;

		case"EXISTING":

		if(programInput==null){

		throw new IllegalStateException("Request body is empty for EXISTING strategy.");}

		if(SharedTestData.programName==null||SharedTestData.programName.isBlank()){

		throw new IllegalStateException("No existing Program Name available.");}

		programInput.setProgramName(SharedTestData.programName);

		System.out.println("Data Strategy : EXISTING");System.out.println("Program Name  : "+SharedTestData.programName);

		break;

		default:

		throw new IllegalStateException("Invalid DataStrategy '"+dataStrategy+"' in Excel for scenario: "+scenarioNameFeature);}

		//
		//----------------------------------------------------------------------------- ----------------- // Request
																										// Specification
																										// //
		//----------------------------------------------------------------------------- -----------------

		RequestSpecification requestSpec=scenarioContext.getRequestSpec();

		if(requestSpec==null){

		throw new IllegalStateException("requestSpec is null. Did the Given step run?");}

		//
		//----------------------------------------------------------------------------- ----------------- // Endpoint //
		//----------------------------------------------------------------------------- -----------------

		String endpoint=data.get("Endpoint");

		if(endpoint==null||endpoint.trim().isEmpty()){

		throw new IllegalStateException("Endpoint is missing in Excel for: "+scenarioNameFeature);}

		//
		//----------------------------------------------------------------------------- ----------------- // Replace
																										// programId if
																										// required //
		//----------------------------------------------------------------------------- -----------------

		if(endpoint.contains("{programId}")){

		if(SharedTestData.programId<=0){

		throw new IllegalStateException("programId is not available.");}

		endpoint=endpoint.replace("{programId}",String.valueOf(SharedTestData.programId));}

		//
		//----------------------------------------------------------------------------- ----------------- // HTTP Method
																										// //
		//----------------------------------------------------------------------------- -----------------

		String method=data.get("Method");

		if(method==null||method.trim().isEmpty()){

		throw new IllegalStateException("HTTP Method is missing in Excel for: "+scenarioNameFeature);}

		//
		//----------------------------------------------------------------------------- ----------------- // Build Request
																										// //
		//----------------------------------------------------------------------------- -----------------

		RequestSpecification requestBuilder=given().spec(requestSpec);

		//
		//----------------------------------------------------------------------------- ----------------- // Content Type
																										// //
		//----------------------------------------------------------------------------- -----------------

		String contentType=data.get("Content-type");

		if(contentType!=null&&!contentType.trim().isEmpty()){

		requestBuilder.contentType(contentType.trim());}

		//
		//----------------------------------------------------------------------------- ----------------- // Request Body
																										// //
		//----------------------------------------------------------------------------- -----------------

		if(programInput!=null){

		requestBuilder.body(programInput);}

		//
		//----------------------------------------------------------------------------- ----------------- // Send Request
																										// //
		//----------------------------------------------------------------------------- -----------------

		System.out.println("METHOD          : "+method);
		System.out.println("ENDPOINT        : "+endpoint);
		System.out.println("EXPECTED STATUS : "+expectedStatus);

		response=requestBuilder.log().all().when().request(method.trim(),endpoint.trim()).then().log().all().extract().response();
	}

	//
	//========================================================================================================= // CREATE
																												// PROGRAM
																												// -
																												// POST
																												// RESPONSE
																												// // //
	//=========================================================================================================

	@Then("Admin verifies the response payload with expected output from the data sheet"
		  ) public void adminVerifiesResponse() {
		  
		  if (response == null) {
		  
		  throw new
		  IllegalStateException("Response is null. API request was not executed."); }
		  
		  if (data == null) {
		  
		  throw new IllegalStateException("Excel data is null."); }
		  
		  int expectedStatus = Integer.parseInt(data.get("ExpectedStatusCode").trim());
		  
		  //
		 //----------------------------------------------------------------------------- ----------------- // INVALID RESPONSE //
		 // -----------------------------------------------------------------------------
		  
		  
		  if (expectedStatus != 201) {
		  System.out.println("Validating INVALID response...");
		  ResponseValidator.validateErrorResponse(response, data); 
		  return; }
		  
		  //
		  //----------------- // VALID RESPONSE //
		  
		  System.out.println("Validating SUCCESS response...");
		  
		// Status Code //
		  
		  Assert.assertEquals(response.getStatusCode(), 201,
		  "Create Program should return HTTP 201");
		  
		  //
		 // -----------------------------------------------------------------------------
		 // ----------------- // JSON Schema //
		  
		  
		  response.then().assertThat().body(matchesJsonSchemaInClasspath(
		  "schemas/Program/CreateProgramSchema.json"));
		  
		  // Convert response to POJO //
		 
		  CreateProgramResponse actualResponse =
		  response.as(CreateProgramResponse.class);
		  
		  // Validate Program ID //
		 
		  
		  Assert.assertTrue(actualResponse.getProgramId() > 0,
		  "ProgramId should be greater than 0");
		  
		  // Validate Program Name //
		 
		  
		  Assert.assertNotNull(actualResponse.getProgramName(),
		  "ProgramName should not be null");
		  
		  Assert.assertFalse(actualResponse.getProgramName().isBlank(),
		  "ProgramName should not be blank");
		  
		   // Validate Description //
		  
		  
		  Assert.assertEquals(actualResponse.getProgramDescription(),
		  programInput.getProgramDescription(), "ProgramDescription mismatch");
		  
		   // Validate Name //
		 
		  Assert.assertEquals(actualResponse.getProgramName(),
		  programInput.getProgramName(), "ProgramName mismatch");
		  
		   // Save Program Data //
		  
		  
		  SharedTestData.programId = actualResponse.getProgramId();
		  
		  SharedTestData.programName = actualResponse.getProgramName();
		  
		  if (!SharedTestData.programIdList.contains(SharedTestData.programId)) {
		  
		  SharedTestData.programIdList.add(SharedTestData.programId); }
		  
		  if (!SharedTestData.programNameList.contains(SharedTestData.programName)) {
		  
		  SharedTestData.programNameList.add(SharedTestData.programName); }
		  
		  System.out.println();
		  System.out.println("==========================================");
		  System.out.println("PROGRAM CREATED SUCCESSFULLY");
		  System.out.println("Program ID   : " + SharedTestData.programId);
		  System.out.println("Program Name : " + SharedTestData.programName);
		  System.out.println("=========================================="); }

	//
	//========================================================================================================= // GET
																												// PROGRAM
																												// //
	//=========================================================================================================

	@Given("Admin creates GET request with {string} for Program")
	public void adminCreatesGetRequestForProgram(String scenarioName) throws IOException {

		System.out.println();System.out.println("==========================================");
		System.out.println("PROGRAM GET REQUEST");
		System.out.println("==========================================");
		System.out.println("Scenario : "+scenarioName);

		//
		//----------------------------------------------------------------------------- ----------------- // Read Excel //
		//----------------------------------------------------------------------------- -----------------

		data=ExcelReader.readExcelData("Program",scenarioName);

		if(data==null||data.isEmpty()){

		throw new IllegalStateException("Program Excel data was not found for scenario: "+scenarioName);}

		//
		//----------------------------------------------------------------------------- ----------------- // Endpoint //
		//----------------------------------------------------------------------------- -----------------

		String endpoint=data.get("Endpoint");

		if(endpoint==null||endpoint.trim().isEmpty()){

		throw new IllegalStateException("Endpoint is missing in Excel for scenario: "+scenarioName);}

		//
		//----------------------------------------------------------------------------- ----------------- // Method FROM
																										// EXCEL //
		//----------------------------------------------------------------------------- -----------------

		String method=data.get("Method");

		if(method==null||method.trim().isEmpty()){

		throw new IllegalStateException("Method is missing in Excel for scenario: "+scenarioName);}

		method=method.trim().toUpperCase();

		
		//----------------------------------------------------------------------------- ----------------- // Authentication
																										// //
		//----------------------------------------------------------------------------- -----------------

		boolean noAuth=isNoAuthScenario(scenarioName);

		RequestSpecification requestSpec;

		if(noAuth){

		requestSpec=RestAssured.given().spec(RequestSpec.getRequestSpecWithoutAuth());

		}else{

		requestSpec=RestAssured.given().spec(RequestSpec.getRequestSpec());}

		//
		//----------------------------------------------------------------------------- ----------------- // Replace
																										// Program ID //
		//----------------------------------------------------------------------------- -----------------

		if(endpoint.contains("{programId}")){

		String excelProgramId=data.get("ProgramId");

		if(excelProgramId!=null&&!excelProgramId.trim().isEmpty()){

		endpoint=endpoint.replace("{programId}",excelProgramId.trim());

		}else{

		if(SharedTestData.programId<=0){

		throw new IllegalStateException("ProgramId is not available for scenario: "+scenarioName);}

		endpoint=endpoint.replace("{programId}",String.valueOf(SharedTestData.programId));}}

		//
		//----------------------------------------------------------------------------- ----------------- // Replace
																										// Program Name
																										// //
		//----------------------------------------------------------------------------- -----------------

		if(endpoint.contains("{programName}"))
		{
		String excelProgramName=data.get("ProgramName");

		if(excelProgramName!=null&&!excelProgramName.trim().isEmpty()){

		endpoint=endpoint.replace("{programName}",excelProgramName.trim());

		}else{

		if(SharedTestData.programName==null||SharedTestData.programName.isBlank()){

		throw new IllegalStateException("ProgramName is not available for scenario: "+scenarioName);}

		endpoint=endpoint.replace("{programName}",SharedTestData.programName);}}

		//
		
		String contentType=data.get("Content-type");

		if(contentType!=null&&!contentType.trim().isEmpty()){

		requestSpec.contentType(contentType.trim());}

		//
		//----------------------------------------------------------------------------- ----------------- // Request Body
																										// //
		//----------------------------------------------------------------------------- -----------------

		String body=data.get("Body");

		if(body!=null&&!body.trim().isEmpty()){

		requestSpec.body(body);}

		//
		//----------------------------------------------------------------------------- ----------------- // Set Endpoint
																										// //
		//----------------------------------------------------------------------------- -----------------

		requestSpec=requestSpec.basePath(endpoint);

		//
		//----------------------------------------------------------------------------- ----------------- // Store request
																										// information
																										// //
		//----------------------------------------------------------------------------- -----------------

		scenarioContext.setRequestSpec(requestSpec);

		scenarioContext.setContext("PROGRAM_SCENARIO",scenarioName);

		scenarioContext.setContext("PROGRAM_METHOD",method);

		scenarioContext.setContext("PROGRAM_ENDPOINT",endpoint);

		System.out.println("Method   : "+method);
		System.out.println("Endpoint : "+endpoint);
		System.out.println("No Auth  : "+noAuth);
		System.out.println("==========================================");
	}

	//
	//========================================================================================================= // SEND
																												// GET
																												// REQUEST
																												// //
	//=========================================================================================================

	@When("Admin sends request to get Programs")
	public void adminSendsRequestToGetPrograms() {

		RequestSpecification requestSpec = scenarioContext.getRequestSpec();

		if (requestSpec == null) {

			throw new IllegalStateException(
					"Request specification is null. " + "Ensure the GET Given step executed first.");
		}

		String method = (String) scenarioContext.getContext("PROGRAM_METHOD");

		if (method == null || method.trim().isEmpty()) {

			throw new IllegalStateException("HTTP Method is missing from scenario context.");
		}

		method = method.trim().toUpperCase();

		System.out.println();
		System.out.println("==========================================");
		System.out.println("SENDING PROGRAM GET REQUEST");
		System.out.println("METHOD : " + method);
		System.out.println("==========================================");

		response = sendProgramRequest(requestSpec, method);

		scenarioContext.setContext("PROGRAM_RESPONSE", response);
	}

	//
	//========================================================================================================= // PUT
																												// PROGRAM
																												// //
	//=========================================================================================================

	@Given("Admin creates PUT request with {string} for Program")
	public void adminCreatesPutRequestForProgram(String scenarioName) throws IOException {

		System.out.println();System.out.println("==========================================");System.out.println("PROGRAM PUT REQUEST");System.out.println("==========================================");System.out.println("Scenario : "+scenarioName);

		//
		//----------------------------------------------------------------------------- ----------------- // Read Excel //
		//----------------------------------------------------------------------------- -----------------

		data=ExcelReader.readExcelData("Program",scenarioName);

		if(data==null||data.isEmpty()){

		throw new IllegalStateException("Program Excel data was not found for scenario: "+scenarioName);}

		//
		/////----------------------------------------------------------------------------- ----------------- // Endpoint //
		//----------------------------------------------------------------------------- -----------------

		String endpoint=data.get("Endpoint");

		if(endpoint==null||endpoint.trim().isEmpty()){

		throw new IllegalStateException("Endpoint is missing in Excel for scenario: "+scenarioName);}

		//
		//----------------------------------------------------------------------------- ----------------- // Method FROM
																										// EXCEL //
		//----------------------------------------------------------------------------- -----------------

		String method=data.get("Method");

		if(method==null||method.trim().isEmpty()){

		throw new IllegalStateException("Method is missing in Excel for scenario: "+scenarioName);}

		method=method.trim().toUpperCase();

		//
		//----------------------------------------------------------------------------- ----------------- // Authentication
																										// //
		//----------------------------------------------------------------------------- -----------------

		boolean noAuth=isNoAuthScenario(scenarioName);

		RequestSpecification requestSpec;

		if(noAuth){

		requestSpec=RestAssured.given().spec(RequestSpec.getRequestSpecWithoutAuth());

		}else{

		requestSpec=RestAssured.given().spec(RequestSpec.getRequestSpec());}

		//
		//----------------------------------------------------------------------------- ----------------- // Replace
																										// Program ID //
		//----------------------------------------------------------------------------- -----------------

		if(endpoint.contains("{programId}"))
		{

		String excelProgramId=data.get("ProgramId");

		if(excelProgramId!=null&&!excelProgramId.trim().isEmpty()){

		endpoint=endpoint.replace("{programId}",excelProgramId.trim());

		}else{

		if(SharedTestData.programId<=0){

		throw new IllegalStateException("ProgramId is not available for scenario: "+scenarioName);}

		endpoint=endpoint.replace("{programId}",String.valueOf(SharedTestData.programId));}}

		//
		//----------------------------------------------------------------------------- ----------------- // Replace
																										// Program Name
																										// //
		//----------------------------------------------------------------------------- -----------------

		if(endpoint.contains("{programName}")){

		String excelProgramName=data.get("ProgramName");

		if(excelProgramName!=null&&!excelProgramName.trim().isEmpty()){

		endpoint=endpoint.replace("{programName}",excelProgramName.trim());

		}else{

		if(SharedTestData.programName==null||SharedTestData.programName.isBlank()){

		throw new IllegalStateException("ProgramName is not available for scenario: "+scenarioName);}

		endpoint=endpoint.replace("{programName}",SharedTestData.programName);}}

		//
		//----------------------------------------------------------------------------- ----------------- // Content Type
																										// //
		//----------------------------------------------------------------------------- -----------------

		String contentType=data.get("Content-type");

		if(contentType!=null&&!contentType.trim().isEmpty()){

		requestSpec.contentType(contentType.trim());}

		//
		//----------------------------------------------------------------------------- ----------------- // Request Body
																										// //
		//----------------------------------------------------------------------------- -----------------

		String body=data.get("Body");

		if(body!=null&&!body.trim().isEmpty()){

		requestSpec.body(body);}

		//
		//----------------------------------------------------------------------------- ----------------- // Set Endpoint
																										// //
		//----------------------------------------------------------------------------- -----------------

		requestSpec=requestSpec.basePath(endpoint);

		//
		//----------------------------------------------------------------------------- ----------------- // Store request
																										// information
																										// //
		//----------------------------------------------------------------------------- -----------------

		scenarioContext.setRequestSpec(requestSpec);

		scenarioContext.setContext("PROGRAM_SCENARIO",scenarioName);

		scenarioContext.setContext("PROGRAM_METHOD",method);

		scenarioContext.setContext("PROGRAM_ENDPOINT",endpoint);

		System.out.println("Method   : "+method);
		System.out.println("Endpoint : "+endpoint);
		System.out.println("No Auth  : "+noAuth);
		System.out.println("==========================================");
	}

	//
	//========================================================================================================= // SEND
																												// PUT
																												// REQUEST
																												// //
	//=========================================================================================================

	@When("Admin sends request to update Program")
	public void adminSendsRequestToUpdateProgram() {

		RequestSpecification requestSpec = scenarioContext.getRequestSpec();

		if (requestSpec == null) {

			throw new IllegalStateException(
					"Request specification is null. " + "Ensure the PUT Given step executed first.");
		}

		String method = (String) scenarioContext.getContext("PROGRAM_METHOD");

		if (method == null || method.trim().isEmpty()) {

			throw new IllegalStateException("HTTP Method is missing from scenario context.");
		}

		method = method.trim().toUpperCase();

		System.out.println();
		System.out.println("==========================================");
		System.out.println("SENDING PROGRAM UPDATE REQUEST");
		System.out.println("METHOD : " + method);
		System.out.println("==========================================");

		response = sendProgramRequest(requestSpec, method);

		scenarioContext.setContext("PROGRAM_RESPONSE", response);
	}

	//
	//========================================================================================================= // COMMON
																												// PROGRAM
																												// REQUEST
																												// //
	//=========================================================================================================

	private Response sendProgramRequest(RequestSpecification requestSpec, String
		  method) {
		  
		  if (method == null || method.trim().isEmpty()) {
		  
		  throw new IllegalArgumentException("HTTP method cannot be null or blank."); }
		  
		  String httpMethod = method.trim().toUpperCase();
		  
		  System.out.println("Executing HTTP Method: " + httpMethod);
		  
		  switch (httpMethod) {
		  
		  case "GET":
		  
		  return requestSpec.when().log().all().get();
		  
		  case "POST":
		  
		  return requestSpec.when().log().all().post();
		  
		  case "PUT":
		  
		  return requestSpec.when().log().all().put();
		  
		  case "DELETE":
		  
		  return requestSpec.when().log().all().delete();
		  
		  case "PATCH":
		  
		  return requestSpec.when().log().all().patch();
		  
		  case "HEAD":
		  
		  return requestSpec.when().log().all().head();
		  
		  case "OPTIONS":
		  
		  return requestSpec.when().log().all().options();
		  
		  default:
		  
		  // Allows the API to receive an intentionally // invalid HTTP method from
		 // Excel, for example: // INVALID // // This is required for the Program //
		//  "invalid method" test case.
		  
		  return requestSpec.when().log().all().request(httpMethod); } }

	//
	//========================================================================================================= // PROGRAM
																												// RESPONSE
																												// VALIDATION
																												// //
	//=========================================================================================================

	@Then("Admin receives expected Program response for {string}")
	public void adminReceivesExpectedProgramResponse(String scenarioName) {

		if (response == null) {

			response = (Response) scenarioContext.getContext("PROGRAM_RESPONSE");
		}

		if (response == null) {

			throw new IllegalStateException("Program API response is null for scenario: " + scenarioName);
		}

		if (data == null || data.isEmpty()) {

			throw new IllegalStateException("Program Excel data is missing for scenario: " + scenarioName);
		}

		System.out.println();
		System.out.println("==========================================");
		System.out.println("PROGRAM RESPONSE VALIDATION");
		System.out.println("==========================================");

		System.out.println("Scenario        : " + scenarioName);

		System.out.println("Expected Status : " + data.get("ExpectedStatusCode"));

		System.out.println("Actual Status   : " + response.getStatusCode());

		System.out.println("Response Body   : " + response.asString());

		System.out.println("==========================================");

		ResponseValidator.validateErrorResponse(response, data);
	}

	//
	//========================================================================================================= // NO AUTH
																												// SCENARIO
																												// //
	//=========================================================================================================

	private boolean isNoAuthScenario(String scenarioName) {

		if (scenarioName == null) {
			return false;
		}

		String scenario = scenarioName.trim().toLowerCase();

		return scenario.contains("noauth") || scenario.contains("no_auth") || scenario.contains("no auth");
	}
}
