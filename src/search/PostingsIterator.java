package search;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class for iterating over postings lists
 * 
 * @author dkauchak
 *
 */
public class PostingsIterator implements Iterator<Posting>{
 
	private PostingsList.Node current;
	
	public PostingsIterator(PostingsList.Node current){
		this.current = current;
	}
	
	public boolean hasNext() {
		return current != null;
	}

	public Posting next() {
		if( hasNext() ){
			Posting temp = current.getPosting();
			current = current.next();
			
			return temp;
		}else{
			throw new NoSuchElementException();
		}
	}

	public void remove() {
		// optional, so we'll ignore requests to delete
	}
}
