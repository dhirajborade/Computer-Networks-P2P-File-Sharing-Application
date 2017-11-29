import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PeerProcess {

	List<Peer> peerInfoVector;
	static Peer currentPeer;
	int noOfPeerHS;
	int noOfPeers;
	boolean isFilePresent;
	ServerSocket serverSocket;
	DateFormat sdf;
	File logfile;
	HashSet<Peer> chokedFrom;
	HashSet<Peer> PreferedNeighbours;
	HashSet<Peer> NewPrefNeighbors;
	HashSet<Peer> sendUnchokePrefNeig;
	Peer optimisticallyUnchokedNeighbor;
	PriorityQueue<DownloadingRate> unchokingIntervalWisePeerDownloadingRate;
	Logger logger;
	static boolean[][] sentRequestMessageByPiece;
	boolean fileComplete;
	static int lastPeerID;
	BlockingQueue<MessageWriter> bqm;
	BlockingQueue<String> bql;
	HashMap<Peer, Socket> peerSocketMap;
	int[][] pieceMatrix;
	public final Object inputSynchronize = new Object();
	Future<?> prefNeighborTask;
	Future<?> optimisticallyUnchokeNeighborTask;
	Future<?> logManagerTask;
	Future<?> messageQueueTask;
	public volatile boolean exit = false;
	static int currentPeerNo = 0;

	PeerProcess() {
		sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		noOfPeers = PeerInfoConfigParser.getTotalPeers();
		fileComplete = false;
		chokedFrom = new HashSet<>();
		peerSocketMap = new HashMap<>();
		bqm = new LinkedBlockingQueue<MessageWriter>();
		bql = new LinkedBlockingQueue<String>();
	}

	public void copyFileUsingStream(String fileSource, String fileDestination) throws IOException {
		FileInputStream srcStream = null;
		FileOutputStream dstStream = null;
		try {
			srcStream = new FileInputStream(fileSource);
			dstStream = new FileOutputStream(fileDestination);
			dstStream.getChannel().transferFrom(srcStream.getChannel(), 0, srcStream.getChannel().size());
		} catch (IOException e) {
		} finally {
			try {
				srcStream.close();
			} catch (Exception e) {
			}
			try {
				dstStream.close();
			} catch (Exception e) {
			}
		}
	}

	private void initateLogFile(String peerId) {
		logger = Logger.getLogger("LogFormatter");
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler(System.getProperty("user.dir") + "//peer_" + peerId + "//log_peer_" + peerId + ".log");
			fh.setFormatter(new LogFormatter());
			logger.addHandler(fh);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		/***
		 * Creates a new process instance with the supplied peerID and initializes the
		 * peer list
		 ***/
		PeerInfoConfigParser peerInfo = new PeerInfoConfigParser();
		CommonPropertiesParser commInfo = new CommonPropertiesParser();
		FileReader peerInfoReader = null;
		FileReader commInfoReader = null;
		try {
			peerInfoReader = new FileReader(PeerInfoConfigParser.getConfigFileName());
			peerInfo.readPeerInfoFile(peerInfoReader);
			/***
			 * Reads common.cfg file and initializes peer process variables
			 ***/
			commInfoReader = new FileReader(CommonPropertiesParser.getConfigFileName());
			commInfo.readCommonFileInfoFile(commInfoReader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		PeerProcess peerProcess = new PeerProcess();
		peerProcess.peerInfoVector = new ArrayList<Peer>();

		try {

			new File("peer_" + args[0]).mkdir();
			peerProcess.initateLogFile(args[0]);

			peerProcess.unchokingIntervalWisePeerDownloadingRate = new PriorityQueue<>(
					new Comparator<DownloadingRate>() {
						/*
						 * (non-Javadoc)
						 *
						 * @see java.util.Comparator#compare(java. lang. Object, java.lang.Object)
						 */
						@Override
						public int compare(DownloadingRate o1, DownloadingRate o2) {
							return o1.downloadingRate > o2.downloadingRate ? 1 : -1;
						}
					});

			peerProcess.pieceMatrix = CommonPropertiesParser.getPieceMatrix();

			/*** Reads peerInfo.cfg file and initializes peerList ***/
			peerInfo.initializePeerList(peerProcess, args[0]);

			PeerProcess.sentRequestMessageByPiece = CommonPropertiesParser.getSentRequestMessageByPiece();

			/*** Initializes File Manager ***/
			peerInfo.initializeFileManager(peerProcess, args[0]);

			lastPeerID = peerInfo.getLastPeerID();
			currentPeer = peerInfo.getCurrentPeer();
			currentPeerNo = peerInfo.getCurrentPeerNo();

			peerInfo.establishConnection(peerProcess);

			peerProcess.createServerSocket(PeerProcess.currentPeer.getPeerPortNumber());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createServerSocket(int portNo) {
		ExecutorService exec = Executors.newFixedThreadPool(4);
		try {
			prefNeighborTask = exec.submit(new PrefferedNeighborsThread(PeerProcess.this));
			optimisticallyUnchokeNeighborTask = exec.submit(new OptimisticallyUnchokedNeighborThread(PeerProcess.this));
			messageQueueTask = exec.submit(new MessageQueueProcess(PeerProcess.this));
			logManagerTask = exec.submit(new LogManager(PeerProcess.this.bql, logger, this));

			int peerCompleteFileReceived = 0;
			serverSocket = new ServerSocket(portNo);
			int totalConnectedPeers = 0;

			while (!PeerProcess.this.exit) {
				peerCompleteFileReceived = 0;
				if (!(currentPeer.getPeerID() != lastPeerID && totalConnectedPeers < peerInfoVector.size())) {

				} else {
					Socket socket;
					if (!(totalConnectedPeers != this.noOfPeers)) {

					} else {
						socket = serverSocket.accept();
						Peer tempPeer = getPeerFromPeerList(socket.getInetAddress().getHostAddress(), socket.getPort());
						PeerProcess.this.bql.put(
								"Peer " + currentPeer.getPeerID() + " is connected from Peer " + tempPeer.getPeerID());
						peerSocketMap.put(peerInfoVector.get(peerInfoVector.indexOf(tempPeer)), socket);
						ClientHandler clientHandler = new ClientHandler(tempPeer, false);
						clientHandler.start();
						totalConnectedPeers++;
					}
				}
				// check for termination of this process
				Iterator<Peer> iter = peerInfoVector.iterator();
				while (iter.hasNext()) {
					Peer p = iter.next();
					if (!checkIfFullFileRecieved(p)) {

					} else {
						peerCompleteFileReceived++;
					}
				}

				if (peerCompleteFileReceived != peerInfoVector.size()) {

				} else {
					// check if you recievecd the whole file
					if (!checkIfFullFileRecieved(currentPeer)) {

					} else {
						// now terminate the process of executorService
						this.exit = true;
						break;
					}
				}
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {

				for (; !exec.isTerminated();) {
					prefNeighborTask.cancel(true);
					optimisticallyUnchokeNeighborTask.cancel(true);
					for (; !bqm.isEmpty();) {
					}
					for (; !bql.isEmpty();) {
					}
					messageQueueTask.cancel(true);
					logManagerTask.cancel(true);
					exec.shutdownNow();
				}

				Iterator<Socket> iter = peerSocketMap.values().iterator();
				while (iter.hasNext()) {
					Socket s = iter.next();
					if (s.isClosed()) {

					} else {
						s.close();
					}
				}

				serverSocket.close();

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		}

	}

	/**
	 * @param hostAddress
	 * @param port
	 * @return
	 *
	 */
	private Peer getPeerFromPeerList(String hostAddress, int port) {

		Iterator<Peer> it = this.peerInfoVector.iterator();
		while (it.hasNext()) {

			Peer tempPeer = (Peer) it.next();
			System.out.println(port);
			if (tempPeer.getPeerIP().equals(hostAddress))
				return tempPeer;
		}
		return null;
	}

	public void connectToPreviousPeer(Peer p) {
		Socket socket;
		try {
			socket = new Socket(p.getPeerIP(), p.getPeerPortNumber());
			PeerProcess.this.bql
					.put("Peer " + currentPeer.getPeerID() + " makes a connection to Peer " + p.getPeerID());
			peerSocketMap.put(peerInfoVector.get(this.peerInfoVector.indexOf(p)), socket);
			ClientHandler clientHandler = new ClientHandler(p, true);
			clientHandler.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setBit(byte[] b, int index) {
		byte b1 = 1;
		b[index / 8] = (byte) (b[index / 8] | b1 << ((index) % 8));
	}

	public static int getBit(byte[] b, int index) {
		byte b1 = b[index / 8];
		byte be = 1;

		if ((b1 & (be << ((index) % 8))) != 0)
			return 1;
		else
			return 0;

	}

	public static void clearBit(byte[] b, int index) {
		byte b1 = 1;
		b[index / 8] = (byte) (b[index / 8] & (~(b1 << ((index) % 8))));

	}

	public boolean checkIfFullFileRecieved(Peer p) {
		for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
			if (getBit(p.getBitfield(), i) == 0) {
				return false;
			}
		}
		return true;
	}

	public class ClientHandler extends Thread {

		private Socket socket;
		MessageReader mread;
		DataOutputStream outputStream;
		Peer peer;
		boolean initiateHandShake;
		long starttime, endtime;

		ClientHandler(Peer p, boolean initiateHS) throws IOException {
			this.socket = PeerProcess.this.peerSocketMap.get(p);
			this.peer = p;

			socket.setSoLinger(true, 70);
			mread = new MessageReader(socket, PeerProcess.this);
			this.initiateHandShake = initiateHS;

			this.peer.setInterestedFromBitfield(new boolean[CommonPropertiesParser.getNumberOfPieces()]);

			if (initiateHandShake)
				sendHandShake();

		}

		/**
		 * @throws IOException
		 *
		 *
		 */
		private void sendHandShake() throws IOException {
			HandShake hs = new HandShake(PeerProcess.currentPeer.getPeerID());

			try {
				PeerProcess.this.bqm.put(new MessageWriter(hs, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {

			while (!PeerProcess.this.exit) {
				try {

					Object o;
					starttime = System.currentTimeMillis();
					o = mread.readObject();
					endtime = System.currentTimeMillis();

					/*
					 * if (o == null) { continue; }
					 */
					if (o instanceof HandShake) {
						HandShake h = (HandShake) o;
						if (ByteBuffer.wrap(h.peerID).getInt() == this.peer.getPeerID()) {

							if (!initiateHandShake)
								sendHandShake();
							else {
								sendBitfield();
							}
						}
						PeerProcess.this.noOfPeerHS++;
					} else if (o instanceof Message) {

						Message message = (Message) o;
						System.out.println(message);
						switch (Byte.toUnsignedInt(message.type)) {

						case 0:
							choke(peer);
							break;
						case 1:
							unchoke(peer);
							break;

						case 2:
							this.peer.setInterestedInPieces(true);
							try {
								PeerProcess.this.bql.put("Peer " + PeerProcess.currentPeer.getPeerID()
										+ " received the 'interested' message from " + peer.getPeerID());
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							break;
						case 3:
							this.peer.setInterestedInPieces(false);
							try {
								PeerProcess.this.bql.put("Peer " + PeerProcess.currentPeer.getPeerID()
										+ " received the 'not interested' message from " + peer.getPeerID());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;

						case 4:
							processHaveMessage(message);
							break;

						case 5:
							this.peer.setHandShakeDone(true);
							peer.setBitfield(message.payload);
							if (!initiateHandShake)
								sendBitfield();

							if (!PeerProcess.this.isFilePresent) {

								sendInterestedifApplicable();
							}

							break;

						case 6:
							processRequest(message);
							break;

						case 7:
							processPieceMessage(message);

							break;

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					PeerProcess.this.exit = true;
					break;
				}

			}
		}

		/**
		 * @throws IOException
		 *
		 *
		 */
		private void sendInterestedifApplicable() throws IOException {

			for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
				int bitAtIndexOfCurrPeer = getBit(currentPeer.getBitfield(), i);
				int bitAtIndexOfPeer = getBit(peer.getBitfield(), i);
				if (bitAtIndexOfCurrPeer == 0 && bitAtIndexOfPeer == 1) {

					Message interested = new Message(1, Byte.valueOf(Integer.toString(2)), null);
					// update the interested from array
					this.peer.getInterestedFromBitfield()[i] = true;
					try {
						PeerProcess.this.bqm
								.put(new MessageWriter(interested, new DataOutputStream(socket.getOutputStream())));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					break;
				}
			}

		}

		private void processPieceMessage(Message message) throws IOException {

			updatePeerDownloadingRate();

			writePieceToFile(message.payload);

			sendHaveMessageToAll(message.payload);

			if (!fileComplete) {
				List<Integer> pieceIndex = new ArrayList<Integer>();

				/*
				 * Get list of all pieces not yet received and for which request has not yet
				 * been sent
				 */
				for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
					if (getBit(PeerProcess.currentPeer.getBitfield(), i) == 0 && getBit(peer.getBitfield(), i) == 1) {
						pieceIndex.add(i);
					}

				}
				if (pieceIndex.size() > 0) {
					Random rnd = new Random();
					int selectedIndex = rnd.nextInt(pieceIndex.size());
					sendRequest(peer, pieceIndex.get(selectedIndex));
				}
			}

			sendNIToSomeNeighbours();
		}

		/**
		 *
		 *
		 */
		private void updatePeerDownloadingRate() {
			DownloadingRate dr = new DownloadingRate(peer,
					(double) (CommonPropertiesParser.getPieceSize() / ((this.endtime - this.starttime) + 1)));

			if (!unchokingIntervalWisePeerDownloadingRate.contains(dr))
				unchokingIntervalWisePeerDownloadingRate.add(dr);
			else {
				unchokingIntervalWisePeerDownloadingRate.remove(dr);
				unchokingIntervalWisePeerDownloadingRate.add(dr);
			}
		}

		/**
		 * @throws IOException
		 *
		 *
		 */
		private void sendHaveMessageToAll(byte[] payload) throws IOException {
			byte[] i = new byte[4];
			System.arraycopy(payload, 0, i, 0, 4);
			int index = ByteBuffer.wrap(i).getInt();

			if (getBit(PeerProcess.currentPeer.getBitfield(), index) == 0) {
				setBit(PeerProcess.currentPeer.getBitfield(), index);
				// if file complete set the bit

				for (Peer p : PeerProcess.this.peerInfoVector) {
					if (p.isHandShakeDone()) {
						Message have = new Message(5, Byte.valueOf(Integer.toString(4)), i);
						this.socket = PeerProcess.this.peerSocketMap.get(p);
						try {
							PeerProcess.this.bqm
									.put(new MessageWriter(have, new DataOutputStream(socket.getOutputStream())));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if (checkIfFullFileRecieved(PeerProcess.currentPeer)) {
					try {
						PeerProcess.this.bql.put(
								"Peer " + PeerProcess.currentPeer.getPeerID() + " has downloaded the complete file.");
						PeerProcess.this.exit = true;
					} catch (InterruptedException e) {
						e.printStackTrace();

					}
				}

			}
		}

		/**
		 * @throws IOException
		 *
		 *
		 */
		private void sendNIToSomeNeighbours() throws IOException {
			List<Integer> NIIndices = new ArrayList<Integer>();

			for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
				if (getBit(PeerProcess.currentPeer.getBitfield(), i) == 1)
					NIIndices.add(i);
			}

			for (Peer p : PeerProcess.this.peerInfoVector) {
				if (p.isHandShakeDone()) {
					boolean amIInterestedInAnyPiecesOfThisPeer = false;
					for (int j = 0; j < CommonPropertiesParser.getNumberOfPieces(); j++) {
						if (getBit(p.getBitfield(), j) == 1 && !NIIndices.contains(j)
								&& !PeerProcess.sentRequestMessageByPiece[peerInfoVector.indexOf(p)][j]) {
							{
								amIInterestedInAnyPiecesOfThisPeer = true;
								break;
							}
						}

					}
					if (!amIInterestedInAnyPiecesOfThisPeer) {
						Message notinterested = new Message(1, Byte.valueOf(Integer.toString(3)), null);
						try {
							PeerProcess.this.bqm.put(new MessageWriter(notinterested,
									new DataOutputStream(peerSocketMap.get(p).getOutputStream())));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		/**
		 * @param message
		 * @throws IOException
		 *
		 *
		 */
		private void processHaveMessage(Message message) throws IOException {
			int index = ByteBuffer.wrap(message.payload).getInt();

			if (getBit(this.peer.getBitfield(), index) == 0)
				setBit(this.peer.getBitfield(), index);

			try {
				PeerProcess.this.bql.put("Peer " + PeerProcess.currentPeer.getPeerID()
						+ " received the 'have' message from " + peer.getPeerID() + " for the piece " + index + ".");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			sendInterestedifApplicable();
			/*
			 * if (getBit(PeerProcess.currentPeer.bitfield, index) == 0) { Message
			 * interested = new Message(1, Byte.valueOf(Integer.toString(2)), null); try {
			 * PeerProcess.this.bqm .put(new MessageWriter(interested, new
			 * DataOutputStream(socket.getOutputStream()))); } catch (InterruptedException
			 * e) { e.printStackTrace(); } // update the interested from array
			 * this.peer.interestedFromBitfield[index] = true; }
			 */
		}

		/**
		 * @param message
		 * @throws IOException
		 *
		 *
		 */
		private void processRequest(Message message) throws IOException {
			/*
			 * if ((PeerProcess.this.PreferedNeighbours != null &&
			 * PeerProcess.this.PreferedNeighbours.contains(peer)) ||
			 * (PeerProcess.this.optimisticallyUnchokedNeighbor != null &&
			 * PeerProcess.this.optimisticallyUnchokedNeighbor.equals(peer))) {
			 */
			int index = ByteBuffer.wrap(message.payload).getInt();
			if (getBit(PeerProcess.currentPeer.getBitfield(), index) == 1) {
				byte[] piece = new byte[CommonPropertiesParser.getPieceSize() + 4];
				System.arraycopy(message.payload, 0, piece, 0, 4);
				RandomAccessFile rafr = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "r");
				rafr.seek(PeerProcess.this.pieceMatrix[index][0]);
				rafr.readFully(piece, 4, PeerProcess.this.pieceMatrix[index][1]);
				rafr.close();
				Message mpiece = new Message(CommonPropertiesParser.getPieceSize() + 5,
						Byte.valueOf(Integer.toString(7)), piece);
				try {
					PeerProcess.this.bqm.put(new MessageWriter(mpiece, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// }
		}

		/**
		 * @param piece
		 * @throws IOException
		 *
		 */
		private void writePieceToFile(byte[] payload) throws IOException {
			byte[] i = new byte[4];
			System.arraycopy(payload, 0, i, 0, 4);
			int index = ByteBuffer.wrap(i).getInt();
			byte[] piece = new byte[PeerProcess.this.pieceMatrix[index][1]];
			System.arraycopy(payload, 4, piece, 0, PeerProcess.this.pieceMatrix[index][1]);
			RandomAccessFile rafw = new RandomAccessFile(new File(CommonPropertiesParser.getFileName()), "rw");
			rafw.seek(PeerProcess.this.pieceMatrix[index][0]);
			rafw.write(piece, 0, PeerProcess.this.pieceMatrix[index][1]);
			rafw.close();

			int nop = 0;

			for (int j = 0; j < CommonPropertiesParser.getNumberOfPieces(); j++)
				if (getBit(PeerProcess.currentPeer.getBitfield(), j) == 1)
					nop++;

			try {
				PeerProcess.this.bql.put(
						"Peer " + PeerProcess.currentPeer.getPeerID() + " has downloaded the piece " + index + " from "
								+ this.peer.getPeerID() + ". Now the number of pieces it has is " + (nop + 1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		private void sendBitfield() throws IOException {
			Message m = new Message(PeerProcess.currentPeer.getBitfield().length + 1, Byte.valueOf(Integer.toString(5)),
					PeerProcess.currentPeer.getBitfield());
			try {
				PeerProcess.this.bqm.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 *
		 */
		private void choke(Peer p) {
			try {
				PeerProcess.this.bql
						.put("Peer " + PeerProcess.currentPeer.getPeerID() + " is choked by " + p.getPeerID() + ".");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chokedFrom.add(p);
			int indexOfPeer = peerInfoVector.indexOf(p);
			// reset the sentRequestMessageBy Piece array by comparing the
			// bitfield array and request array
			for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
				if (PeerProcess.sentRequestMessageByPiece[indexOfPeer][i]) {
					// check if piece received, if not reset the request message
					// field
					PeerProcess.sentRequestMessageByPiece[indexOfPeer][i] = false;
				}
			}
		}

		/**
		 * @param p
		 */
		private void unchoke(Peer p) {
			try {
				PeerProcess.this.bql
						.put("Peer " + PeerProcess.currentPeer.getPeerID() + " is unchoked by " + p.getPeerID() + ".");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chokedFrom.remove(p);

			if (!isFilePresent) {
				// after receiving unchoke, check if this peer is interested in
				// any
				// of the pieces of the peerUnchokedFrom
				// if interested, check if that piece is not requested to any
				// other
				// peer
				List<Integer> interestedPieces = new ArrayList<Integer>();
				for (int i = 0; i < CommonPropertiesParser.getNumberOfPieces(); i++) {
					int bitPresent = getBit(currentPeer.getBitfield(), i);
					int bitPresentAtPeerWeRequesting = getBit(p.getBitfield(), i);
					if (bitPresent == 0 && bitPresentAtPeerWeRequesting == 1) {
						interestedPieces.add(i);
					}
				}
				if (interestedPieces.size() > 0) {
					// select any one piece randomly
					Random ran = new Random();
					int index = ran.nextInt(interestedPieces.size());
					// PeerProcess.this.sentRequestMessageByPiece[indexOfPeer][index]
					// =
					// true;
					sendRequest(p, interestedPieces.get(index));
				}
			}
		}

		private void sendRequest(Peer p, int pieceIndex) {
			if (getBit(PeerProcess.currentPeer.getBitfield(), pieceIndex) == 0
					&& getBit(p.getBitfield(), pieceIndex) == 1) {
				Message m = new Message(5, Byte.valueOf(Integer.toString(6)),
						ByteBuffer.allocate(4).putInt(pieceIndex).array());

				try {
					PeerProcess.this.bqm.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				PeerProcess.sentRequestMessageByPiece[PeerProcess.this.peerInfoVector.indexOf(p)][pieceIndex] = true;
			}
		}
	}

	public class DownloadingRate {
		Peer p;
		double downloadingRate;

		/**
		 * @param p
		 * @param downloadingRate
		 */
		public DownloadingRate(Peer p, double downloadingRate) {
			super();
			this.p = p;
			this.downloadingRate = downloadingRate;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((p == null) ? 0 : p.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DownloadingRate other = (DownloadingRate) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (p == null) {
				if (other.p != null)
					return false;
			} else if (!p.equals(other.p))
				return false;
			return true;
		}

		private PeerProcess getOuterType() {
			return PeerProcess.this;
		}

	}

	public void sendChokeMessage(HashSet<Peer> peers) {
		Message m = new Message(1, Byte.valueOf(Integer.toString(0)), null);
		for (Peer p : peers) {
			if (p.isHandShakeDone()) {
				try {
					Socket socket = PeerProcess.this.peerSocketMap.get(p);
					PeerProcess.this.bqm.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void sendUnChokeMessage(HashSet<Peer> peers) {
		Message m = new Message(1, Byte.valueOf(Integer.toString(1)), null);
		for (Peer p : peers) {
			if (p.isHandShakeDone()) {
				try {
					System.out.println("Sent Unchoke to " + p.getPeerID());
					Socket socket = PeerProcess.this.peerSocketMap.get(p);
					PeerProcess.this.bqm.put(new MessageWriter(m, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
