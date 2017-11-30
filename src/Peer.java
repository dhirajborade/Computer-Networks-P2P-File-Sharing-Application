import java.net.Socket;

public class Peer {

	private int peerID;
	private String peerIP;
	private int peerPortNumber;
	private boolean filePresent;
	private byte[] bitfield;
	private boolean unChoked;
	private long downloadSpeed;
	private Socket hostSocket;
	private boolean peerUp;
	private boolean isHandShakeDone;
	private boolean[] interestedFromBitfield;
	private boolean interestedInPieces;

	/**
	 * @param peerID
	 * @param peerIP
	 * @param peerPortNumber
	 * @param filePresent
	 */
	public Peer(int peerID, String peerIP, int peerPortNumber, boolean filePresent) {
		super();
		this.setPeerID(peerID);
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(peerPortNumber);
		this.setFilePresent(filePresent);
	}

	/**
	 * @param peerID
	 * @param peerIP
	 * @param peerPortNumber
	 * @param filePresent
	 */
	public Peer(String peerID, String peerIP, String peerPortNumber, boolean filePresent) {
		super();
		this.setPeerID(Integer.parseInt(peerID));
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(Integer.parseInt(peerPortNumber));
		this.setFilePresent(filePresent);
	}

	public Peer(int peerID, String peerIP, int peerPortNumber) {
		super();
		this.setPeerID(peerID);
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(peerPortNumber);
	}

	public Peer(String peerIP, int peerPortNumber) {
		super();
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(peerPortNumber);
	}

	public Peer(String peerID, String peerIP, String peerPortNumber) {
		super();
		this.setPeerID(Integer.parseInt(peerID));
		this.setPeerIP(peerIP);
		this.setPeerPortNumber(Integer.parseInt(peerPortNumber));
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
	 * @return the filePresent
	 */
	public boolean isFilePresent() {
		return filePresent;
	}

	/**
	 * @param filePresent
	 *            the filePresent to set
	 */
	public void setFilePresent(boolean filePresent) {
		this.filePresent = filePresent;
	}

	/**
	 * @return the bitfield
	 */
	public byte[] getBitfield() {
		return bitfield;
	}

	/**
	 * @param bitfield
	 *            the bitfield to set
	 */
	public void setBitfield(byte[] bitfield) {
		this.bitfield = bitfield;
	}

	/**
	 * @return the unChoked
	 */
	public boolean isUnChoked() {
		return unChoked;
	}

	/**
	 * @param unChoked
	 *            the unChoked to set
	 */
	public void setUnChoked(boolean unChoked) {
		this.unChoked = unChoked;
	}

	/**
	 * @return the downloadSpeed
	 */
	public long getDownloadSpeed() {
		return downloadSpeed;
	}

	/**
	 * @param downloadSpeed
	 *            the downloadSpeed to set
	 */
	public void setDownloadSpeed(long downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	/**
	 * @return the hostSocket
	 */
	public Socket getHostSocket() {
		return hostSocket;
	}

	/**
	 * @param hostSocket
	 *            the hostSocket to set
	 */
	public void setHostSocket(Socket hostSocket) {
		this.hostSocket = hostSocket;
	}

	/**
	 * @return the peerUp
	 */
	public boolean isPeerUp() {
		return peerUp;
	}

	/**
	 * @param peerUp
	 *            the peerUp to set
	 */
	public void setPeerUp(boolean peerUp) {
		this.peerUp = peerUp;
	}

	/**
	 * @return the isHandShakeDone
	 */
	public boolean isHandShakeDone() {
		return isHandShakeDone;
	}

	/**
	 * @param isHandShakeDone
	 *            the isHandShakeDone to set
	 */
	public void setHandShakeDone(boolean isHandShakeDone) {
		this.isHandShakeDone = isHandShakeDone;
	}

	/**
	 * @return the interestedFromBitfield
	 */
	public boolean[] getInterestedFromBitfield() {
		return interestedFromBitfield;
	}

	/**
	 * @param interestedFromBitfield
	 *            the interestedFromBitfield to set
	 */
	public void setInterestedFromBitfield(boolean[] interestedFromBitfield) {
		this.interestedFromBitfield = interestedFromBitfield;
	}

	/**
	 * @return the interestedInPieces
	 */
	public boolean isInterestedInPieces() {
		return interestedInPieces;
	}

	/**
	 * @param interestedInPieces
	 *            the interestedInPieces to set
	 */
	public void setInterestedInPieces(boolean interestedInPieces) {
		this.interestedInPieces = interestedInPieces;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getPeerIP() == null) ? 0 : this.getPeerIP().hashCode());
		result = prime * result + this.getPeerPortNumber();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Peer other = (Peer) obj;
		if (this.getPeerIP() == null) {
			if (other.getPeerIP() != null)
				return false;
		} else if (!this.getPeerIP().equals(other.getPeerIP()))
			return false;
		if (this.getPeerPortNumber() != other.getPeerPortNumber())
			return false;
		return true;
	}

}
