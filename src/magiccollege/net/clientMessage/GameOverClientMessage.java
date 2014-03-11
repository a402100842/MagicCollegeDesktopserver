package magiccollege.net.clientMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import magiccollege.net.GameConstants;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;


public class GameOverClientMessage  extends ClientMessage implements GameConstants{
	private String myID;
	private String battleKey;
	private int size;
	private ArrayList<Double> list = new ArrayList<Double>();
	
	public void set(final String myid, final String key,  ArrayList<Double> al){
		myID = myid;
		battleKey = key;
		list.addAll(al);
		size = al.size();
	}

	public  ArrayList<Double> get(){
		return list;
	}
	
	public String getBattleKey() {
		return battleKey;
	}
	
	public String getID(){
		return myID;
	}
	
	@Override
	public short getFlag() {
		return FLAG_CLIENTMESSAGE_GAMEOVER;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		list.clear();
		myID = pDataInputStream.readUTF();
		battleKey = pDataInputStream.readUTF();
		size = pDataInputStream.readInt();
		for (int i = 0; i < size; i++){
			list.add(pDataInputStream.readDouble());
		}
	}

	@Override
	protected void onWriteTransmissionData(DataOutputStream pDataOutputStream)
			throws IOException {
		pDataOutputStream.writeUTF(myID);
		pDataOutputStream.writeUTF(battleKey);
		pDataOutputStream.writeInt(size);
		for (Double i : list){
			pDataOutputStream.writeDouble(i);
		}
	}

}
