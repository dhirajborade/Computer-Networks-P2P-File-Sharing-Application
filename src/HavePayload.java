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
	private byte[] havePayload; // 4-byte piece index field we will index from -2,147,483,648 to 2,147,483,647

	/**
	 * @param index
	 */
	public HavePayload(byte[] payload) {
		super(payload);
		this.setHavePayload(payload);
	}

	/**
	 * @return the havePayload
	 */
	public byte[] getHavePayload() {
		return havePayload;
	}

	/**
	 * @param havePayload
	 *            the havePayload to set
	 */
	public void setHavePayload(byte[] havePayload) {
		this.havePayload = havePayload;
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
