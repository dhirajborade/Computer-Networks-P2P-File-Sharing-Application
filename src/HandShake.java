import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandShake extends Message implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8799977982265952720L;
	byte[] header;
	byte[] zerobits;
	byte[] peerID;
	
	public HandShake(int peerId){
		String h = "P2PFILESHARINGPROJ";
		
		header = new byte[18];
		for(int i=0;i<h.length();i++){
			header[i]=(byte)(h.charAt(i));
		}
		peerID = new byte[4];
		peerID = ByteBuffer.allocate(4).putInt(peerId).array();
		zerobits = new byte[10];
		for(int i =0 ; i<10 ; i++)
			zerobits[i] = (byte)0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HandShake [peerID=" + Arrays.toString(peerID) + "]";
	}
	
	
		
}