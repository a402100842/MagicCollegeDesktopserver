package org.anddev.andengine.extension.multiplayer.protocol.server.connector;

import java.io.IOException;

import org.anddev.andengine.extension.multiplayer.protocol.server.IClientMessageReader;
import org.anddev.andengine.extension.multiplayer.protocol.shared.SocketConnection;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 15:44:42 - 04.03.2011
 */
public class SocketConnectionClientConnector extends ClientConnector<SocketConnection> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public SocketConnectionClientConnector(final SocketConnection pSocketConnection) throws IOException {
		super(pSocketConnection);
	}

	public SocketConnectionClientConnector(final SocketConnection pSocketConnection, final IClientMessageReader<SocketConnection> pClientMessageReader) throws IOException {
		super(pSocketConnection, pClientMessageReader);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static interface ISocketConnectionClientConnectorListener extends IClientConnectorListener<SocketConnection> {
		
	}

	public static class DefaultSocketConnectionClientConnectorListener implements ISocketConnectionClientConnectorListener {
		@Override
		public void onStarted(ClientConnector<SocketConnection> pClientConnector) {
			System.out.println("Accepted Client-Connection from: '" + pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}

		@Override
		public void onTerminated(ClientConnector<SocketConnection> pClientConnector) {
			System.out.println("Closed Client-Connection from: '" + pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}
	}
}
