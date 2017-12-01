package com.edu.ufl.cise.cnt5106c.Payload;
import java.io.Serializable;

/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class Payload implements Serializable {

	private static final long serialVersionUID = -5424746124279275035L;
	private byte[] payload;

	/**
	 *
	 */
	public Payload() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param payload
	 */
	public Payload(byte[] payload) {
		super();
		this.setPayload(payload);
	}

	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
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
