import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
						@Override
						public int compare(DownloadingRate o1, DownloadingRate o2) {
							if (o1.getDownloadingRate() > o2.getDownloadingRate()) {
								return 1;
							} else {
								return -1;
							}
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

			for (;!PeerProcess.this.exit;) {
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
						new Thread(new ConnectionManager(this, tempPeer, false)).start();
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
					// check if you received the whole file
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
					prefNeighborTask.cancel(false);
					optimisticallyUnchokeNeighborTask.cancel(false);
					for (; !bqm.isEmpty();) {
					}
					for (; !bql.isEmpty();) {
					}
					messageQueueTask.cancel(false);
					logManagerTask.cancel(false);
					exec.awaitTermination(1, TimeUnit.SECONDS);
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
			new Thread(new ConnectionManager(this, p, true)).start();
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
