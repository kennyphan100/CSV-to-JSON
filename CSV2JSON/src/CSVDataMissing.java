// -------------------------------------------
// April 3, 2021
// Written by: Kenny Phan
// -------------------------------------------

/**
 * This class implements the CSVDataMissing which displays that an attribute is missing.
 * @author Kenny Phan
 */
public class CSVDataMissing extends Exception{

	/**
	 * Default constructor for the CSVDataMissing class.
	 */
	public CSVDataMissing() {
		super("Not converted to JSON file.\nError saved in the log file.");
	}
	
	/**
	 * Parametrized constructor for the CSVDataMissing class.
	 * @param s A string to be printed when this exception is thrown.
	 */
	public CSVDataMissing(String s) {
		super(s);
	}
	
}
