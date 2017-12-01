/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class BitfieldPayload extends Payload {

	/**
	 *
	 */
	private static final long serialVersionUID = 8052696107658542567L;

	private byte[] payload;

	/**
	 * @param bitfield
	 */
	public BitfieldPayload(byte[] payload) {
		super(payload);
		this.setBitfield(payload);
	}

	/**
	 * @return the bitfield
	 */
	public byte[] getBitfield() {
		return payload;
	}

	/**
	 * @param bitfield
	 *            the bitfield to set
	 */
	public void setBitfield(byte[] payload) {
		this.payload = payload;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
