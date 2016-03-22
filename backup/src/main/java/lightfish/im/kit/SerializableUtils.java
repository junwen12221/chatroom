package lightfish.im.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializableUtils {
	private static final Logger log = LoggerFactory.getLogger(SerializableUtils.class);
	public static byte[] serialize(Object object) {
		try(ByteArrayOutputStream  baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			log.error("serialize fail:"+object);
		}
		return null;
	}

	public static Object deserialize(byte[] bytes) {
		try(ByteArrayInputStream bais =new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais)) {
			// 反序列化
			return ois.readObject();
		} catch (Exception e) {
			log.error("unserialize fail:"+bytes);
		}
		return null;
	}
}
