package de.tud.tk3.distsnake.connectivity;

import org.umundo.s11n.TypedPublisher;

import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.HelloObserver;

public class HelloPublisher extends TypedPublisher implements HelloObserver {

	public HelloPublisher(Game game) {
		super("hello");
		game.setHelloObserver(this);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onGameStart(String username) {
		System.out.println("Entered onGameStart");
//		if(this.getSubscribers().isEmpty()){
//			return false;
//		}else{
			try{
				Hello msg = Hello.newBuilder().setName(username).setMsg("I am here").build();
				this.sendObject(msg);
				System.out.println("Sent the hello message");
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			
//		}
	}
}
