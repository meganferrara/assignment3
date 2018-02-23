package search;

import java.util.Iterator;

/**
 * an implementation of postings list based on a singly linked list
 * 
 * @author dkauchak edited by LB
 */
public class PostingsList implements QueryResult, Iterable<Posting>{
	private Node head = null;
	private Node tail = null;
	private int occurrences = 0;
	
	/**
	 * From the linked list structure, generate an integer array containing 
	 * all of the document ids.  This will make our life easy when we want to 
	 * print out the ids.  (another option would have been to write an iterator, but
	 * this is easier).
	 * 
	 * @return
	 */
	public int[] getIDs(){
		if( head == null ){
			return null;
		}else{
			int[] ids = new int[occurrences];
			Node temp = head;
			int i = 0;
			
			while( temp != null ){
				ids[i] = temp.getPosting().docID();
				temp = temp.next();
				i++;
			}
			
			return ids;
		}
	}
	
	/**
	 * Since this is for boolean queries, the score should be 1.0 for all
	 * documents that are in this posting list
	 */
	public double[] getScores(){
		int[] ids = getIDs();
		double[] scores = new double[ids.length];
		
		for( int i = 0; i < scores.length; i++ ){
			scores[i] = 1.0;
		}
		
		return scores;
	}
	
	public Iterator<Posting> iterator() {
		return new PostingsIterator(head);
	}
	
	/**
	 * add a document ID to this posting list
	 *
	 * @param docID the docID of the document being added
	 */
	public void addDoc(int docID){
		if( head == null ){
			head = new Node(new Posting(docID));
			tail = head;
		}else{
			tail.setNext(new Node(new Posting(docID)));
			tail = tail.next();
		}
		
		occurrences++;
	}
	
	/**
	 * Given a postings list, return the NOT of the postings list, i.e.
	 * a postings list that contains all document ids not in "list"
	 * 
	 * document IDs should range from 0 to maxDocID
	 * 
	 * @param list the postings list to NOT
	 * @param maxDocID the maximum allowable document ID
	 * @return not of the posting list
	 */
	public static PostingsList not(PostingsList list, int maxDocID){
		PostingsList returnMe = new PostingsList();
		
		int prev_entry = -1;

		Node posting = list.head;
		
		while( posting != null ){
			// add all the docIDs from prev_entry to the current entry
			for( int i = prev_entry+1; i < posting.getPosting().docID(); i++ ){
				returnMe.addDoc(i);
			}
			
			prev_entry = posting.getPosting().docID();
			posting = posting.next();
		}
		
		// add all the remaining entries from the last doc in the posting list to the
		// last available doc
		for( int i = prev_entry+1; i <= maxDocID; i++ ){
			returnMe.addDoc(i);
		}
		
		return returnMe;
	}
	
	/**
	 * Given two postings lists, return a new postings list that contains the AND
	 * of the postings, i.e. all the docIDs that occur in both posting1 and posting2
	 * 
	 * @param posting1
	 * @param posting2
	 * @return the AND of the postings lists
	 */
	public static PostingsList andMerge(PostingsList posting1, PostingsList posting2){
		PostingsList merged = new PostingsList();
		
		if( posting1 != null && posting2 != null &&
			posting1.size() > 0 && posting2.size() > 0 ){
			
			Node p1 = posting1.head;
			Node p2 = posting2.head;
			
			while( p1 != null && p2 != null ){
				if( p1.getPosting().docID() == p2.getPosting().docID() ){
					// we found a match, so add it to the list
					merged.addDoc(p1.getPosting().docID());
					p1 = p1.next();
					p2 = p2.next();
				}else if( p1.getPosting().docID() < p2.getPosting().docID() ){
					// move up p1
					p1 = p1.next();
				}else{
					// move up p2
					p2 = p2.next();
				}
			}
		}
		
		return merged;
	}
	
	/**
	 * Given two postings lists, return a new postings list that contains the OR
	 * of the postings, i.e. all those docIDs that occur in either posting1 and posting2
	 * 
	 * @param posting1
	 * @param posting2
	 * @return the OR of the postings lists
	 */
	public static PostingsList orMerge(PostingsList posting1, PostingsList posting2){
		PostingsList merged = new PostingsList();
		
		Node p1 = null;
		Node p2 = null;
		
		if( posting1 != null ){
			p1 = posting1.head;
		}
		
		if( posting2 != null ){
			p2 = posting2.head;
		}		

		
		while( p1 != null && p2 != null ){
			if( p1.getPosting().docID() == p2.getPosting().docID() ){
				// we found a match, so add it to the list
				merged.addDoc(p1.getPosting().docID());
				p1 = p1.next();
				p2 = p2.next();
			}else if( p1.getPosting().docID() < p2.getPosting().docID() ){
				// move up p1 and add it
				merged.addDoc(p1.getPosting().docID());
				p1 = p1.next();
			}else{
				// move up p2 and add it
				merged.addDoc(p2.getPosting().docID());
				p2 = p2.next();
			}
		}
			
		if( p1 != null && p2 != null ){
			throw new RuntimeException("Houston, we have a problem...");
		}
			
		while( p1 != null ){
			merged.addDoc(p1.getPosting().docID());
			p1 = p1.next();
		}
			
		while( p2 != null ){
			merged.addDoc(p2.getPosting().docID());
			p2 = p2.next();
		}
		
		return merged;
	}
		
	/**
	 * @return the number of docIDs for this posting list
	 */
	public int size(){
		return occurrences;
	}
		
	/**
	 * A private node class for creating a linked list
	 * within the PostingsList class
	 * 
	 * @author dkauchak
	 *
	 */
	protected class Node{
		private Node next = null;
		private Posting posting;
		
		public Node(Posting posting){
			this.posting = posting;
		}
		
		public Node next(){
			return next;
		}
		
		public Posting getPosting(){
			return posting;
		}
		
		public void setNext(Node next){
			this.next = next;
		}
	}
}
