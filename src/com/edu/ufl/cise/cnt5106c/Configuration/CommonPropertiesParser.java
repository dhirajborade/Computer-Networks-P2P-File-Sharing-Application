package com.edu.ufl.cise.cnt5106c.Configuration;

/**
 *
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import com.edu.ufl.cise.cnt5106c.Message.MessageWriter;
import com.edu.ufl.cise.cnt5106c.Peer.Peer;

/**
 * @author dhirajborade
 *
 */
public class CommonPropertiesParser {

	private static final String CONFIG_FILE_NAME = "Common.cfg";
	private static int numberOfPreferredNeighbors;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static int fileSize;
	private static int pieceSize;

	private static int[][] pieceMatrix;
	private static int numberOfPieces;
	private static int totalPeers;
	private static boolean[][] sentRequestMessageByPiece;
	private static BlockingQueue<MessageWriter> bqm;
	private static BlockingQueue<String> bql;
	private static HashMap<Peer, Socket> peerSocketMap;
	private static HashSet<Peer> chokedFrom;

	/**
	 *
	 */
	public CommonPropertiesParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the numberOfPreferredNeighbors
	 */
	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	/**
	 * @param numberOfPreferredNeighbors
	 *            the numberOfPreferredNeighbors to set
	 */
	public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		CommonPropertiesParser.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}

	/**
	 * @return the unchokingInterval
	 */
	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	/**
	 * @param unchokingInterval
	 *            the unchokingInterval to set
	 */
	public static void setUnchokingInterval(int unchokingInterval) {
		CommonPropertiesParser.unchokingInterval = unchokingInterval;
	}

	/**
	 * @return the optimisticUnchokingInterval
	 */
	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	/**
	 * @param optimisticUnchokingInterval
	 *            the optimisticUnchokingInterval to set
	 */
	public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		CommonPropertiesParser.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}

	/**
	 * @return the fileName
	 */
	public static String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public static void setFileName(String fileName) {
		CommonPropertiesParser.fileName = fileName;
	}

	/**
	 * @return the fileSize
	 */
	public static int getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public static void setFileSize(int fileSize) {
		CommonPropertiesParser.fileSize = fileSize;
	}

	/**
	 * @return the pieceSize
	 */
	public static int getPieceSize() {
		return pieceSize;
	}

	/**
	 * @param pieceSize
	 *            the pieceSize to set
	 */
	public static void setPieceSize(int pieceSize) {
		CommonPropertiesParser.pieceSize = pieceSize;
	}

	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	/**
	 * @return the pieceMatrix
	 */
	public static int[][] getPieceMatrix() {
		return pieceMatrix;
	}

	/**
	 * @param pieceMatrix
	 *            the pieceMatrix to set
	 */
	public static void setPieceMatrix(int[][] pieceMatrix) {
		CommonPropertiesParser.pieceMatrix = pieceMatrix;
	}

	/**
	 * @return the numberOfPieces
	 */
	public static int getNumberOfPieces() {
		return numberOfPieces;
	}

	/**
	 * @param numberOfPieces
	 *            the numberOfPieces to set
	 */
	public static void setNumberOfPieces(int numberOfPieces) {
		CommonPropertiesParser.numberOfPieces = numberOfPieces;
	}

	/**
	 * @return the totalPeers
	 */
	public static int getTotalPeers() {
		return totalPeers;
	}

	/**
	 * @param totalPeers
	 *            the totalPeers to set
	 */
	public static void setTotalPeers(int totalPeers) {
		CommonPropertiesParser.totalPeers = totalPeers;
	}

	/**
	 * @return the sentRequestMessageByPiece
	 */
	public static boolean[][] getSentRequestMessageByPiece() {
		return sentRequestMessageByPiece;
	}

	/**
	 * @param sentRequestMessageByPiece
	 *            the sentRequestMessageByPiece to set
	 */
	public static void setSentRequestMessageByPiece(boolean[][] sentRequestMessageByPiece) {
		CommonPropertiesParser.sentRequestMessageByPiece = sentRequestMessageByPiece;
	}

	/**
	 * @return the bqm
	 */
	public static BlockingQueue<MessageWriter> getBqm() {
		return bqm;
	}

	/**
	 * @param bqm
	 *            the bqm to set
	 */
	public static void setBqm(BlockingQueue<MessageWriter> bqm) {
		CommonPropertiesParser.bqm = bqm;
	}

	/**
	 * @return the bql
	 */
	public static BlockingQueue<String> getBql() {
		return bql;
	}

	/**
	 * @param bql
	 *            the bql to set
	 */
	public static void setBql(BlockingQueue<String> bql) {
		CommonPropertiesParser.bql = bql;
	}

	/**
	 * @return the peerSocketMap
	 */
	public static HashMap<Peer, Socket> getPeerSocketMap() {
		return peerSocketMap;
	}

	/**
	 * @param peerSocketMap
	 *            the peerSocketMap to set
	 */
	public static void setPeerSocketMap(HashMap<Peer, Socket> peerSocketMap) {
		CommonPropertiesParser.peerSocketMap = peerSocketMap;
	}

	/**
	 * @return the chokedFrom
	 */
	public static HashSet<Peer> getChokedFrom() {
		return chokedFrom;
	}

	/**
	 * @param chokedFrom
	 *            the chokedFrom to set
	 */
	public static void setChokedFrom(HashSet<Peer> chokedFrom) {
		CommonPropertiesParser.chokedFrom = chokedFrom;
	}

	public void readCommonFileInfoFile(Reader reader) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Common.cfg"));
		try {
			String line;
			while ((line = in.readLine()) != null) {
				String[] configFile = line.split(" ");
				String propertyName = configFile[0];
				String propertyValue = configFile[1];
				switch (propertyName) {
				case "NumberOfPreferredNeighbors":
					numberOfPreferredNeighbors = Integer.parseInt(propertyValue) + 1;
					break;
				case "UnchokingInterval":
					unchokingInterval = Integer.parseInt(propertyValue);
					break;
				case "OptimisticUnchokingInterval":
					optimisticUnchokingInterval = Integer.parseInt(propertyValue);
					break;
				case "FileName":
					fileName = propertyValue;
					break;
				case "FileSize":
					fileSize = Integer.parseInt(propertyValue);
					break;
				case "PieceSize":
					pieceSize = Integer.parseInt(propertyValue);
					break;
				default:
					break;
				}
			}
			numberOfPieces = (fileSize / pieceSize) + 1;
			pieceMatrix = new int[numberOfPieces][2];
			int startPosition = 0;
			int tempPieceSize = pieceSize;
			int totalPieceSize = pieceSize;
			int indexI = 0;
			while (indexI < numberOfPieces) {
				pieceMatrix[indexI][0] = startPosition;
				pieceMatrix[indexI][1] = tempPieceSize;
				startPosition += tempPieceSize;
				if ((fileSize - totalPieceSize) > pieceSize) {

				} else {
					tempPieceSize = fileSize - totalPieceSize;
				}
				totalPieceSize += tempPieceSize;
				indexI++;
			}
			totalPeers = PeerInfoConfigParser.getTotalPeers();
			sentRequestMessageByPiece = new boolean[totalPeers][numberOfPieces];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
