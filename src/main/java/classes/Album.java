package classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable, Comparable<Album> {

	private static final long serialVersionUID = 1L;
	private String name;
	private final ArrayList<Photo> photos;

	public Album( String name ) {
		this.name = name;
		photos = new ArrayList<>();
	}

	public Album( String name, ArrayList<Photo> photos ) {
		this.name = name;
		this.photos = photos;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public boolean deletePhoto( Photo photo ) {
		return photos.remove( photo );
	}

	public boolean addPhoto( Photo photo ) {
		for ( Photo existingPhoto : photos ) {
			if ( existingPhoto.equals( photo ) ) {
				return false;
			}
		}
		photos.add( photo );
		return true;
	}

	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	public boolean equals( Album other ) {
		return name.equalsIgnoreCase( other.name );
	}

	@Override
	public int compareTo( Album other ) {
		return name.compareToIgnoreCase( other.name );
	}

	@Override
	public String toString() {
		return name;
	}
}
