package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;



public class AfterCalculateClientMessage extends ClientMessage implements GameConstants{

	private String myID;
	private String battleKey;
	
	
	public void set(final String myid, final String key){
		myID = myid;
		battleKey = key;
	}

	public String getBattleKey() {
		return battleKey;
	}
	
	public String getID(){
		return myID;
	}
	
	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_AFTERCALCULATE;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		myID = pDataInputStream.readUTF();
		battleKey = pDataInputStream.readUTF();
		
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(battleKey);
		
	}
}
