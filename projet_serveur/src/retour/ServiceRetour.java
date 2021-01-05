package retour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application_serveur.Abonne;
import application_serveur.Documents;

/**
 * G�re le retour d'un document
 */
public class ServiceRetour implements Runnable {
	static List<Abonne> abonnes = new ArrayList<>();
	static List<Documents> documents = new ArrayList<>();

	private Socket client;

	public ServiceRetour(Socket s) {
		this.client = s;
	}

	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);

			socketOut.println("Connexion au service de retour.\nMerci de renseigner votre num�ro de client");

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

			socketOut.println("Bienvenue " + ab.getNom() + "\nVoici la liste de vos documents :");

			// Affichage des docs de l'abo
			List<Documents> docAbo = ab.getDocuments();
			for (Documents doc : docAbo) {
				socketOut.println("  - " + doc);
			}
			socketOut.println(
					"Veuillez saisir le num�ro du document que vous souhaitez retourner\nTapez \"terminer\" pour mettre fin au service d'emprunt");
			socketOut.println("finliste");

			// Retour d'un document
			while (true) {
				String s = socketIn.readLine();
				if (s.equalsIgnoreCase("terminer")) {
					socketOut.println("Merci d'avoir utiliser le service de retour");
					client.close();
				}
				int numDoc = -1;
				boolean docFound = false;
				if (s.matches("-?\\d+")) {
					numDoc = (int) Integer.valueOf(s);
					for (Documents doc : docAbo) {
						if (doc.numero() == numDoc) {
							docFound = true;
							doc.retour();
							socketOut.println("Document retourn� avec succ�s !");
						}
					}
					if (!docFound)
						socketOut.println("Vous ne poss�dez pas ce documents");
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
		ServiceRetour.abonnes = a;
	}

	/**
	 * Initialise la liste des documents
	 * 
	 * @param d : liste de documents
	 */
	public static void setDocuments(List<Documents> d) {
		ServiceRetour.documents = d;
	}

}
