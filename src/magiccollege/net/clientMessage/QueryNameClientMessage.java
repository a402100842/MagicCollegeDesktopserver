package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class QueryNameClientMessage extends ClientMessage implements GameConstants {
	private String message = "";
	private int myID = -1;
	
	public String getName(){
		return message;
	}
	
	public int getID(){
		return myID;
	}
	
	public void set(final int pMyID, final String pMessage){
		myID = pMyID;
		message = pMessage;
	}

	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_QUERYNAME;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream arg0)
			throws IOException {
		myID = arg0.readInt();
		message = arg0.readUTF();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream arg0)
			throws IOException {
		arg0.writeInt(myID);
		arg0.writeUTF(message);
	}

}
