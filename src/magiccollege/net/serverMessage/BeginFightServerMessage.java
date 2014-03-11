package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class BeginFightServerMessage extends ServerMessage implements GameConstants{
	private String battleID;
	
	public void set(final String pBattleID){
		battleID = pBattleID;
	}
	
	public String getBattleID(){
		return battleID;
	}
	
	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_BEGINFIGHT;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		battleID = this.readString(pDataInputStream.readInt(), pDataInputStream);
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		this.writeString(battleID, pDataOutputStream);
	}

}
