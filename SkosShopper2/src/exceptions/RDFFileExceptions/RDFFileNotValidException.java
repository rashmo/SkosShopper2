package exceptions.RDFFileExceptions;

/**************************************************************
 * RDFFileNotValidException
 *
 * Project "Semantic Web"
 *  * Professor:	xxxxxxxxxxxxxx
 *  
 * This exception is thrown if a file has not a valid RDF-format
 * like Turtle, Owl etc.
 *  Created on: 26.05.2014
 *      Author: Arkadius Pawlak
**************************************************************/

public class RDFFileNotValidException extends RDFFileException{

	public RDFFileNotValidException(){
		
	}//end Contructor
	
	public RDFFileNotValidException(String e){
		super(e);
	}
}
