import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandShake extends Message implements Serializable {

	private static final long serialVersionUID = 8799977982265952720L;
	private static final String headerContent = "P2PFILESHARINGPROJ";
	private byte[] header;
	private byte[] zeroBits;
	private byte[] peerID;

	public HandShake(int peerId) {
		this.setHeader(new byte[18]);
		for (int i = 0; i < getHeadercontent().length(); i++) {
			this.getHeader()[i] = (byte) (getHeadercontent().charAt(i));
		}
		this.setPeerID(new byte[4]);
		this.setPeerID(ByteBuffer.allocate(4).putInt(peerId).array());
		this.setZeroBits(new byte[10]);
		for (int i = 0; i < 10; i++) {
			this.getZeroBits()[i] = (byte) 0;
		}
	}

	/**
	 * @return the header
	 */
	public byte[] getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(byte[] header) {
		this.header = header;
	}

	/**
	 * @return the zeroBits
	 */
	public byte[] getZeroBits() {
		return zeroBits;
	}

	/**
	 * @param zeroBits
	 *            the zeroBits to set
	 */
	public void setZeroBits(byte[] zeroBits) {
		this.zeroBits = zeroBits;
	}

	/**
	 * @return the peerID
	 */
	public byte[] getPeerID() {
		return peerID;
	}

	/**
	 * @param peerID
	 *            the peerID to set
	 */
	public void setPeerID(byte[] peerID) {
		this.peerID = peerID;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the headercontent
	 */
	public static String getHeadercontent() {
		return headerContent;
	}

	@Override
	public String toString() {
		return "HandShake [peerID=" + Arrays.toString(this.getPeerID()) + "]";
	}

}