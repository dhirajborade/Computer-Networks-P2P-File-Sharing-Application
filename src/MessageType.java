/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public enum MessageType {
	CHOKE(0), UNCHOKE(1), INTERESTED(2), NOT_INTERESTED(3), HAVE(4), BITFIELD(5), REQUEST(6), PIECE(7);

	private byte val;

	MessageType(int val) {
		this.val = (byte) val;
	}

	/**
	 * @return the val
	 */
	public byte getVal() {
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(byte val) {
		this.val = val;
	}

}
