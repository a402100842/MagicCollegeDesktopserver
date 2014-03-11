package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class AcceptFightServerMessage extends ServerMessage implements GameConstants{
	private String myID;
	private String othersID;
	private String battleKey;
	
	public String getMyID(){
		return myID;
	}
	
	public String getOthersID(){
		return othersID;
	}
	
	public String getBattleKey(){
		return battleKey;
	}
	
	public void set(final String pMyID, final String pOthersID, final String pBattleKey){
		myID = pMyID;
		othersID =  pOthersID;
		battleKey = pBattleKey;
	}

	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_ACCEPTFIGHT;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		myID = this.readString(pDataInputStream.readInt(), pDataInputStream);
		othersID = this.readString(pDataInputStream.readInt(), pDataInputStream);
		battleKey = this.readString(pDataInputStream.readInt(), pDataInputStream);
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		this.writeString(myID, pDataOutputStream);
		this.writeString(othersID, pDataOutputStream);
		this.writeString(battleKey, pDataOutputStream);
	}

}
