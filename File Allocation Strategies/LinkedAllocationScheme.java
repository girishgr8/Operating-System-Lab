import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;

// Object of FAT class stores the File Allocation Table or Directory Table....
class FAT{
  String fileName;
  int fileSize;
  int startBlock;
  public FAT(){}
  public FAT(String fileName, int fileSize, int startBlock){
    this.fileName = fileName;
    this.fileSize = fileSize;
    this.startBlock = startBlock;
  }
}

class LinkedBlock{
  int blockNo, nextPtr;
  boolean isFree;
  public LinkedBlock() {}
  public LinkedBlock(int blockNo) {
    this.blockNo = blockNo;
    this.nextPtr = -1;
    this.isFree = true;
  }
}

class LinkedAllocation{
  static Scanner sc = new Scanner(System.in);
  static Vector<FAT> fat = new Vector<>();
  static int totalBlockSize;
  static int bptr = 0;
  static int totalMemorySize; 
  static int totalBlocks;
  static String memory[];
  static int cntFree;
  static Vector<LinkedBlock> freeBlocks = new Vector<LinkedBlock>();

    public static void performAllocation(int totalMemorySize,int totalBlockSize, int totalBlocks) {
        LinkedAllocation.totalMemorySize = totalMemorySize;
        LinkedAllocation.totalBlockSize = totalBlockSize;
        LinkedAllocation.totalBlocks = totalBlocks;
        cntFree = totalBlocks;
        for(int i=0;i<totalBlocks;i++) {
          LinkedBlock b = new LinkedBlock(i);
              freeBlocks.add(b);
        }
        int choice;
        System.out.printf("Choose operation you want to perform:\n1)Create new file\n2)Delete a file\n3)Show all available files\n4)Show FAT table\n5)Exit\nEnter your choice: ");
        choice = sc.nextInt();
        do{
            switch(choice){
                case 1: createFile(); break;
                case 2: deleteFile(); break;
                case 3: showAllAvailableFiles(); break;
                case 4: showFATTable(); break;
                default: break;
            }
            System.out.printf("Choose operation you want to perform:\n1)Create new file\n2)Delete a file\n3)Show all available files\n4)Show FAT table\n5)Exit\nEnter your choice: ");
            choice = sc.nextInt();
        }while(choice!=5);
    }

    public static void createFile() {
        System.out.printf("Enter name for file you want to create: ");
        String fileName = sc.next();
        System.out.printf("Enter file size in KB: ");
        int fileSize = sc.nextInt();
        int blocksNeeded = (int) Math.ceil((double)fileSize/(double)totalBlockSize);
        System.out.println("Number of blocks needed to allocate new file \""+fileName+"\" are: "+blocksNeeded);
        // One extra block needed to store the index which contains the pointers to the file blocks...
        bptr = allocateBlocks(blocksNeeded);
        if(bptr!=-1) {
            System.out.println("Created new file: \""+fileName+"\"");
            FAT file = new FAT(fileName,fileSize,bptr);
            fat.add(file);
        }
        else {
            System.out.println("Insufficient memory. Could not create new file: \""+fileName+"\". ");
            System.out.println("Free some memory by deleting some file. Do you want to delete some file? Y or N");
            if(sc.next().equals("Y")) {
                showAllAvailableFiles();
                deleteFile();
                bptr = allocateBlocks(blocksNeeded);
                if(bptr!=-1) {
                    System.out.println("Created new file: \""+fileName+"\"");
                    FAT file = new FAT(fileName,fileSize,bptr);
                    fat.add(file);
                }
            }else {
                System.out.println("Could not create a new file");
            }   
        }
    }
  
    public static int allocateBlocks(int blocksNeeded) {
        int startPtr = -1;
        System.out.println("Free blocks before allocation: "+cntFree);
        if(blocksNeeded>cntFree)
            return -1;
        int entered = 0;
        int lastIndex = -1;
        int checkBlockCount = 0;
        LinkedBlock b = new LinkedBlock();
        for(int i=0;i<totalBlocks && checkBlockCount<blocksNeeded;i++) {
            b = (LinkedBlock) freeBlocks.get(i);
            if(b.isFree == true) {
                checkBlockCount++;
                entered++;
                // If found the first free block then it will become the start pointer of the LinkedList...
                if(entered==1)
                    startPtr = b.blockNo;
                if(lastIndex!=-1)
                    freeBlocks.get(lastIndex).nextPtr = b.blockNo;
                lastIndex = b.blockNo;
                b.isFree = false;
                cntFree--;
            }
        }
        System.out.println("Free blocks after allocation: "+cntFree);
        return startPtr;
    } 
  
    public static void deleteFile() {
        String fileName;
        System.out.printf("Enter name of file you want to delete: ");
        fileName = sc.next();
        deallocateBlocks(fileName);
    }
  
    public static void deallocateBlocks(String fileName) {
        int ptr = -1;
        System.out.println("Free blocks before deallocation: "+cntFree);
        FAT delFile = new FAT();
        System.out.println("Free blocks before deallocation: "+cntFree);
        for(FAT file: fat) {
            if(file.fileName.equals(fileName)) {
                delFile = file;
                ptr = file.startBlock;
                break;
            }
        }
        // If the initial startPointer doesn't exist.....
        if(ptr==-1) {
            System.out.println("File "+fileName+" is not found");
            return;
        }
        while(ptr!=-1) {
            int prevPtr = freeBlocks.get(ptr).blockNo;
            cntFree++;
            freeBlocks.get(ptr).isFree = true;
            ptr = freeBlocks.get(ptr).nextPtr;
            freeBlocks.get(prevPtr).nextPtr=-1;
        }
        fat.remove(delFile);
        System.out.println("Free blocks after deallocation: "+cntFree);
    }
  
    public static void showAllAvailableFiles() {
        System.out.println("\n\t\t\tList of available files: ");
        System.out.println("SrNo.\tFile Name\t\t\t\tFile Size\t\t");
        int i = 1;
        for(FAT file: fat)
            System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t\t"+file.fileSize+"KB");
        System.out.println();
    }
  
    public static void showFATTable() {
        System.out.println("\n\t\t\tFAT Table for Linnked File Allocation Scheme: ");
        System.out.println("SrNo.\tFile Name\t\t\tFile Size\t\tLinkPointers");
        int i = 1;
        for(FAT file: fat) {
            LinkedBlock b = new LinkedBlock();
            System.out.printf((i++)+"\t"+file.fileName+"\t\t\t\t"+file.fileSize+"KB\t\t\t");
            b = freeBlocks.get(file.startBlock);
            while(b.nextPtr!=-1) {
                System.out.printf(b.blockNo+"->");
                b = freeBlocks.get(b.nextPtr);
            }
            System.out.printf(""+b.blockNo+"\n");
        }
        System.out.println();
    }
}

public class LinkedAllocationScheme {
    static Scanner sc = new Scanner(System.in);
    static Vector<FAT> fat = new Vector<>();
    static int totalBlockSize;
    static int totalMemorySize; 
    static int totalBlocks;
  
    public static void main(String[] args) {
    	System.out.println("\t\Linked File Allocation Strategies\n");
    	System.out.printf("Enter total memory size in KB: ");
        totalMemorySize = sc.nextInt(); 
        System.out.printf("Enter size of each block in KB: ");
        totalBlockSize = sc.nextInt();
        totalBlocks = (int) totalMemorySize/totalBlockSize;
        System.out.println("Total number of blocks present in memory: "+totalBlocks);
    	LinkedAllocation.performAllocation(totalMemorySize,totalBlockSize, totalBlocks);
    }  
}