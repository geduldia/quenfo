package quenfo.de.uni_koeln.spinfo.information_extraction.applications;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import quenfo.de.uni_koeln.spinfo.information_extraction.data.IEType;
import quenfo.de.uni_koeln.spinfo.information_extraction.db_io.IE_DBConnector;
import quenfo.de.uni_koeln.spinfo.information_extraction.workflow.Extractor;

/**
 * @author geduldia
 * 
 *         workflow to match the already validated tools (from tools.txt)
 *         against all as class 3 and/or class 2 classified paragraphs
 * 
 *         input: as class 3 (= applicants profile) and/or class 2 (=job
 *         description) classified paragraphs output: all matching tools
 *         together with their containing sentence
 *
 */
public class MatchTools {

	// Pfad zur Input-DB mit den klassifizierten Paragraphen
	static String paragraphsDB = "src/main/resources/classification/output/ClassifiedParagraphs.db";

	static String extractionsDB = "src/main/resources/information_extraction/output/tools/ExtractedTools.db";

	// Ordner in dem die neue Output-DB angelegt werden soll
	static String outputFolder = "src/main/resources/information_extraction/output/tools/"; 

	// Name der Output-DB
	static String outputDB = "ToolMatches.db";

	// txt-File mit allen bereits validierten Tools
	static File tools = new File("src/main/resources/information_extraction/input/tools/tools.txt");

	// txt-File mit bekannten Extraktionsfehlern
	static File mistakes = new File("src/main/resources/information_extraction/input/tools/mistakes.txt");

	// txt-File zur Speicherung der Match-Statistiken
	static File statisticsFile = new File("src/main/resources/information_extraction/output/tools/matchingStats.txt");

	// Anzahl der Paragraphen aus der Input-DB, gegen die gematcht werden soll
	// (-1 = alle)
	static int maxCount = -1;

	// Falls nicht alle Paragraphen gematcht werden sollen, hier die
	// Startposition angeben
	static int startPos = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		// Verbindung mit Input-DB
		Connection inputConnection = null;
		if (!new File(paragraphsDB).exists()) {
			System.out.println("Database don't exists " + paragraphsDB + "\nPlease change configuration and start again.");
			System.exit(0);
		} else {
			inputConnection = IE_DBConnector.connect(paragraphsDB);
		}

		// Verbindung mit Output-DB
		if (!new File(outputFolder).exists()) {
			new File(outputFolder).mkdirs();
		}
		Connection outputConnection = IE_DBConnector.connect(outputFolder + outputDB);
		IE_DBConnector.createExtractionOutputTable(outputConnection, IEType.TOOL, false);

		// Prüfe ob maxCount und startPos gültige Werte haben
		String query = "SELECT COUNT(*) FROM ClassifiedParagraphs;";
		Statement stmt = inputConnection.createStatement();
		ResultSet countResult = stmt.executeQuery(query);
		int tableSize = countResult.getInt(1);
		stmt.close();
		if (tableSize <= startPos) {
			System.out.println("startPosition (" + startPos + ")is greater than tablesize (" + tableSize + ")");
			System.out.println("please select a new startPosition and try again");
			System.exit(0);
		}
		if (maxCount > tableSize - startPos) {
			maxCount = tableSize - startPos;
		}
		
		//Verbindung zu Extractions-DB
		Connection extractionsConnection = IE_DBConnector.connect(extractionsDB);


		// starte Matching
		long before = System.currentTimeMillis();
		IE_DBConnector.createIndex(inputConnection, "ClassifiedParagraphs", "ClassTWO, ClassTHREE");
		Extractor extractor = new Extractor(tools, mistakes, null, IEType.TOOL, extractionsConnection);
		extractor.stringMatch(statisticsFile, inputConnection, outputConnection, maxCount, startPos);
		long after = System.currentTimeMillis();
		double time = (((double) after - before) / 1000) / 60;
		if (time > 60.0) {
			System.out.println("\nfinished matching in " + (time / 60) + " hours");
		} else {
			System.out.println("\nfinished matching in " + time + " minutes");
		}

	}

}
