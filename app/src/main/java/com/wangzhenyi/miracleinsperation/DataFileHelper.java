/**
 * Created by Zhenyi Wang.
 * Last modified on 12 Jan 2015.
 */

package com.wangzhenyi.miracleinsperation;

import android.content.Context;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * This class is used by the MiracleInsperation App to handle requests relating to files,
 * such as exporting data to a backup file.
 *
 * The current version is 1, content of each file is formatted as
 *   HEADER ROW_SEPRATOR
 *   item1 ROW_SEPERATOR
 *   item2 ROW_SEPERATOR
 *   ...
 *
 * for each item, it consists of
 *   id COLUMN_SEPERATOR date COLUMN_SEPERATOR content COLUMN_SEPERATOR deleted
 *   **note that all new line characters in the content are replaced by NEW_LINE_CHARACTER
 *
 * For version 1, the HEADER is the version number "1"
 * the COLUMN_SEPERATOR is "\t<%COL%>\t";
 * the ROW_SEPERATOR is "<%ROW%>\n"
 * the NEW_LINE_CHARACTER is "<%NL%>"
 */
public class DataFileHelper {
    public static final int CURRENT_VERSION = 1;
    public static final String HEADER = "" + CURRENT_VERSION;
    public static final String DIVIDOR_COLUMN = "\t<%COL%>\t";
    public static final String DIVIDOR_ROW = "<%ROW%>\n";
    public static final String REPLACE_NEW_LINE = "<%NL%>";


    /**
     * Converts a single Insperation object to string to be stored in a backup file
     * @param insperation the data object to be stored
     * @return the string representation of the data object
     */
    public static String convertDataToString(Insperation insperation) {
        return insperation.getId() + DIVIDOR_COLUMN
                + insperation.getDate().getTime() + DIVIDOR_COLUMN
                + insperation.getContent().replace("\n", REPLACE_NEW_LINE) + DIVIDOR_COLUMN
                + insperation.getDeleted();
    }

    /**
     * Generates a vector of data objects from a string
     * @param input the string directly read from a backup file
     * @return a vector of data objects from a string; null if data is empty or invalid
     */
    public static Vector<Insperation> convertStringToData(String input) {

        // Returns null if empty
        if (input == null || input.length() == 0) {
            return null;
        }

        // Returns null if data is empty or format is not valid
        String[] rows = input.split(DIVIDOR_ROW);
        if (rows.length <= 1) {
            return null;
        }

        // Checks header and version, null if invalid
        try {
            int dataVersion = Integer.parseInt(rows[0]);
            if (dataVersion != CURRENT_VERSION) {
                return null;
            }
        } catch(NumberFormatException e) {
            return null;
        }

        Vector<Insperation> insperations = new Vector<Insperation>(rows.length);

        // Converts each row and stores in a vector; ignores any invalid inputs
        for (int i = 1; i < rows.length; i++) {
            Insperation insperation = convertRow(rows[i]);

            if (insperation != null) {
                insperations.add(insperation);
            }
        }

        // Checks result
        if (insperations.isEmpty()) {
            return null;
        }

        return insperations;
    }

    /**
     * Generates an Insperation object from a row from a backup file
     * @param row a string representation of data
     * @return a data object represented by the string; null if invalid or empty
     */
    private static Insperation convertRow(String row) {

        // Checks if empty
        if (row == null || row.length() == 0) {
            return null;
        }

        // Splits by column separator
        String[] elements = row.split(DIVIDOR_COLUMN);

        // Checks number of parts
        if (elements.length != 4) {
            return null;
        }

        // Parses each element
        int id = Integer.parseInt(elements[0]);
        Date date = new Date(Long.parseLong(elements[1]));
        String content = elements[2].replace(REPLACE_NEW_LINE, "\n");
        int deleted = Integer.parseInt(elements[3]);

        // Returns an object
        return new Insperation(id, date, content, deleted);
    }

    /**
     * Converts data objects to a string representation to be saved in a
     * backup file
     * @param insperations a vector of Insperation objects
     * @return a string ready to be saved; empty if data is null or empty
     */
    public static String convertDataToString(Vector<Insperation> insperations) {

        // Returns empty string if input is null or empty
        if (insperations == null || insperations.isEmpty()) {
            return "";
        }

        // Uses a string builder to buffer contents, header row first
        StringBuilder sb = new StringBuilder(HEADER);
        sb.append(DIVIDOR_ROW);

        // Iterates and reads data to to string builder
        for (Insperation insperation : insperations) {
            sb.append(convertDataToString(insperation));
            sb.append(DIVIDOR_ROW);
        }

        return sb.toString();
    }

    /**
     * Writes a string to a file
     *
     * @param path the path of the parent folder
     * @param fileName the name of the target file
     * @param write_str the string to be written to the file
     * @throws IOException
     */
    public static void writeStringToFile(String path, String fileName, String write_str) throws IOException {
        String fullFileName = path + "/" + fileName;
        File pathFolder = null;
        File file = null;

        // Creates parent folder if does not exist
        pathFolder = new File(path);
        if (!pathFolder.exists()) {
            pathFolder.mkdirs();
        }


        file = new File(fullFileName);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
        } catch (IOException e) {
            Log.v("error", e.toString());
            throw e;
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }

    /**
     * Reads a file and returns its content as a string
     *
     * @param file the file to be read from
     * @return the string representation of the content of the file
     * @throws IOException
     */
    public static String readFileToString(File file) throws IOException {
        String result; // null
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);
            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        } catch (IOException e) {
            throw e;
        } finally {
            if (fin != null)
                fin.close();
        }

        return result;
    }
}
