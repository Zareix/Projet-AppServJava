package exception;

/**
 * Exception en cas d'erreur lors d'une r�servation
 * 
 * @see Exception
 */
@SuppressWarnings("serial")
public class ReservationException extends Exception {
	public ReservationException(String message) {
		super(message);
	}
}
