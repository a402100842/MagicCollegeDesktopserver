package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class DoubleClientMessage extends ClientMessage implements GameConstants{
	
	private double message;
	private String myID;
	private String battleKey;
	
	public void set(final double d, final String id, final String key){
		message = d;
		myID = id;
		battleKey = key;
	}

	public double getMessage(){
		return message;
	}
	
	public String getID(){
		return myID;
	}
	
	public String getBattleKey(){
		return battleKey;
	}
	
	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_DOUBLE;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		message = pDataInputStream.readDouble();
		myID = pDataInputStream.readUTF();
		battleKey = pDataInputStream.readUTF();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeDouble(message);
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(battleKey);
	}

}
