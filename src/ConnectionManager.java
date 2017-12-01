import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class ConnectionManager implements Runnable {

	private Socket socket;
	private MessageReader messageRead;
	private DataOutputStream outputStream;
	private Peer peer;
	private boolean initiateHandShake;
	private long startTime;
	private long endTime;
	private PeerProcess peerProc;

	ConnectionManager(PeerProcess peerProc, Peer peer, boolean initiateHandShake) throws IOException {
		this.setPeerProc(peerProc);
		this.setSocket(peerProc.peerSocketMap.get(peer));
		this.setPeer(peer);
		this.getSocket().setSoLinger(true, 70);
		this.setMessageRead(new MessageReader(this.getSocket(), peerProc));
		this.setInitiateHandShake(initiateHandShake);
		this.getPeer().setInterestedFromBitfield(new boolean[CommonPropertiesParser.getNumberOfPieces()]);
		this.messageRead = new MessageReader(this.socket, peerProc);
		this.initiateHandShake = initiateHandShake;
		this.peer.setInterestedFromBitfield(new boolean[CommonPropertiesParser.getNumberOfPieces()]);
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
		HandShake hs = new HandShake(PeerProcess.currentPeer.getPeerID());
		try {
			this.peerProc.blockingQueueMessageWriter.put(new MessageWriter(hs, new DataOutputStream(socket.getOutputStream())));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		for (; !peerProc.exit;) {
			try {
				Object obj;
				startTime = System.currentTimeMillis();
				obj = messageRead.readObject();
				endTime = System.currentTimeMillis();
				if (obj instanceof HandShake) {
					HandShake handShakeMessage = (HandShake) obj;
					if (ByteBuffer.wrap(handShakeMessage.getPeerID()).getInt() != this.peer.getPeerID()) {

					} else {
						if (initiateHandShake)
							sendBitfield();
						else {
							sendHandShake();
						}
					}
				} else if (obj instanceof Message) {
					Message message = (Message) obj;
					System.out.println(message);
					int messageType = Byte.toUnsignedInt(message.getType());
					if (messageType == 0) {
						choke(this.peer);
					} else if (messageType == 1) {
						unchoke(this.peer);
					} else if (messageType == 2) {
						this.peer.setInterestedInPieces(true);
						try {
							peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID()
									+ " received the 'interested' message from " + this.peer.getPeerID());
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} else if (messageType == 3) {
						this.peer.setInterestedInPieces(false);
						try {
							peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID()
									+ " received the 'not interested' message from " + this.peer.getPeerID());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (messageType == 4) {
						processHaveMessage(message);
					} else if (messageType == 5) {
						this.peer.setHandShakeDone(true);
						this.peer.setBitfield(message.getPayload());
						if (!initiateHandShake)
							sendBitfield();
						if (!peerProc.isFilePresent) {
							sendInterestedifApplicable();
						}
					} else if (messageType == 6) {
						processRequest(message);
					} else if (messageType == 7) {
						processPieceMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				peerProc.exit = true;
				break;
			}

		}
	}

	private void sendInterestedifApplicable() throws IOException {
		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			int bitAtIndexOfCurrPeer = PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), indexI);
			int bitAtIndexOfPeer = PeerProcess.getBit(this.peer.getBitfield(), indexI);
			if (!(bitAtIndexOfCurrPeer == 0 && bitAtIndexOfPeer == 1)) {

			} else {
				Message interested = new Message(1, Byte.valueOf(Integer.toString(2)), null);
				this.peer.getInterestedFromBitfield()[indexI] = true;
				try {
					peerProc.blockingQueueMessageWriter.put(new MessageWriter(interested, new DataOutputStream(socket.getOutputStream())));
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
		if (peerProc.fileComplete) {

		} else {
			List<Integer> pieceIndex = new ArrayList<Integer>();
			/*
			 * Get list of all pieces not yet received and for which request has not yet
			 * been sent
			 */
			int indexI = 0;
			while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
				if (!(PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), indexI) == 0
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
		if (peerProc.unchokingIntervalWisePeerDownloadingRate.contains(downRate)) {
			peerProc.unchokingIntervalWisePeerDownloadingRate.remove(downRate);
			peerProc.unchokingIntervalWisePeerDownloadingRate.add(downRate);
		} else {
			peerProc.unchokingIntervalWisePeerDownloadingRate.add(downRate);
		}
	}

	private void sendHaveMessageToAll(byte[] payload) throws IOException {
		byte[] i = new byte[4];
		System.arraycopy(payload, 0, i, 0, 4);
		int index = ByteBuffer.wrap(i).getInt();

		if (PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), index) == 0) {
			PeerProcess.setBit(PeerProcess.currentPeer.getBitfield(), index);
			// if file complete set the bit

			Iterator<Peer> iter = peerProc.peerInfoVector.iterator();
			while (iter.hasNext()) {
				Peer p = iter.next();
				if (!p.isHandShakeDone()) {

				} else {
					Message have = new Message(5, Byte.valueOf(Integer.toString(4)), i);
					this.socket = peerProc.peerSocketMap.get(p);
					try {
						peerProc.blockingQueueMessageWriter.put(new MessageWriter(have, new DataOutputStream(socket.getOutputStream())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (!peerProc.checkIfFullFileRecieved(PeerProcess.currentPeer)) {

			} else {
				try {
					peerProc.blockingQueueLogWriter
							.put("Peer " + PeerProcess.currentPeer.getPeerID() + " has downloaded the complete file.");
					peerProc.exit = true;
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
			if (PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), indexI) != 1) {

			} else {
				notInterestedIndices.addElement(indexI);
			}
			indexI++;
		}

		Iterator<Peer> iter = peerProc.peerInfoVector.iterator();
		while (iter.hasNext()) {
			Peer p = iter.next();
			if (p.isHandShakeDone()) {
				boolean amIInterestedInAnyPiecesOfThisPeer = false;
				int indexJ = 0;
				while (indexJ < CommonPropertiesParser.getNumberOfPieces()) {
					if (!(PeerProcess.getBit(p.getBitfield(), indexJ) == 1 && !notInterestedIndices.contains(indexJ)
							&& !PeerProcess.sentRequestMessageByPiece[peerProc.peerInfoVector.indexOf(p)][indexJ])) {

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
						peerProc.blockingQueueMessageWriter.put(new MessageWriter(notinterested,
								new DataOutputStream(peerProc.peerSocketMap.get(p).getOutputStream())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void processHaveMessage(Message message) throws IOException {
		int index = ByteBuffer.wrap(message.getPayload()).getInt();

		if (PeerProcess.getBit(this.peer.getBitfield(), index) != 0) {

		} else {
			PeerProcess.setBit(this.peer.getBitfield(), index);
		}

		try {
			peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID() + " received the 'have' message from "
					+ this.peer.getPeerID() + " for the piece " + index + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendInterestedifApplicable();
	}

	private void processRequest(Message message) throws IOException {
		int index = ByteBuffer.wrap(message.getPayload()).getInt();
		if (PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), index) != 1) {

		} else {
			byte[] piece = new byte[CommonPropertiesParser.getPieceSize() + 4];
			System.arraycopy(message.getPayload(), 0, piece, 0, 4);
			RandomAccessFile rafr = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "r");
			rafr.seek(peerProc.pieceMatrix[index][0]);
			rafr.readFully(piece, 4, peerProc.pieceMatrix[index][1]);
			rafr.close();
			Message mpiece = new Message(CommonPropertiesParser.getPieceSize() + 5, Byte.valueOf(Integer.toString(7)),
					piece);
			try {
				peerProc.blockingQueueMessageWriter.put(new MessageWriter(mpiece, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void writePieceToFile(byte[] payload) throws IOException {
		byte[] i = new byte[4];
		System.arraycopy(payload, 0, i, 0, 4);
		int index = ByteBuffer.wrap(i).getInt();
		byte[] piece = new byte[peerProc.pieceMatrix[index][1]];
		System.arraycopy(payload, 4, piece, 0, peerProc.pieceMatrix[index][1]);
		RandomAccessFile rafw = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "rw");
		rafw.seek(peerProc.pieceMatrix[index][0]);
		rafw.write(piece, 0, peerProc.pieceMatrix[index][1]);
		rafw.close();

		int nop = 0;
		int indexJ = 0;
		while (indexJ < CommonPropertiesParser.getNumberOfPieces()) {
			if (PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), indexJ) != 1) {

			} else {
				nop++;
			}
			indexJ++;
		}
		try {
			peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID() + " has downloaded the piece " + index
					+ " from " + this.peer.getPeerID() + ". Now the number of pieces it has is " + (nop + 1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendBitfield() throws IOException {
		Message m = new Message(PeerProcess.currentPeer.getBitfield().length + 1, Byte.valueOf(Integer.toString(5)),
				PeerProcess.currentPeer.getBitfield());
		try {
			peerProc.blockingQueueMessageWriter.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void choke(Peer p) {
		try {
			peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID() + " is choked by " + p.getPeerID() + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		peerProc.chokedFrom.add(p);
		int indexOfPeer = peerProc.peerInfoVector.indexOf(p);
		// reset the sentRequestMessageBy Piece array by comparing the
		// bitfield array and request array
		int i = 0;
		while (i < CommonPropertiesParser.getNumberOfPieces()) {
			if (PeerProcess.sentRequestMessageByPiece[indexOfPeer][i]) {

			} else {
				// check if piece received, if not reset the request message
				// field
				PeerProcess.sentRequestMessageByPiece[indexOfPeer][i] = false;
			}
			i++;
		}
	}

	private void unchoke(Peer p) {
		try {
			peerProc.blockingQueueLogWriter.put("Peer " + PeerProcess.currentPeer.getPeerID() + " is unchoked by " + p.getPeerID() + ".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		peerProc.chokedFrom.remove(p);

		if (peerProc.isFilePresent) {

		} else {
			// after receiving unchoke, check if this peer is interested in
						// any
						// of the pieces of the peerUnchokedFrom
						// if interested, check if that piece is not requested to any
						// other
						// peer
						Vector<Integer> interestedPieces = new Vector<Integer>();
						int indexI = 0;
						while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
							int bitPresent = PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), indexI);
							int bitPresentAtPeerWeRequesting = PeerProcess.getBit(p.getBitfield(), indexI);
							if (!(bitPresent == 0 && bitPresentAtPeerWeRequesting == 1)) {

							} else {
								interestedPieces.addElement(indexI);
							}
							indexI++;
						}
						if (!(interestedPieces.size() > 0)) {

						} else {
							// select any one piece randomly
							Random rand = new Random();
							int index = rand.nextInt(interestedPieces.size());
							sendRequest(p, interestedPieces.get(index));
						}
		}
	}

	private void sendRequest(Peer p, int pieceIndex) {
		if (!(PeerProcess.getBit(PeerProcess.currentPeer.getBitfield(), pieceIndex) == 0
				&& PeerProcess.getBit(p.getBitfield(), pieceIndex) == 1)) {

		} else {
			Message m = new Message(5, Byte.valueOf(Integer.toString(6)),
					ByteBuffer.allocate(4).putInt(pieceIndex).array());

			try {
				peerProc.blockingQueueMessageWriter.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			PeerProcess.sentRequestMessageByPiece[peerProc.peerInfoVector.indexOf(p)][pieceIndex] = true;
		}
	}
}