package states;

import com.example.android31.MainActivity;

import java.util.ArrayList;

import classes.Album;

public class HomeScreenState extends State {
	public HomeScreenState( MainActivity application ) {

		super( application );
	}

	/**
	 * Accept a signal from the source and relay control to the appropriate method.
	 */
	@Override
	public void handleSignal( String source ) {
		if ( source.endsWith( "searchButton" ) ) {
			getApplication().searchPhotos();
		}
		else if ( source.endsWith( "createNewAlbumButton" ) ) {
			getApplication().createNewAlbum();
		}
	}
}
