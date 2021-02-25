package application_serveur;

import java.util.TimerTask;

/**
 * D�bannit un abonn� apr�s le temps choisi
 *
 * @see TimerTask
 * @see Abonne
 */
public class TimerTaskDeban extends TimerTask {
	private Abonne abonne;

	public TimerTaskDeban(Abonne ab) {
		this.abonne = ab;
	}

	@Override
	public void run() {
		this.abonne.debannir();
	}

}
