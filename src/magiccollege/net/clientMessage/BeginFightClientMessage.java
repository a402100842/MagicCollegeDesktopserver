package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

public class BeginFightClientMessage extends ClientMessage implements
		GameConstants {
	private String myID;
	private String othersID;
	private String battleKey;
	private int deskSize;

	public String getMyID() {
		return myID;
	}

	public String getOthersID() {
		return othersID;
	}
	
	public int getSize(){
		return deskSize;
	}

	public String getBattleKey() {
		return battleKey;
	}

	public void set(final String pMyID, final String pOthersID,
			final String pBattleKey, final int size) {
		myID = pMyID;
		othersID = pOthersID;
		battleKey = pBattleKey;
		deskSize = size;
	}

	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_BEGINFIGHT;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		myID = pDataInputStream.readUTF();
		othersID = pDataInputStream.readUTF();
		battleKey = pDataInputStream.readUTF();
		deskSize = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(othersID);
		pDataOutputStream.writeUTF(battleKey);
		pDataOutputStream.writeInt(deskSize);
	}
}
