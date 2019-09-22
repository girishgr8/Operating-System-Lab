import java.util.*;
import java.io.*;

interface Global {
    Random r = new Random();
    final static int n = r.nextInt((5) + 1) + 5;
    static Vector<Process> copy = new Vector<Process>();
    static Vector<Process> readyQueue = new Vector<Process>();
    static Queue<Process> globalQueue = new LinkedList<Process>();
    static Vector<Integer> timeline = new Vector();
}

class SJF implements Global {
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("\nSJF(Shortest Job First) is non-preemptive scheduling algorithm where the process to be scheduled next is selected on basis of burst time.\nHere, the process with lesser burst time is selected first.\nIn this method, there may be starvation of longer processes.\nBut, this method gives least average WT and TT.\n");
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

    public int getProcessByLeastBurstTime() {
        int least_burst = readyQueue.firstElement().bt;
        int index = 0;
        for (int i = 1; i < readyQueue.size(); i++) {
            if (least_burst > readyQueue.get(i).bt) {
                index = i;
                least_burst = readyQueue.get(i).bt;
            } else
                continue;
        }
        return index;
    }
}

class Process {
    int at, bt, wt, tt, pid, pr;

    public Process(int at, int bt, int pr, int i) {
        this.at = at;
        this.bt = bt;
        this.pr = pr;
        this.wt = 0;
        this.tt = 0;
        this.pid = i;
    }

    @Override
    public String toString() {
        return pid + "\t\t" + at + "\t\t" + bt + "\t\t" + wt + "\t\t" + tt + "\n";
    }
}

class findProcessByPriority implements Comparator<Process> {
    public int compare(Process first, Process second) {
        return first.bt - second.bt;
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
                startTime = Math.abs(r.nextInt(((startTime + 5) - startTime) + 1) + startTime);
                Process process = new Process(startTime, r.nextInt(10) + 1, r.nextInt(6) + 1, (i + 1));
                copy.add(process);
                globalQueue.add(process);
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
                Thread.currentThread().sleep(5000);
                while (readyQueue.size() != 0 || globalQueue.size() != 0) {
                    addProcessByArrivalTime();
                    if(!(timeline.lastElement() == 0 && readyQueue.firstElement().at>0)){    
                        System.out.println("Processes in ready queue are:");
                        System.out.println("\nPID\t\tAT\t\tBT\t\tWT\t\tTT");
                        System.out.println(readyQueue);
                    }
                    if (readyQueue.size() == 0 && timeline.lastElement() != 0) {
                        timeline.add(globalQueue.peek().at);
                        ganttChart+= "____|IDLE|____"+globalQueue.peek().at;
                        continue;
                    }
                    if (readyQueue.firstElement().at - timeline.lastElement() > 0 && timeline.lastElement() == 0) {
                        timeline.add(readyQueue.firstElement().at);
                        ganttChart+= "____|IDLE|____"+readyQueue.firstElement().at;
                        continue;
                    }
                    int i = new SJF().getProcessByLeastBurstTime();
                    Process process = readyQueue.remove(i);
                    System.out.println("Process P"+process.pid+" selected as it has smaller execution time.");
                    process.wt = timeline.lastElement() - process.at;
                    ganttChart+="____|P" + process.pid + "|____";
                    process.tt = process.wt + process.bt;
                    timeline.add(timeline.lastElement() + process.bt);
                    ganttChart+=timeline.lastElement();
                    Thread.currentThread().sleep(process.bt * 1000);
                    System.out.println(ganttChart);
                }
                System.out.println("\nThe Gantt Chart is: \n"+ganttChart);
                System.out.println();
                System.out.println("\nPID\t\tAT\t\tBT\t\tWT\t\tTT");
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
