package documents;

import java.util.TimerTask;

import Abonne.Abonne;

/**
 * Effectue le bannissement de l'abonn� si celui-ci met trop de temps � rendre
 * un document
 * 
 * @see TimerTask
 * @see Abonne
 */
public class TimerTaskEmprunt extends TimerTask {
	private Abonne abonne;

	public TimerTaskEmprunt(Abonne ab) {
		this.abonne = ab;
	}

	@Override
	public void run() {
		this.abonne.bannir();
	}

}
