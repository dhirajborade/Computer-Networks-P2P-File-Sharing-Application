/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class PiecePayload extends Payload {
	/**
	 *
	 */
	private static final long serialVersionUID = 5795021442144294811L;

	// 4-byte piece index field we will index from -2,147,483,648 to 2,147,483,647
	private byte[] payload;

	/**
	 * @param content
	 * @param index
	 */
	public PiecePayload(byte[] payload) {
		super(payload);
		this.setPayload(payload);

	}

	/**
	 * @return the content
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
