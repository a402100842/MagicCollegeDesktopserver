package magiccollege.net.serverMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class QueryNameServerMessage extends ServerMessage implements GameConstants{
	private short whatHappened;
	private String othersID;
	
	public short get(){
		return whatHappened;
	}
	
	public String getID(){
		return othersID;
	}
	
	public void set(final String str, final short p){
		othersID = str;
		whatHappened = p;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream arg0)
			throws IOException {
		this.whatHappened = arg0.readShort();
		othersID = this.readString(arg0.readInt(), arg0);
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream arg0)
			throws IOException {
		arg0.writeShort(whatHappened);
		this.writeString(othersID, arg0);
	}
	
	public short getMessage(){
		return whatHappened;
	}
	
	@Override
	public short getFlag() {
		return FLAG_SERVERMESSAGE_QUERYNAME;
	}

}
