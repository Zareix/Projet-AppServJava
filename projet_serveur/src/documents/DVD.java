package documents;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Timer;

import abonne.Abonne;
import application_serveur.Document;
import exception.ReservationException;
import exception.EmpruntException;

/**
 * @see Document
 */
public class DVD implements Document {
	private static final long DUREE_RESERV = 2; // en heures
	private static final long DUREE_EMPRUNT = 2; // en semaines
	private static final int AGE_ADULTE = 16; // �ge minimum requis
	private static final double RISQUE_DEGRADATION = 10; // % de risque de d�gradation d'un doc au rendu

	private int numero;
	private String titre;
	private boolean adulte;

	private Abonne abonne;

	private LocalDateTime dateFinReserv;

	private Timer timer;

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
	 * heure(s) qui retourne le DVD s'il n'a pas �t� emprunt� dans ce lapse de temps
	 * 
	 * @param ab : l'abonn� qui r�serve
	 */
	@Override
	public void reservationPour(Abonne ab) throws ReservationException {
		if (ab.isBanni())
			throw new ReservationException("Vous �tes interdit de r�servation jusqu'au "
					+ ab.getFinBan().getDayOfMonth() + " " + Month.of(ab.getFinBan().getMonthValue()));
		if (adulte)
			if (ab.getAge() < AGE_ADULTE)
				throw new ReservationException("Vous n'avez pas l'�ge requis pour r�server ce DVD");

		synchronized (this) {
			if (this.dateFinReserv != null) {
				if (this.abonne == ab)
					throw new ReservationException("Vous r�serv� d�j� ce DVD jusqu'� : " + this.dateFinReserv.getHour()
							+ "h" + this.dateFinReserv.getMinute());
				else
					throw new ReservationException("Ce DVD est r�serv� par quelqu'un d'autre, jusqu'� : "
							+ this.dateFinReserv.getHour() + "h" + this.dateFinReserv.getMinute());
			}
			if (this.abonne != null) {
				if (this.abonne == ab)
					throw new ReservationException("Vous poss�dez d�j� ce DVD");
				else
					throw new ReservationException("Ce DVD est d�j� emprunt�");
			}

			// Aucun des pr�c�dents donc le DVD est disponible � la r�servation
			this.abonne = ab;
			this.timer = new Timer();
			this.timer.schedule(new TimerTaskReservation(this), DUREE_RESERV * 60 * 60 * 1000); // conversion h en ms
			this.dateFinReserv = LocalDateTime.now().plusHours(2);
		}
	}

	/**
	 * Permet l'emprunt du DVD <br>
	 * Si l'emprunt est impossible : throw une EmpruntException, avec le message
	 * correspondant <br>
	 * Si l'emprunt est effectu� : lance un timer de {@value #DUREE_EMPRUNT}
	 * semaine(s) au bout duquel l'abonn� sera banni ({@link Abonne#bannir()}) s'il
	 * n'a pas encore rendu le DVD
	 * 
	 * @param ab : l'abonn� qui emprunte
	 */
	@Override
	public void empruntPar(Abonne ab) throws EmpruntException {
		if (ab.isBanni())
			throw new EmpruntException("Vous �tes interdit d'emprunt jusqu'au " + ab.getFinBan().getDayOfMonth() + " "
					+ Month.of(ab.getFinBan().getMonthValue()));
		if (adulte)
			if (ab.getAge() < AGE_ADULTE)
				throw new EmpruntException("Vous n'avez pas l'�ge requis pour emprunter ce DVD");

		synchronized (this) {
			if (this.abonne != null) {
				if (this.dateFinReserv != null && this.abonne != ab)
					throw new EmpruntException("Ce DVD est r�serv� par quelqu'un d'autre, jusqu'� : "
							+ this.dateFinReserv.getHour() + "h" + this.dateFinReserv.getMinute());
				else if (this.abonne != ab)
					throw new EmpruntException("Ce DVD est d�j� emprunt�");
				else if (this.dateFinReserv == null)
					throw new EmpruntException("Vous poss�dez d�j� ce DVD");
			}

			// Aucun des pr�c�dents donc le DVD est disponible � l'emprunt
			if (this.timer != null)
				this.timer.cancel();
			this.abonne = ab;
			this.timer = new Timer();
			this.timer.schedule(new TimerTaskEmprunt(this.abonne), DUREE_EMPRUNT * 1000 * 60 * 60 * 24 * 7);
			this.dateFinReserv = null;
		}
	}

	/**
	 * Permet le retour ou l'annulation de la r�servation du DVD <br>
	 * {@value #RISQUE_DEGRADATION}% de risque que le DVD soit rendu d�grad�
	 */
	@Override
	public void retour() {
		synchronized (this) {
			if (this.abonne != null) {
				if (this.timer != null)
					this.timer.cancel();
				if (this.dateFinReserv == null && this.abonne != null && Math.random() * 100 < RISQUE_DEGRADATION)
					this.abonne.bannir();
				this.abonne = null;
				this.dateFinReserv = null;
			}
		}
	}

	@Override
	public String toString() {
		return "DVD : " + this.numero + " " + this.titre + (this.adulte ? " (Pour adulte)" : "");
	}

}
