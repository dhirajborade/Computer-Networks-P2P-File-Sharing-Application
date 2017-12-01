/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class HavePayload extends Payload {

	/**
	 *
	 */
	private static final long serialVersionUID = 3777628630171683471L;
	private byte[] payload; // 4-byte piece index field we will index from -2,147,483,648 to 2,147,483,647

	/**
	 * @param index
	 */
	public HavePayload(byte[] payload) {
		super(payload);
		this.setPayload(payload);
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
	public void setIndex(byte[] payload) {
		this.payload = payload;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
