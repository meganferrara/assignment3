package search;

import java.util.ArrayList;

/**
 * Tokenizer interface.
 * 
 * @author dkauchak
 *
 */
public interface Tokenizer {
	/**
	 * Break the input text into words.
	 * 
	 * @param text
	 * @return The words in the input text in their original order
	 */
	public ArrayList<String> tokenize(String text);
}
