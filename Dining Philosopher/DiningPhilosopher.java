import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

class Semaphore extends Thread{
	static Vector<AtomicInteger> fork = new Vector<AtomicInteger>();
	// condition variables(objects) as monitor....
	static Object condition[] = new Object[5];
	
	Semaphore(){
		for(int i=0;i<5;i++){
			fork.add(new AtomicInteger(1));
			condition[i] =  new Object();
		}
	}	
	public static void waitSemaphore(String str, int pid) {
		// wait on semaphore if not allowed to enter.....
		try{
			synchronized (condition[pid]){
				Semaphore.fork.get(pid).getAndDecrement();
				if(Semaphore.fork.get(pid).intValue()<0)
					condition[pid].wait();
				System.out.println(Thread.currentThread().getName()+" picked up his "+str+" (fork "+pid+")");
			}

		}catch(Exception e){System.out.println(e);}
	}
	
	public static void signalSemaphore(String str, int pid) {
		try{
			synchronized (condition[pid]){
				Semaphore.fork.get(pid).getAndIncrement();
				System.out.println(Thread.currentThread().getName()+" put down his "+str+" (fork "+pid+")");
				if(Semaphore.fork.get(pid).intValue()<=0)
					condition[pid].notify();		
			}
		}catch(Exception e){ System.out.println(e); }
	}
	synchronized public static void eat(){
		try{
			System.out.println(Thread.currentThread().getName()+" is eating.......");
		}catch(Exception e){}
	}
}

class Philosopher extends Thread{
	int pid;
	public Philosopher(int pid) {
		this.pid = pid;
	}
	public Philosopher() {}
	
	public void run() {
		while(true) {
			try{
				//think() section....;
				System.out.println(Thread.currentThread().getName()+" is thinking.......");
				if(pid==4){
					Semaphore.waitSemaphore("right fork",((pid+1)%5));
					Semaphore.waitSemaphore("left fork", pid);
					//eat() section... (critical section entered);
					System.out.println(Thread.currentThread().getName()+" is eating.......");
					Thread.sleep(3000);
					Semaphore.signalSemaphore("left fork", pid);
					Semaphore.signalSemaphore("right fork", ((pid+1)%5));
				}else{
					Semaphore.waitSemaphore("left fork", pid);
					Semaphore.waitSemaphore("right fork",((pid+1)%5));
					//eat();
					System.out.println(Thread.currentThread().getName()+" is eating.......");
					Thread.sleep(3000);
					Semaphore.signalSemaphore("right fork", ((pid+1)%5));
					Semaphore.signalSemaphore("left fork", pid);
				}
			}catch(Exception e){System.out.println(e);}
		}		
	}
}


public class DiningPhilosopher{
	DiningPhilosopher(){}
	public static void main(String[] args) {
		try{
			Thread t = new Thread();
			Semaphore s = new Semaphore();
			Philosopher p1 = new Philosopher(0);
			Philosopher p2 = new Philosopher(1);
			Philosopher p3 = new Philosopher(2);
			Philosopher p4 = new Philosopher(3);
			Philosopher p5 = new Philosopher(4);
			p1.setName("Philosopher 0");
			p2.setName("Philosopher 1");
			p3.setName("Philosopher 2");
			p4.setName("Philosopher 3");
			p5.setName("Philosopher 4");
			p1.start();
			p2.start();
			p3.start();
			p4.start();
			p5.start();
		}catch(Exception e){ System.out.println(e);}
	}
}
