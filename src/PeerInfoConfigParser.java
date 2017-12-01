import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class PeerInfoConfigParser {

	private static int totalPeers;
	private static final String CONFIG_FILE_NAME = "PeerInfo.cfg";
	private final String COMMENT_CHAR = "#";
	private static Vector<Peer> peerInfoVector = new Vector<Peer>();
	private static int lastPeerID;
	private static int currentPeerNo;
	private static Peer currentPeer;

	/**
	 *
	 */
	public PeerInfoConfigParser() {
		setTotalPeers(0);
		this.setCurrentPeerNo(0);
	}

	/**
	 * @return the totalPeers
	 */
	public static int getTotalPeers() {
		return totalPeers;
	}

	/**
	 * @param totalPeers
	 *            the totalPeers to set
	 */
	public static void setTotalPeers(int totalPeers) {
		PeerInfoConfigParser.totalPeers = totalPeers;
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
	public static List<Peer> getPeerInfoVector() {
		return new ArrayList<Peer>(peerInfoVector);
	}

	/**
	 * @return the lastPeerID
	 */
	public int getLastPeerID() {
		return lastPeerID;
	}

	/**
	 * @param lastPeerID
	 *            the lastPeerID to set
	 */
	public void setLastPeerID(int lastPeerID) {
		PeerInfoConfigParser.lastPeerID = lastPeerID;
	}

	/**
	 * @return the currentPeerNo
	 */
	public int getCurrentPeerNo() {
		return currentPeerNo;
	}

	/**
	 * @param currentPeerNo
	 *            the currentPeerNo to set
	 */
	public void setCurrentPeerNo(int currentPeerNo) {
		PeerInfoConfigParser.currentPeerNo = currentPeerNo;
	}

	/**
	 * @return the currentPeer
	 */
	public static Peer getCurrentPeer() {
		return currentPeer;
	}

	/**
	 * @param currentPeer
	 *            the currentPeer to set
	 */
	public static void setCurrentPeer(Peer currentPeer) {
		PeerInfoConfigParser.currentPeer = currentPeer;
	}

	public void readPeerInfoFile(Reader reader) throws FileNotFoundException, IOException, ParseException {
		BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/PeerInfo.cfg"));
		try {
			int i = 0;
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				inputLine = inputLine.trim();
				if ((inputLine.length() <= 0) || (inputLine.startsWith(COMMENT_CHAR))) {
					continue;
				}
				String[] tokens = inputLine.split("\\s+");
				if (tokens.length != 4) {
					throw new ParseException(inputLine, i);
				}
				final boolean peerHasFile = (tokens[3].trim().compareTo("1") == 0);
				peerInfoVector.add(new Peer(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), peerHasFile));
				i++;
				setTotalPeers(getTotalPeers() + 1);
			}
			setTotalPeers(getTotalPeers() - 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	public void initializePeerList(PeerProcess peerProc, String peerID) throws IOException {
		BufferedReader pireader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/" + "PeerInfo.cfg"));
		String line, tokens[];
		try {
			while ((line = pireader.readLine()) != null) {
				tokens = line.split(" ");
				this.setLastPeerID(Integer.parseInt(tokens[0]));
				final boolean peerHasFile = (tokens[3].trim().compareTo("1") == 0);
				if (!tokens[0].equals(peerID)) {
					System.out.println("t:" + tokens[0] + " " + tokens[1] + " " + tokens[2]);
					Peer peer = new Peer(tokens[0], tokens[1], tokens[2], peerHasFile);
					if (Integer.parseInt(tokens[3]) == 0) {
						peer.setHandShakeDone(false);
					}
					peerProc.peerInfoVector.addElement(peer);
				} else {
					PeerInfoConfigParser.setCurrentPeer(new Peer(tokens[0], tokens[1], tokens[2], peerHasFile));
					this.setCurrentPeerNo(peerProc.peerInfoVector.size());
					if (Integer.parseInt(tokens[3]) == 1) {
						peerProc.isFilePresent = true;
					}
				}
			}
		} finally {
			pireader.close();
		}
	}

	public void initializeFileManager(PeerProcess peerProc, String peerID) throws IOException {
		int bufferSize = (int) Math.ceil((double) (CommonPropertiesParser.getNumberOfPieces() / 8.0));
		Iterator<Peer> iteratorPeer = getPeerInfoVector().iterator();
		while (iteratorPeer.hasNext()) {
			Peer tempPeer = (Peer) iteratorPeer.next();
			this.setLastPeerID(tempPeer.getPeerID());
			if (tempPeer.getPeerID() != Integer.parseInt(peerID)) {
				peerProc.peerInfoVector.remove(tempPeer);
				System.out.println("t:" + tempPeer.getPeerID());
				Peer peer = tempPeer;
				peer.setBitfield(new byte[bufferSize]);
				Arrays.fill(peer.getBitfield(), (byte) 0);
				peerProc.peerInfoVector.addElement(peer);
			} else {
				PeerInfoConfigParser.setCurrentPeer(tempPeer);
				if (peerProc.isFilePresent) {
					peerProc.copyFileUsingStream(
							new String(System.getProperty("user.dir") + "/" + CommonPropertiesParser.getFileName()),
							new String(System.getProperty("user.dir") + "/peer_" + peerID + "/"
									+ CommonPropertiesParser.getFileName()));
					CommonPropertiesParser.setFileName(System.getProperty("user.dir") + "/peer_"
							+ PeerInfoConfigParser.getCurrentPeer().getPeerID() + "/" + CommonPropertiesParser.getFileName());
					System.out.println(CommonPropertiesParser.getFileName());
					peerProc.fileComplete = true;
					PeerInfoConfigParser.getCurrentPeer().setBitfield(new byte[bufferSize]);
					for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
						PeerProcess.setBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), i);
					}
				} else {
					CommonPropertiesParser.setFileName(System.getProperty("user.dir") + "/peer_"
							+ PeerInfoConfigParser.getCurrentPeer().getPeerID() + "/" + CommonPropertiesParser.getFileName());
					new File(CommonPropertiesParser.getFileName()).delete();
					new File(CommonPropertiesParser.getFileName()).createNewFile();
					PeerInfoConfigParser.getCurrentPeer().setBitfield(new byte[bufferSize]);
					Arrays.fill(PeerInfoConfigParser.getCurrentPeer().getBitfield(), (byte) 0);
				}
			}
		}
	}

	public void establishConnection(PeerProcess peerProc) {
		int indexI = 0;
		while (this.getCurrentPeerNo() != 0 && indexI <= this.getCurrentPeerNo() - 1) {
			peerProc.connectToPreviousPeer(peerProc.peerInfoVector.get(indexI));
			indexI++;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
