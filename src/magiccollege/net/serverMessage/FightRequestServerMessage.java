package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class FightRequestServerMessage extends ServerMessage implements GameConstants{
	private String name = "";
	private String othersID = "";

	public String getName(){
		return name;
	}
	
	public String getID(){
		return othersID;
	}
	
	public void set(final String id, final String pName){
		othersID = id;
		name = pName;
	}
	
	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_FIGHTREQUEST;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream arg0)
			throws IOException {
		name = this.readString(arg0.readInt(), arg0);
		othersID = this.readString(arg0.readInt(), arg0);
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream arg0)
			throws IOException {
		this.writeString(name, arg0);
		this.writeString(othersID, arg0);
	}

}
