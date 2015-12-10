package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class SystemMonitor {
	
	final static int DEFUALT_INTERVAL = 5000;
	static int interval = 500;
	static Scheduler cpumemSched; // CPU and Memory scheduler
	static Scheduler pidsSched; // PIDs (processes) scheduler 
	static BufferedReader br;
	static int cores;
	
	// This is called when the user selects a new update interval
	// from the GUI
	public static void setUpdateInterval(int updateInterval)
	{
		interval = updateInterval;
		// TODO : set the delay for cpumemSched to interval
	}
	
	
	public static void main (String[] args)
	{
		SystemMonitorWindow mySysMon = new SystemMonitorWindow();
		
		// number of cores
		cores = Runtime.getRuntime().availableProcessors();
		
		// ArrayList of type Harvester for CPU/memory scheduler and PIDs scheduler  
		ArrayList<Harvester> cpumemSchedList = new ArrayList<Harvester>();
		ArrayList<Harvester> pidsSchedList = new ArrayList<Harvester>();

		
		// add to the ArrayList
		cpumemSchedList.add(cpuSchedPopulate(mySysMon));
		cpumemSchedList.add(memSchedPopulate(mySysMon));
		pidsSchedList.add(pidsSchedPopulate(mySysMon));
		

		// create the Schedulers, cpumemSched uses interval specified by the user
		// pidsSched uses the default interval of 5000 milliseconds
		cpumemSched = new Scheduler(interval, cpumemSchedList);
		pidsSched = new Scheduler(DEFUALT_INTERVAL, pidsSchedList);
		
		/* test CPU calculation
		ArrayList<ArrayList<Integer>> step1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> step2 = new ArrayList<ArrayList<Integer>>();
		calcCPUPercent(step1,step2, mySysMon);
		*/
		
		// start the schedulers
		cpumemSched.start();
		pidsSched.start();
	}

	// populate the cpumemSched ArrayList from /proc/stat
	private static Harvester cpuSchedPopulate(SystemMonitorWindow smw) {
		// put initial info in step1, final info in step2, then use to calculate %
		ArrayList<ArrayList<Integer>> step1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> step2 = new ArrayList<ArrayList<Integer>>();
		
		try {
			
			String line = ""; // current line of the buffered reader
			br = new BufferedReader(new FileReader("/proc/stat"));
			line = br.readLine();
			
			// step1
			while(( line = br.readLine()) != null) {
				ArrayList<Integer> info = new ArrayList<Integer>();
				if(line.startsWith("cpu")) {
					String[] tokens = line.split(" ");
					info.add(Integer.parseInt(tokens[1]));
					info.add(Integer.parseInt(tokens[2]));
					info.add(Integer.parseInt(tokens[3]));
					info.add(Integer.parseInt(tokens[4]));
					step1.add(info);
				}
				
			}
			
			// delay in-between the file read
			Thread.sleep(250);
			
			line = ""; // re-initialize
			br = new BufferedReader(new FileReader("/proc/stat"));
			line = br.readLine();
			
			// step2
			while(( line = br.readLine()) != null) {
				ArrayList<Integer> info = new ArrayList<Integer>();
				if(line.startsWith("cpu")) {
					String[] tokens = line.split(" ");
					info.add(Integer.parseInt(tokens[1]));
					info.add(Integer.parseInt(tokens[2]));
					info.add(Integer.parseInt(tokens[3]));
					info.add(Integer.parseInt(tokens[4]));
					step2.add(info);
				}
				
			}
			
		} catch (FileNotFoundException e) { // catch for BufferedReader
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine method
			e.printStackTrace();
		} catch (InterruptedException e) { // catch for sleep method
			e.printStackTrace();
		}
		
		// close the file
		try {
			br.close();
		} catch (IOException e) { // catch for close method
			e.printStackTrace();
		}
		
		
		// final step is to calculate the CPU usage percentage 
		// using the info stored in the step1 and step2 ArrayLists
		// then update the Graph in the GUI
		calcCPUPercent(step1, step2, smw);
		Harvester h = new Harvester();
		return h;
	}


	// populate the cpumemSched ArrayList from /proc/<pid> for every pid that exists
	private static Harvester pidsSchedPopulate(SystemMonitorWindow smw) {
		String name;
		String pid;
		String state;
		String threads;
		String voluntary;
		String nonvoluntary;
		File directory;
		String[] names;
		String[] processInfo;
		String status;
		
		directory = new File("/proc");
		names = directory.list();
		processInfo = new String[6];
		status = "";
		
		try {
		
		// clear out the process list
		smw.removeAllRowsFromProcList();
		
		for(int i = 0; i < names.length; i++) {
			
			// is it a directory?
			// first check if an integer
			if(true) {
			
				status = "/proc/" + names[i].toString() + "/status";
				
				// current line
				String line = "";
				
				
					br = new BufferedReader(new FileReader(status));
				
				
				while((line = br.readLine()) != null) {
					
					// name
					
					// pid
					
					// state
					
					// threads
					
					// voluntary
					
					// nonvoluntary
					
				}

				// update gui

			}
				

		}
				
		} catch (FileNotFoundException e) { // catch for br
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine()
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) { // catch close()
			e.printStackTrace();
		}
			
		
		
		
		
		Harvester h = new Harvester();
		return h;
	}
	
	// populate the cpumemSched ArrayList from /proc/meminfo
	private static Harvester memSchedPopulate(SystemMonitorWindow smw) {
		int totalMem = 0;
		int memFree = 0;
		int memActive = 0;
		int memInactive = 0;
		int swapTotal = 0;
		int swapFree = 0;
		int dirtyPages = 0;
		int writeback = 0;
		
		try {
			// current line in /proc/meminfo
			String line = "";
			
			br = new BufferedReader(new FileReader("/proc/meminfo"));
			
			while((line = br.readLine()) != null) {
				// get memory total
				
				// get memory free
				
				// get memory active
				
				// get memory inactive
				
				// get swap total
				
				// get swap free
				
				// get dirty pages
				
				// get write back
				
				// calculate RAM
				int ram = ((totalMem - memFree)/totalMem)*100;
				
				// update the GUI
				smw.updateMemoryInfo(totalMem, memFree, memActive, memInactive, swapTotal, swapFree, dirtyPages, writeback);
				smw.getCPUGraph().addDataPoint(cores, ram);
			}
			
		} catch (FileNotFoundException e) { // catch for file
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine()
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) { // catch for close()
			e.printStackTrace();
		}
		
		
		Harvester h = new Harvester();
		return h;
	}
	
	// ( (time2 - time1)+(idleTime2 - idleTime1)+(mode2 - mode1) ) / 
	// ( [All the stuff on top] + (task2 - task1) )
	// * 100
	private static void calcCPUPercent(ArrayList<ArrayList<Integer>> step1, ArrayList<ArrayList<Integer>> step2, SystemMonitorWindow smw) {
		int time1 = 0;
		int time2 = 0;
		int idleTime1 = 0;
		int idleTime2 = 0;
		int mode1 = 0;
		int mode2 = 0;
		int task1 = 0;
		int task2 = 0;
		
		
		// loop over the number of cores
		for(int i = 0; i < cores; i++) {
			// step1
			time1 = step1.get(0).get(0);
			idleTime1 = step1.get(0).get(1);
			mode1 = step1.get(0).get(2);
			task1 = step1.get(0).get(3);
			
			// step2
			time2 = step2.get(0).get(0);
			idleTime2 = step2.get(0).get(1);
			mode2 = step2.get(0).get(2);
			task2 = step2.get(0).get(3);
		}
		// end loop
			
		// calculation
		double top = (time2 - time1) + (idleTime2 - idleTime1) + (mode2 - mode1);
		double bottom = (top + (task2 - task1));
		int calculation = (int) ((top / bottom) * 100);
		System.out.println("calculation: " + calculation);
		
		// update the GUI
		synchronized(smw) {
			smw.getCPUGraph().addDataPoint(0, calculation);
			smw.getCPUGraph().repaint();
		}
	}

}