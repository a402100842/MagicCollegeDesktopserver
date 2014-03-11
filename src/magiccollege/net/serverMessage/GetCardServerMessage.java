package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class GetCardServerMessage extends ServerMessage implements GameConstants{
	private short message;
	private int r;
	
	public void set(final short p, final int pR){
		message = p;
		r = pR;
	}
	
	public int getMessage(){
		return message;
	}
	
	public int getR(){
		return r;
	}

	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_GETCARD;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		message = pDataInputStream.readShort();
		r = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeShort(message);
		pDataOutputStream.writeInt(r);
	}

}
