package magiccollege.net;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import magiccollege.net.clientMessage.AcceptFightClientMessage;
import magiccollege.net.clientMessage.AfterAttackClientMessage;
import magiccollege.net.clientMessage.AfterCalculateClientMessage;
import magiccollege.net.clientMessage.BeforeCalculateClientMessage;
import magiccollege.net.clientMessage.BeginAttackClientMessage;
import magiccollege.net.clientMessage.BeginFightClientMessage;
import magiccollege.net.clientMessage.GameOverClientMessage;
import magiccollege.net.clientMessage.LogOnClientMessage;
import magiccollege.net.clientMessage.QueryNameClientMessage;
import magiccollege.net.clientMessage.SelectHandClientMessage;
import magiccollege.net.serverMessage.AcceptFightServerMessage;
import magiccollege.net.serverMessage.AfterCalculateServerMessage;
import magiccollege.net.serverMessage.BeforeCalculateServerMessage;
import magiccollege.net.serverMessage.BeginAttackServerMessage;
import magiccollege.net.serverMessage.BeginFightServerMessage;
import magiccollege.net.serverMessage.FightRequestServerMessage;
import magiccollege.net.serverMessage.GetCardServerMessage;
import magiccollege.net.serverMessage.LogOnServerMessage;
import magiccollege.net.serverMessage.QueryNameServerMessage;

import org.anddev.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.anddev.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.anddev.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.anddev.andengine.extension.multiplayer.protocol.server.IClientMessageReader.ClientMessageReader;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.anddev.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.anddev.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.anddev.andengine.util.SparseArray;

/**
 * MagicCollege (服务器端)
 * @author magicTeamSYSU
 */
public class Server implements GameConstants{
	/**
	 * 数据库
	 */
	//主机号
	final public String url = "jdbc:mysql://"+DB_SERVER_IP+":3306/magic";//
	// MySQL配置时的用户名
	final public String user = "1a781";
	// MySQL配置时的密码
	final public String password1 = "r4idn3";
	
	/**
	 * 服务器套接字
	 */
	private SocketServer<SocketConnectionClientConnector> serverSocket;
	
	private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();
	private  final SparseArray<Battle> battles = new SparseArray<Battle>();
	private int battleKey;
	private ClientMessageReader<SocketConnection> mMessageReader = new ClientMessageReader<SocketConnection>();
	
	private void initMessagePool() {
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_LOGON, LogOnServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_QUERYNAME, QueryNameServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_FIGHTREQUEST, FightRequestServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_ACCEPTFIGHT, AcceptFightServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_BEGINFIGHT, BeginFightServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_GETCARD, GetCardServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_BEFORECALCULATE, BeforeCalculateServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_AFTERCALCULATE, AfterCalculateServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_SERVERMESSAGE_BEGINATTACK, BeginAttackServerMessage.class);
		
	}
	
	private void initMessageReader(){
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_LOGON, LogOnClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				LogOnClientMessage cm = (LogOnClientMessage) pClientMessage;
				serverSocket.addOnlineClient(cm.get(), (SocketConnectionClientConnector) pClientConnector);//添加到在线用户
				
				LogOnServerMessage sm = (LogOnServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_LOGON);
				pClientConnector.sendServerMessage(sm);//返回确认信息
				
				System.out.println("receive "+cm.get()+" as playerID from client!" + pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
				Server.this.mMessagePool.recycleMessage(sm);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_QUERYNAME, QueryNameClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				boolean ok = false;
				QueryNameClientMessage cm = (QueryNameClientMessage) pClientMessage;//获得客户消息
				int othersID = getIdByName(cm.getName());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(othersID);
				QueryNameServerMessage sm = (QueryNameServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_QUERYNAME);
				
				if (othersID == cm.getID()){
					sm.set(String.valueOf(othersID), (short)5);//自己打自己
				}else if (othersID == 0){
					sm.set(String.valueOf(othersID), (short)6);//没有这个玩家
				}else if (cc  == null){
					sm.set(String.valueOf(othersID), (short)7);//这个玩家不在线
				}else{
					sm.set(String.valueOf(othersID), (short)8);//请等待对方回复
					ok = true;
				}
				pClientConnector.sendServerMessage(sm);
				Server.this.mMessagePool.recycleMessage(sm);
				if (ok){//向这个玩家发请求
					FightRequestServerMessage frsm = (FightRequestServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_FIGHTREQUEST);
					int myID = cm.getID();
					frsm.set(String.valueOf(myID), getNameByID(myID));
					cc.sendServerMessage(frsm);
					Server.this.mMessagePool.recycleMessage(frsm);				
				}
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_ACCEPTFIGHT, AcceptFightClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				AcceptFightClientMessage afcm = (AcceptFightClientMessage) pClientMessage;
				int othersID = Integer.parseInt(afcm.getOthersID());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(othersID);
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				String p0ID = afcm.getOthersID();
				String p1ID = afcm.getMyID();
				int battleID = createBattle(p0ID, p1ID);
				AcceptFightServerMessage afsm = (AcceptFightServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_ACCEPTFIGHT);
				afsm.set(p0ID, p1ID, String.valueOf(battleID));
				cc.sendServerMessage(afsm);
				Server.this.mMessagePool.recycleMessage(afsm);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_BEGINFIGHT, BeginFightClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				BeginFightClientMessage bfcm = (BeginFightClientMessage) pClientMessage;
				int othersID = Integer.parseInt(bfcm.getOthersID());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(othersID);
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				Battle battle = battles.get(Integer.parseInt(bfcm.getBattleKey()));
				battle.setP0deskSize(bfcm.getSize());
				
				BeginFightServerMessage bfsm = (BeginFightServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_BEGINFIGHT);
				bfsm.set(bfcm.getBattleKey());
				cc.sendServerMessage(bfsm);
				Server.this.mMessagePool.recycleMessage(bfsm);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_SELECTHAND, SelectHandClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				SelectHandClientMessage shcm = (SelectHandClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(shcm.getBattleID()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				battle.setP1deskSize(shcm.getSize());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(battle.getP0()));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				int r;
				GetCardServerMessage gcsm0 = (GetCardServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_GETCARD);
				GetCardServerMessage gcsm1 = (GetCardServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_GETCARD);
				if (shcm.getMessage() == 1){//p1选择先手
					r = (int) (Math.random() * battle.getP1deskSize());
					gcsm0.set((short)3, r);
					gcsm1.set((short)4, r);
				}else{//p1选择后手
					r = (int) (Math.random() * battle.getP0deskSize());
					gcsm0.set((short)4, r);
					gcsm1.set((short)3, r);
				}
				cc.sendServerMessage(gcsm0);
				pClientConnector.sendServerMessage(gcsm1);
				Server.this.mMessagePool.recycleMessage(gcsm0);
				Server.this.mMessagePool.recycleMessage(gcsm1);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_BEFORECALCULATE, BeforeCalculateClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
//				System.out.println("receive BeforeCalculateClientMessage!");
				BeforeCalculateClientMessage bccm = (BeforeCalculateClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(bccm.getBattleKey()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(battle.getOthersID(bccm.getID())));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				ArrayList<Integer> tempList = bccm.get();
				BeforeCalculateServerMessage bcsm = (BeforeCalculateServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_BEFORECALCULATE);
				bcsm.set(tempList);
				cc.sendServerMessage(bcsm);
				Server.this.mMessagePool.recycleMessage(bcsm);
//				System.out.println("send BeforeCalculateServerMessage!");
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_AFTERCALCULATE, AfterCalculateClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				AfterCalculateClientMessage accm = (AfterCalculateClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(accm.getBattleKey()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(battle.getOthersID(accm.getID())));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				AfterCalculateServerMessage acsm = (AfterCalculateServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_AFTERCALCULATE);
				cc.sendServerMessage(acsm);
				Server.this.mMessagePool.recycleMessage(acsm);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_BEGINATTACK, BeginAttackClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				BeginAttackClientMessage bacm = (BeginAttackClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(bacm.getBattleKey()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				
				String othersID = battle.getOthersID(bacm.getID());
//				if (othersID.equals(battle.getP0()))
//					battle.setP0deskSize(bacm.getDeskSize());
//				else
//					battle.setP1deskSize(bacm.getDeskSize());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(othersID));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				ArrayList<Double> tempList = bacm.get();
				ArrayList<Integer> intList = new ArrayList<Integer>();
				intList.clear();
				for (Double d : tempList){
					int i = (int) (d * 20);
					intList.add(i);
				}
				
				BeginAttackServerMessage basm = (BeginAttackServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_BEGINATTACK);
				basm.set(intList);
				cc.sendServerMessage(basm);
				Server.this.mMessagePool.recycleMessage(basm);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_AFTERATTACK, AfterAttackClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				AfterAttackClientMessage aacm = (AfterAttackClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(aacm.getBattleKey()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				
				String othersID = battle.getOthersID(aacm.getID());
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(othersID));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				int r;
				GetCardServerMessage gcsm0 = (GetCardServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_GETCARD);
				GetCardServerMessage gcsm1 = (GetCardServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_GETCARD);
				
				if (othersID.equals(battle.getP0())){//pClientConnector  为p1
					battle.setP0deskSize(aacm.getDeskSize());
					battle.setP1deskSize(aacm.getDeskSize2());
					r = (int) (Math.random() * battle.getP1deskSize());
					gcsm0.set((short)3, r);
					gcsm1.set((short)4, r);
					cc.sendServerMessage(gcsm0);
					pClientConnector.sendServerMessage(gcsm1);
				}else{//pClientConnector  为p0
					battle.setP1deskSize(aacm.getDeskSize());
					battle.setP0deskSize(aacm.getDeskSize2());
					r = (int) (Math.random() * battle.getP0deskSize());
					gcsm0.set((short)4, r);
					gcsm1.set((short)3, r);
					cc.sendServerMessage(gcsm1);
					pClientConnector.sendServerMessage(gcsm0);
				}
				
				Server.this.mMessagePool.recycleMessage(gcsm0);
				Server.this.mMessagePool.recycleMessage(gcsm1);
			}});
		
		mMessageReader.registerMessage(FLAG_CLIENTMESSAGE_GAMEOVER, GameOverClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(
					ClientConnector<SocketConnection> pClientConnector,
					IClientMessage pClientMessage) throws IOException {
				GameOverClientMessage gocm = (GameOverClientMessage) pClientMessage;
				Battle battle = battles.get(Integer.parseInt(gocm.getBattleKey()));
				if (battle == null){
					//TODO 向pClientConnector返回错误信息
					return;
				}
				
				ClientConnector<SocketConnection> cc = serverSocket.findClientByID(Integer.parseInt(battle.getOthersID(gocm.getID())));
				if (cc == null){
					//TODO 返回错误信息
					return;
				}
				
				ArrayList<Double> tempList = gocm.get();
				ArrayList<Integer> intList = new ArrayList<Integer>();
				intList.clear();
				for (Double d : tempList){
					int i = (int) (d * 20);
					intList.add(i);
				}				
				BeginAttackServerMessage basm = (BeginAttackServerMessage) Server.this.mMessagePool.obtainMessage(FLAG_SERVERMESSAGE_BEGINATTACK);
				basm.set(intList);
				cc.sendServerMessage(basm);
				Server.this.mMessagePool.recycleMessage(basm);
			}});
	}
	
	private synchronized int createBattle(String p1, String p2){
		battles.append(++battleKey, new Battle(p1,p2));
		return battleKey;
	}

	public Server() {
		initMessagePool();
		initMessageReader();
		battleKey = 0;
		
		serverSocket = new SocketServer<SocketConnectionClientConnector>(SERVER_PORT, new ExampleClientConnectorListener(), new ExampleServerStateListener()) {
			@Override
			protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
				final SocketConnectionClientConnector clientConnector = new SocketConnectionClientConnector(pSocketConnection, mMessageReader);
				return clientConnector;
			}
		};
		serverSocket.start();
		System.out.println("服务器已启动...");
		
	}
	
	//TODO
	private String getNameByID(final int id){
		return "helloworld";
	}

	private int getIdByName(String nam) {
		int result = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// 加载驱动程序
			Class.forName("com.mysql.jdbc.Driver");

			Connection connection = (Connection) DriverManager.getConnection(
					url, user, password1);

			if (!connection.isClosed()){
//				System.out.println("Succeeded connecting to the Database!");
			}
			// statement用来执行SQL语句
			Statement statement = (Statement) connection.createStatement();

			// 要执行的SQL语句
			String sql = "select id from user where name = '" + nam + "'";
			// 执行SQL语句并返回结果集
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				result =  rs.getInt("id");
			}
//			System.out.println("result = "+ result);
			// 关闭结果集
			rs.close();
			statement.close();
			// 关闭连接
			connection.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	private class ExampleClientConnectorListener implements ISocketConnectionClientConnectorListener {
		@Override
		public void onStarted(final ClientConnector<SocketConnection> pConnector) {
			System.out.println("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}

		@Override
		public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
			System.out.println("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}
	}
	
	private class ExampleServerStateListener implements ISocketServerListener<SocketConnectionClientConnector> {
		@Override
		public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
			System.out.println("SERVER: Started.");
		}

		@Override
		public void onTerminated(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
			System.out.println("SERVER: Terminated.");
		}

		@Override
		public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) {
			System.out.println("SERVER: Exception: " + pThrowable);
		}
	}
}
