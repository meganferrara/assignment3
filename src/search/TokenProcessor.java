package search;

import java.util.ArrayList;
import java.util.HashSet;

import java.io.*;

/**
 * A class for performing various data normalization techniques
 * on tokens.
 * 
 * @author dkauchak
 *
 */
public class TokenProcessor{
	private boolean lowercase = false;
	private boolean stem = false;
	private boolean foldNumbers = false;
	private HashSet<String> stoplist = null; 
	private Porter stemmer = null;
	
	/**
	 * Set whether or not to lowercase the tokens.
	 * 
	 * @param b
	 */
	public void setLowercase(boolean b){
		lowercase = b;
	}
	
	/**
	 * Set whether or not to stem the tokens using the Porter stemmer
	 * @param b
	 */
	public void setStem(boolean b){
		stem = b;
		
		if( stemmer == null ){
			stemmer = new Porter();
		}
	}
	
	/**
	 * Set whether to replace numbers with <NUM> when processing
	 * 
	 * @param b
	 */
	public void setFoldNumbers(boolean b){
		foldNumbers = b;
	}
	
	/**
	 * Set the list of words to use as a stoplist.  If the setStopList method is
	 * not called, then don't do any stoplisting.
	 * 
	 * @param list The list of stop words
	 */
	public void setStopList(ArrayList<String> list){
		stoplist = new HashSet<String>(list.size());
		
		for( String word: list ){
			stoplist.add(word);
		}
	}
	
	/**
	 * Set the list of words to use as a stoplist.  If the setStopList method is
	 * not called, then don't do any stoplisting.
	 * 
	 * @param filename a file containing the list of stop words
	 */
	public void setStopList(String filename){
		stoplist = new HashSet<String>();
		
		try{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			
			String line = in.readLine();
			
			while( line != null ){
				stoplist.add(line);
				
				line = in.readLine();
			}
		}catch(IOException e){
			throw new RuntimeException("Problems processing stoplist file: " + filename + "\n" + e.toString());
		}
	}
	
	/**
	 * Go through the strings in "tokens", apply all normalization techniques
	 * that are enabled and return the new set of tokens.
	 * 
	 * @param tokens
	 * @return The normalized tokens
	 */
	public ArrayList<String> process(ArrayList<String> tokens){
		ArrayList<String> processed_tokens = new ArrayList<String>();
		
		for( String token: tokens ){
			if( stoplist == null ||
				!stoplist.contains(token.toLowerCase()) )
			{
				if( lowercase ){
					token = token.toLowerCase();
				}
			
				if( stem ){
					token = stemmer.stem(token);
				}
				
				if( foldNumbers &&
					token.matches("^[+-]?(\\d+[.,]?)*\\d$") ){
					token = "<NUM>";
				}
				
				processed_tokens.add(token);
			}
		}
		
		return processed_tokens;
	}
}
