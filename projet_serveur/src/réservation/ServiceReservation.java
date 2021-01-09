package r�servation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import abonne.Abonne;
import application_serveur.Document;
import application_serveur.ServiceTools;
import exception.ReservationException;

/**
 * G�re la r�servation d'un document par un abonn�
 */
public class ServiceReservation implements Runnable {
	static List<Abonne> abonnes = new ArrayList<>();
	static List<Document> documents = new ArrayList<>();

	private Socket client;

	public ServiceReservation(Socket s) {
		this.client = s;
	}

	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);

			socketOut.println("Connect� !\nMerci de renseigner votre num�ro de client.");

			Abonne ab = ServiceTools.connexion(socketIn, socketOut, abonnes);

			socketOut.println("Bienvenue " + ab.getNom());

			ServiceTools.affichageDocs(socketOut, documents);

			socketOut.println(
					"Veuillez saisir le num�ro du document que vous souhaitez r�server. Tapez \"terminer\" pour mettre fin au service de r�servation.");

			// R�servation d'un documents
			while (true) {
				String s = socketIn.readLine();
				if (s.equalsIgnoreCase("terminer")) {
					socketOut.println("Merci d'avoir utiliser le service de r�servation.");
					client.close();
				}
				boolean docFound = false;
				try {
					int numDoc = Integer.parseInt(s);
					for (Document doc : documents) {
						if (doc.numero() == numDoc) {
							docFound = true;
							try {
								doc.reservationPour(ab);
								socketOut.println("Document r�serv� avec succ�s !");
							} catch (ReservationException e) {
								socketOut.println(e.getMessage());
							}
						}
					}
					if (!docFound)
						socketOut.println("Ce num�ro de document n'existe pas.");
				} catch (NumberFormatException e) {
					socketOut.println("Merci de rentrer un num�ro valide.");
				}
			}
		} catch (IOException e) {
			// Fin service r�servation
		}
	}

	protected void finalize() throws Throwable {
		client.close();
	}

	/**
	 * Initialise la liste des abonn�s
	 * 
	 * @param a : liste d'abonn�s
	 */
	public static void setAbonnes(List<Abonne> a) {
		ServiceReservation.abonnes = a;
	}

	/**
	 * Initialise la liste des documents
	 * 
	 * @param d : liste de documents
	 */
	public static void setDocuments(List<Document> d) {
		ServiceReservation.documents = d;
	}

}
