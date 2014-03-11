package magiccollege.net.clientMessage;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

public class LogOnClientMessage extends ClientMessage implements GameConstants{
	private int playerID;
	
	public LogOnClientMessage(){
		playerID = -1;
	}
	
	public LogOnClientMessage(int p){
		playerID = p;
	}
	
	public void set(int p){
		playerID = p;
	}
	
	public int get(){
		return playerID;
	}
	
	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_LOGON;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream arg0)
			throws IOException {
		playerID = arg0.readInt();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream arg0)
			throws IOException {
		arg0.writeInt(playerID);
	}
}
