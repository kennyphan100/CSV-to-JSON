// -------------------------------------------
// April 3, 2021
// Written by: Kenny Phan
// -------------------------------------------

/**
 * This class implements the CSVFileInvalidException which displays that a data value is missing.
 * @author Kenny Phan
 */
public class CSVFileInvalidException extends Exception{

	// Constructors
	/**
	 * Default constructor for the InvalidException class.
	 */
	public CSVFileInvalidException() {
		super("Error: Input row cannot be parsed due to missing information.");
	}
	
	/**
	 * Parameterized constructor for the InvalidException class.
	 * @param s A string that represents a message to be printed when this exception is thrown.
	 */
	public CSVFileInvalidException(String s) {
		super(s);
	}
	
}
