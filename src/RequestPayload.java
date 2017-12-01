/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class RequestPayload extends Payload {

	/**
	 *
	 */
	private static final long serialVersionUID = 9208727206802180498L;

	// 4-byte piece index field we will index from -2,147,483,648 to 2,147,483,647
	private byte[] payload;

	/**
	 * @param index
	 */
	public RequestPayload(byte[] payload) {
		super();
		this.payload = payload;
	}

	/**
	 * @return the index
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * @param index
	 *            the index to set
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
