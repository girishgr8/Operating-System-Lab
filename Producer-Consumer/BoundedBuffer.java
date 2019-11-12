import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Semaphore{
	static int n = 5;
	static int queue[] = new int[n];
	static AtomicInteger semaphore = new AtomicInteger(1);	
	static AtomicInteger filled = new AtomicInteger(0);
	static AtomicInteger empty = new AtomicInteger(n);
	static AtomicInteger item = new AtomicInteger(1);	
	static AtomicInteger lastProducedIndex = new AtomicInteger(0);
	static AtomicInteger lastConsumedIndex = new AtomicInteger(0);
	
	public Semaphore(){}
	public static synchronized int readSemaphore(){
		return semaphore.intValue();
	}

	public static synchronized void updateSemaphore(){
		if(readSemaphore()==0)
			semaphore.getAndIncrement(); 
		else  
			semaphore.getAndDecrement();
	}
	
	public static void waitOnSemaphore(String name){
		boolean once = true;
		while(readSemaphore()==0){
			try{
				Thread.currentThread().sleep(1000);
				if(once==true){
					System.out.println(name+" has an exclusive lock over shared resource.-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>."); 
					once=false;
				}
			}catch(Exception e){}
		}
	}
	
	public static synchronized int produceItem() {
		queue[lastProducedIndex.intValue()%n] = item.intValue();
		lastProducedIndex.getAndIncrement();
		filled.getAndIncrement();
		empty.getAndDecrement();
		item.getAndIncrement();
		return item.intValue()-1;
	}
	
	public static synchronized int consumeItem() {
		int consumedItem = queue[lastConsumedIndex.intValue()%n];
		lastConsumedIndex.getAndIncrement();
		filled.getAndDecrement();
		empty.getAndIncrement();
		return (consumedItem);
	}
}

class Producer extends Thread implements Runnable{
	public Producer() {}
	Random r = new Random();
	public void run() {
		while(true){
			try {
				Thread.currentThread().sleep(1000);
				System.out.println("Producer Thread Running");
				int maxProdLimit = r.nextInt(Semaphore.n)+1;
				System.out.println("Maximum Production Limit is: "+maxProdLimit);
				Semaphore.waitOnSemaphore("Consumer");
				System.out.println("Producer acquired a lock !");
				for(int i=0;i<maxProdLimit;i++) {
					if(Semaphore.empty.intValue()!=0)// If not filled... 
						System.out.println("Producer produced item "+Semaphore.produceItem());
					else {
						Semaphore.updateSemaphore();
						System.out.println("There is no space to store newly produced resources.");
						System.out.println("Lock released, Semaphore value from Producer = "+Semaphore.semaphore);
						notify();
						wait(1000);
					}
				}
				Semaphore.updateSemaphore();
			}catch(Exception e){}
		}
	}
}

class Consumer extends Thread implements Runnable{
	public Consumer() {}
	Random r = new Random();
	public void run() {
		while(true){
			try {
				Thread.currentThread().sleep(1000);
				System.out.println("Consumer Thread Running");
				int maxConsLimit = r.nextInt(Semaphore.n)+1;
				System.out.println("Maximum Consumption Limit is: "+maxConsLimit);
				Semaphore.waitOnSemaphore("Producer"); //acquire lock
				System.out.println("Consumer acquired a lock !");
				for(int i=0;i<maxConsLimit;i++) {
					if(Semaphore.filled.intValue()!= 0)// If not empty... 
						System.out.println("Consumer consumed item "+Semaphore.consumeItem());
					else {
						Semaphore.updateSemaphore();//release lock
						System.out.println("There is no more item to be consumed.");
						System.out.println("Lock released, Semaphore value from Consumer = "+Semaphore.semaphore);
						wait(1000);
						notify();
					}
				}
				Semaphore.updateSemaphore();//release lock
			}catch(Exception e){}
		}
	}
}


class BoundedBuffer { 
	public static void main(String[] args) {
    System.out.println("\t\tProducer-Consumer Problem using Semaphores\n");
		try{	
			Producer producer = new Producer();
			Consumer consumer = new Consumer();
			producer.setName("Producer");
			consumer.setName("Consumer");
			producer.start();
			Thread.currentThread().sleep(1000);
			consumer.start();
		}catch(Exception e){}	
	}
}
