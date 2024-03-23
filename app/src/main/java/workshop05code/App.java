package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        logger.log(Level.INFO, "Database words.db created.");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Database connected.");
            System.out.println("Wordle created and connected.");
        } else {
            logger.log(Level.SEVERE, "Database not connected.");
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "Table created.");
            System.out.println("Wordle structures in place.");
        } else {
            logger.log(Level.SEVERE, "Table creation failed.");
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING,
                    "Addition of words was intruptted. Not all words have been added. Error: " + e.toString());
            System.out.println("Not able to load. Sorry!");
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if (guess.matches("[a-z]{4}")) { // input check
                    logger.log(Level.INFO, "Input check '[a-z]{4}' passed.");
                    System.out.println("You've guessed '" + guess + "'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) {
                        logger.log(Level.INFO, "Word '" + guess + "' found.");
                        System.out.println("Success! It is in the the list.\n");
                    } else {
                        logger.log(Level.INFO, "Word '" + guess + "' not found found.");
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                } else {
                    logger.log(Level.INFO, "Input check '[a-z]{4}' failed.");
                    System.out.println(
                            "Sorry. The word you've typed is invalid. Please type in a 4-letter string that consists only of lowercase letters a-z.\n");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
            e.printStackTrace();
        }

    }
}