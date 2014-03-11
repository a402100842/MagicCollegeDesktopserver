package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class AfterCalculateServerMessage extends ServerMessage implements GameConstants{

	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_AFTERCALCULATE;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		
	}

}
