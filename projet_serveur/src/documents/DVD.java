package documents;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Timer;

import application_serveur.Abonne;
import application_serveur.Documents;
import exception.ReservationException;
import r�servation.TimerReservation;
import exception.EmpruntException;

/**
 * @see Documents
 */
public class DVD implements Documents {
	private static final long DUREE_RESERV = 2; // en heures
	private static final int AGE_ADULTE = 16;
	private int numero;
	private String titre;
	private boolean adulte;

	private Abonne abonne;

	private LocalDateTime dateFinReserv;
	private Timer t = new Timer();

	public DVD(int num, String t, boolean a) {
		this.numero = num;
		this.titre = t;
		this.adulte = a;
	}

	/**
	 * Retourne le num�ro du DVD
	 * 
	 * @return le num�ro du DVD
	 */
	@Override
	public int numero() {
		return this.numero;
	}

	/**
	 * Permet la r�servation du DVD <br>
	 * Si la r�servation est impossible : throw une EmpruntException, avec le
	 * message correspondant <br>
	 * Si la r�servation est effectu�e : lance un timer de {@value #DUREE_RESERV}
	 * heures qui retourne le DVD s'il n'a pas �t� emprunt� dans ce lapse de temps
	 * 
	 * @param ab : l'abonn� qui r�serve
	 */
	@Override
	public void reservationPour(Abonne ab) throws ReservationException {
		synchronized (this) {
			if (adulte)
				if (ab.getAge() < AGE_ADULTE)
					throw new ReservationException("Vous n'avez pas l'age requis pour r�server ce DVD");
			if (this.dateFinReserv != null) {
				if (this.abonne == ab)
					throw new ReservationException("Vous r�serv� d�j� ce DVD");
				throw new ReservationException("Ce DVD est r�serv� jusqu'� : " + this.dateFinReserv.getHour() + "h" + this.dateFinReserv.getMinute());
			}
			if (this.abonne != null)
				throw new ReservationException("Ce DVD est d�j� emprunt�");
			// Aucun des pr�c�dents donc le doc est disponible � la r�servation
			this.abonne = ab;
			this.t = new Timer();
			this.t.schedule(new TimerReservation(this), DUREE_RESERV * 60 * 60 * 1000); // 2h
			this.dateFinReserv = LocalDateTime.now().plusHours(2);
		}
	}

	/**
	 * Permet l'emprunt du DVD <br>
	 * Si l'emprunt est impossible : throw une EmpruntException, avec le message
	 * correspondant
	 * 
	 * @param ab : l'abonn� qui emprunte
	 */
	@Override
	public void empruntPar(Abonne ab) throws EmpruntException {
		synchronized (this) {
			if (adulte)
				if (ab.getAge() < AGE_ADULTE)
					throw new EmpruntException("Vous n'avez pas l'age requis pour emprunter ce DVD");
			if (this.dateFinReserv != null && ab != this.abonne) {
				throw new EmpruntException("Ce DVD est r�serv� jusqu'� : " + this.dateFinReserv.getHour() + "h" + this.dateFinReserv.getMinute());
				}
			if (this.abonne != null) {
				if (this.abonne == ab)
					throw new EmpruntException("Vous poss�d� d�j� ce DVD.");
				throw new EmpruntException("Ce DVD est d�j� emprunt�.");
			}
			// Aucun des pr�c�dents donc le doc est disponible � l'emprunt
			this.abonne = ab;
			ab.addDocuments(this);
			this.t.cancel();
			this.dateFinReserv = null;
		}
	}

	/**
	 * Permet le retour ou l'annulation de la r�servation du DVD
	 */
	@Override
	public void retour() {
		synchronized (this) {
			if (this.abonne != null) {
				this.abonne.retirerDocuments(this);
				this.abonne = null;
				this.dateFinReserv = null;
				this.t.cancel();
			}
		}
	}

	@Override
	public String toString() {
		return "DVD : " + this.numero + " " + this.titre;
	}

}
