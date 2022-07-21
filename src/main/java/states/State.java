package states;

import com.example.android31.MainActivity;

import java.io.Serializable;

/**
 * Framework for all states to inherit
 * 
 * @author Eric Zhang
 * @author Stanley Cai
 *
 */
public abstract class State implements Serializable {

	private MainActivity application;
	public State( MainActivity application ) {
		this.application = application;
	}

	public abstract void handleSignal( String source );

	public MainActivity getApplication() {
		return application;
	}
}
