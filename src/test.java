/**
 * @author Tejas
 *
 */
public class test {

	/**
	 * @param args
	 *
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// File testFile = new File(args[0]);
		byte[] b = { (byte) 0, ((byte) 15) };

		for (int i = 0; i < 8; i++)
			System.out.println(getBit(b, i) + " " + Byte.toUnsignedInt(b[0]));

		setBit(b, 0);

		for (int i = 0; i < 8; i++)
			System.out.println(getBit(b, i) + " " + Byte.toUnsignedInt(b[0]));

		// for(int i = 0 ; i<8 ; i++)
		// System.out.println(getBit(b,i) + " " + Byte.toUnsignedInt(b[1]));

		/*
		 * setBit(b,8);
		 *
		 * System.out.println(getBit(b,8) + " " + Byte.toUnsignedInt(b[1]));
		 *
		 * clearBit(b,8);
		 *
		 * System.out.println(getBit(b,8) + " " + Byte.toUnsignedInt(b[1]));
		 */
		// testFile.createNewFile();
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
		b[index / 8] = (byte) (b[index / 8] & (~(1 << ((index) % 8))));

	}

}
