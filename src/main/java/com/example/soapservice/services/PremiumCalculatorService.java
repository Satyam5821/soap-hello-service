package com.example.soapservice.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PremiumCalculatorService {

  private static final Logger logger = LoggerFactory.getLogger(PremiumCalculatorService.class);

  private static final String LOCATION_URBAN = "URBAN";
  private static final String LOCATION_SUBURBAN = "SUBURBAN";
  private static final String LOCATION_RURAL = "RURAL";

	 private List<PremiumData> premiumDataList = new ArrayList<>();
	  @PostConstruct
	  public void init() {
	    loadPremiumData();

	  }
	  private void loadPremiumData() {
	    try {
	      ClassPathResource resource = new ClassPathResource("carlist.csv");       
	      if (!resource.exists()) {
	        logger.warn("CSV file not found: carlist.csv");
	        loadDefaultData();
	        return;
	      }	     
	      try (CSVReader reader = new CSVReader(new InputStreamReader(
	          resource.getInputStream(), StandardCharsets.UTF_8))) {         
	        List<String[]> records = reader.readAll();	         
	        if (records.isEmpty()) {
	          logger.warn("CSV file is empty");
	          loadDefaultData();
	          return;
	        }	         
	        // Skip header row if exists
	        int startIndex = (records.get(0)[0].equalsIgnoreCase("ageMin") || 
	                records.get(0)[0].equalsIgnoreCase("age_min")) ? 1 : 0;         
	        for (int i = startIndex; i < records.size(); i++) {
	          String[] premiumRecord = records.get(i);           
	          if (premiumRecord.length < 6) {
	            logger.warn("Skipping invalid row {}: insufficient columns", i);
	            continue;
	          }	        
	          try {
	            PremiumData data = new PremiumData(
	              Integer.parseInt(premiumRecord[0].trim()), // ageMin
	              Integer.parseInt(premiumRecord[1].trim()), // ageMax
	              premiumRecord[2].trim().toUpperCase(), // vehicleType
	              premiumRecord[3].trim().toUpperCase(), // location
	              Double.parseDouble(premiumRecord[4].trim()), // basePremium
	              Double.parseDouble(premiumRecord[5].trim()) // riskFactor
	            );

	            premiumDataList.add(data);
	          } catch (NumberFormatException e) {
	            logger.warn("Skipping invalid row {}: {}", i, e.getMessage());
	          }
	        }
	        logger.info("Loaded {} premium records from CSV", premiumDataList.size());	         
	      }
	    } catch (IOException | CsvException e) {
	      logger.error("Error loading CSV file: {}", e.getMessage());
	      loadDefaultData();
	    }	     
	    if (premiumDataList.isEmpty()) {
	      loadDefaultData();
	    }
	  }
	   
	  private void loadDefaultData() {
	    logger.info("Loading default premium data...");
	    // Add some default data

	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_URBAN, 1200, 1.5));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_SUBURBAN, 1000, 1.3));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_RURAL, 800, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_URBAN, 900, 1.2));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_SUBURBAN, 750, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_RURAL, 650, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_URBAN, 800, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_SUBURBAN, 700, 0.9));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_RURAL, 600, 0.8));

	    logger.info("Loaded {} default premium records", premiumDataList.size());

	  }   

	  public double calculatePremium(int customerAge, String vehicleType, String location) {
	    if (!StringUtils.hasText(vehicleType) || !StringUtils.hasText(location)) {
	      return 1000.0;
	    }	    
	    String normalizedVehicleType = vehicleType.trim().toUpperCase();
	    String normalizedLocation = location.trim().toUpperCase();
	   
	    for (PremiumData data : premiumDataList) {
	      if (customerAge >= data.getAgeMin() && 
	        customerAge <= data.getAgeMax() &&
	        data.getVehicleType().equals(normalizedVehicleType) &&
	        data.getLocation().equals(normalizedLocation)) {	         
	        double calculatedPremium = data.getBasePremium() * data.getRiskFactor();
	        logger.info(
	          "Premium calculated: {} (Base: {}, Risk Factor: {})",
	          calculatedPremium,
	          data.getBasePremium(),
	          data.getRiskFactor()
	        );
	        return calculatedPremium;
	      }
	    }
	    logger.info(
	      "No matching premium data found for age: {}, vehicle: {}, location: {}",
	      customerAge,
	      normalizedVehicleType,
	      normalizedLocation
	    );
	    return 1000.0; // Default premium
	  }	 
	  public List<PremiumData> getAllPremiumData() {
	    return new ArrayList<>(premiumDataList);
	  }
}

package com.example.soapservice.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PremiumCalculatorService {

  private static final Logger logger = LoggerFactory.getLogger(PremiumCalculatorService.class);

  private static final String LOCATION_URBAN = "URBAN";
  private static final String LOCATION_SUBURBAN = "SUBURBAN";
  private static final String LOCATION_RURAL = "RURAL";

	 private List<PremiumData> premiumDataList = new ArrayList<>();
	  @PostConstruct
	  public void init() {
	    loadPremiumData();

	  }
	  private void loadPremiumData() {
	    try {
	      ClassPathResource resource = new ClassPathResource("carlist.csv");       
	      if (!resource.exists()) {
	        logger.warn("CSV file not found: carlist.csv");
	        loadDefaultData();
	        return;
	      }	     
	      try (CSVReader reader = new CSVReader(new InputStreamReader(
	          resource.getInputStream(), StandardCharsets.UTF_8))) {         
	        List<String[]> records = reader.readAll();	         
	        if (records.isEmpty()) {
	          logger.warn("CSV file is empty");
	          loadDefaultData();
	          return;
	        }	         
	        // Skip header row if exists
	        int startIndex = (records.get(0)[0].equalsIgnoreCase("ageMin") || 
	                records.get(0)[0].equalsIgnoreCase("age_min")) ? 1 : 0;         
	        for (int i = startIndex; i < records.size(); i++) {
	          String[] premiumRecord = records.get(i);           
	          if (premiumRecord.length < 6) {
	            logger.warn("Skipping invalid row {}: insufficient columns", i);
	            continue;
	          }	        
	          try {
	            PremiumData data = new PremiumData(
	              Integer.parseInt(premiumRecord[0].trim()), // ageMin
	              Integer.parseInt(premiumRecord[1].trim()), // ageMax
	              premiumRecord[2].trim().toUpperCase(), // vehicleType
	              premiumRecord[3].trim().toUpperCase(), // location
	              Double.parseDouble(premiumRecord[4].trim()), // basePremium
	              Double.parseDouble(premiumRecord[5].trim()) // riskFactor
	            );

	            premiumDataList.add(data);
	          } catch (NumberFormatException e) {
	            logger.warn("Skipping invalid row {}: {}", i, e.getMessage());
	          }
	        }
	        logger.info("Loaded {} premium records from CSV", premiumDataList.size());	         
	      }
	    } catch (IOException | CsvException e) {
	      logger.error("Error loading CSV file: {}", e.getMessage());
	      loadDefaultData();
	    }	     
	    if (premiumDataList.isEmpty()) {
	      loadDefaultData();
	    }
	  }
	   
	  private void loadDefaultData() {
	    logger.info("Loading default premium data...");
	    // Add some default data

	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_URBAN, 1200, 1.5));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_SUBURBAN, 1000, 1.3));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", LOCATION_RURAL, 800, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_URBAN, 900, 1.2));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_SUBURBAN, 750, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", LOCATION_RURAL, 650, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_URBAN, 800, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_SUBURBAN, 700, 0.9));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", LOCATION_RURAL, 600, 0.8));

	    logger.info("Loaded {} default premium records", premiumDataList.size());

	  }   

	  public double calculatePremium(int customerAge, String vehicleType, String location) {
	    if (!StringUtils.hasText(vehicleType) || !StringUtils.hasText(location)) {
	      return 1000.0;
	    }	    
	    String normalizedVehicleType = vehicleType.trim().toUpperCase();
	    String normalizedLocation = location.trim().toUpperCase();
	   
	    for (PremiumData data : premiumDataList) {
	      if (customerAge >= data.getAgeMin() && 
	        customerAge <= data.getAgeMax() &&
	        data.getVehicleType().equals(normalizedVehicleType) &&
	        data.getLocation().equals(normalizedLocation)) {	         
	        double calculatedPremium = data.getBasePremium() * data.getRiskFactor();
	        logger.info(
	          "Premium calculated: {} (Base: {}, Risk Factor: {})",
	          calculatedPremium,
	          data.getBasePremium(),
	          data.getRiskFactor()
	        );
	        return calculatedPremium;
	      }
	    }
	    logger.info(
	      "No matching premium data found for age: {}, vehicle: {}, location: {}",
	      customerAge,
	      normalizedVehicleType,
	      normalizedLocation
	    );
	    return 1000.0; // Default premium
	  }	 
	  public List<PremiumData> getAllPremiumData() {
	    return new ArrayList<>(premiumDataList);
	  }
}
