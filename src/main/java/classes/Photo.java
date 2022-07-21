package classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileAbsolutePath;

	private ArrayList<String> people;
	private ArrayList<String> locations;

	public Photo( String fileLocation ) {
		this.fileAbsolutePath = fileLocation;
		people = new ArrayList<>();
		locations = new ArrayList<>();
	}

	public ArrayList<String> getPeople() {
		return people;
	}

	public ArrayList<String> getLocations() {
		return locations;
	}

	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}

	public boolean matchesTag( String key, String value ) {
		if ( key.equalsIgnoreCase( "person" ) ) {
			for ( String person : people ) {
				if ( person.toLowerCase().startsWith( value.toLowerCase() ) ) {
					return true;
				}
			}
		}
		else if ( key.equalsIgnoreCase( "location" ) ) {
			for ( String location : locations ) {
				if ( location.toLowerCase().startsWith( value.toLowerCase() ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean equals( Photo other ) {
		return fileAbsolutePath.equalsIgnoreCase( other.fileAbsolutePath);
	}
}
