/**
 *
 */
package com.edu.ufl.cise.cnt5106c.Peer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Vector;

/**
 * @author dhirajborade
 *
 */
public class RemotePeerInfo {

	private int peerID;
	private String peerIP;
	private int peerPortNumber;
	private String projectPath;
	private static final String CONFIG_FILE_NAME = "PeerProp.cfg";
	private final String COMMENT_CHAR = "#";
	private final Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();

	/**
	 *
	 */
	public RemotePeerInfo() {
		super();
	}

	/**
	 * @param peerID
	 * @param peerIP
	 * @param peerPortNumber
	 * @param filePresent
	 */
	public RemotePeerInfo(String peerID, String peerIP, String peerPortNumber, String projectPath) {
		super();
		this.setPeerID(Integer.parseInt(peerID));
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(Integer.parseInt(peerPortNumber));
		this.setProjectPath(projectPath);
	}

	/**
	 * @return the peerID
	 */
	public int getPeerID() {
		return peerID;
	}

	/**
	 * @param peerID
	 *            the peerID to set
	 */
	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}

	/**
	 * @return the peerIP
	 */
	public String getPeerIP() {
		return peerIP;
	}

	/**
	 * @param peerIP
	 *            the peerIP to set
	 */
	public void setPeerIP(String peerIP) {
		this.peerIP = peerIP;
	}

	/**
	 * @return the peerPortNumber
	 */
	public int getPeerPortNumber() {
		return peerPortNumber;
	}

	/**
	 * @param peerPortNumber
	 *            the peerPortNumber to set
	 */
	public void setPeerPortNumber(int peerPortNumber) {
		this.peerPortNumber = peerPortNumber;
	}

	/**
	 * @return the projectPath
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * @param projectPath
	 *            the projectPath to set
	 */
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	/**
	 * @return the cOMMENT_CHAR
	 */
	public String getCOMMENT_CHAR() {
		return COMMENT_CHAR;
	}

	/**
	 * @return the peerInfoVector
	 */
	public Vector<RemotePeerInfo> getPeerInfoVector() {
		return peerInfoVector;
	}

	public void read(Reader reader) throws FileNotFoundException, IOException, ParseException {
		BufferedReader in = new BufferedReader(reader);
		int i = 0;
		for (String line; (line = in.readLine()) != null;) {
			line = line.trim();
			if ((line.length() <= 0) || (line.startsWith(COMMENT_CHAR))) {
				continue;
			}
			String[] tokens = line.split("\\s+");
			if (tokens.length != 4) {
				throw new ParseException(line, i);
			}
			peerInfoVector.addElement(
					new RemotePeerInfo(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim()));
			i++;
		}
	}

	public Vector<RemotePeerInfo> getPeerInfo() {
		return new Vector<RemotePeerInfo>(peerInfoVector);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
