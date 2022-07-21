package states;

import com.example.android31.MainActivity;

import java.util.ArrayList;

import classes.Photo;
/**
 * The state of being in the search results display
 * 
 * @author Eric Zhang
 * @author Stanley Cai
 *
 */
public class SearchResultsDisplayState extends State {

	private ArrayList<Photo> photos;
	private int photosIndex;
	private String searchInput;

	public SearchResultsDisplayState(MainActivity application, String searchInput, ArrayList<Photo> photos ) {
		super( application );
		this.photos = photos;
		this.searchInput = searchInput;
		if ( photos.size() == 0 ) {
			this.photosIndex = -1;
		}
		else {
			this.photosIndex = 0;
		}
	}


	@Override
	public void handleSignal( String source ) {
		if ( source.endsWith( "nextPhotoButton" ) ) {
			getApplication().goToNextPhoto();
		}
		else if ( source.endsWith( "previousPhotoButton" ) ) {
			getApplication().goToPreviousPhoto();
		}
	}

	/**
	 * Check if the currently pointed photo in the search results has a next photo
	 * @return true if there is a next photo; false otherwise
	 */
	public boolean hasNextPhoto() {
		return photosIndex + 1 < photos.size();
	}

	/**
	 * Increment the photo pointer to point to the next photo in the search results
	 */
	public void nextPhoto() {
		if ( photosIndex + 1 < photos.size() ) {
			photosIndex++;
		}
	}

	/**
	 * Check if the currently pointed photo in the search results has a previous photo
	 * @return true if there is a previous photo; false otherwise
	 */
	public boolean hasPreviousPhoto() {
		return photosIndex > 0;
	}

	/**
	 * Decrement the photo pointer to point to the previous photo in the search results
	 */
	public void previousPhoto() {
		if ( photosIndex > 0 ) {
			photosIndex--;
		}
	}
	
	/**
	 * Retrieve the current photo pointed in the search results
	 * @return photo specifically pointed to in the search results
	 */
	public Photo getCurrentPhoto() {
		if ( photosIndex == -1 ) {
			return null;
		}
		else {
			return photos.get( photosIndex );
		}
	}
	
	/**
	 * Retrieve the entire arraylist of relevant photos according to the specific search input
	 * @return arraylist of relevant photos
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	/**
	 * Retrieve the search input for finding specific photos
	 * @return search input
	 */
	public String getSearchInput() {
		return searchInput;
	}
}
