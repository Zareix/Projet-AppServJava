package emprunt;

import java.util.TimerTask;

import application_serveur.Abonne;

/**
 * Effectue le bannissement de l'abonn� si celui-ci met trop de temps � rendre
 * un document
 * 
 * @see TimerTask
 * @see Abonne
 */
public class TimerEmprunt extends TimerTask {
	private Abonne abonne;

	public TimerEmprunt(Abonne ab) {
		this.abonne = ab;
	}

	@Override
	public void run() {
		this.abonne.bannir();
	}

}
