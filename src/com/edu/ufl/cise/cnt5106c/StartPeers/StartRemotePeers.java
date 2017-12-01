/**
 *
 */
package com.edu.ufl.cise.cnt5106c.StartPeers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import com.edu.ufl.cise.cnt5106c.Peer.RemotePeerInfo;

/**
 * @author dhirajborade
 *
 */
public class StartRemotePeers {

	/**
	 *
	 */
	public StartRemotePeers() {
		// TODO Auto-generated constructor stub
	}

	public Vector<RemotePeerInfo> peerInfoVector;

	public void getConfiguration(String[] inputArgs) {
		peerInfoVector = new Vector<RemotePeerInfo>();
		final String configFile = (inputArgs.length == 0 ? RemotePeerInfo.getConfigFileName() : inputArgs[0]);
		FileReader inputFileReader = null;
		try {
			inputFileReader = new FileReader(configFile);
			BufferedReader inputBufferedReader = new BufferedReader(inputFileReader);
			RemotePeerInfo peerInfo = new RemotePeerInfo();
			peerInfo.read(inputBufferedReader);
			peerInfoVector = peerInfo.getPeerInfo();
			inputBufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputFileReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			StartRemotePeers myStart = new StartRemotePeers();
			myStart.getConfiguration(args);

			// get current path
			// String path = System.getProperty("user.dir");

			// start clients at remote hosts
			for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
				RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
				System.out.println("Start remote peer " + pInfo.getPeerID() + " at " + pInfo.getPeerIP());
				Runtime.getRuntime().exec(
						"ssh " + pInfo.getPeerIP() + " cd " + pInfo.getProjectPath() + "; javac PeerProcess.java");
			}
			for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
				RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
				System.out.println("Start remote peer " + pInfo.getPeerID() + " at " + pInfo.getPeerIP());
				Runtime.getRuntime().exec("ssh " + pInfo.getPeerIP() + " cd " + pInfo.getProjectPath()
						+ "; java PeerProcess " + pInfo.getPeerID());
			}
			System.out.println("Starting all remote peers has done.");

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
