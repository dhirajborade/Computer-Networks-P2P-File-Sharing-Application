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

	private byte[] bitfieldPayload;

	/**
	 * @param bitfield
	 */
	public BitfieldPayload(byte[] payload) {
		super(payload);
		this.setBitfieldPayload(payload);
	}


	/**
	 * @return the bitfieldPayload
	 */
	public byte[] getBitfieldPayload() {
		return bitfieldPayload;
	}


	/**
	 * @param bitfieldPayload the bitfieldPayload to set
	 */
	public void setBitfieldPayload(byte[] bitfieldPayload) {
		this.bitfieldPayload = bitfieldPayload;
	}


	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
