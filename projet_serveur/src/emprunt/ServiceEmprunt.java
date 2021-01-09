package emprunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application_serveur.Abonne;
import application_serveur.Document;
import application_serveur.ServiceTools;
import exception.EmpruntException;

/**
 * G�re l'emprunt d'un document par un abonn�
 */
public class ServiceEmprunt implements Runnable {
	static List<Abonne> abonnes = new ArrayList<>();
	static List<Document> documents = new ArrayList<>();

	private Socket client;

	public ServiceEmprunt(Socket s) {
		this.client = s;
	}

	@Override
	public void run() {
		try {
			BufferedReader socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter socketOut = new PrintWriter(client.getOutputStream(), true);

			socketOut.println("Connect� !\nMerci de renseigner votre num�ro de client");

			Abonne ab = ServiceTools.connexion(socketIn, socketOut, abonnes);
					
			socketOut.println("Bienvenue " + ab.getNom());

			ServiceTools.affichageDocs(socketOut, documents);

			socketOut.println(
					"Veuillez saisir le num�ro du document que vous souhaitez emprunter. Tapez \"terminer\" pour mettre fin au service d'emprunt");
			
			// Emprunt d'un document
			while (true) {
				String s = socketIn.readLine();
				if (s.equalsIgnoreCase("terminer")) {
					socketOut.println("Merci d'avoir utiliser le service d'emprunt");
					client.close();
				}
				boolean docFound = false;
				if (s.matches("-?\\d+")) {
					int numDoc = Integer.valueOf(s);
					for (Document doc : documents) {
						if (doc.numero() == numDoc) {
							docFound = true;
							try {
								doc.empruntPar(ab);
								socketOut.println("Document emprunt� avec succ�s !");
							} catch (EmpruntException e) {
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
			// Fin service emprunt
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
		ServiceEmprunt.abonnes = a;
	}

	/**
	 * Initialise la liste des documents
	 * 
	 * @param d : liste de documents
	 */
	public static void setDocuments(List<Document> d) {
		ServiceEmprunt.documents = d;
	}

}
