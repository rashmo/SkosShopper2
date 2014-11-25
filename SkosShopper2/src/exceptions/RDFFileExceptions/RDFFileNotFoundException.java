package exceptions.RDFFileExceptions;

/**************************************************************
 * RDFFileNotFoundException
 *
 * Project "Semantic Web"
 *  * Professor:	xxxxxxxxxxxxxx
 *  
 * This exception is thrown when a rdf-file path is corrupted
 * 
 *  Created on: 26.05.2014
 *      Author: Arkadius Pawlak
**************************************************************/

public class RDFFileNotFoundException extends RDFFileException{
	public RDFFileNotFoundException() {
		
	}//end Contructor
	
	public RDFFileNotFoundException(String e){
		super(e);
	}//end Contructor
}
