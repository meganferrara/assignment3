package search;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * A document reader for the TDT corpus.
 * 
 * @author dkauchak
 * 
 */
public class TDTReader implements DocumentReader{

	private Tokenizer tokenizer = null;
	private TokenProcessor tokenProcessor = null;
	private BufferedReader in;
	private String nextDocText;
	private int nextDocID = 0;
	
	/**
	 * The text file containing the TDT data with documents delimited
	 * by <DOC> ... </DOC>
	 * 
	 * @param documentFile
	 */
	public TDTReader(String documentFile){
		try{
			in = new BufferedReader(new FileReader(documentFile));
			nextDocText = readNextDocText();
		}catch(IOException e){
			throw new RuntimeException("Problems opening file: " + documentFile + "\n" + e.toString());
		}
	}
	
	/**
	 * Set the tokenizer for this reader
	 * 
	 * @param tokenizer
	 */
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	/**
	 * set the token processor for this reader
	 * 
	 * @param tokenProcessor
	 */
	public void setTokenProcessor(TokenProcessor tokenProcessor) {
		this.tokenProcessor = tokenProcessor;
	}

	/**
	 * Are there more documents to be read?
	 */
	public boolean hasNext() {
		return nextDocText != null;
	}

	/**
	 * Get the next document.
	 */
	public Document next(){
		if( !hasNext() ){
			throw new NoSuchElementException();
		}
		
		if( tokenizer == null ){
			throw new RuntimeException("TDTReader::next() - call without setting tokenizer");
		}
		
		ArrayList<String> tokens = tokenizer.tokenize(nextDocText);
		
		if( tokenProcessor != null ){
			tokens = tokenProcessor.process(tokens);
		}
		
		Document returnMe = new Document(nextDocID, tokens);
		nextDocID++;
		
		try{
			nextDocText = readNextDocText();
		}catch(IOException e){
			throw new RuntimeException("Problems reading file\n" + e.toString());
		}
		
		return returnMe;
	}

	/**
	 * Read through the file and extract the text between the next <DOC> and </DOC> tags
	 * @return The text between the next DOC tags or null if no more documents exist
	 * @throws IOException
	 */
	private String readNextDocText() throws IOException{
		String line = in.readLine();
		
		// find the beginning of the document
		while( line != null &&
			   !line.equals("<DOC>") ){
			line = in.readLine();
		}
		
		if( line == null ){
			return null;
		}else{
			StringBuffer buffer = new StringBuffer();
		
			line = in.readLine();
			
			// grab all the text between <DOC> and </DOC>
			while( line != null &&
				   !line.equals("<\\DOC>") ){
				buffer.append(" " + line);
				line = in.readLine();
			}
		
			return buffer.toString();
		}
	}
	
	public void remove() {
		// method is optional
	}
}