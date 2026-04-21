package com.example.soapservice.services;

public class PremiumData {

	  private int ageMin;
	  private int ageMax;
	  private String vehicleType;
	  private String location;
	  private double basePremium;
	  private double riskFactor;
	   
	  public PremiumData() {}
	  public PremiumData(int ageMin, int ageMax, String vehicleType, String location, 

	           double basePremium, double riskFactor) {

	    this.ageMin = ageMin;
	    this.ageMax = ageMax;
	    this.vehicleType = vehicleType;
	    this.location = location;
	    this.basePremium = basePremium;
	    this.riskFactor = riskFactor;

	  }

	  // Getters and Setters

	  public int getAgeMin() { return ageMin; }
	  public void setAgeMin(int ageMin) { this.ageMin = ageMin; }	 
	  public int getAgeMax() { return ageMax; }
	  public void setAgeMax(int ageMax) { this.ageMax = ageMax; }	   
	  public String getVehicleType() { return vehicleType; }
	  public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }   
	  public String getLocation() { return location; }
	  public void setLocation(String location) { this.location = location; }	   
	  public double getBasePremium() { return basePremium; }
	  public void setBasePremium(double basePremium) { this.basePremium = basePremium; }	   
	  public double getRiskFactor() { return riskFactor; }
	  public void setRiskFactor(double riskFactor) { this.riskFactor = riskFactor; }


}
