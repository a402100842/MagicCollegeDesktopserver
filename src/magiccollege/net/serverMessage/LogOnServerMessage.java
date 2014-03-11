package magiccollege.net.serverMessage;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class LogOnServerMessage extends ServerMessage implements GameConstants{
	private short message;

	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_LOGON;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream arg0)
			throws IOException {
		this.message = arg0.readShort();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream arg0)
			throws IOException {
		arg0.writeShort(99);
	}
	
	public short getMessage(){
		return message;
	}
	
}
