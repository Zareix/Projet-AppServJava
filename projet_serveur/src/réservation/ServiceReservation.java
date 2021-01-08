package r�servation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application_serveur.Abonne;
import application_serveur.Document;
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

			socketOut.println("Connect� !\nMerci de renseigner votre num�ro de client");

			Abonne ab = null;
			// TODO : factoriser connexion
			// Connexion de l'abonn� avec son num�ro
			while (true) {
				String s = socketIn.readLine();
				int numAbo = -1;
				if (!s.matches("-?\\d+")) {
					socketOut.println("Merci d'entrer un num�ro valide");
				} else {
					numAbo = (int) Integer.valueOf(s);
					for (Abonne abonne : abonnes) {
						if (abonne.getId() == numAbo) {
							ab = abonne;
							break;
						}
					}
					if (ab == null)
						socketOut.println("Ce num�ro d'abonn� n'est pas reconnu");
					else
						break;
				}
			}

			socketOut.println("Bienvenue " + ab.getNom() + "\nVoici la liste des documents :");

			// Affichage des documents
			for (Document doc : documents) {
				socketOut.println("  - " + doc);
			}
			socketOut.println(
					"Veuillez saisir le num�ro du document que vous souhaitez retourner\nTapez \"terminer\" pour mettre fin au service d'emprunt");
			socketOut.println("finliste");

			// R�servation d'un documents
			while (true) {
				String s = socketIn.readLine();
				if (s.equalsIgnoreCase("terminer")) {
					socketOut.println("Merci d'avoir utiliser le service de r�servation");
					client.close();
				}
				int numDoc = -1;
				boolean docFound = false;
				if (s.matches("-?\\d+")) {
					numDoc = (int) Integer.valueOf(s);
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
						socketOut.println("Ce num�ro de document n'existe pas");
				} else {
					socketOut.println("Merci de rentrer un num�ro valide");
				}
			}
		} catch (IOException e) {
			// Fin service retour
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
