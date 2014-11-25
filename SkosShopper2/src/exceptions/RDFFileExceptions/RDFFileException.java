package exceptions.RDFFileExceptions;

/**************************************************************
 * RDFFileException
 *
 * Project "Semantic Web"
 *  * Professor:	xxxxxxxxxxxxxx
 *  
 * Custom Exception Interface for further exceptions referring to
 * RDF-files and operations on RDF-files
 * 
 *  Created on: 26.05.2014
 *      Author: Arkadius Pawlak
**************************************************************/

public class RDFFileException extends Exception{
	   //Parameterless Constructor
    public RDFFileException() {}

    //Constructor that accepts a message
    public RDFFileException(String message)
    {
       super(message);
    }

}
