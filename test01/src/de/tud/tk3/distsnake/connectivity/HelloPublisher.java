package de.tud.tk3.distsnake.connectivity;

import org.umundo.s11n.TypedPublisher;

import de.tud.tk3.distsnake.connectivity.HelloMSG.Hello;
import de.tud.tk3.distsnake.gameLogic.Game;
import de.tud.tk3.distsnake.gameLogic.HelloObserver;

public class HelloPublisher extends TypedPublisher implements HelloObserver {

	public HelloPublisher() {
		super("hello");
		Game.getInstance().setHelloObserver(this);
	}

	@Override
	public void onGameStart(String username) {
		System.out.println("Entered onGameStart");
		try{
			Hello msg = Hello.newBuilder().setName(username).setMsg("I am here").build();
			this.sendObject(msg);
			System.out.println("Sent the hello message");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isOnlyPlayer(){
		return this.getSubscribers().isEmpty();
	}

	@Override
	public void onGameLeave(String username) {
		System.out.println("Entered onGameLeave");
		try{
			Hello msg = Hello.newBuilder().setName(username).setMsg("farewell").build();
			this.sendObject(msg);
			System.out.println("Sent the farewell message");
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}	
}
