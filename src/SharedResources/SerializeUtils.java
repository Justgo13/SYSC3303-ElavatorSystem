package SharedResources;

import java.io.*;

/**
 * @author Michael Quach
 * 
 *         A library to deal with serializable objects.
 */

public class SerializeUtils {

	/**
	 * Serialize an object to obtain its byte array
	 * @param value the object to be serialized
	 * @return the serialized value as a byte array
	 * @throws IOException when serialization fails
	 */
    public static byte[] serialize(Serializable value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try(ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(value);
        }

        return out.toByteArray();
    }

    /**
     * Deserialize a byte array to obtain its original value
     * @param <T> A generic operator
     * @param data the serialized data
     * @return the deserialized object from the serialized data
     * @throws IOException when deserialization fails
     * @throws ClassNotFoundException when the class to deserialize is not found
     */
    public static <T extends Serializable> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            //noinspection unchecked
            return (T) new ObjectInputStream(bis).readObject();
        }
    }
}