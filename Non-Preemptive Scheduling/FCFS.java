import java.util.*;
import java.io.*;

interface Global {
	Random r = new Random();
	final static int n = r.nextInt((5) + 1) + 5;
	static Vector<Process> copy = new Vector<Process>();
	static Queue<Process> readyQueue = new LinkedList<Process>();
	static Queue<Process> globalQueue = new LinkedList<Process>();
	static Vector<Integer> timeline = new Vector();
}

class FCFS implements Global {
	Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("\nFCFS(First Come First Serve) is a non-preemptive scheduling algorithm, process which arrived(assigned to RQ) first is selected first.\nThere may be starvation of processes if one executing currently has longer burst time.This leads to Convoy effect.\n");
		Thread.currentThread().sleep(3000);
		System.out.println("Number of processes = " + n + "\n");
		Output output = new Output();
		output.setName("Output Thread");
		ForLoop forloop = new ForLoop();
		forloop.setName("ForLoop Thread");
		timeline.add(0);
		forloop.start();
		output.start();
	}
}

class Process implements Global {
	int at, bt, wt, tt, pid;
	Thread thread;

	public Process(int at, int bt, int i) {
		this.at = at;
		this.bt = bt;
		this.wt = 0;
		this.tt = 0;
		this.pid = i;
		this.thread = new Thread(String.valueOf(i));
	}

	@Override
	public String toString() {
		return pid + "\t" + at + "\t" + bt + "\t" + wt + "\t" + tt + "\n";
	}
}

class ForLoop extends Thread implements Global {
	static int startTime = 0;

	public ForLoop() {
	}

	public void run() {
		try {
			Random r = new Random();
			for (int i = 0; i < n; i++) {
				Thread.currentThread().sleep(1000);
				Thread thread = new Thread();
				startTime = Math.abs(r.nextInt(((startTime + 10) - startTime) + 1) + startTime);
				Process p = new Process(startTime, r.nextInt(20) + 1, (i + 1));
				copy.add(p);
				globalQueue.add(p);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}

class Output extends Thread implements Global {
	String ganttChart = "0";
	public Output() {
	}

	public void run() {
		try {
			while (true) {
				Thread.currentThread().sleep(4000);

				while (readyQueue.size() != 0 || globalQueue.size() != 0) {
					addProcessByArrivalTime();
					if(!(timeline.lastElement()==0 && readyQueue.peek().at>0)){
                        System.out.println("Processes in ready queue are:");
                        System.out.println("\nPID\tAT\tBT\tWT\tTT");
                        System.out.println(readyQueue);
                    }
					if (readyQueue.size() == 0 && timeline.lastElement() != 0) {
						ganttChart+= "____|IDLE|____"+(globalQueue.peek().at);
						timeline.add(globalQueue.peek().at);
						System.out.println(ganttChart);
						continue;
					}
					if (readyQueue.peek().at - timeline.lastElement() > 0 && timeline.lastElement() == 0) {
						timeline.add(readyQueue.peek().at);
						ganttChart+= "____|IDLE|____"+(readyQueue.peek().at);
						System.out.println(ganttChart);
						continue;
					}
					Process process = readyQueue.poll();
					process.wt = timeline.lastElement() - process.at;
					ganttChart+="____|P" + process.pid + "|____";
					process.tt = process.wt + process.bt;
					timeline.add(timeline.lastElement() + process.bt);
					ganttChart+=timeline.lastElement().toString();
					Thread.currentThread().sleep(process.bt * 1000);
					System.out.println(ganttChart);

				}
				System.out.println("\nThe Gantt Chart is: \n"+ganttChart);
				System.out.println();
				System.out.println("\nPID\tAT\tBT\tWT\tTT");
				System.out.println(copy);
				int total_wait = 0, total_turn = 0;
				for (Process p : copy) {
					total_wait += p.wt;
					total_turn += p.tt;
				}
				System.out.println(
						"Average Waiting Time in the queue: " + (double) (total_wait) / (double) (n) + " units");
				System.out.println(
						"Average Waiting Time in the queue: " + (double) (total_turn) / (double) (n) + " units");
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void addProcessByArrivalTime() {
		while (true) {
			if (timeline.lastElement() == 0) {
				readyQueue.add(globalQueue.poll());
				break;
			}
			if (globalQueue.size() != 0 && globalQueue.peek().at <= timeline.lastElement())
				readyQueue.add(globalQueue.poll());
			else
				break;
		}
	}
}
