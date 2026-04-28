private static final String MSG_EMPLOYEE_NOT_FOUND = "Employee not found for ID: ";
private static final String MSG_EMPLOYEE_FETCH_SUCCESS = "Employee data fetched successfully";
private static final String MSG_ERROR_READING_FILE = "Error reading file: ";
package com.example.soapservice.endpoint;

import io.spring.guides.gs_producing_web_service.AddRequest;
import io.spring.guides.gs_producing_web_service.AddResponse;
import io.spring.guides.gs_producing_web_service.CalculateEmiRequest;
import io.spring.guides.gs_producing_web_service.CalculateEmiResponse;
import io.spring.guides.gs_producing_web_service.DivideRequest;
import io.spring.guides.gs_producing_web_service.DivideResponse;

import io.spring.guides.gs_producing_web_service.GetHelloRequest;
import io.spring.guides.gs_producing_web_service.GetHelloResponse;
import io.spring.guides.gs_producing_web_service.MultiplyRequest;
import io.spring.guides.gs_producing_web_service.MultiplyResponse;
import io.spring.guides.gs_producing_web_service.PremiumCalculatorRequest;
import io.spring.guides.gs_producing_web_service.PremiumCalculatorResponse;
import io.spring.guides.gs_producing_web_service.ShippingRequest;
import io.spring.guides.gs_producing_web_service.ShippingResponse;
import io.spring.guides.gs_producing_web_service.SubtractRequest;
import io.spring.guides.gs_producing_web_service.SubtractResponse;
import io.spring.guides.gs_producing_web_service.TextFileRequest;
import io.spring.guides.gs_producing_web_service.TextFileResponse;
import io.spring.guides.gs_producing_web_service.UserFileRequest;
import io.spring.guides.gs_producing_web_service.UserFileResponse;
import io.spring.guides.gs_producing_web_service.UserFileidRequest;
import io.spring.guides.gs_producing_web_service.UserFileidResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.BiConsumer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


import com.example.soapservice.services.PremiumCalculatorService;

@Endpoint
public class HelloEndpoint {

	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
    private static final String FILE_READ_SUCCESS_MESSAGE = "File read successfully";
    private static final String ERROR_READING_FILE_MESSAGE = "Error reading file: ";
  
  private final PremiumCalculatorService premiumCalculatorService;

  public HelloEndpoint(PremiumCalculatorService premiumCalculatorService) {
    this.premiumCalculatorService = premiumCalculatorService;
  }

    private <R> R buildIntResultResponse(R response, int a, int b, IntBinaryOperator op, IntConsumer setResult) {
        setResult.accept(op.applyAsInt(a, b));
        return response;
    }

    private <R> R buildFileReadResponse(
            R response,
            String fileName,
            BiConsumer<R, String> setContent,
            BiConsumer<R, Boolean> setSuccess,
            BiConsumer<R, String> setMessage
    ) {
        try {
            String content = readClasspathFile(fileName);
            setContent.accept(response, content);
            setSuccess.accept(response, true);
            setMessage.accept(response, FILE_READ_SUCCESS_MESSAGE);
        } catch (IOException e) {
            setContent.accept(response, "");
            setSuccess.accept(response, false);
            setMessage.accept(response, ERROR_READING_FILE_MESSAGE + e.getMessage());
        }
        return response;
    }

    private String readClasspathFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHelloRequest")
	@ResponsePayload
	public GetHelloResponse getHello(@RequestPayload GetHelloRequest request) {

		GetHelloResponse response = new GetHelloResponse();
		response.setGreeting("Hello " + request.getName() + "!");
		return response;

	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddRequest")
    @ResponsePayload
    public AddResponse add(@RequestPayload AddRequest request) {
        AddResponse response = new AddResponse();
        return buildIntResultResponse(response, request.getA(), request.getB(), (a, b) -> a + b, response::setResult);
    }
 
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SubtractRequest")
    @ResponsePayload
    public SubtractResponse subtract(@RequestPayload SubtractRequest request) {
        SubtractResponse response = new SubtractResponse();
        return buildIntResultResponse(response, request.getA(), request.getB(), (a, b) -> a - b, response::setResult);
    }
 
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "MultiplyRequest")
    @ResponsePayload
    public MultiplyResponse multiply(@RequestPayload MultiplyRequest request) {
        MultiplyResponse response = new MultiplyResponse();
        return buildIntResultResponse(response, request.getA(), request.getB(), (a, b) -> a * b, response::setResult);
    }
 
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DivideRequest")
    @ResponsePayload
    public DivideResponse divide(@RequestPayload DivideRequest request) {
        DivideResponse response = new DivideResponse();
        if (request.getB() == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed");
        }
        return buildIntResultResponse(response, request.getA(), request.getB(), (a, b) -> a / b, response::setResult);
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CalculateEmiRequest")
    @ResponsePayload
    public CalculateEmiResponse calculateEmi(@RequestPayload CalculateEmiRequest request) {
        double principal = request.getPrincipal();
        double annualRate = request.getAnnualInterestRate();
        int tenureMonths = request.getTenureMonths();
 
        double monthlyRate = annualRate / 12 / 100;
        // BUG: incorrect EMI formula and no validation for invalid tenure
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                     Math.pow(1 + monthlyRate, tenureMonths - 1);
 
        CalculateEmiResponse response = new CalculateEmiResponse();
        response.setEmi(Math.round(emi * 100.0) / 100.0);
        return response;
    }
    
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ShippingRequest")
    @ResponsePayload
    public ShippingResponse calculateETA(@RequestPayload ShippingRequest request) {
 
        // BUG: no validation for missing origin/destination values
        String origin = request.getOrigin().trim().toLowerCase();
        String destination = request.getDestination().trim().toLowerCase();

 
        int daysToAdd;
 
        if (origin.equals(destination)) {
            daysToAdd = 1;
        } else if (getState(origin).equals(getState(destination))) {
            daysToAdd = 5;
        } else {
            daysToAdd = 10;
        }
 
        LocalDate estimatedDate = LocalDate.now().plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 
        ShippingResponse response = new ShippingResponse();
        response.setEstimatedDeliveryDate(estimatedDate.format(formatter));
 
        return response;
    }
 
    // Dummy state  (we can hve more later)
    private String getState(String city) {
        if (city.contains("delhi") || city.contains("gurgaon") || city.contains("noida")) {
            return "Delhi NCR";
        } else if (city.contains("mumbai") || city.contains("pune")) {
            return "Maharashtra";
        } else if (city.contains("bangalore") || city.contains("mysore")) {
            return "Karnataka";
        }
        return "Other";
    }
    
    // premiumCalculatorService is constructor-injected
 

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PremiumCalculatorRequest")
    @ResponsePayload
    public PremiumCalculatorResponse calculatePremium(@RequestPayload PremiumCalculatorRequest request) {
      PremiumCalculatorResponse response = new PremiumCalculatorResponse();
      try {
        // Validate input
        if (request.getCustomerAge() < 18 || request.getCustomerAge() > 100) {
          response.setSuccess(false);
          response.setMessage("Invalid age. Age must be between 18 and 100.");
          response.setPremium(0.0);
          return response;
        }
        if (request.getVehicleType() == null || request.getLocation() == null) {
          response.setSuccess(false);
          response.setMessage("Vehicle type and location are required.");
          response.setPremium(0.0);
          return response;

        }       
        double premium = premiumCalculatorService.calculatePremium(
          request.getCustomerAge(),
          request.getVehicleType(),
          request.getLocation()
        );

         

        response.setSuccess(true);
        response.setPremium(premium);
        response.setMessage("Premium calculated successfully");

      } catch (Exception e) {
        response.setSuccess(false);
        response.setMessage("Error calculating premium: " + e.getMessage());
        response.setPremium(0.0);

      }
      return response;

    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "TextFileRequest")
    @ResponsePayload
    public TextFileResponse getTextFileContent(@RequestPayload TextFileRequest request) {
        TextFileResponse response = new TextFileResponse();
        return buildFileReadResponse(
                response,
                request.getFileName(),
                TextFileResponse::setContent,
                TextFileResponse::setSuccess,
                TextFileResponse::setMessage
        );
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UserFileRequest")
    @ResponsePayload
    public UserFileResponse getuserFileContent(@RequestPayload UserFileRequest request) {
    	UserFileResponse response = new UserFileResponse();
        return buildFileReadResponse(
                response,
                request.getFileName(),
                UserFileResponse::setContent,
                UserFileResponse::setSuccess,
                UserFileResponse::setMessage
        );
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UserFileidRequest")
    @ResponsePayload
    public UserFileidResponse getFileidResponse(@RequestPayload UserFileidRequest request) {
    	UserFileidResponse response = new UserFileidResponse();
    	 
        try {
            // Fetch employee data (filtered by ID)
            String employeeData = getEmployeeDataById(request.getEmployeeId());
 
            if (employeeData == null || employeeData.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Employee not found for ID: " + request.getEmployeeId());
            } else {
                response.setContent(employeeData);
                response.setSuccess(true);
                response.setMessage("Employee data fetched successfully");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error reading file: " + e.getMessage());
        }
        return response;
    }
 
    private String getEmployeeDataById(int employeeId) {
        try {
            
            ClassPathResource resource = new ClassPathResource("userlistid.txt");
            if (!resource.exists()) {
                throw new IOException("File not found: " + resource.getFilename());
            }           
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
 
            
            String[] records = content.split("\n");
            if (records == null || records.length == 0) {
                return null; 
            }      
            for (String employeeRecord : records) {
                if (!StringUtils.hasText(employeeRecord)) {
                    continue;
                }

                String[] fields = employeeRecord.split(","); 
                // BUG: wrong field count expectation; actual data has 3 columns
                if (fields.length == 4 && Integer.parseInt(fields[0]) == employeeId) {  
                    return formatEmployeeData(fields); 
                }
            }
 
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
 
        return null;  
    }
 
    
    private String formatEmployeeData(String[] fields) {
        return String.format("ID: %s%nName: %s%nEmail: %s", fields[0], fields[1], fields[2]);
    }
  


    


}