package r�servation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application_serveur.Abonne;
import application_serveur.Documents;
import exception.ReservationException;

public class ServiceReservation implements Runnable {
	static List<Abonne> abonnes = new ArrayList<>();
	static List<Documents> documents = new ArrayList<>();

	private Socket client;

	public ServiceReservation(Socket s) {
		this.client = s;
	}

	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);

			socketOut.println("Connexion au service de r�servation.\nMerci de renseigner votre num�ro de client");

			Abonne ab = null;
			// TODO : factoriser connexion
			// Connexion
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
			for (Documents doc : documents) {
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
					try {
						client.close();
					} catch (IOException e2) {
					}
					;
				}
				int numDoc = -1;
				boolean docFound = false;
				if (s.matches("-?\\d+")) {
					numDoc = (int) Integer.valueOf(s);
					for (Documents doc : documents) {
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

	public static void setAbonnes(List<Abonne> a) {
		ServiceReservation.abonnes = a;

	}

	public static void setDocuments(List<Documents> d) {
		ServiceReservation.documents = d;
	}

}
