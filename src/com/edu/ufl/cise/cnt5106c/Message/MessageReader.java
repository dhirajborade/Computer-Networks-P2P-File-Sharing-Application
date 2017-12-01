package com.edu.ufl.cise.cnt5106c.Message;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.edu.ufl.cise.cnt5106c.Handshake.HandShake;
import com.edu.ufl.cise.cnt5106c.Payload.BitfieldPayload;
import com.edu.ufl.cise.cnt5106c.Payload.HavePayload;
import com.edu.ufl.cise.cnt5106c.Payload.PiecePayload;
import com.edu.ufl.cise.cnt5106c.Payload.RequestPayload;
import com.edu.ufl.cise.cnt5106c.Peer.PeerProcess;

public class MessageReader {

	private Socket socket;
	private PeerProcess peerProc;
	private boolean isHandshakeDone = false;

	public MessageReader(Socket socket, PeerProcess peerProc) throws IOException {
		this.setSocket(socket);
		this.setPeerProc(peerProc);
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @return the peerProcess
	 */
	public PeerProcess getPeerProc() {
		return peerProc;
	}

	/**
	 * @param peerProc
	 *            the peerProcess to set
	 */
	public void setPeerProc(PeerProcess peerProc) {
		this.peerProc = peerProc;
	}

	/**
	 * @return the isHandshakeDone
	 */
	public boolean isHandshakeDone() {
		return isHandshakeDone;
	}

	/**
	 * @param isHandshakeDone
	 *            the isHandshakeDone to set
	 */
	public void setHandshakeDone(boolean isHandshakeDone) {
		this.isHandshakeDone = isHandshakeDone;
	}

	public Object readObject() throws Exception {
		InputStream inputStream = this.getSocket().getInputStream();
		if (this.isHandshakeDone()) {
			for (; (!this.getPeerProc().exit && inputStream.available() < 4);) {
			}
			byte[] lengthBytes = new byte[4];
			inputStream.read(lengthBytes, 0, 4);
			int length = ByteBuffer.wrap(lengthBytes).getInt();
			for (; inputStream.available() < length;) {
			}
			byte[] typeBuffer = new byte[1];
			try {
				inputStream.read(typeBuffer, 0, 1);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte type = typeBuffer[0];
			byte[] payload = null;
			if (!(length > 1)) {

			} else {
				payload = new byte[length - 1];
				int recievedBytes = 0;
				for (; recievedBytes < (length - 1);) {
					try {
						recievedBytes = recievedBytes + inputStream.read(payload, recievedBytes, length - 1);
					} catch (IOException e) {
						e.printStackTrace();
						throw e;
					}
				}
			}

			Message msg = null;
			switch ((int) type) {

			case 4:
				HavePayload havePayLoad = new HavePayload(payload);
				msg = new Message(length, type, havePayLoad, null);
				break;
			case 5:
				BitfieldPayload bitFieldPayLoad = new BitfieldPayload(payload);
				msg = new Message(length, type, bitFieldPayLoad, null);
				break;
			case 6:
				RequestPayload requestPayLoad = new RequestPayload(payload);
				msg = new Message(length, type, requestPayLoad, null);
				break;
			case 7:
				PiecePayload piecePayLoad = new PiecePayload(payload);
				msg = new Message(length, type, piecePayLoad, null);
				break;
			default:
				msg = new Message(length, type, payload);
				break;
			}
			System.out.println("Available after reading Payload:" + inputStream.available());
			return msg;
		} else {
			byte[] header = new byte[18];
			try {
				inputStream.read(header, 0, 18);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte[] zerobits = new byte[10];
			try {
				inputStream.read(zerobits, 0, 10);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte[] peerId = new byte[4];
			try {
				inputStream.read(peerId);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			this.setHandshakeDone(true);
			return new HandShake(ByteBuffer.wrap(peerId).getInt());
		}

	}

}
