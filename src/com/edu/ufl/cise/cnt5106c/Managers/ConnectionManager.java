package com.edu.ufl.cise.cnt5106c.Managers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import com.edu.ufl.cise.cnt5106c.Configuration.CommonPropertiesParser;
import com.edu.ufl.cise.cnt5106c.Configuration.DownloadingRate;
import com.edu.ufl.cise.cnt5106c.Configuration.PeerInfoConfigParser;
import com.edu.ufl.cise.cnt5106c.Handshake.HandShake;
import com.edu.ufl.cise.cnt5106c.Message.Message;
import com.edu.ufl.cise.cnt5106c.Message.MessageReader;
import com.edu.ufl.cise.cnt5106c.Message.MessageType;
import com.edu.ufl.cise.cnt5106c.Message.MessageWriter;
import com.edu.ufl.cise.cnt5106c.Peer.Peer;
import com.edu.ufl.cise.cnt5106c.Peer.PeerProcess;

public class ConnectionManager implements Runnable {

	private Socket socket;
	private MessageReader messageRead;
	private DataOutputStream outputStream;
	private Peer peer;
	private boolean initiateHandShake;
	private long startTime;
	private long endTime;
	private PeerProcess peerProc;
	public HashSet<Peer> chokedFrom;

	public ConnectionManager(PeerProcess peerProc, Peer peer, boolean initiateHandShake) throws IOException {
		this.setPeerProc(peerProc);
		this.setSocket(peerProc.getPeerSocketMap().get(peer));
		this.setPeer(peer);
		this.getSocket().setSoLinger(true, 70);
		this.setMessageRead(new MessageReader(this.getSocket(), peerProc));
		this.setInitiateHandShake(initiateHandShake);
		this.getPeer().setInterestedFromBitfield(new boolean[CommonPropertiesParser.getNumberOfPieces()]);
		this.chokedFrom = new HashSet<>();
		if (!this.isInitiateHandShake()) {

		} else {
			this.sendHandShake();
		}
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
	 * @return the messageRead
	 */
	public MessageReader getMessageRead() {
		return messageRead;
	}

	/**
	 * @param messageRead
	 *            the messageRead to set
	 */
	public void setMessageRead(MessageReader messageRead) {
		this.messageRead = messageRead;
	}

	/**
	 * @return the outputStream
	 */
	public DataOutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param outputStream
	 *            the outputStream to set
	 */
	public void setOutputStream(DataOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @return the peer
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * @return the initiateHandShake
	 */
	public boolean isInitiateHandShake() {
		return initiateHandShake;
	}

	/**
	 * @param initiateHandShake
	 *            the initiateHandShake to set
	 */
	public void setInitiateHandShake(boolean initiateHandShake) {
		this.initiateHandShake = initiateHandShake;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the peerProc
	 */
	public PeerProcess getPeerProc() {
		return peerProc;
	}

	/**
	 * @param peerProc
	 *            the peerProc to set
	 */
	public void setPeerProc(PeerProcess peerProc) {
		this.peerProc = peerProc;
	}

	private void sendHandShake() throws IOException {
		HandShake handShake = new HandShake(PeerInfoConfigParser.getCurrentPeer().getPeerID());
		try {
			this.peerProc.getBlockingQueueMessages()
					.put(new MessageWriter(handShake, new DataOutputStream(socket.getOutputStream())));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		for (; !this.getPeerProc().isExit();) {
			try {
				Object obj;
				startTime = System.currentTimeMillis();
				obj = messageRead.readObject();
				endTime = System.currentTimeMillis();
				if (obj instanceof HandShake) {
					HandShake handShakeMessage = (HandShake) obj;
					if (ByteBuffer.wrap(handShakeMessage.getPeerID()).getInt() != this.peer.getPeerID()) {

					} else {
						if (initiateHandShake) {
							sendBitfield();
						} else {
							sendHandShake();
						}
					}
				} else if (obj instanceof Message) {
					Message message = (Message) obj;
					System.out.println("Received Message : " + message);
					MessageType messageType = message.getMessageType();
					if (messageType == MessageType.CHOKE) {
						choke(this.peer);
					} else if (messageType == MessageType.UNCHOKE) {
						unchoke(this.peer);
					} else if (messageType == MessageType.INTERESTED) {
						this.peer.setInterestedInPieces(true);
						try {
							peerProc.getBlockingQueueLogging()
									.put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
											+ " received the 'interested' message from " + this.peer.getPeerID());
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} else if (messageType == MessageType.NOT_INTERESTED) {
						this.peer.setInterestedInPieces(false);
						try {
							peerProc.getBlockingQueueLogging()
									.put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
											+ " received the 'not interested' message from " + this.peer.getPeerID());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (messageType == MessageType.HAVE) {
						processHaveMessage(message);
					} else if (messageType == MessageType.BITFIELD) {
						this.peer.setHandShakeDone(true);
						this.peer.setBitfield(message.getPayload());
						if (!initiateHandShake) {
							sendBitfield();
						}
						if (!peerProc.isFilePresent()) {
							sendInterestedifApplicable();
						}
					} else if (messageType == MessageType.REQUEST) {
						processRequest(message);
					} else if (messageType == MessageType.PIECE) {
						processPieceMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				peerProc.setExit(true);
				break;
			}

		}
	}

	private void sendInterestedifApplicable() throws IOException {
		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			int bitAtIndexOfCurrPeer = PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), indexI);
			int bitAtIndexOfPeer = PeerProcess.getBit(this.peer.getBitfield(), indexI);
			if (!(bitAtIndexOfCurrPeer == 0 && bitAtIndexOfPeer == 1)) {

			} else {
				Message interested = new Message(1, Byte.valueOf(Integer.toString(2)), null);
				this.peer.getInterestedFromBitfield()[indexI] = true;
				try {
					peerProc.getBlockingQueueMessages()
							.put(new MessageWriter(interested, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			indexI++;
		}
	}

	private void processPieceMessage(Message message) throws IOException {
		updatePeerDownloadingRate();
		writePieceToFile(message.getPayload());
		sendHaveMessageToAll(message.getPayload());
		if (peerProc.isFileComplete()) {

		} else {
			Vector<Integer> pieceIndex = new Vector<Integer>();
			/*
			 * Get list of all pieces not yet received and for which request has not yet
			 * been sent
			 */
			int indexI = 0;
			while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
				if (!(PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), indexI) == 0
						&& PeerProcess.getBit(this.peer.getBitfield(), indexI) == 1)) {

				} else {
					pieceIndex.add(indexI);
				}
				indexI++;

			}
			if (!(pieceIndex.size() > 0)) {

			} else {
				Random rnd = new Random();
				int selectedIndex = rnd.nextInt(pieceIndex.size());
				sendRequest(this.peer, pieceIndex.get(selectedIndex));
			}
		}
		sendNotInterestedToSomeNeighbours();
	}

	private void updatePeerDownloadingRate() {
		DownloadingRate downRate = new DownloadingRate(this.peerProc, this.peer,
				(double) (CommonPropertiesParser.getPieceSize() / ((this.endTime - this.startTime) + 1)));
		if (peerProc.getUnchokingIntervalWisePeerDownloadingRate().contains(downRate)) {
			peerProc.getUnchokingIntervalWisePeerDownloadingRate().remove(downRate);
			peerProc.getUnchokingIntervalWisePeerDownloadingRate().add(downRate);
		} else {
			peerProc.getUnchokingIntervalWisePeerDownloadingRate().add(downRate);
		}
	}

	private void sendHaveMessageToAll(byte[] payload) throws IOException {
		byte[] i = new byte[4];
		System.arraycopy(payload, 0, i, 0, 4);
		int index = ByteBuffer.wrap(i).getInt();

		if (!(PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), index) == 0)) {

		} else {
			PeerProcess.setBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), index);
			// if file complete set the bit

			Iterator<Peer> iteratorPeer = peerProc.getPeerInfoVector().iterator();
			while (iteratorPeer.hasNext()) {
				Peer p = iteratorPeer.next();
				if (!p.isHandShakeDone()) {

				} else {
					Message have = new Message(5, Byte.valueOf(Integer.toString(4)), i);
					this.socket = peerProc.getPeerSocketMap().get(p);
					try {
						peerProc.getBlockingQueueMessages()
								.put(new MessageWriter(have, new DataOutputStream(socket.getOutputStream())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!peerProc.checkIfFullFileReceived(PeerInfoConfigParser.getCurrentPeer())) {

			} else {
				try {
					peerProc.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
							+ " has downloaded the complete file.");
					peerProc.setExit(true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendNotInterestedToSomeNeighbours() throws IOException {
		Vector<Integer> notInterestedIndices = new Vector<Integer>();

		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			if (!(PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), indexI) == 1)) {

			} else {
				notInterestedIndices.addElement(indexI);
			}
			indexI++;
		}

		Iterator<Peer> iteratorPeer = peerProc.getPeerInfoVector().iterator();
		while (iteratorPeer.hasNext()) {
			Peer p = iteratorPeer.next();
			if (!p.isHandShakeDone()) {

			} else {
				boolean amIInterestedInAnyPiecesOfThisPeer = false;
				int indexJ = 0;
				while (indexJ < CommonPropertiesParser.getNumberOfPieces()) {
					if (!(PeerProcess.getBit(p.getBitfield(), indexJ) == 1 && !notInterestedIndices.contains(indexJ)
							&& !CommonPropertiesParser.getSentRequestMessageByPiece()[peerProc.getPeerInfoVector()
									.indexOf(p)][indexJ])) {

					} else {
						amIInterestedInAnyPiecesOfThisPeer = true;
						break;
					}
					indexJ++;
				}
				if (amIInterestedInAnyPiecesOfThisPeer) {

				} else {
					Message notinterested = new Message(1, Byte.valueOf(Integer.toString(3)), null);
					try {
						peerProc.getBlockingQueueMessages().put(new MessageWriter(notinterested,
								new DataOutputStream(peerProc.getPeerSocketMap().get(p).getOutputStream())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void processHaveMessage(Message message) throws IOException {
		int index = ByteBuffer.wrap(message.getPayload()).getInt();

		if (!(PeerProcess.getBit(this.peer.getBitfield(), index) == 0)) {

		} else {
			PeerProcess.setBit(this.peer.getBitfield(), index);
		}

		try {
			peerProc.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
					+ " received the 'have' message from " + this.peer.getPeerID() + " for the piece " + index + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendInterestedifApplicable();
	}

	private void processRequest(Message message) throws IOException {
		int index = ByteBuffer.wrap(message.getPayload()).getInt();
		if (PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), index) == 1) {
			byte[] piece = new byte[CommonPropertiesParser.getPieceSize() + 4];
			System.arraycopy(message.getPayload(), 0, piece, 0, 4);
			RandomAccessFile randAccessFile = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "r");
			randAccessFile.seek(CommonPropertiesParser.getPieceMatrix()[index][0]);
			randAccessFile.readFully(piece, 4, CommonPropertiesParser.getPieceMatrix()[index][1]);
			randAccessFile.close();
			Message mpiece = new Message(CommonPropertiesParser.getPieceSize() + 5, Byte.valueOf(Integer.toString(7)),
					piece);
			try {
				peerProc.getBlockingQueueMessages()
						.put(new MessageWriter(mpiece, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void writePieceToFile(byte[] payload) throws IOException {
		byte[] tempIndex = new byte[4];
		System.arraycopy(payload, 0, tempIndex, 0, 4);
		int index = ByteBuffer.wrap(tempIndex).getInt();
		byte[] piece = new byte[CommonPropertiesParser.getPieceMatrix()[index][1]];
		System.arraycopy(payload, 4, piece, 0, CommonPropertiesParser.getPieceMatrix()[index][1]);
		RandomAccessFile randAccessFile = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "rw");
		randAccessFile.seek(CommonPropertiesParser.getPieceMatrix()[index][0]);
		randAccessFile.write(piece, 0, CommonPropertiesParser.getPieceMatrix()[index][1]);
		randAccessFile.close();

		int noOperation = 0;
		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			if (!(PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), indexI) == 1)) {

			} else {
				noOperation++;
			}
			indexI++;
		}
		try {
			peerProc.getBlockingQueueLogging()
					.put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID() + " has downloaded the piece "
							+ index + " from " + this.peer.getPeerID() + ". Now the number of pieces it has is "
							+ (noOperation + 1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendBitfield() throws IOException {
		Message m = new Message(PeerInfoConfigParser.getCurrentPeer().getBitfield().length + 1,
				Byte.valueOf(Integer.toString(5)), PeerInfoConfigParser.getCurrentPeer().getBitfield());
		try {
			peerProc.getBlockingQueueMessages()
					.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void choke(Peer p) {
		try {
			peerProc.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
					+ " is choked by " + p.getPeerID() + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		chokedFrom.add(p);
		int indexOfPeer = peerProc.getPeerInfoVector().indexOf(p);
		// reset the sentRequestMessageBy Piece array by comparing the bitfield array
		// and request array
		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			if (!CommonPropertiesParser.getSentRequestMessageByPiece()[indexOfPeer][indexI]) {

			} else {
				// check if piece received, if not reset the request message field
				CommonPropertiesParser.getSentRequestMessageByPiece()[indexOfPeer][indexI] = false;
			}
			indexI++;
		}
	}

	private void unchoke(Peer peer) {
		try {
			peerProc.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
					+ " is unchoked by " + peer.getPeerID() + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		chokedFrom.remove(peer);

		if (peerProc.isFilePresent()) {

		} else {
			// after receiving unchoke, check if this peer is interested in any of the
			// pieces of the peerUnchokedFrom
			// if interested, check if that piece is not requested to any other peer
			Vector<Integer> interestedPieces = new Vector<Integer>();
			int indexI = 0;
			while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
				int bitPresent = PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), indexI);
				int bitPresentAtPeerWeRequesting = PeerProcess.getBit(peer.getBitfield(), indexI);
				if (!(bitPresent == 0 && bitPresentAtPeerWeRequesting == 1)) {

				} else {
					interestedPieces.add(indexI);
				}
				indexI++;
			}
			if (!(interestedPieces.size() > 0)) {

			} else {
				// select any one piece randomly
				Random ran = new Random();
				int index = ran.nextInt(interestedPieces.size());
				sendRequest(peer, interestedPieces.get(index));
			}
		}
	}

	private void sendRequest(Peer p, int pieceIndex) {
		if (!(PeerProcess.getBit(PeerInfoConfigParser.getCurrentPeer().getBitfield(), pieceIndex) == 0
				&& PeerProcess.getBit(p.getBitfield(), pieceIndex) == 1)) {

		} else {
			Message m = new Message(5, Byte.valueOf(Integer.toString(6)),
					ByteBuffer.allocate(4).putInt(pieceIndex).array());

			try {
				peerProc.getBlockingQueueMessages()
						.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			CommonPropertiesParser.getSentRequestMessageByPiece()[peerProc.getPeerInfoVector()
					.indexOf(p)][pieceIndex] = true;
		}
	}
}