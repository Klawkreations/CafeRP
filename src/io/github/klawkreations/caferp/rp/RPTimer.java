package io.github.klawkreations.caferp.rp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class RPTimer {
	private Timer timer;
	private int seconds;
	private int count;
	
	public RPTimer (int seconds, ActionListener listener){
		this.seconds = seconds;
		this.count = 0;
		
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count++;
				if(count >= seconds){
					listener.actionPerformed(e);
					count = 0;
				}
			}
		});
	}
	
	public void start(){
		timer.start();
	}
	
	public void stop(){
		timer.stop();
	}
	
	public String timeLeft(){
		int secondsLeft = seconds - count;
		return (secondsLeft / 60) + " minutes, " + (secondsLeft % 60) + " seconds left until payday!";
	}
}
