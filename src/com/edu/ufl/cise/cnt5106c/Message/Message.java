package com.edu.ufl.cise.cnt5106c.Message;
import java.io.Serializable;

import com.edu.ufl.cise.cnt5106c.Payload.Payload;

public class Message implements Serializable {

	private static final long serialVersionUID = 985205199122584865L;
	private int length;
	private byte type;
	private byte[] payload;
	private MessageType messageType;
	private Payload msgPayload;

	public Message() {
		super();
	}

	public Message(int length, byte type, byte[] payload) {
		this.setLength(length);
		this.setType(type);
		this.setPayload(payload);
		setMsgType(type);
	}

	public Message(int length, byte type, Payload payload, Object dummyObj) {
		this.setLength(length);
		this.setType(type);
		this.setPayload(payload.getPayload());
		setMsgType(type);
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(byte type) {
		this.type = type;
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

	public void setMsgType(byte type) {
		switch ((int) type) {
		case 0:
			this.setMessageType(MessageType.CHOKE);
			break;
		case 1:
			this.setMessageType(MessageType.UNCHOKE);
			break;
		case 2:
			this.setMessageType(MessageType.INTERESTED);
			break;
		case 3:
			this.setMessageType(MessageType.NOT_INTERESTED);
			break;
		case 4:
			this.setMessageType(MessageType.HAVE);
			break;
		case 5:
			this.setMessageType(MessageType.BITFIELD);
			break;
		case 6:
			this.setMessageType(MessageType.REQUEST);
			break;
		case 7:
			this.setMessageType(MessageType.PIECE);
			break;
		}
	}

	/**
	 * @return the messageType
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the msgPayload
	 */
	public Payload getMsgPayload() {
		return msgPayload;
	}

	/**
	 * @param msgPayload
	 *            the msgPayload to set
	 */
	public void setMsgPayload(Payload msgPayload) {
		this.msgPayload = msgPayload;
	}

	@Override
	public String toString() {
		return "Message [length=" + this.getLength() + ", type=" + this.getMessageType() + "]";
	}

}