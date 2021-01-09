package application_serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import Abonne.Abonne;

/**
 * Diff�rents outils utilis�s par les services d'emprunt, retour et r�servation.
 * <br>
 * Permet de factoriser le code des services et d'�viter les redondances.
 */
public class ServiceTools {
	public static Abonne connexion(BufferedReader socketIn, PrintWriter socketOut, List<Abonne> abonnes)
			throws IOException {
		while (true) {
			String s = socketIn.readLine();
			if (!s.matches("-?\\d+")) {
				socketOut.println("Merci d'entrer un num�ro valide");
			} else {
				int numAbo = Integer.valueOf(s);
				for (Abonne abonne : abonnes) {
					if (abonne.getId() == numAbo)
						return abonne;
				}
				socketOut.println("Ce num�ro d'abonn� n'est pas reconnu");
			}
		}
	}

	public static void affichageDocs(PrintWriter socketOut, List<Document> documents) {
		socketOut.println("Voici la liste des documents :");
		String s = "";
		for (Document doc : documents)
			s += "  - " + doc + "\n";
		s += "finliste";
		socketOut.println(s);
	}
}
