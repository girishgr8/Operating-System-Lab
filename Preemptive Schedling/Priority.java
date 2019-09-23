import java.util.*;
import java.io.*;

interface Global {
    Random r = new Random();
    final static int n = 7;
    static Vector<Process> copy = new Vector<Process>();
    static Vector<Process> readyQueue = new Vector<Process>();
    static Queue<Process> globalQueue = new LinkedList<Process>();
    static Vector<Integer> timeline = new Vector<Integer>();
}

class Priority implements Global {
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(
                "Priority Scheduling (preemptive) is scheduling algorithm in which if a higher priority process arrives, then currently executing process is preempted.\nThere may be starvation of the processes if new processes with higher priority arrives simultaneously.\n");
        // Thread.currentThread().sleep(3000);
        System.out.println("Number of processes = " + n + "\n");
        Output output = new Output();
        output.setName("Output Thread");
        ForLoop forloop = new ForLoop();
        forloop.setName("ForLoop Thread");
        timeline.add(0);
        forloop.start();
        output.start();
    }

    public int getProcessByHighestPriority() {
        int pr = readyQueue.firstElement().pr;
        int index = 0;
        if (timeline.size() == 1) {
            return index;
        }
        for (int i = 1; i < readyQueue.size(); i++) {
            if (pr > readyQueue.get(i).pr) {
                index = i;
                pr = readyQueue.get(i).pr;
            }
        }
        return index;
    }
}

class Process {
    int at, bt, wt, tt, pid, rt, pr;
    boolean newly;

    public Process(int at, int bt, int pr, int i) {
        this.at = at;
        this.bt = bt;
        this.rt = bt;
        this.pr = pr;
        this.wt = 0;
        this.tt = 0;
        this.pid = i;
        this.newly = true;
    }

    @Override
    public String toString() {
        return pid + "\t\t" + at + "\t\t" + bt + "\t\t" + pr + "\t\t" + wt + "\t\t" + tt + "\n";
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
                startTime = Math.abs(r.nextInt(((startTime + 4) - startTime) + 1) + startTime);
                int burstTime = r.nextInt(10) + 1;
                Process p = new Process(startTime, burstTime, r.nextInt(6), (i + 1));
                copy.add(p);
                globalQueue.add(p);
            }
        } catch (Exception e) {
            System.out.println("ForLoop class error" + e);
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
                Thread.currentThread().sleep(5000);
                while (readyQueue.size() != 0 || globalQueue.size() != 0) {
                    int pet = 0;
                    addProcessByArrivalTime();

                    if (!(timeline.lastElement() == 0 && readyQueue.firstElement().at > 0)) {
                        System.out.println("\nProcesses in ready queue are:");
                        System.out.println("\nPID\t\tAT\t\tBT\t\tRT\t\tPR");
                        for (Process temp : readyQueue)
                            System.out.println(temp.pid + "\t\t" + temp.at + "\t\t" + temp.bt + "\t\t" + temp.rt
                                    + "\t\t" + temp.pr);
                    }

                    System.out.println();
                    // System.out.print(timeline.lastElement());
                    int i = new Priority().getProcessByHighestPriority();
                    Process process;
                    if (readyQueue.size() == 0 && timeline.lastElement() != 0) {
                        System.out.print("____|IDLE|____"); // print IDLE
                        timeline.add(globalQueue.peek().at);
                        ganttChart += "____|IDLE|____" + globalQueue.peek().at;
                        continue;
                    } else if (readyQueue.firstElement().at - timeline.lastElement() > 0
                            && timeline.lastElement() == 0) {
                        System.out.print("0____|IDLE|____" + readyQueue.firstElement().at); // print IDLE
                        timeline.add(readyQueue.firstElement().at);
                        ganttChart += "____|IDLE|____" + readyQueue.firstElement().at;
                        continue;
                    } else if (readyQueue.firstElement().at - timeline.lastElement() > 0
                            && timeline.lastElement() != 0) {
                        System.out.print("____|IDLE|____" + readyQueue.firstElement().at); // print IDLE
                        timeline.add(readyQueue.firstElement().at);
                        ganttChart += "____|IDLE|____" + readyQueue.firstElement().at;
                        continue;
                    }else if(readyQueue.size()==1 && globalQueue.size()==0){
						timeline.add(timeline.lastElement() + readyQueue.firstElement().rt);
						ganttChart += "____|P"+ readyQueue.get(0).pid + "|____" +timeline.lastElement();
						System.out.println(ganttChart);
						readyQueue.remove(0);
					}					
					else if (readyQueue.size() == 0 && timeline.lastElement() != 0 && globalQueue.size() != 0) {
                        timeline.add(timeline.lastElement() + globalQueue.peek().at);
                        System.out.print("____|IDLE|____" + globalQueue.peek().at);
                        ganttChart += "____|IDLE|____" + globalQueue.peek().at;
                    } 
					else {
                        process = readyQueue.remove(i);
                        boolean preempt = false;
                        ganttChart += "____|P" + process.pid + "|____";
                        out: while (!preempt && process.rt != 0) {
                            System.out.println("Remaining time of P" + process.pid + " is: " + process.rt);
                            System.out.println("\nRQ at time instant = " + timeline.lastElement() + " is: ");
                            System.out.println("\nPID\t\tAT\t\tBT\t\tRT\t\tPR");
                            for (Process temp : readyQueue)
                                System.out.println(temp.pid + "\t\t" + temp.at + "\t\t" + temp.bt + "\t\t" + temp.rt + "\t\t" + temp.pr);
                            int min_pr = process.pr;
                            int index = 0;
                            for (int j = 0; j < readyQueue.size(); j++) {
                                if (readyQueue.get(j).newly == true) {
                                    if (min_pr > readyQueue.get(j).pr) {
                                        preempt = true;
                                        min_pr = readyQueue.get(j).pr;
                                        index = j;
                                    }
                                }
                            }
                            if (preempt) {
                                System.out.println("Preempting process P" + process.pid);
                                break out;
                            }
                            process.rt--;
                            timeline.add(timeline.lastElement() + 1);
                            Thread.currentThread().sleep(1000);
                            System.out.println(ganttChart + timeline.lastElement());
                            addProcessByArrivalTime();
                        }
                        ganttChart += timeline.lastElement();
						if(readyQueue.size()==1 && globalQueue.size()==0){
							timeline.add(timeline.lastElement() + readyQueue.firstElement().rt);
							ganttChart += "____|P"+ readyQueue.firstElement().pid + "|____" +timeline.lastElement();
							System.out.println(ganttChart);
							readyQueue.remove(0);
						}
                        if (preempt)
                            readyQueue.add(process);
                        else if (process.rt == 0) {
                            process.tt = timeline.lastElement() - process.at;
                            process.wt = process.tt - process.bt;
                        }
                    }
                }
                System.out.println("\nThe Gantt Chart is:\n" + ganttChart);
                System.out.println();
                System.out.println("\nPID\t\tAT\t\tBT\t\tPR\t\tWT\t\tTT");
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
            // Thread.currentThread().interrupt();
            System.out.println("Output class error= " + e);
        }
    }

    public static void addProcessByArrivalTime() {
        while (true) {
            if (timeline.lastElement() == 0) {
                readyQueue.add(globalQueue.poll());
            }
            if (globalQueue.size() != 0 && globalQueue.peek().at <= timeline.lastElement())
                readyQueue.add(globalQueue.poll());
            else
                break;
        }
        // System.out.println("readyQueue = \n" + readyQueue);
    }
}
