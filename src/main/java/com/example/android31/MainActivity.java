package com.example.android31;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import classes.Album;
import classes.Photo;
import states.AlbumDisplayState;
import states.PhotoDisplayState;
import states.SearchResultsDisplayState;
import states.HomeScreenState;
import states.State;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    private State state;
    private ArrayList<Album> allAlbums;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
       super.onCreate( savedInstanceState );

       String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
       requestPermissions( permissions, 101 );

       loadData();
       updateState( state );
    }
    
    public void updateState( State newState ) {
        state = newState;
        updateContentView();
        updateEventListeners();
    }

    public void updateContentView() {
        if ( state instanceof HomeScreenState ) {
            setContentView( R.layout.home_screen );
            updateAlbumListView();
        }
        else if ( state instanceof AlbumDisplayState ) {
            setContentView( R.layout.album_display );
            TextView albumName = findViewById( R.id.albumName );
            albumName.setText( ((AlbumDisplayState) state).getOpenAlbum().getName() );
            populateThumbnailDisplay();
        }
        else if ( state instanceof PhotoDisplayState ) {
            setContentView( R.layout.photo_display );
            displayCurrentPhoto();
        }
        else {
            setContentView( R.layout.search_results_display );
            TextView title = findViewById( R.id.title );
            title.setText( ( (SearchResultsDisplayState) state).getSearchInput() );
            displayCurrentPhoto();
        }
    }
    public void updateEventListeners() {
        Button[] buttons;
        // Add event listeners to buttons
        if ( state instanceof HomeScreenState ) {
            buttons = new Button[] { findViewById( R.id.createNewAlbumButton ), findViewById( R.id.searchButton ) };

            ListView albumListView = findViewById( R.id.albumListView );
            MainActivity mainActivity = this;
            albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    updateState( new AlbumDisplayState( mainActivity, allAlbums.get( position ) ) );
                    saveData();
                }
            });
        }
        else if ( state instanceof AlbumDisplayState ) {
            buttons = new Button[] { findViewById( R.id.addPhotoButton ), findViewById( R.id.renameAlbumButton ),
                    findViewById( R.id.deleteAlbumButton ) };
            GridView thumbnailDisplay = findViewById( R.id.thumbnailDisplay );
            MainActivity mainActivity = this;
            thumbnailDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                    updateState( new PhotoDisplayState( mainActivity, getOpenAlbum(), position ) );
                    saveData();
                }
            });
        }
        else if ( state instanceof PhotoDisplayState ) {
            buttons = new Button[] {
                findViewById( R.id.previousPhotoButton ), findViewById( R.id.deletePhotoButton ),
                findViewById( R.id.nextPhotoButton ), findViewById( R.id.addTagButton ), 
                findViewById( R.id.deleteTagButton ), findViewById( R.id.movePhotoButton ), 
                };

                ListView tagListView = findViewById( R.id.tagListView );
                tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                        String selectedTag = (String) tagListView.getItemAtPosition( position );
                        setSelectedTag( selectedTag );
                    }
                });
        }
        else {
            buttons = new Button[] { findViewById( R.id.previousPhotoButton ), findViewById( R.id.nextPhotoButton ) };
        }

        for ( Button button : buttons ) {
            activateButton( button );
        }
    }

    public void activateButton( Button button ) {
        button.setOnClickListener( e -> {
            state.handleSignal( button.getResources().getResourceName( button.getId() ) );
        });
    }

    // Start HomeScreenState methods
    private void updateAlbumListView() {
        String[] albumsToString = new String[allAlbums.size()];
        for (int i = 0; i < allAlbums.size(); i++) {
            albumsToString[i] = allAlbums.get( i ).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.album, albumsToString );
        ListView albumListView = findViewById( R.id.albumListView );
        albumListView.setAdapter( adapter );
    }
    
    public void openSelectedAlbum() {
        if ( state instanceof HomeScreenState ) {
            Album selectedAlbum = getSelectedAlbum();
            if ( selectedAlbum != null ) {
                state = new PhotoDisplayState( this, selectedAlbum );
                updateState( state );
            }

            saveData();
        }
    }
    public void createNewAlbum() {
        if ( state instanceof HomeScreenState ) {
            EditText newAlbumInput = findViewById( R.id.newAlbumInput );
            String newAlbumName = newAlbumInput.getText().toString().trim();
            if ( newAlbumName.length() > 0 ) {
                Album newAlbum = new Album( newAlbumInput.getText().toString() );
                for ( Album existingAlbum : allAlbums ) {
                    if ( existingAlbum.compareTo( newAlbum ) == 0 ) {
                        return;
                    }
                }
                
                allAlbums.add( newAlbum );
                updateAlbumListView();
                newAlbumInput.setText( "" );
            }
        }

        saveData();
    }

    private Album getSelectedAlbum() {
        if ( state instanceof HomeScreenState ) {
            ListView albumListView = findViewById( R.id.albumListView );
            String selectedAlbumName = (String) albumListView.getSelectedItem();
            if ( selectedAlbumName != null ) {
                for ( Album album : allAlbums ) {
                    if ( selectedAlbumName.compareTo( album.getName() ) == 0 ) {
                        return album;
                    }
                }
            }
        }

        return null;
    }

    public void searchPhotos() {
        if ( state instanceof HomeScreenState ) {
            EditText searchInput = findViewById( R.id.searchInput );
            String input = searchInput.getText().toString().trim();
            ArrayList<Photo> matches = new ArrayList<>();

            String[] data = input.split( " " );
            try {
                if ( data.length == 1 ) {
                    String tag = data[0];
                    data = tag.split( "=" );
                    String key = data[0].substring( 1, data[0].length() - 1 );
                    String value = data[1].substring( 1, data[1].length() - 1 );
    
                    for ( Album album : allAlbums ) {
                        for ( Photo photo : album.getPhotos() ) {
                            if ( photo.matchesTag( key, value ) ) {
                                matches.add( photo );
                            }
                        }
                    }
                }
                else if ( data.length == 3 ) {
                    String tag1 = data[0];
                    String conjunction = data[1];
                    String tag2 = data[2];
    
                    data = tag1.split( "=" );
                    String key1 = data[0].substring( 1, data[0].length() - 1 );
                    String value1 = data[1].substring( 1, data[1].length() - 1 );
    
                    data = tag2.split( "=" );
                    String key2 = data[0].substring( 1, data[0].length() - 1 );
                    String value2 = data[1].substring( 1, data[1].length() - 1 );
    
                    if ( conjunction.equalsIgnoreCase( "and" ) ) {
                        for ( Album album : allAlbums ) {
                            for ( Photo photo : album.getPhotos() ) {
                                if ( photo.matchesTag( key1, value1 ) && photo.matchesTag( key2, value2 ) ) {
                                    matches.add( photo );
                                }
                            }
                        }
                    }
                    else if ( conjunction.equalsIgnoreCase( "or" ) ) {
                        for ( Album album : allAlbums ) {
                            for ( Photo photo : album.getPhotos() ) {
                                if ( photo.matchesTag( key1, value1 ) || photo.matchesTag( key2, value2 ) ) {
                                    matches.add( photo );
                                }
                            }
                        }
                    }
                    else {
                        return;
                    }
                }
                else {
                    return;
                }

                updateState( new SearchResultsDisplayState( this, input, matches ) );
            }
            catch ( StringIndexOutOfBoundsException e ) {
            }
            
        }
    }
    // End HomeScreenState methods (not done yet)

    // Start AlbumDisplayState methods
    // https://stackoverflow.com/questions/14110163/getting-image-thumbnail-in-android
    public void populateThumbnailDisplay() {
        GridView thumbnailDisplay = findViewById( R.id.thumbnailDisplay );
        thumbnailDisplay.setAdapter( new ImageAdapterGridView( this ) );
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context context;
        final int thumbnailSize = 180;

        public ImageAdapterGridView( Context context ) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return getOpenAlbum().getPhotos().size();
        }
        @Override
        public Object getItem( int i ) {
            return null;
        }
        @Override
        public long getItemId( int i ) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent ) {
            ImageView photoView;

            if ( convertView == null ) {
                photoView = new ImageView( context );
                Photo photo = getOpenAlbum().getPhotos().get( position );
                Bitmap bitmap = ThumbnailUtils.extractThumbnail( BitmapFactory.decodeFile( photo.getFileAbsolutePath() ),
                    thumbnailSize, thumbnailSize );
                photoView.setImageBitmap( bitmap );
            } else {
                photoView = (ImageView) convertView;
            }
            return photoView;
        }
    }


    public void addPhotoToAlbum() {
        if ( state instanceof AlbumDisplayState ) {
            Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( intent, 3 );
        }
    }
    protected void onActivityResult( int requestCode, int resultCode, final Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK && data != null ) {
            Uri selectedData = data.getData();
            Photo newPhoto = new Photo( pathFromUri( selectedData ) );
            for ( Photo existingPhoto : getOpenAlbum().getPhotos() ) {
                if ( existingPhoto.equals( newPhoto ) ) {
                    return;
                }
            }

            AlbumDisplayState tempState = (AlbumDisplayState) state;
            tempState.getOpenAlbum().addPhoto( newPhoto );

            populateThumbnailDisplay();
        }

        saveData();
    }
    private String pathFromUri( Uri uri ) {
        Cursor cursor = getContentResolver().query( uri, null, null, null, null );
        String result = null;
        if ( cursor.moveToFirst() ) {
            int columnIndex = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
            result = cursor.getString( columnIndex );
        }

        cursor.close();
        return result;
    }

    public void renameAlbum() {
        if ( state instanceof AlbumDisplayState ) {
            EditText renameAlbumInput = findViewById( R.id.renameAlbumInput );
            String newAlbumName = renameAlbumInput.getText().toString().trim();
            if ( newAlbumName.length() > 0 ) {
                Album openAlbum = getOpenAlbum();
                for ( Album existingAlbum : allAlbums ) {
                    if ( existingAlbum != openAlbum && newAlbumName.compareToIgnoreCase( existingAlbum.getName() ) == 0 ) {
                        return;
                    }
                }
                openAlbum.setName( newAlbumName );
                renameAlbumInput.setText( "" );

                TextView albumName = findViewById( R.id.albumName );
                albumName.setText( openAlbum.getName() );
            }

            saveData();
        }


    }
    public void deleteAlbum() {
        if ( state instanceof AlbumDisplayState ) {
            allAlbums.remove( getOpenAlbum() );
            updateState( new HomeScreenState( this ) );

            saveData();
        }
    }
    // End AlbumDisplayState methods

    @Override
    public void onBackPressed() {
        if ( state instanceof AlbumDisplayState || state instanceof PhotoDisplayState
                || state instanceof SearchResultsDisplayState ) {
            onBackButtonClick();
        }
        else {
            super.onBackPressed();
        }
    }

    public void onBackButtonClick() {
        if ( state instanceof AlbumDisplayState || state instanceof SearchResultsDisplayState ) {
            updateState( new HomeScreenState( this ) );
            saveData();
        }
        else if ( state instanceof PhotoDisplayState ) {
            updateState( new AlbumDisplayState( this, ( (PhotoDisplayState) state ).getOpenAlbum() ) );
            saveData();
        }
    }

    // Start PhotoDisplayState and SearchPhotoResultsDisplay methods
    public int getOpenAlbumIndex() {
        if ( state instanceof PhotoDisplayState ) {
            return ( (PhotoDisplayState) state ).getOpenAlbumIndex();
        } else {
            return -1;
        }
    }
    public void displayCurrentPhoto() {
        if ( state instanceof PhotoDisplayState ) {
            PhotoDisplayState tempState = (PhotoDisplayState) state;
            Photo currentPhoto = tempState.getCurrentPhoto();

            ImageView photoView = findViewById( R.id.photoView );
            ListView tagListView = findViewById( R.id.tagListView );
            if ( currentPhoto != null ) {
                Bitmap bitmap = BitmapFactory.decodeFile( currentPhoto.getFileAbsolutePath() );
                photoView.setImageBitmap( bitmap );

                String[] tags = new String[currentPhoto.getPeople().size() + currentPhoto.getLocations().size()];
                int index = 0;
                for ( int i = 0; i < currentPhoto.getPeople().size(); i++ ) {
                    tags[index] = "\"person\":\"" + currentPhoto.getPeople().get( i ) + "\"";
                    index++;
                }
                for ( int i = 0; i < currentPhoto.getLocations().size(); i++ ) {
                    tags[index] = "\"location\":\"" + currentPhoto.getLocations().get( i ) + "\"";
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.tag, tags );
                tagListView.setAdapter( adapter );
            }
            else {
                photoView.setImageResource( 0 );
                tagListView.setAdapter( new ArrayAdapter<>( this, R.layout.tag, new String[] {} ) );
            }

            setSelectedTag( "" );
            saveData();
        }
        else if ( state instanceof SearchResultsDisplayState ) {
            SearchResultsDisplayState tempState = (SearchResultsDisplayState) state;
			Photo currentPhoto = tempState.getCurrentPhoto();

            ImageView photoView = findViewById( R.id.photoView );
			ListView tagListView = findViewById( R.id.tagListView );
            if ( currentPhoto != null ) {
                Bitmap bitmap = BitmapFactory.decodeFile( currentPhoto.getFileAbsolutePath() );
                photoView.setImageBitmap( bitmap );
                photoView.invalidate();

                String[] tags = new String[currentPhoto.getPeople().size() + currentPhoto.getLocations().size()];
                int index = 0;
                for ( int i = 0; i < currentPhoto.getPeople().size(); i++ ) {
                    tags[index] = "\"person\":\"" + currentPhoto.getPeople().get( i ) + "\"";
                    index++;
                }
                for ( int i = 0; i < currentPhoto.getLocations().size(); i++ ) {
                    tags[index] = "\"location\":\"" + currentPhoto.getLocations().get( i ) + "\"";
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.tag, tags );
                tagListView.setAdapter( adapter );
            }
            else {
                photoView.setImageResource( 0 );
                tagListView.setAdapter( new ArrayAdapter<>( this, R.layout.tag, new String[] {} ) );
            }
        }
    }

    public void goToNextPhoto() {
        if ( state instanceof PhotoDisplayState) {
            PhotoDisplayState tempState = (PhotoDisplayState) state;
            if( tempState.hasNextPhoto() ) {
                tempState.nextPhoto();
                displayCurrentPhoto();
            }
        }
        else if ( state instanceof SearchResultsDisplayState ) {
            SearchResultsDisplayState tempState = (SearchResultsDisplayState) state;
            if( tempState.hasNextPhoto() ) {
                tempState.nextPhoto();
                displayCurrentPhoto();
            }
        }
    }
    public void goToPreviousPhoto() {
        if ( state instanceof PhotoDisplayState ) {
            PhotoDisplayState tempState = (PhotoDisplayState) state;
            if(tempState.hasPreviousPhoto() ) {
                tempState.previousPhoto();
                displayCurrentPhoto();
            }
        }
        else if ( state instanceof SearchResultsDisplayState ) {
            SearchResultsDisplayState tempState = (SearchResultsDisplayState) state;
            if( tempState.hasPreviousPhoto() ) {
                tempState.previousPhoto();
                displayCurrentPhoto();
            }
        }
    }
    public void deleteCurrentPhoto() {
        if ( state instanceof PhotoDisplayState){
            PhotoDisplayState tempState = (PhotoDisplayState) state;
            tempState.deleteCurrentPhoto();
            displayCurrentPhoto();
        }
    }


    public void addTagToPhoto() {
        if ( state instanceof PhotoDisplayState) {
            PhotoDisplayState tempState = (PhotoDisplayState) state;
			Photo currentPhoto = tempState.getCurrentPhoto();
			if ( currentPhoto == null ) {
				return;
			}

            Spinner tagKeySpinner = findViewById( R.id.tagKeySpinner );
            EditText tagValueInput = findViewById( R.id.tagValueInput );
            String tagValue = tagValueInput.getText().toString().trim();
            if ( tagValue.length() == 0 ) {
                return;
            }
            tagValue = tagValue.replaceAll( " ", "-" );

            ArrayList<String> list;
            if ( tagKeySpinner.getSelectedItem().toString().equals( "person:" ) ) {
                list = currentPhoto.getPeople();
            }
            else {
                list = currentPhoto.getLocations();
            }
            for ( String existingTagValue : list ) {
                if ( existingTagValue.equalsIgnoreCase( tagValue ) ) {
                    return;
                }
            }
            list.add( tagValue );
            Collections.sort( list, String::compareToIgnoreCase );
            tagValueInput.setText( "" );
            displayCurrentPhoto();
		}
    }

    public void deleteSelectedTag() {
        if ( state instanceof PhotoDisplayState
                && ((PhotoDisplayState)state).getCurrentPhoto() != null ) {
            String selectedTag = ((PhotoDisplayState)state).getSelectedTag();
            if ( selectedTag.equals( "" ) ) {
                return;
            }
            String[] data = selectedTag.split( ":" );
            String key = data[0].substring( 1, data[0].length() - 1 );
            String value = data[1].substring( 1, data[1].length() - 1 );
            ArrayList<String> list;
            if ( key.equals( "person" ) ) {
                list = ((PhotoDisplayState)state).getCurrentPhoto().getPeople();
            }
            else {
                list = ((PhotoDisplayState)state).getCurrentPhoto().getLocations();
            }
            list.remove( value );

            displayCurrentPhoto();
        }
    }

    public void movePhoto() {
        if ( state instanceof PhotoDisplayState ) {
            Photo currentPhoto = ( (PhotoDisplayState) state ).getCurrentPhoto();
            if ( currentPhoto == null ) {
                return;
            }

            EditText albumNameInput = findViewById( R.id.albumNameInput );
            String albumName = albumNameInput.getText().toString().trim();
            for ( Album album : allAlbums ) {
                if ( album.getName().equals( albumName ) && album.addPhoto( currentPhoto ) ) {
                    deleteCurrentPhoto();
                    albumNameInput.setText( "" );
                }
            }
        }
    }

    public Album getOpenAlbum() {
        if ( state instanceof AlbumDisplayState ) {
            return ((AlbumDisplayState) state).getOpenAlbum();
        }
        if ( state instanceof PhotoDisplayState ) {
            return ((PhotoDisplayState) state).getOpenAlbum();
        }
        else {
            return null;
        }
    }

    public String getSelectedTag() {
        if ( state instanceof PhotoDisplayState ) {
            return ((PhotoDisplayState) state).getSelectedTag();
        }
        return null;
    }
    public void setSelectedTag( String selectedTag ) {
        if ( state instanceof PhotoDisplayState ) {
            ((PhotoDisplayState) state).setSelectedTag( selectedTag );
        }
    }

    // End PhotoDisplayState and SearchResultsDisplayState methods

    public void loadData() {
        state = new HomeScreenState( this );
        try {
            File directory = getFilesDir();
            File dataFile = new File( directory, "data.bat" );
            if ( !dataFile.exists() ) {
                dataFile.createNewFile();
            }
            else {
                ObjectInputStream in = new ObjectInputStream(
                        new BufferedInputStream( new FileInputStream( dataFile ) ) );
                allAlbums = (ArrayList<Album>) in.readObject();
                String storedState = in.readUTF();
                if ( storedState.equals( "HomeScreenState" ) ) {
                    state = new HomeScreenState( this );
                }
                else if ( storedState.equals( "AlbumDisplayState" ) ) {
                    Album openAlbum = allAlbums.get( in.readInt() );
                    state = new AlbumDisplayState( this, openAlbum );
                }
                else {
                    Album openAlbum = allAlbums.get( in.readInt() );
                    int openAlbumIndex = in.readInt();
                    state = new PhotoDisplayState( this, openAlbum, openAlbumIndex );
                }
                in.close();
            }
        }
        catch ( ClassNotFoundException e ) {
        }
        catch ( IOException e ) {
            state = new HomeScreenState( this );
            allAlbums = new ArrayList<>();
        }
    }

    public void saveData() {
        try {
            File directory = getFilesDir();

            File dataFile = new File( directory, "data.bat" );
            if ( !dataFile.exists() ) {
                dataFile.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream( new FileOutputStream( dataFile ) ) );
            out.writeObject( allAlbums );
            if ( state instanceof PhotoDisplayState ) {
                out.writeUTF( "PhotoDisplayState" );
                out.writeInt( allAlbums.indexOf( getOpenAlbum() ) );
                out.writeInt ( ((PhotoDisplayState) state).getOpenAlbumIndex() );
            }
            else if ( state instanceof AlbumDisplayState ) {
                out.writeUTF( "AlbumDisplayState" );
                out.writeInt( allAlbums.indexOf( getOpenAlbum() ) );
            }
            else {
                out.writeUTF( "HomeScreenState" );
            }
            out.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}