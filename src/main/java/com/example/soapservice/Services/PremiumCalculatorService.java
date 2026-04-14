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

@Service
private static final Logger logger = LoggerFactory.getLogger(PremiumCalculatorService.class);
import org.slf4j.Logger;
private static final Logger logger = LoggerFactory.getLogger(PremiumCalculatorService.class);
import org.slf4j.LoggerFactory;
public class PremiumCalculatorService {

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
	          String[] record = records.get(i);           
	          if (record.length < 6) {
	            logger.warn("Skipping invalid row {}: insufficient columns", i);
	            continue;
	          }	        
	          try {
	            PremiumData data = new PremiumData(
	              Integer.parseInt(record[0].trim()), // ageMin
	              Integer.parseInt(record[1].trim()), // ageMax
	              record[2].trim().toUpperCase(), // vehicleType
	              record[3].trim().toUpperCase(), // location
	              Double.parseDouble(record[4].trim()), // basePremium
	              Double.parseDouble(record[5].trim()) // riskFactor
	            );

	            premiumDataList.add(data);
	          } catch (NumberFormatException e) {
	            logger.warn("Skipping invalid row {}: {}", i, e.getMessage());
	          }
	        }
	        System.out.println("Loaded " + premiumDataList.size() + " premium records from CSV");	         
	      }
	    } catch (IOException | CsvException e) {
	      System.err.println("Error loading CSV file: " + e.getMessage());
	      loadDefaultData();
	    }	     
	    if (premiumDataList.isEmpty()) {
	      loadDefaultData();
	    }
	  }
	   
	  private void loadDefaultData() {
	    System.out.println("Loading default premium data...");
	    // Add some default data

	    premiumDataList.add(new PremiumData(18, 25, "CAR", "URBAN", 1200, 1.5));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", "SUBURBAN", 1000, 1.3));
	    premiumDataList.add(new PremiumData(18, 25, "CAR", "RURAL", 800, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", "URBAN", 900, 1.2));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", "SUBURBAN", 750, 1.1));
	    premiumDataList.add(new PremiumData(26, 35, "CAR", "RURAL", 650, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", "URBAN", 800, 1.0));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", "SUBURBAN", 700, 0.9));
	    premiumDataList.add(new PremiumData(36, 50, "CAR", "RURAL", 600, 0.8));

	    System.out.println("Loaded " + premiumDataList.size() + " default premium records");

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
	        System.out.println("Premium calculated: " + calculatedPremium + 

	                 " (Base: " + data.getBasePremium() + 
	                 ", Risk Factor: " + data.getRiskFactor() + ")");
	        return calculatedPremium;
	      }
	    }
	    System.out.println("No matching premium data found for age: " + customerAge + 

	             ", vehicle: " + normalizedVehicleType + 

	             ", location: " + normalizedLocation);
	    return 1000.0; // Default premium
	  }	 
	  public List<PremiumData> getAllPremiumData() {
	    return new ArrayList<>(premiumDataList);
	  }
}
