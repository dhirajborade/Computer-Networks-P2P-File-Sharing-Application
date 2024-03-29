package com.edu.ufl.cise.cnt5106c.Message;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.edu.ufl.cise.cnt5106c.Handshake.HandShake;

public class MessageWriter {
	private Message message;
	private DataOutputStream outStream;

	/**
	 * @param message
	 * @param outStream
	 */
	public MessageWriter(Message message, DataOutputStream outStream) {
		this.setMessage(message);
		this.setOutStream(outStream);
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the outStream
	 */
	public DataOutputStream getOutStream() {
		return outStream;
	}

	/**
	 * @param outStream
	 *            the outStream to set
	 */
	public void setOutStream(DataOutputStream outStream) {
		this.outStream = outStream;
	}

	public void writeObject() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.out.println("Sending Message : " + this.getMessage());
		if (this.getMessage() instanceof HandShake) {
			HandShake handShakeMessage = (HandShake) this.getMessage();
			bos.write(handShakeMessage.getHeader(), 0, handShakeMessage.getHeader().length);
			bos.write(handShakeMessage.getZeroBits(), 0, handShakeMessage.getZeroBits().length);
			bos.write(handShakeMessage.getPeerID(), 0, handShakeMessage.getPeerID().length);
		} else {
			bos.write(ByteBuffer.allocate(4).putInt(this.getMessage().getLength()).array(), 0, 4);
			bos.write(new byte[] { this.getMessage().getType() }, 0, 1);
			if (!((this.getMessage().getPayload() != null) && (this.getMessage().getPayload().length > 0))) {

			} else {
				bos.write(this.getMessage().getPayload(), 0, this.getMessage().getPayload().length);
			}
		}
		this.getOutStream().write(bos.toByteArray());
		this.getOutStream().flush();
	}

}