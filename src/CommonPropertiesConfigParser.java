import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class CommonPropertiesConfigParser {

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
	public CommonPropertiesConfigParser() {
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
		CommonPropertiesConfigParser.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
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
		CommonPropertiesConfigParser.unchokingInterval = unchokingInterval;
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
		CommonPropertiesConfigParser.optimisticUnchokingInterval = optimisticUnchokingInterval;
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
		CommonPropertiesConfigParser.fileName = fileName;
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
		CommonPropertiesConfigParser.fileSize = fileSize;
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
		CommonPropertiesConfigParser.pieceSize = pieceSize;
	}

	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	public void readCommonPropertiesFile(Reader reader) throws FileNotFoundException, IOException, ParseException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Common.cfg"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				String[] configurationFile = inputLine.split("\\s+");
				String configName = configurationFile[0];
				String configValue = configurationFile[1];

				if (configName.equals("NumberOfPreferredNeighbors")) {
					setNumberOfPreferredNeighbors(Integer.parseInt(configValue) + 1);
				} else if (configName.equals("UnchokingInterval")) {
					unchokingInterval = Integer.parseInt(configValue);
					setUnchokingInterval(Integer.parseInt(configValue));
				} else if (configName.equals("OptimisticUnchokingInterval")) {
					setOptimisticUnchokingInterval(Integer.parseInt(configValue));
				} else if (configName.equals("FileName")) {
					fileName = configValue;
				} else if (configName.equals("FileSize")) {
					fileSize = Integer.parseInt(configValue);
				} else if (configName.equals("PieceSize")) {
					pieceSize = Integer.parseInt(configValue);
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
