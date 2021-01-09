package application_serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import abonne.Abonne;

/**
 * Diff�rents outils utilis�s par les services d'emprunt, retour et r�servation.
 * <br>
 * Permet de factoriser le code des services et d'�viter les redondances.
 */
public class ServiceTools {

	/**
	 * Permet � l'utilisateur de se connecter et renvoie l'abonn� qui vient de se
	 * connecter
	 * 
	 * @param socketIn  : le socket de lecture
	 * @param socketOut : le socket d'ecriture
	 * @param abonnes   : la liste des abonn�s
	 * @return : l'abonn� connect�
	 * @throws IOException
	 */
	public static Abonne connexion(BufferedReader socketIn, PrintWriter socketOut, List<Abonne> abonnes)
			throws IOException {
		while (true) {
			String s = socketIn.readLine();
			try {
				int numAbo = Integer.parseInt(s);
				for (Abonne abonne : abonnes) {
					if (abonne.getId() == numAbo)
						return abonne;
				}
				socketOut.println("Ce num�ro d'abonn� n'est pas reconnu");
			} catch (NumberFormatException e) {
				socketOut.println("Merci d'entrer un num�ro valide");
			}
		}
	}

	/**
	 * Envoie dans le socket la liste des documents
	 * 
	 * @param socketOut : le socket d'�criture
	 * @param documents : la liste des documents
	 */
	public static void affichageDocs(PrintWriter socketOut, List<Document> documents) {
		socketOut.println("Voici la liste des documents :");
		String s = "";
		for (Document doc : documents)
			s += "  - " + doc + "\n";
		s += "finliste";
		socketOut.println(s);
	}
}
