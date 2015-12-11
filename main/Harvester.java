package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class Harvester implements Runnable {
	
	private static int cores;
	private SystemMonitorWindow smw;
	private BufferedReader br;
	HarvesterType hType;
	
	
	public Harvester(SystemMonitorWindow smw, Boolean proc, Boolean mem, Boolean cpu) {
		this.smw = smw;
		cores = getCores();

		
		if(proc) {
			//System.out.println("proc");
			collectProc(smw);
			hType = HarvesterType.PROCESS;
		} 
		if(mem) {
			//System.out.println("mem");
			collectMem(smw);
			hType = HarvesterType.MEMORY;
		}
		if(cpu) {
			//System.out.println("cpu");
			collectCPU(smw);
			hType = HarvesterType.CPU;
		}
	}


	public synchronized void run() {
		while(true) {
			switch(hType) {
			case CPU:
				collectCPU(smw);
				break;
			case MEMORY:
				collectMem(smw);
				break;
			case PROCESS:
				collectProc(smw);
				break;
			}
			pause();
		}
	}

	// wait until the program is ready
	private void pause() {
		try {
			wait();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void collectProc(SystemMonitorWindow smw) {
		String name = "";
		String pid = "";
		String state = "";
		String threads = "";
		String voluntary = "";
		String nonvoluntary = "";
		File directory;
		String[] names;
		String status;
		
		directory = new File("/proc"); // directory to start search of PIDs
		names = directory.list();
		status = "";
		
		try {
		
			// clear out the process list
			smw.removeAllRowsFromProcList();
			
			for(int i = 0; i < names.length; i++) {

				// first check if names[i] is an integer -> makes it a pid
				if(names[i].matches("^\\d+$")) { 
					
					status = "/proc/" + names[i].toString() + "/status";
					
					// current line
					String line = "";
					
					
					br = new BufferedReader(new FileReader(status));
					
					
					while((line = br.readLine()) != null) {
						//System.out.println(line);
						
						// name
						if(line.startsWith("Name")) {
							name = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("name: " + name);
						}
						
						// PID
						if(line.startsWith("Pid")) {
							pid = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("pid: " + pid);
						}
						
						// state
						if(line.startsWith("State")) {
							state = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("state: " + state);
						}
						
						// threads
						if(line.startsWith("Threads")) {
							threads = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("threads: " + threads);
						}
						
						// voluntary
						if(line.startsWith("voluntary_ctxt_switches")) {
							voluntary = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("voluntary: " + voluntary);
						}
						
						// non-voluntary
						if(line.startsWith("nonvoluntary_ctxt_switches")) {
							nonvoluntary = line.substring(line.indexOf(':')+1).trim();
							//System.out.println("nonvoluntary: " + nonvoluntary);
						}
						
					} // end while
	
					// update GUI
					// Order: {"Name", "pid", "State", "# Threads", "Vol ctxt sw", "nonVol ctxt sw"};
					String [] data = {name, pid, state, threads, voluntary, nonvoluntary};
					smw.addRowToProcList(data);
	
				} // end if
				

			} // end for
				
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
	}
	
	
	public void collectMem(SystemMonitorWindow smw) {
		int memTotal = 0; 	// total amount of physical RAM
		int memFree = 0; 	// amount of physical RAM left unused
		int active = 0; 	// buffer or cache memory in use
		int inactive = 0; 	// buffer or cache memory that is free or unavailable
		int swapTotal = 0; 	// total amount of swap available
		int swapFree = 0; 	// total amount of swap free
		int dirty = 0; 		// total amount of memory waiting to be written back to disk
		int writeback = 0; 	// total amount of memory actively being written back to disk
		
		try {
			// current line in /proc/meminfo
			String line = "";
			
			br = new BufferedReader(new FileReader("/proc/meminfo"));
			
			while((line = br.readLine()) != null) {
				
				String [] a = line.split(" "); // break up the current line into an array, split by spaces, integer value will be 2nd to last element
				//System.out.println("line: " + line);
				
				// get memory total
				if(line.startsWith("MemTotal:")) {
					//System.out.println("memTotal: " + a[a.length - 2]);
					memTotal = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory free
				if(line.startsWith("MemFree:")) {
					//System.out.println("memTotal: " + a[a.length - 2]);
					memFree = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory active
				if(line.startsWith("Active:")) {
					//System.out.println("Active: " + a[a.length - 2]);
					active = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory inactive
				if(line.startsWith("Inactive:")) {
					//System.out.println("Inactive: " + a[a.length - 2]);
					inactive = Integer.parseInt(a[a.length - 2]);
				}
				
				// get swap total
				if(line.startsWith("SwapTotal:")) {
					//System.out.println("SwapTotal: " + a[a.length - 2]);
					swapTotal = Integer.parseInt(a[a.length - 2]);
				}
				
				// get swap free
				if(line.startsWith("SwapFree:")) {
					//System.out.println("SwapFree: " + a[a.length - 2]);
					swapFree = Integer.parseInt(a[a.length - 2]);
				}
				
				// get dirty pages
				if(line.startsWith("Dirty:")) {
					//System.out.println("dirty: " + a[a.length - 2]);
					dirty = Integer.parseInt(a[a.length - 2]);
				}
				
				// get write back
				if(line.startsWith("Writeback:")) {
					//System.out.println("Writeback: " + a[a.length - 2]);
					writeback = Integer.parseInt(a[a.length - 2]);
				}
				
				
			}
			
			// calculate RAM
			double dif = memTotal - memFree;
			double ram = dif/memTotal;
			ram = ram*100;
			
			// update the GUI
			smw.updateMemoryInfo(memTotal, memFree, active, inactive, swapTotal, swapFree, dirty, writeback);
			smw.getCPUGraph().addDataPoint(4, (int)ram);
			System.out.println("RAM: " + ram);
			
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
		
	}
	
	public void collectCPU(SystemMonitorWindow smw) {
		// put initial info in step1, final info in step2, then use to calculate %
		ArrayList<ArrayList<Integer>> step1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> step2 = new ArrayList<ArrayList<Integer>>();
		
		try {
			
			String line = ""; // current line of the buffered reader
			br = new BufferedReader(new FileReader("/proc/stat"));
			line = br.readLine();
			
			/*
			 * Format of proc/stat:
			 * cpuNum	time	idleTime	mode	task
			 * 0		1		2			3		4
			 */
			
			// step1
			while(( line = br.readLine()) != null) {
				ArrayList<Integer> info = new ArrayList<Integer>(); // ArrayList to store data in first collection
				if(line.startsWith("cpu")) {
					String[] tokens = line.split(" ");
					//System.out.println("line: " + line);
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
				ArrayList<Integer> info = new ArrayList<Integer>(); // ArrayList to store data in second collection
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
	}
	
	
	/* getCores()
	 * use Runnable to find the number of cores in the current machine
	 * return the number of cores (int)
	 */
	public int getCores() {
		cores = Runtime.getRuntime().availableProcessors();
		return cores;
	}
	
	/*	calcCPUPercent(ArrayList<ArrayList<Integer>> step1, ArrayList<ArrayList<Integer>> step2, SystemMonitorWindow smw)
	 *  calculates the CPU percentage and updates the GUI
	 *  returns void
	 *  Calculation for CPU percentage:
	 *  ( (time2 - time1)+(idleTime2 - idleTime1)+(mode2 - mode1) ) / 
	 *  ( [All the stuff on top] + (task2 - task1) ) * 100 
	 */
	private static void calcCPUPercent(ArrayList<ArrayList<Integer>> step1, ArrayList<ArrayList<Integer>> step2, SystemMonitorWindow smw) {
		int time1 = 0;
		int time2 = 0;
		int idleTime1 = 0;
		int idleTime2 = 0;
		int mode1 = 0;
		int mode2 = 0;
		int task1 = 0;
		int task2 = 0;
		
		// loop over the cores => max is 4 because only 5 lines on graph, one for RAM
		for(int i = 0; i < cores; i++) {
			// step1
			time1 = step1.get(i).get(0);
			idleTime1 = step1.get(i).get(1);
			mode1 = step1.get(i).get(2);
			task1 = step1.get(i).get(3);
			
			// step2
			time2 = step2.get(i).get(0);
			idleTime2 = step2.get(i).get(1);
			mode2 = step2.get(i).get(2);
			task2 = step2.get(i).get(3);
			
			// calculation: (top / bottom) * 100
			double top = (time2 - time1) + (idleTime2 - idleTime1) + (mode2 - mode1);
			double bottom = (top + (task2 - task1));
			int calculation = (int) ((top / bottom) * 100);
			System.out.println("calculation: " + calculation);
			
			// update the GUI
			synchronized(smw) {
				if(i < 4) { // only 4 lines for CPUs
					smw.getCPUGraph().addDataPoint(i, calculation);
					smw.getCPUGraph().repaint();
				}
			}
		}
		// end loop
	}

}
