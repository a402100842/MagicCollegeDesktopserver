package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class AcceptFightClientMessage extends ClientMessage implements GameConstants {
	private String myID;
	private String othersID;
	
	public String getMyID(){
		return myID;
	}
	
	public String getOthersID(){
		return othersID;
	}
	
	public void set(final String pMyID, final String pOthersID){
		myID = pMyID;
		othersID =  pOthersID;
	}

	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_ACCEPTFIGHT;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		myID = pDataInputStream.readUTF();
		othersID = pDataInputStream.readUTF();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(othersID);
	}

}
