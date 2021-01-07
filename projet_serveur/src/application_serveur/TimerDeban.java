package application_serveur;

import java.util.TimerTask;

/**
 * D�bannit un abonn� apr�s le temps choisi
 *
 * @see TimerTask
 * @see Abonne
 */
public class TimerDeban extends TimerTask {
	private Abonne abonne;

	public TimerDeban(Abonne ab) {
		this.abonne = ab;
	}

	@Override
	public void run() {
		this.abonne.debannir();
	}

}
