package search;

import java.util.Collections;

import java.util.Hashtable;
import java.util.ArrayList;

/**
 * this holds the actual index and also provides the interface to the search
 * engine for querying the index
 * 
 * @author dkauchak edited by LB
 */

public class Index {
	// possible settings for the term modifier (note these only apply to the document vectors):
	//  n = none
	//  l = logarithm adjusted
	//  b = boolean
	public static enum TERM_MODIFIER {n, l, b};
	
	// possible settings for the term weighting modifier:
	// n = none
	// t = inverse document frequency
	public static enum DOC_MODIFIER {n, t};
	
	// possible settings for the length normalization modifier
	// n = none
	// c = cosine
	public static enum LENGTH_MODIFIER {n, c};
	
	private int maxDocID;
	private Hashtable<String,PostingsList> index;
	
	/**
	 * Creates a new index based on the documents supplied by the document reader.
	 * The different modifiers dictate how the terms are weights, etc.
	 * 
	 * @param reader containing the documents
	 * @param termModifier different term schemes
	 * @param docModifier different document weighting schemes
	 * @param lengthModifier different length normalization schemes
	 */
	public Index(DocumentReader reader, TERM_MODIFIER termModifier, DOC_MODIFIER docModifier, 
			LENGTH_MODIFIER lengthModifier){
	
		// TODO: you'll likely need to modify your constructor to deal with the normalization
		index = combinePostings(collectEntries(reader));
	}
	
	/**
	 * Given a text query, issues the query against the index
	 * and generates the set of ranked results
	 * 
	 * @param textQuery the text query
	 * @return the ranked results
	 */
	public VectorResult rankedQuery(String textQuery){
		// TODO: Implement this functionality
		return null;
	}

	/**
	 *  Given a boolean query (see the handout for what types of boolean
	 *  queries are valid), return a PostingsList containing the document
	 *  IDs that match the query.  If no documents match, you should still return a
	 *  PostingsList, but it will not have any document ids.
	 * 
	 * @param textQuery
	 * @return
	 */
	public PostingsList booleanQuery(String textQuery){
		ArrayList<Object> process = getPostingsLists(textQuery);
		
		while( process.size() != 1 ){
			// remove the last three entries
			// perform the appropriate operation
			// and add the result back on to the process queue
			
			PostingsList p1 = (PostingsList)process.remove(process.size()-1);
			BooleanQueryEntry connectorEntry = (BooleanQueryEntry)process.remove(process.size()-1);
			PostingsList p2 = (PostingsList)process.remove(process.size()-1);
			
			if( connectorEntry.isAnd() ){
				process.add(PostingsList.andMerge(p1, p2));
			}else{
				process.add(PostingsList.orMerge(p1, p2));
			}
		}
		
		return (PostingsList)process.get(0);
	}
	
	/**
	 * Grab all of the query terms and "not" any variables that we need to
	 * the process array will consist of alternating PostingsList entries
	 * and BooleanQueryEntry entries
	 * 
	 * @param textQuery
	 * @return
	 */
	private ArrayList<Object> getPostingsLists(String textQuery){
		ArrayList<BooleanQueryEntry> query = BooleanQueryEntry.processQuery(textQuery);

		
		ArrayList<Object> process = new ArrayList<Object>(query.size());
		
		for( BooleanQueryEntry entry: query ){
			if( entry.isConnector() ){
				process.add(entry);
			}else{
				// get the postings list
				if( entry.isNegated() ){
					if( index.containsKey(entry.getTerm()) ){
						process.add(PostingsList.not(index.get(entry.getTerm()), maxDocID));
					}else{
						process.add(PostingsList.not(new PostingsList(), maxDocID));
					}
				}else{
					if( index.containsKey(entry.getTerm()) ){
						process.add(index.get(entry.getTerm()));
					}else{
						process.add(new PostingsList());
					}
				}
			}
		}
		
		return process;
	}
	
	/**
	 * Read through the documents and generate the term/docID pairs as
	 * the first step in creating the index
	 * 
	 * @param reader
	 * @return
	 */
	private ArrayList<docEntry> collectEntries( DocumentReader reader ){
		ArrayList<docEntry> pairs = new ArrayList<docEntry>();

		while( reader.hasNext() ){
			Document doc = reader.next();
			int docID = doc.getDocID();

			for( String token: doc.getText() ){
				pairs.add(new docEntry(token, docID));
			}

			/*if( docID % 100 == 0 ){
				System.out.println(docID);
			}*/

			if( docID > maxDocID ){
				maxDocID = docID;
			}
		}

		return pairs;
	}

	/**
	 * Given a list of docEnty objects generate postings list for these entries
	 * (the second step in creating the index)
	 * 
	 * @param pairs
	 * @return
	 */
	private Hashtable<String,PostingsList> combinePostings(ArrayList<docEntry> pairs){
		// stable sort the pairs by term
		Collections.sort(pairs);

		// create the actual postings list
		Hashtable<String,PostingsList> finalIndex = new Hashtable<String,PostingsList>();

		String prevTerm = "";
		int prevDocID = -1;
		PostingsList currentList = null;

		for( docEntry entry: pairs ){
			if( !entry.getToken().equals(prevTerm) ){
				if( currentList != null ){
					finalIndex.put(prevTerm, currentList);
				}

				prevTerm = entry.getToken();
				currentList = new PostingsList();
				currentList.addDoc(entry.getDocID());
			}else if( entry.getDocID() != prevDocID ){
				// add another docID to the postings list
				currentList.addDoc(entry.getDocID());
			}

			prevDocID = entry.getDocID();
		}

		// add the final postings list
		finalIndex.put(prevTerm, currentList);

		return finalIndex;
	}

	/**
	 * The docEntry class will be used to keep track of an entry as we parse 
	 * through the text of the form:
	 *     token, docID
	 * 
	 * @author dkauchak
	 *
	 */
	private class docEntry implements Comparable<docEntry>{
		private int docID;
		private String token;

		public docEntry(String token, int docID){
			this.docID = docID;
			this.token = token;
		}


		public int getDocID() {
			return docID;
		}

		public String getToken() {
			return token;
		}


		public int compareTo(docEntry anotherDoc) {
			return token.compareTo(anotherDoc.getToken());

			// the version below will work with any sorting algorithm
			// According to the documentation, the sorting is stable,
			// so we only need sort on the token
			/*int returnMe = token.compareTo(anotherDoc.getToken());

		if( returnMe == 0 ){
			// if they're equal, break ties based on docID
			if( docID > anotherDoc.getDocID() ){
				returnMe = 1;
			}else if( docID < anotherDoc.getDocID() ){
				returnMe = -1;
			}
		}

		return returnMe;*/
		}

		public String toString(){
			return token + "\t" + docID;
		}
	}
}
