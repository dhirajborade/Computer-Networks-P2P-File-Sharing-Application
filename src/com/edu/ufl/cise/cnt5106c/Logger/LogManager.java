package com.edu.ufl.cise.cnt5106c.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.edu.ufl.cise.cnt5106c.Peer.PeerProcess;

class LogManager implements Runnable {
	private static BlockingQueue<String> blockingQueueLog;
	private static Logger logger;
	private static PeerProcess peerProcess;

	public LogManager(BlockingQueue<String> blockingQueueLog, Logger logger, PeerProcess peerProcess) {
		LogManager.blockingQueueLog = blockingQueueLog;
		LogManager.logger = logger;
		LogManager.peerProcess = peerProcess;
	}

	/**
	 * @return the blockingQueueLog
	 */
	public static BlockingQueue<String> getBlockingQueueLog() {
		return blockingQueueLog;
	}

	/**
	 * @param blockingQueueLog
	 *            the blockingQueueLog to set
	 */
	public static void setBlockingQueueLog(BlockingQueue<String> blockingQueueLog) {
		LogManager.blockingQueueLog = blockingQueueLog;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public static void setLogger(Logger logger) {
		LogManager.logger = logger;
	}

	/**
	 * @return the peerProcess
	 */
	public static PeerProcess getPeerProcess() {
		return peerProcess;
	}

	/**
	 * @param peerProcess
	 *            the peerProcess to set
	 */
	public static void setPeerProcess(PeerProcess peerProcess) {
		LogManager.peerProcess = peerProcess;
	}

	@Override
	public void run() {
		try {
			for (; !getPeerProcess().exit;) {
				for (; !getBlockingQueueLog().isEmpty();)
					getLogger().log(Level.INFO, getBlockingQueueLog().take());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			getPeerProcess().exit = true;
		}
	}
}