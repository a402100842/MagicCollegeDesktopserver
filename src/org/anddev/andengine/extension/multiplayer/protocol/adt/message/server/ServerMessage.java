package org.anddev.andengine.extension.multiplayer.protocol.adt.message.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.Message;


/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 19:20:38 - 02.03.2011
 */
public abstract class ServerMessage extends Message implements IServerMessage {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	protected String readString(int length, DataInputStream dis) throws IOException{
		String str = "";
		for (int i = 0; i < length; i++)
			str += dis.readChar();
		return str;
	}
	
	protected void writeString(String str, DataOutputStream dos) throws IOException{
		dos.writeInt(str.length());
		dos.writeChars(str);
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
