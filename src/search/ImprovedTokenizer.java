package search;

import java.util.ArrayList;

/**
 * An improved tokenizer class that uses the following tokenization rules:
 * - tokens are delimited by whitespace
 * - Single quotes at the beginning and end of words should be separate tokens
 * - Numbers should stay together.  A number can start with a `+' or a `-', 
 *   can have any number of digits, commas and periods interspersed, but must 
 *   end in a digit (note this is a more general definition that accepts things like ``192.168.1" 
 *   and other things like ``-129.,24.34").
 * - An abbreviation is any single letter followed by a period repeated 2 or more times.  
 *   In regex terms, ``(\w\.){2,}".  For example, ``I.B.M.", ``S.A.T." and ``p.m." are all 
 *   valid abbreviations, while ``Mr." or ``I.B" are not.  All abbreviations should have 
 *   the periods removed, i.e. ``I.B.M" becomes ``IBM".
 * - Finally, ``. , ? : ; " ` ( ) % $" should all be treated as separate tokens, regardless of 
 *   where they appear (as long as they don't conflict with the above rules).  So ``$10,000" 
 *   becomes two tokens ``$" and ``10,000" and ``I wondered,is this a test?" becomes 8 
 *   tokens, with the ``," and ``?" as separate tokens.
 * 
 * @author dkauchak
 *
 */
public class ImprovedTokenizer implements Tokenizer{
	/**
	 * Given text, tokenize the text into individual tokens and 
	 * return and ArrayList with those tokens
	 */
	public ArrayList<String> tokenize(String text){	
		// for all of the special punctuation characters, separate them from the words
		text = text.replaceAll("([.,?:;\\\"\\)\\(%\\$`])", " $1 ");
		
		// remove any duplicate white space
		text = text.replaceAll("\\s+", " ");
		
		// remove the starting whitespace
		if( text.startsWith(" ") ){
			text = text.substring(1);
		}
		
		// remove trailing whitespace
		if( text.endsWith(" ") ){
			text = text.substring(0, text.length()-1);
		}
		
		// now split the words based on whitespace
		String[] temp_tokens = text.split(" ");
		
		// do one pass to take care of single quotes
		ArrayList<String> pass_one = new ArrayList<String>();
		
		for( String temp: temp_tokens ){
			while( temp.startsWith("'") ){
				pass_one.add("'");
				temp = temp.substring(1);
			}
		
			// handle ending single quotes
			int trailing_single_quotes = 0;
		
			while( temp.endsWith("'") ){
				trailing_single_quotes++;
				temp = temp.substring(0, temp.length()-1);
			}
			
			pass_one.add(temp);
			
			for( int i = 0; i < trailing_single_quotes; i++ ){
				pass_one.add("'");
			}
		}
		
		// do another pass to handle abbreviations
		ArrayList<String> pass_two = new ArrayList<String>();
		
		for( int i = 0; i < pass_one.size(); i++ ){
			int j = i;
			
			while( j+1 < pass_one.size() &&
				   pass_one.get(j).length() == 1 &&
				   pass_one.get(j+1).equals(".") ){
				j += 2;
			}
			
			if( j - i > 2 ){
				// we've found an abbreviation
				String abbrev = "";
				
				for( int k = i; k < j; k+= 2){
					abbrev += pass_one.get(k);
				}
				
				pass_two.add(abbrev);
				
				i = j-1;
			}else{
				pass_two.add(pass_one.get(i));
			}
		}
		
		
		// do one final pass and put numbers back together and
		ArrayList<String> tokens = new ArrayList<String>();
		
		String number = "";
				
		for( String temp: pass_two ){			
			// technically speaking, this approach will also end up tokenizing things like:
			// a.1000, etc., but those are rare enough, we won't worry about them
			if( temp.matches("^\\d+$") ||
				temp.equals(".") ||
				temp.equals(",") ||
				(number.length() == 0 &&
				 temp.matches("^[+-]?\\d+$"))){
				number += temp;
			}else{		
				if( !number.equals("")){	
					process_number(number, tokens);
					number = "";
				}
				
				if( !temp.equals("") ){
					tokens.add(temp);
				}
			}			
		}
		
		if( !number.equals("")){
			process_number(number, tokens);
		}
		
		return tokens;
	}
	
	/**
	 * Deal with the putting back together numbers from our token string.
	 * 
	 * @param number 
	 * @param tokens the set of tokens to add to
	 */
	private void process_number(String number, ArrayList<String> tokens){
		ArrayList<String> end = new ArrayList<String>();
		
		while( number.endsWith(".") || 
			   number.endsWith(",") ){
			
			end.add(number.substring(number.length()-1, number.length()));
			number = number.substring(0, number.length()-1);
		}
		
		if( !number.equals("") ){
			tokens.add(number);
		}
		
		for( int i = end.size()-1; i >= 0; i-- ){
			tokens.add(end.get(i));
		}
	}
	
	/**
	 * Run all of the test cases
	 */
	public void runAllTests(){
		generalTest();
		testNumbers();
		abbrevTest();
		quotesTest();
	}
	
	/**
	 * Test a few different number tokenizations
	 */
	private void testNumbers(){
		String test = "10.000";
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("10.000");
		runTest(test, answer);
		
		test = "1,000,000.00";
		answer = new ArrayList<String>();
		answer.add(test);
		runTest(test, answer);
		
		test = "0.1234";
		answer = new ArrayList<String>();
		answer.add(test);
		runTest(test, answer);
		
		test = "-3.14159";
		answer = new ArrayList<String>();
		answer.add(test);
		runTest(test, answer);
		
		test = "+3.14159";
		answer = new ArrayList<String>();
		answer.add(test);
		runTest(test, answer);
	}
	
	/**
	 * Some general tokenization tests
	 */
	private void generalTest(){
		String test = "To be, or not to be? That is the question.";
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("To");
		answer.add("be");
		answer.add(",");
		answer.add("or");
		answer.add("not");
		answer.add("to");
		answer.add("be");
		answer.add("?");
		answer.add("That");
		answer.add("is");
		answer.add("the");
		answer.add("question");
		answer.add(".");
		runTest(test, answer);
		
		test = "The N.Y.S.E. is up $10,000 or 1%.";
		answer = new ArrayList<String>();
		answer.add("The");
		answer.add("NYSE");
		answer.add("is");
		answer.add("up");
		answer.add("$");
		answer.add("10,000");
		answer.add("or");
		answer.add("1");
		answer.add("%");
		answer.add(".");
		runTest(test, answer);
	}
	
	/**
	 * Test a few abbreviations
	 */
	private void abbrevTest(){
		String test = "I.B.M.";
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("IBM");
		runTest(test, answer);
		
		test = "could I, work at i.b.m.?";
		answer = new ArrayList<String>();
		answer.add("could");
		answer.add("I");
		answer.add(",");
		answer.add("work");
		answer.add("at");
		answer.add("ibm");
		answer.add("?");
		runTest(test, answer);
	}
	
	/**
	 * Test quotations
	 */
	private void quotesTest(){
		String test = "'this is a test'";
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("'");
		answer.add("this");
		answer.add("is");
		answer.add("a");
		answer.add("test");
		answer.add("'");
		runTest(test, answer);
		
		test = "'10,000.00'";
		answer = new ArrayList<String>();
		answer.add("'");
		answer.add("10,000.00");
		answer.add("'");
		runTest(test, answer);
		
		test = "'this isn't a test'";
		answer = new ArrayList<String>();
		answer.add("'");
		answer.add("this");
		answer.add("isn't");
		answer.add("a");
		answer.add("test");
		answer.add("'");
		runTest(test, answer);
		
		test = "'ain't ain't a word, is it?'";
		answer = new ArrayList<String>();
		answer.add("'");
		answer.add("ain't");
		answer.add("ain't");
		answer.add("a");
		answer.add("word");
		answer.add(",");
		answer.add("is");
		answer.add("it");
		answer.add("?");
		answer.add("'");
		runTest(test, answer);
	}
	
	/**
	 * Test helper.  Tokenizes the test string and compares
	 * the result to the answer.  Outputs pass or fail.
	 * 
	 * @param test test string
	 * @param answer the correct tokenization of test
	 */
	private void runTest(String test, ArrayList<String> answer){
		ArrayList<String> result = tokenize(test);
		
		if( !checkResult(result, answer) ){
			System.out.println("Failed: " + test);
			
			for( String s: result ){
				System.out.print(s + " ");
				System.out.println();
			}
		}else{
			System.out.println("Passed: " + test);
		}
	}
	
	/**
	 * Compares two ArrayLists of strings to see if they're the same.
	 * 
	 * @param result
	 * @param answer
	 * @return true if the ArrayLists are the same, false otherwise
	 */
	private boolean checkResult(ArrayList<String> result, ArrayList<String> answer){
		if( result.size() != answer.size() ){
			return false;
		}else{
			boolean returnMe = true;
			
			for( int i = 0; i < result.size(); i++ ){
				if( !result.get(i).equals(answer.get(i)) ){
					returnMe = false;
				}
			}
			
			return returnMe;
		}
	}
}
