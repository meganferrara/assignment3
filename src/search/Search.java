package search;

import java.io.*;

import java.util.Hashtable;

/**
 * A search class that will allow us to run queries against the TDT corpus.
 * It supports BOTH boolean and ranked queries.
 * 
 * @author dkauchak
 *
 */
public class Search{
	private Index index;
	private Hashtable<Integer,Document> id2doc = null;
	
	public static void main(String[] args){
		TDTReader reader = new TDTReader("TDT-file");
		ImprovedTokenizer tokenizer = new ImprovedTokenizer();
		TokenProcessor processor = new TokenProcessor();				
		processor.setLowercase(true);
		reader.setTokenizer(tokenizer);
		reader.setTokenProcessor(processor);
		
		Search system = new Search(reader, Index.TERM_MODIFIER.l, Index.DOC_MODIFIER.t, Index.LENGTH_MODIFIER.c);
		
		//Subsitituting the code below for the line above this would result in documents being returned rather
		// than document ids
		// TDTReader reader2 = new TDTReader("TDT-file");
		// reader2.setTokenizer(tokenizer);
		// reader2.setTokenProcessor(processor);
		// Search system = new Search(reader, reader2,
		//		 Index.TERM_MODIFIER.l, Index.DOC_MODIFIER.t, Index.LENGTH_MODIFIER.c);
		
		system.run();
	}
	
	/**
	 * Use this constructor if you want to have document IDs
	 * 
	 * @param reader
	 * @param termModifier
	 * @param docModifier
	 * @param lengthModifier
	 */
	public Search(DocumentReader reader, Index.TERM_MODIFIER termModifier, Index.DOC_MODIFIER docModifier,
			  							 Index.LENGTH_MODIFIER lengthModifier){
		index = new Index(reader, termModifier, docModifier, lengthModifier);		
	}
	
	/**
	 * Use this constructor if you want to have the actual document text returned
	 * 
	 * You should pass two instances of the reader, but that are of the same type
	 * and read from the same file.
	 * NOTE: For now, this is a bit of a hack, but it was the best I could think to do without
	 * making life too complicated
	 * 
	 * @param reader
	 * @param reader2
	 * @param termModifier
	 * @param docModifier
	 * @param lengthModifier
	 */
	public Search(DocumentReader reader, DocumentReader reader2, Index.TERM_MODIFIER termModifier, 
			Index.DOC_MODIFIER docModifier, Index.LENGTH_MODIFIER lengthModifier){
		index = new Index(reader, termModifier, docModifier, lengthModifier);
		
		id2doc = new Hashtable<Integer,Document>();
		
		while( reader2.hasNext() ){
			Document next = reader2.next();
			id2doc.put(next.getDocID(), next);
		}
	}
	
	/**
	 * Run the query interaction system.  The system
	 * waits for a query to be issued and then prints out the result.
	 * 
	 * If the user types "exit" the program ends, otherwise it will continue
	 * to accept queries.
	 * 
	 * Notice that both boolean and ranked queries can be issued.
	 */
	public void run(){
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try{
			String query = in.readLine();
		
			while( !query.equals("exit") ){
				QueryResult result;
				
				if( query.contains("!") ||
					query.contains("AND") ||
					query.contains("OR") ){
					result = index.booleanQuery(query);
				}else{
					result = index.rankedQuery(query);
				}
				
				if( result.size() == 0 ){
					System.out.println("No results");
				}else{
					int[] ids = result.getIDs();
					double[] scores = result.getScores();
					
					for( int i = 0; i < scores.length; i++ ){
						if( id2doc == null ){
							System.out.print(ids[i] + ":" + scores[i] + " ");
						}else{
							System.out.println(id2doc.get(ids[i]));
						}
					}
				
						System.out.println();
				}
				
				
				query = in.readLine();
			}
		}catch(IOException e){
			throw new RuntimeException(e.toString());
		}
	}
}