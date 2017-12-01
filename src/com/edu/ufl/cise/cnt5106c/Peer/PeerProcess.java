package com.edu.ufl.cise.cnt5106c.Peer;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.edu.ufl.cise.cnt5106c.Configuration.CommonPropertiesParser;
import com.edu.ufl.cise.cnt5106c.Configuration.DownloadingRate;
import com.edu.ufl.cise.cnt5106c.Configuration.PeerInfoConfigParser;
import com.edu.ufl.cise.cnt5106c.Logger.LogFormatter;
import com.edu.ufl.cise.cnt5106c.Logger.LogManager;
import com.edu.ufl.cise.cnt5106c.Managers.ConnectionManager;
import com.edu.ufl.cise.cnt5106c.Managers.PeerManager;
import com.edu.ufl.cise.cnt5106c.Message.Message;
import com.edu.ufl.cise.cnt5106c.Message.MessageQueueProcess;
import com.edu.ufl.cise.cnt5106c.Message.MessageWriter;

public class PeerProcess {

	private Vector<Peer> peerInfoVector;
	private boolean isFilePresent;
	private ServerSocket serverSocket;
	private DateFormat sdf;
	private File logfile;
	private PriorityQueue<DownloadingRate> unchokingIntervalWisePeerDownloadingRate;
	private Logger logger;
	private boolean fileComplete;
	private BlockingQueue<MessageWriter> blockingQueueMessages;
	private BlockingQueue<String> blockingQueueLogging;
	private HashMap<Peer, Socket> peerSocketMap;
	private final Object inputSynchronize = new Object();
	private Future<?> logManagerTask;
	private Future<?> messageQueueTask;
	private volatile boolean exit = false;

	PeerProcess() {
		sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fileComplete = false;
		peerSocketMap = new HashMap<>();
		blockingQueueMessages = new LinkedBlockingQueue<MessageWriter>();
		blockingQueueLogging = new LinkedBlockingQueue<String>();
	}

	/**
	 * @return the peerInfoVector
	 */
	public Vector<Peer> getPeerInfoVector() {
		return peerInfoVector;
	}

	/**
	 * @param peerInfoVector
	 *            the peerInfoVector to set
	 */
	public void setPeerInfoVector(Vector<Peer> peerInfoVector) {
		this.peerInfoVector = peerInfoVector;
	}

	/**
	 * @return the isFilePresent
	 */
	public boolean isFilePresent() {
		return isFilePresent;
	}

	/**
	 * @param isFilePresent
	 *            the isFilePresent to set
	 */
	public void setFilePresent(boolean isFilePresent) {
		this.isFilePresent = isFilePresent;
	}

	/**
	 * @return the serverSocket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * @param serverSocket
	 *            the serverSocket to set
	 */
	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	/**
	 * @return the sdf
	 */
	public DateFormat getSdf() {
		return sdf;
	}

	/**
	 * @param sdf
	 *            the sdf to set
	 */
	public void setSdf(DateFormat sdf) {
		this.sdf = sdf;
	}

	/**
	 * @return the logfile
	 */
	public File getLogfile() {
		return logfile;
	}

	/**
	 * @param logfile
	 *            the logfile to set
	 */
	public void setLogfile(File logfile) {
		this.logfile = logfile;
	}

	/**
	 * @return the unchokingIntervalWisePeerDownloadingRate
	 */
	public PriorityQueue<DownloadingRate> getUnchokingIntervalWisePeerDownloadingRate() {
		return unchokingIntervalWisePeerDownloadingRate;
	}

	/**
	 * @param unchokingIntervalWisePeerDownloadingRate
	 *            the unchokingIntervalWisePeerDownloadingRate to set
	 */
	public void setUnchokingIntervalWisePeerDownloadingRate(
			PriorityQueue<DownloadingRate> unchokingIntervalWisePeerDownloadingRate) {
		this.unchokingIntervalWisePeerDownloadingRate = unchokingIntervalWisePeerDownloadingRate;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return the fileComplete
	 */
	public boolean isFileComplete() {
		return fileComplete;
	}

	/**
	 * @param fileComplete
	 *            the fileComplete to set
	 */
	public void setFileComplete(boolean fileComplete) {
		this.fileComplete = fileComplete;
	}

	/**
	 * @return the blockingQueueMessages
	 */
	public BlockingQueue<MessageWriter> getBlockingQueueMessages() {
		return blockingQueueMessages;
	}

	/**
	 * @param blockingQueueMessages
	 *            the blockingQueueMessages to set
	 */
	public void setBlockingQueueMessages(BlockingQueue<MessageWriter> blockingQueueMessages) {
		this.blockingQueueMessages = blockingQueueMessages;
	}

	/**
	 * @return the blockingQueueLogging
	 */
	public BlockingQueue<String> getBlockingQueueLogging() {
		return blockingQueueLogging;
	}

	/**
	 * @param blockingQueueLogging
	 *            the blockingQueueLogging to set
	 */
	public void setBlockingQueueLogging(BlockingQueue<String> blockingQueueLogging) {
		this.blockingQueueLogging = blockingQueueLogging;
	}

	/**
	 * @return the peerSocketMap
	 */
	public HashMap<Peer, Socket> getPeerSocketMap() {
		return peerSocketMap;
	}

	/**
	 * @param peerSocketMap
	 *            the peerSocketMap to set
	 */
	public void setPeerSocketMap(HashMap<Peer, Socket> peerSocketMap) {
		this.peerSocketMap = peerSocketMap;
	}

	/**
	 * @return the logManagerTask
	 */
	public Future<?> getLogManagerTask() {
		return logManagerTask;
	}

	/**
	 * @param logManagerTask
	 *            the logManagerTask to set
	 */
	public void setLogManagerTask(Future<?> logManagerTask) {
		this.logManagerTask = logManagerTask;
	}

	/**
	 * @return the messageQueueTask
	 */
	public Future<?> getMessageQueueTask() {
		return messageQueueTask;
	}

	/**
	 * @param messageQueueTask
	 *            the messageQueueTask to set
	 */
	public void setMessageQueueTask(Future<?> messageQueueTask) {
		this.messageQueueTask = messageQueueTask;
	}

	/**
	 * @return the exit
	 */
	public boolean isExit() {
		return exit;
	}

	/**
	 * @param exit
	 *            the exit to set
	 */
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	/**
	 * @return the inputSynchronize
	 */
	public Object getInputSynchronize() {
		return inputSynchronize;
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
		this.setLogger(Logger.getLogger("LogFormatter"));
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler(System.getProperty("user.dir") + "//peer_" + peerId + "//log_peer_" + peerId + ".log");
			fh.setFormatter(new LogFormatter());
			this.getLogger().addHandler(fh);

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
		peerProcess.setPeerInfoVector(new Vector<Peer>());

		try {

			new File("peer_" + args[0]).mkdir();
			peerProcess.initateLogFile(args[0]);

			peerProcess
					.setUnchokingIntervalWisePeerDownloadingRate(new PriorityQueue<>(new Comparator<DownloadingRate>() {
						@Override
						public int compare(DownloadingRate o1, DownloadingRate o2) {
							if (o1.getDownloadingRate() > o2.getDownloadingRate()) {
								return 1;
							} else {
								return -1;
							}
						}
					}));

			/*** Reads peerInfo.cfg file and initializes peerList ***/
			peerInfo.initializePeerList(peerProcess, args[0]);

			/*** Initializes File Manager ***/
			peerInfo.initializeFileManager(peerProcess, args[0]);

			peerInfo.establishConnection(peerProcess);

			peerProcess.createServerSocket(PeerInfoConfigParser.getCurrentPeer().getPeerPortNumber());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createServerSocket(int portNo) {
		ExecutorService exec = Executors.newFixedThreadPool(4);
		try {
			new Thread(new PeerManager(this)).start();
			this.setMessageQueueTask(exec.submit(new MessageQueueProcess(PeerProcess.this)));
			this.setLogManagerTask(exec.submit(new LogManager(PeerProcess.this.blockingQueueLogging, logger, this)));

			int peerCompleteFileReceived = 0;
			this.setServerSocket(new ServerSocket(portNo));
			int totalConnectedPeers = 0;

			for (; !this.isExit();) {
				peerCompleteFileReceived = 0;
				if (!(PeerInfoConfigParser.getCurrentPeer().getPeerID() != PeerInfoConfigParser.getLastPeerID()
						&& totalConnectedPeers < this.getPeerInfoVector().size())) {

				} else {
					Socket socket;
					if (!(totalConnectedPeers != PeerInfoConfigParser.getTotalPeers())) {

					} else {
						socket = this.getServerSocket().accept();
						Peer tempPeer = getPeerFromPeerList(socket.getInetAddress().getHostAddress(), socket.getPort(),
								socket.getLocalPort());
						this.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
								+ " is connected from Peer " + tempPeer.getPeerID());
						this.getPeerSocketMap()
								.put(this.getPeerInfoVector().get(this.getPeerInfoVector().indexOf(tempPeer)), socket);
						new Thread(new ConnectionManager(this, tempPeer, false)).start();
						totalConnectedPeers++;
					}
				}
				// check for termination of this process
				Iterator<Peer> iter = this.getPeerInfoVector().iterator();
				while (iter.hasNext()) {
					Peer p = iter.next();
					if (!checkIfFullFileReceived(p)) {

					} else {
						peerCompleteFileReceived++;
					}
				}

				if (peerCompleteFileReceived != this.getPeerInfoVector().size()) {

				} else {
					// check if you received the whole file
					if (!checkIfFullFileReceived(PeerInfoConfigParser.getCurrentPeer())) {

					} else {
						// now terminate the process of executorService
						this.setExit(true);
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
					for (; !this.getBlockingQueueMessages().isEmpty();) {
					}
					for (; !this.getBlockingQueueLogging().isEmpty();) {
					}
					this.getMessageQueueTask().cancel(false);
					this.getLogManagerTask().cancel(false);
					exec.awaitTermination(1, TimeUnit.SECONDS);
				}

				Iterator<Socket> iter = this.getPeerSocketMap().values().iterator();
				while (iter.hasNext()) {
					Socket s = iter.next();
					if (s.isClosed()) {

					} else {
						s.close();
					}
				}

				this.getServerSocket().close();

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		}

	}

	private Peer getPeerFromPeerList(String hostAddress, int port, int peerPort) {

		Iterator<Peer> it = this.getPeerInfoVector().iterator();
		while (it.hasNext()) {

			Peer tempPeer = (Peer) it.next();
			System.out.println(port);
			System.out.println(hostAddress);
			if (hostAddress.equals("127.0.0.1")) {
				if (tempPeer.getPeerPortNumber() == peerPort) {
					return tempPeer;
				}
			} else {
				if (tempPeer.getPeerIP().equals(hostAddress)) {
					return tempPeer;
				}
			}
		}
		return null;
	}

	public void connectToPreviousPeer(Peer peer) {
		Socket socket;
		try {
			socket = new Socket(peer.getPeerIP(), peer.getPeerPortNumber());
			this.getBlockingQueueLogging().put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
					+ " makes a connection to Peer " + peer.getPeerID());
			this.getPeerSocketMap().put(peerInfoVector.get(this.peerInfoVector.indexOf(peer)), socket);
			new Thread(new ConnectionManager(this, peer, true)).start();
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
		return (b1 & (be << ((index) % 8))) != 0 ? 1 : 0;
	}

	public static void clearBit(byte[] b, int index) {
		byte b1 = 1;
		b[index / 8] = (byte) (b[index / 8] & (~(b1 << ((index) % 8))));
	}

	public boolean checkIfFullFileReceived(Peer peer) {
		boolean result = true;
		int indexI = 0;
		while (indexI < CommonPropertiesParser.getNumberOfPieces()) {
			if (!(getBit(peer.getBitfield(), indexI) == 0)) {

			} else {
				result = false;
			}
			indexI++;
		}
		return result;
	}

	public void sendChokeMessage(HashSet<Peer> peers) {
		Message msg = new Message(1, Byte.valueOf(Integer.toString(0)), null);
		Iterator<Peer> iteratorPeer = peers.iterator();
		while (iteratorPeer.hasNext()) {
			Peer p = iteratorPeer.next();
			if (!p.isHandShakeDone()) {

			} else {
				try {
					Socket socket = this.getPeerSocketMap().get(p);
					this.getBlockingQueueMessages()
							.put(new MessageWriter(msg, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void sendUnChokeMessage(HashSet<Peer> peers) {
		Message msg = new Message(1, Byte.valueOf(Integer.toString(1)), null);
		Iterator<Peer> iteratorPeer = peers.iterator();
		while (iteratorPeer.hasNext()) {
			Peer p = iteratorPeer.next();
			if (!p.isHandShakeDone()) {

			} else {
				try {
					System.out.println("Sent Unchoke to " + p.getPeerID());
					Socket socket = this.getPeerSocketMap().get(p);
					this.getBlockingQueueMessages()
							.put(new MessageWriter(msg, new DataOutputStream(socket.getOutputStream())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
