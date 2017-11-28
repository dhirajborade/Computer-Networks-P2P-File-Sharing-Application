/**
 *
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * @author dhirajborade
 *
 */
public class CommonPropertiesParser {

	private static final String CONFIG_FILE_NAME = "Common.cfg";
	private static int numberOfPreferredNeighbors;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static int fileSize;
	private static int pieceSize;

	/**
	 *
	 */
	public CommonPropertiesParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the numberOfPreferredNeighbors
	 */
	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	/**
	 * @param numberOfPreferredNeighbors
	 *            the numberOfPreferredNeighbors to set
	 */
	public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		CommonPropertiesParser.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}

	/**
	 * @return the unchokingInterval
	 */
	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	/**
	 * @param unchokingInterval
	 *            the unchokingInterval to set
	 */
	public static void setUnchokingInterval(int unchokingInterval) {
		CommonPropertiesParser.unchokingInterval = unchokingInterval;
	}

	/**
	 * @return the optimisticUnchokingInterval
	 */
	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	/**
	 * @param optimisticUnchokingInterval
	 *            the optimisticUnchokingInterval to set
	 */
	public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		CommonPropertiesParser.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}

	/**
	 * @return the fileName
	 */
	public static String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public static void setFileName(String fileName) {
		CommonPropertiesParser.fileName = fileName;
	}

	/**
	 * @return the fileSize
	 */
	public static int getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public static void setFileSize(int fileSize) {
		CommonPropertiesParser.fileSize = fileSize;
	}

	/**
	 * @return the pieceSize
	 */
	public static int getPieceSize() {
		return pieceSize;
	}

	/**
	 * @param pieceSize
	 *            the pieceSize to set
	 */
	public static void setPieceSize(int pieceSize) {
		CommonPropertiesParser.pieceSize = pieceSize;
	}

	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	public void parseCommonConfigFile(Reader reader) throws FileNotFoundException, IOException, ParseException {
		try {
			BufferedReader in = new BufferedReader(new FileReader("Common.cfg"));
			String line;
			while ((line = in.readLine()) != null) {
				String[] configFile = line.split("");
				String propertyName = configFile[0];
				String propertyValue = configFile[1];
				switch (propertyName) {
				case "NumberOfPreferredNeighbors":
					numberOfPreferredNeighbors = Integer.parseInt(propertyValue) + 1;
					break;
				case "UnchokingInterval":
					unchokingInterval = Integer.parseInt(propertyValue);
					break;
				case "OptimisticUnchokingInterval":
					optimisticUnchokingInterval = Integer.parseInt(propertyValue);
					break;
				case "FileName":
					fileName = propertyValue;
					break;
				case "FileSize":
					fileSize = Integer.parseInt(propertyValue);
					break;
				case "PieceSize":
					pieceSize = Integer.parseInt(propertyValue);
					break;
				default:
					break;
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
