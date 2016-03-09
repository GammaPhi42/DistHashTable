package cs455.overlay.wireformats;

import java.io.IOException;

public interface Event {
	public byte getType();
	public byte[] getBytes() throws IOException;
}
