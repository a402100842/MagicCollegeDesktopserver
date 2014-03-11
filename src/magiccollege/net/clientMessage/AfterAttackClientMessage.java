package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class AfterAttackClientMessage extends ClientMessage implements GameConstants{
	
	private String myID;
	private String battleKey;
	private int deskSize;
	private int myDeskSize;
	
	public void set(final String myid, final String key, final int desksize, final int mysize){
		myID = myid;
		battleKey = key;
		deskSize = desksize;
		myDeskSize = mysize;
	}

	public String getBattleKey() {
		return battleKey;
	}
	
	public String getID(){
		return myID;
	}
	
	public int getDeskSize(){
		return deskSize;
	}
	
	public int getDeskSize2(){
		return myDeskSize;
	}

	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_AFTERATTACK;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		myID = pDataInputStream.readUTF();
		battleKey = pDataInputStream.readUTF();
		deskSize = pDataInputStream.readInt();
		myDeskSize = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(battleKey);
		pDataOutputStream.writeInt(deskSize);
		pDataOutputStream.writeInt(myDeskSize);
	}

}
