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
	private static final List<Peer> peerInfoVector = new ArrayList<Peer>();
	private static int lastPeerID;
	private static int currentPeerNo = 0;
	private static Peer currentPeer;

	/**
	 *
	 */
	public PeerInfoConfigParser() {
		setTotalPeers(0);
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
	public Peer getCurrentPeer() {
		return currentPeer;
	}

	/**
	 * @param currentPeer
	 *            the currentPeer to set
	 */
	public void setCurrentPeer(Peer currentPeer) {
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
				// final boolean peerHasFile = (tokens[3].trim().compareTo("1") == 0);
				peerInfoVector.add(new Peer(tokens[0].trim(), tokens[1].trim(), tokens[2].trim()));
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

	public void initializePeerList(PeerProcess p, String peerID) throws IOException {
		BufferedReader pireader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/" + "PeerInfo.cfg"));
		String line, tokens[];
		try {
			while ((line = pireader.readLine()) != null) {
				tokens = line.split(" ");
				lastPeerID = Integer.parseInt(tokens[0]);
				if (!tokens[0].equals(peerID)) {
					System.out.println("t:" + tokens[0] + " " + tokens[1] + " " + tokens[2]);
					Peer peer = new Peer(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]));
					if (Integer.parseInt(tokens[3]) == 0) {
						peer.setHandShakeDone(false);
					}
					p.peerInfoVector.add(peer);
				} else {
					currentPeer = new Peer(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]));
					currentPeerNo = p.peerInfoVector.size();
					if (Integer.parseInt(tokens[3]) == 1) {
						p.isFilePresent = true;
					}
				}
			}
		} finally {
			pireader.close();
		}
	}

	public void initializeFileManager(PeerProcess p, String peerID) throws IOException {
		int bfsize = (int) Math.ceil((double) (p.noOfPieces / 8.0));
		Iterator<Peer> itr = getPeerInfoVector().iterator();
		while (itr.hasNext()) {
			Peer tempPeer = (Peer) itr.next();
			lastPeerID = tempPeer.getPeerID();
			if (tempPeer.getPeerID() != Integer.parseInt(peerID)) {
				p.peerInfoVector.remove(tempPeer);
				System.out.println("t:" + tempPeer.getPeerID());
				Peer peer = tempPeer;
				peer.setBitfield(new byte[bfsize]);
				Arrays.fill(peer.getBitfield(), (byte) 0);
				p.peerInfoVector.add(peer);
			} else {
				currentPeer = tempPeer;
				if (p.isFilePresent) {
					p.copyFileUsingStream(new String(System.getProperty("user.dir") + "/" + p.FileName),
							new String(System.getProperty("user.dir") + "/peer_" + peerID + "/" + p.FileName));
					p.FileName = System.getProperty("user.dir") + "/peer_" + currentPeer.getPeerID() + "/"
							+ p.FileName;
					System.out.println(p.FileName);
					p.fileComplete = true;
					currentPeer.setBitfield(new byte[bfsize]);
					for (int i = 0; i < p.noOfPieces; i++) {
						PeerProcess.setBit(currentPeer.getBitfield(), i);
					}
				} else {
					p.FileName = System.getProperty("user.dir") + "/peer_" + currentPeer.getPeerID() + "/"
							+ p.FileName;
					new File(p.FileName).delete();
					new File(p.FileName).createNewFile();
					currentPeer.setBitfield(new byte[bfsize]);
					Arrays.fill(currentPeer.getBitfield(), (byte) 0);
				}
			}
		}
	}

	public void establishConnection(PeerProcess p) {
		for (int i = 0; currentPeerNo != 0 && i <= currentPeerNo - 1; i++) {
			p.connectToPreviousPeer(p.peerInfoVector.get(i));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
