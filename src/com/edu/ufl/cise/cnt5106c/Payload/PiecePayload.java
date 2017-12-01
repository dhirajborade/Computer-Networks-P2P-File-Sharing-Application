package com.edu.ufl.cise.cnt5106c.Payload;
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
	private byte[] piecePayload;

	/**
	 * @param content
	 * @param index
	 */
	public PiecePayload(byte[] payload) {
		super(payload);
		this.setPiecePayload(payload);

	}

	/**
	 * @return the piecePayload
	 */
	public byte[] getPiecePayload() {
		return piecePayload;
	}

	/**
	 * @param piecePayload
	 *            the piecePayload to set
	 */
	public void setPiecePayload(byte[] piecePayload) {
		this.piecePayload = piecePayload;
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
