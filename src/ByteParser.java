import java.nio.ByteBuffer;

/**
 * @author Jason Gao
 *
 *         A class containing methods for parsing to and from byte arrays
 */
public class ByteParser {
	public static final int _32BIT_BYTE_LENGTH = 4;
	public static final int _8BIT_BYTE_LENGTH = 1;

	/**
	 * Convert a float to byte array
	 * 
	 * @param floatVal The float to convert
	 * @return The byte array representing the float
	 */
	public static byte[] floatToBytes(float floatVal) {
		return ByteBuffer.allocate(_32BIT_BYTE_LENGTH).putFloat(floatVal).array();
	}

	/**
	 * Convert an int to byte array
	 * 
	 * @param intVal The int to convert
	 * @return The byte array representing the int
	 */
	public static byte[] intToBytes(int intVal) {
		return ByteBuffer.allocate(_32BIT_BYTE_LENGTH).putInt(intVal).array();
	}

	/**
	 * Takes a byte array and overrides the bytes starting at the startIndex offset
	 * with the bytes from the newBytes array
	 * 
	 * @param origBytes  The original byte array to modify
	 * @param newBytes   The byte array to add
	 * @param startIndex The starting index to start modifying origBytes from
	 * @return A modified byte array
	 */
	public static byte[] overwriteBytes(byte[] origBytes, byte[] newBytes, int startIndex) {
		for (int i = 0; i < newBytes.length; i++) {
			origBytes[startIndex] = newBytes[i];
			startIndex += 1;
		}
		return origBytes;
	}

	/**
	 * Converts a byte array to a float
	 * 
	 * @param bytes The byte array to convert
	 * @return A float value
	 */
	public static float bytesToFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	/**
	 * Converts a byte array to an int
	 * 
	 * @param bytes The byte array to convert
	 * @return An int value
	 */
	public static int bytesToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}
}
