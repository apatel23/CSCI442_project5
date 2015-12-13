package main;

import java.util.ArrayList;


public class Scheduler extends Thread {
	
	ArrayList<Harvester> taskList;
	int delay;
	
	public Scheduler(int delay, ArrayList<Harvester> taskList)
	{
		// test
		//System.out.println("Scheduler delay: " + delay);
		this.delay = delay;
		this.taskList = taskList;
	}
	
	public void setDelay(int cpumemDelay) {
		this.delay = cpumemDelay;
	}

	private synchronized void delay()
	{
		// Here we acquire the lock for our current instance of our scheduler
		// and wait for the specified timeout
		try
		{
			wait(delay);
		} catch (InterruptedException e) {}
	}
	
	// synchronized to avoid the IllegalMonitorException
	public synchronized void run()
	{
		// create the new threads and start them by using start()
		for(Harvester harvester : taskList) {
			// create a new thread with the corresponding harvester
			Thread thread = new Thread(harvester);
			// start the thread
			thread.start();
			//System.out.println("start thread");
		}
		
		while (true)
		{
			// Wait for the specified timeout
			delay();
			
			// Dispatch each harvester to collect data/update gui.
			
			// For those unfamiliar with Java, this is the equivalent of
			// a for-each loop... For each Harvester h in Tasks...do...
			for (Harvester h : taskList)
			{
				// synchronized to avoid the IllegalMonitorException
				synchronized(h) {
					// wake up data extraction threads
					h.notifyAll();
				}
			}
		}
	}
}
