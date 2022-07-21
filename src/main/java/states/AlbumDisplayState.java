package states;

import com.example.android31.MainActivity;

import classes.Album;

public class AlbumDisplayState extends State {
    private final Album openAlbum;

    public AlbumDisplayState( MainActivity application, Album openAlbum ) {
        super( application );
        this.openAlbum = openAlbum;
    }

    public void handleSignal( String source ) {
        if ( source.endsWith( "addPhotoButton" ) ) {
            getApplication().addPhotoToAlbum();
        }
        else if ( source.endsWith( "renameAlbumButton" ) ) {
            getApplication().renameAlbum();
        }
        else if ( source.endsWith( "deleteAlbumButton" ) ) {
            getApplication().deleteAlbum();
        }
    }

    public Album getOpenAlbum() {
        return openAlbum;
    }
}
