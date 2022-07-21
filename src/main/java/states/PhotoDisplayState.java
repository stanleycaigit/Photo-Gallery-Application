package states;

import com.example.android31.MainActivity;

import classes.Album;
import classes.Photo;

public class PhotoDisplayState extends State {
	private final Album openAlbum;
	private int openAlbumIndex;
	private String selectedTag;

	public PhotoDisplayState( MainActivity application, Album album ) {
		super( application );
		this.openAlbum = album;
		if ( album.getPhotos().size() == 0 ) {
			this.openAlbumIndex = -1;
		}
		else {
			this.openAlbumIndex = 0;
		}
		selectedTag = "";
	}

	public PhotoDisplayState( MainActivity application, Album album, int openAlbumIndex ) {
		this( application, album );
		this.openAlbumIndex = openAlbumIndex;
		selectedTag = "";
	}

	public void handleSignal( String source ) {
		if ( source.endsWith( "nextPhotoButton" ) ) {
			getApplication().goToNextPhoto();
		}
		else if ( source.endsWith( "previousPhotoButton" ) ) {
			getApplication().goToPreviousPhoto();
		}
		else if ( source.endsWith( "deletePhotoButton" ) ) {
			getApplication().deleteCurrentPhoto();
		}
		else if ( source.endsWith( "addTagButton" ) ) {
			getApplication().addTagToPhoto();
		}
		else if ( source.endsWith( "deleteTagButton" ) ) {
			getApplication().deleteSelectedTag();
		}
		else if ( source.endsWith( "movePhotoButton" ) ) {
			getApplication().movePhoto();
		}
		else if ( source.endsWith( "renameAlbumButton" ) ) {
			getApplication().renameAlbum();
		}
		else if ( source.endsWith(( "deleteAlbumButton" ) ) ) {
			getApplication().deleteAlbum();
		}
	}

	public Album getOpenAlbum() {
		return openAlbum;
	}

	public int getOpenAlbumIndex() {
		return openAlbumIndex;
	}

	public boolean hasNextPhoto() {
		return openAlbumIndex + 1 < openAlbum.getPhotos().size();
	}

	public void nextPhoto() {
		if ( openAlbumIndex + 1 < openAlbum.getPhotos().size() ) {
			openAlbumIndex++;
		}
	}

	public boolean hasPreviousPhoto() {
		return openAlbumIndex > 0;
	}

	public void previousPhoto() {
		if ( openAlbumIndex > 0 ) {
			openAlbumIndex--;
		}
	}

	public void deleteCurrentPhoto() {
		if ( openAlbumIndex == -1 ) {
			return;
		}

		if ( openAlbumIndex < openAlbum.getPhotos().size() - 1 ) {
			openAlbum.getPhotos().remove( openAlbumIndex );
		}
		else {
			openAlbum.getPhotos().remove( openAlbumIndex );
			openAlbumIndex--;
		}
	}

	public void addPhotoToAlbum( Photo photo ) {
		if ( openAlbum.addPhoto( photo ) ) {
			openAlbumIndex = openAlbum.getPhotos().size() - 1;
		}
	}

	public Photo getCurrentPhoto() {
		if ( openAlbumIndex == -1 ) {
			return null;
		}
		else {
			return openAlbum.getPhotos().get( openAlbumIndex );
		}
	}

	public String getSelectedTag() {
		return selectedTag;
	}

	public void setSelectedTag( String selectedTag ) {
		this.selectedTag = selectedTag;
	}
}
