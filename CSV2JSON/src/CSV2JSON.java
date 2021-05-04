// -------------------------------------------
// April 3, 2021
// Written by: Kenny Phan
// -------------------------------------------

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileInputStream;

/**
 * This class implements the CSV to JSON converter.
 * @author Kenny Phan
 */
public class CSV2JSON {
	
	/**
	 * This method attempts to convert a CSV file into a JSON file
	 * @param fileName The CSV file to convert to a JSON file.
	 */
	public static void processFilesForValidation(String fileName) {
		PrintWriter pw = null; // PrintWriter used to create the CSV file
		Scanner sc = null; // Scanned used to read the CSV file	
		
		// Try to open the input CSV file to read
		try {
			// Replace the "CSV" by "txt"
			String CSVFile = fileName.substring(0, fileName.length()-3) + "txt";
			sc = new Scanner(new FileInputStream(CSVFile));
		} catch(FileNotFoundException e) {
			System.out.println("Could not open file \"" + fileName + "\" for reading.");
			System.out.println("Please check if file exists!");
			System.out.println("Program will terminate after closing any opened files.");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Could not open file \"" + fileName + "\" for reading.");
			System.out.println("Please check if file exists!");
			System.out.println("Program will terminate after closing any opened files.");
			System.exit(0);
		}
		
		// Attempt to create the JSON file
		System.out.println("~~~~~ Attempting to create the JSON file of \"" + fileName.substring(0, fileName.length() - 3) + "CSV\" ~~~~~");
		
		// Set the known passive attributes for each type of file
		String[] carRentalAttributes = {"Date", "Driver's Name", "Driver's Licence number", "Car Make", "Plate Number", "Meter at the start of the trip", "Meter at the end of the trip", "Mileage made"};
		String[] carMaintenanceAttributes = {"Date", "Plate Number", "Maintenance summary", "Workshop", "Phone number", "Address", "Labor cost", "Material cost", "Total cost"};
		
		// Split the attributes into an array of strings
		String[] attributes = sc.nextLine().split(",", -1);	
		
		PrintWriter pw2 = null; // PrintWriter used to create the log file
		boolean isMissingAttribute = false;
		
		//Check for a missing attribute
		try { 
			// Loop through the attributes
			for(int i = 0; i < attributes.length; i++) {
				// Check if an attribute is empty
				if (attributes[i] == "") {
					System.out.println("File \"" + fileName + "\" is invalid: attribute(s) missing.");
					
					// Try to open/create the log file
					try {
						pw2 = new PrintWriter(new FileOutputStream("LogFile.txt", true));
					} catch(FileNotFoundException e) {
						System.out.println("Could not create the log file.");
						System.out.println("Program will terminate.");
						System.exit(0);
					}			
					
					// Write on the log file
					pw2.println("File \"" + fileName + "\" is invalid.");
					
					int detectedAttributes = 0;
					int missingAttributes = 0;
					
					// Print the attributes into the log file and print the missing attribute with "***"
					for(int j = 0; j < attributes.length; j++) {
						
						// Check if the missing attribute is at the end
						if(attributes[j].equals("") && j == attributes.length-1) {
							missingAttributes++;
							pw2.print("***"); 
						}
						// Check if the missing attribute is not at the end
						else if (attributes[j].equals("")) {
							missingAttributes++;
							pw2.print("***, ");                                                
						}
						else {
							detectedAttributes++;
							if(j == attributes.length-1) {
								pw2.print(attributes[j]);
							} else {
								pw2.print(attributes[j] + ", ");
							}
						}
					}		
					
					pw2.println();
					pw2.println("Missing field: "  + detectedAttributes + " detected, " + missingAttributes + " missing");
					pw2.println();
					
					// Close PrintWriter
					pw2.close();
					
					isMissingAttribute = true;
					
				}
			}
			
			if (isMissingAttribute) {
				throw new CSVDataMissing();
			}
			
		} catch(CSVDataMissing e) {
			System.out.println(e.getMessage());
		}
		
		// If this point is reached, then the file has no missing attribute (file is valid)
		if (!isMissingAttribute) {
			// Try to create the JSON file to write
			try {
				pw = new PrintWriter(new FileOutputStream(fileName.substring(0, fileName.length() - 4) + ".json" + ".txt"));
			} catch (FileNotFoundException e) {
				System.out.println("Could not create a new JSON file for this CSV file.");
				System.out.println("Program will terminate.");
				System.exit(0);
			}
			
			pw.println("[");	
			int lineNumber = 2; // Used to keep track of the record line
			
			// Loop through all the record lines
			while (sc.hasNextLine()) {
				
				boolean validRecord = true;
				
				// Acquire a whole row
				String record = sc.nextLine();
				
				// Split the record of values into an array of string
				String[] values = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				
				// Check for a missing data value
				try {
					// Loop through the data values
					for(int i = 0; i < values.length; i++) {
						// Check if a data value is empty
						if(values[i] == "") {
							// This record is no longer valid to be printed as a JSON object because it has a missing attribute
							validRecord = false;
							
							// Try to open/create the log file for a missing data value
							try {
								pw2 = new PrintWriter(new FileOutputStream("LogFile.txt", true));
								
							} catch(FileNotFoundException e) {
								System.out.println("Could not open/create the log file.");
							}
							
							pw2.println("In file \"" + fileName + "\" line " + lineNumber + " not converted to JSON object: missing data value.");						
							
							String missingData = null;
							int missingDataIndex = 0;
							
							// Print the values inside the log file
							for(int j = 0; j < values.length; j++) {
								if (values[j].equals("") && j == values.length-1) {
									missingDataIndex = j;
									pw2.print("***");	
								} 
								else if(values[j].equals("")) {
									missingDataIndex = j;
									pw2.print("***, ");
								}
								else {
									if(j == values.length-1) {
										pw2.print(values[j]);
									} else {
										pw2.print(values[j] + ", ");
									}
								}
							}
							
							if(fileName.contains("Car Rental Record")) {
								missingData = carRentalAttributes[missingDataIndex];
							} else if(fileName.contains("Car Maintenance Record.txt")) {
								missingData = carMaintenanceAttributes[missingDataIndex];
							}			
							
							pw2.println();
							pw2.println("Missing data: " + missingData);
							pw2.println();
							
							pw2.close();
							
							throw new CSVFileInvalidException("This record has a missing data value at line " + lineNumber + ".\nThe JSON object of this record will not be created.\nReporting to the log file.");					
						}
					}
					
				} catch(CSVFileInvalidException e) {
					System.out.println(e.getMessage());
				}
	
				// Only convert those records with no missing data into JSON object
				if (validRecord) {
					pw.println("  {");
					// Print the JSON array of each record into the JSON file
					for (int i = 0; i < values.length; i++) {
						// If the data can be parsed into an integer then it is a number: Don't put quotes in data values that are numbers
						try {
							// Check if the data value is just a number, if not then it jumps to the catch block
							Double.parseDouble(values[i]);
							
							// If the code reaches here, then the data value is a number
							if(i == attributes.length-1) {
								// Print the data values that are enclosed in quotes using this format
								pw.println("    \"" + attributes[i] + "\": " + values[i] + "");
								
							} else {
								// Print the data values that are not enclosed in quotes using this format
								pw.println("    \"" + attributes[i] + "\": " + values[i] + ",");
							}				
							
						} catch(NumberFormatException e) {
							// If the code reaches here, then the data value is not a number
							
							// Print the data values that are already enclosed in quotes using this format
							if(values[i].substring(0,1).equals("\"")) {
								// Print the last data value record without comma
								if(i == attributes.length-1) {
									pw.println("    \"" + attributes[i] + "\": " + values[i] + "");
								} else {
									// Print the data values that are not enclosed in quotes using this format
									pw.println("    \"" + attributes[i] + "\": " + values[i] + ",");
								}						
								
							// Print the data values that are not enclosed in quotes using this format
							} else {
								// Print the last data value record without comma
								if(i == attributes.length-1) {
									pw.println("    \"" + attributes[i] + "\": \"" + values[i] + "\"");
								
								// Print the data values with comma
								} else {
									pw.println("    \"" + attributes[i] + "\": \"" + values[i] + "\",");
								}			
							}
						}													
					}
	
					if (sc.hasNextLine() == false) {
						pw.println("  }");
					} else {
						pw.println("  },");	
					}
				}
	
				// Reset the boolean validRecord
				validRecord = true;
				
				// Increment record line
				lineNumber++;
				
			}
			
			pw.println("]");
			
			System.out.println("SUCCESSFULLY CREATED THE JSON FILE.");
					
			pw.close();
		}
		sc.close();
		
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("--------------------------------------------------");
		System.out.println("\t WELCOME TO CSV2JSON CONVERTER!");
		System.out.println("--------------------------------------------------");
		System.out.println();
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("   CSV to JSON files MODE ");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		// Prompt user to convert CSV file to JSON file
		while (true) {
			System.out.println();
			System.out.println("Enter a CSV file to convert to JSON file (Enter \"1\" when finished): ");
			String fileName = scanner.nextLine();
			
			if (fileName.equals("1")) {
				System.out.println("Going into display mode where the created CSV files can be displayed.");
				break;
			}
			
			processFilesForValidation(fileName);
		}
	
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("    Display MODE    ");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
		
		boolean displayMODE = true;
		
		// Prompt user to input a JSON file to read
		while (displayMODE) {
			System.out.println("Enter a JSON file to read (Enter \"1\" to exit): ");
			String readFile = scanner.nextLine() + ".txt";
			System.out.println();
			
			if (readFile.equals("1.txt")) {
				System.out.println("Thank you for using CSV2JSON converter. Goodbye!");
				break;
			}
			
			BufferedReader br = null;
			
			// Try to the read JSON file using BufferedReader
			try {
				br = new BufferedReader(new FileReader(readFile));
				
				System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
				int x;
				x = br.read();
				while(x != -1) {
					System.out.print((char) x);
					x = br.read();
				}		
				System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
				br.close();
				
			} catch(FileNotFoundException e) {
				
				// Give user a second chance to read a file
				System.out.println("Problem opening files. Cannot proceed to read.");
				System.out.println("Last chance to input a correct file to display.");
				System.out.println("Enter the JSON file that you would like to read (Enter \"1\" to exit): ");
				String readFile1 = scanner.nextLine() + ".txt";
				System.out.println();
				
				if (readFile1.equals("1.txt")) {
					System.out.println("Thank you for using CSV2JSON converter. Goodbye!");
					break;
				}
				
				try {
					br = new BufferedReader(new FileReader(readFile1));
					
					System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
					int x;
					x = br.read();
					while(x != -1) {
						System.out.print((char) x);
						x = br.read();
					}		
					System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
					br.close();
					
				} catch(FileNotFoundException e1) {
					System.out.println("Problem opening files. Cannot proceed to read.");
					System.out.println("Program will terminate.");
					System.exit(0);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			System.out.println();
		}
			
		// Closer scanner
		scanner.close();
		
	}

}
