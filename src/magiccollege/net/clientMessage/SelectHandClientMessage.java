package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class SelectHandClientMessage extends ClientMessage implements GameConstants{
	private short message;
	private String battleID;
	private int deskSize;
	
	public void set(final short pMessage, final String pBattleID, final int size){
		message = pMessage;
		battleID = pBattleID;
		deskSize = size;
	}
	
	public int getMessage(){
		return message;
	}
	
	public String getBattleID(){
		return battleID;
	}
	
	public int getSize(){
		return deskSize;
	}

	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_SELECTHAND;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		message = pDataInputStream.readShort();
		battleID = pDataInputStream.readUTF();
		deskSize = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeShort(message);
		pDataOutputStream.writeUTF(battleID);
		pDataOutputStream.writeInt(deskSize);
	}

}
