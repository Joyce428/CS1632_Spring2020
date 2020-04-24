import gov.nasa.jpf.vm.Verify;

import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.text.DecimalFormat;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>BeanCounterLogic: The bean counter, also known as a quincunx or the Galton
 * box, is a device for statistics experiments named after English scientist Sir
 * Francis Galton. It consists of an upright board with evenly spaced nails (or
 * pegs) in a triangular form. Each bean takes a random path and falls into a
 * slot.
 *
 * <p>Beans are dropped from the opening of the board. Every time a bean hits a
 * nail, it has a 50% chance of falling to the left or to the right. The piles
 * of beans are accumulated in the slots at the bottom of the board.
 * 
 * <p>This class implements the core logic of the machine. The MainPanel uses the
 * state inside BeanCounterLogic to display on the screen.
 * 
 * <p>Note that BeanCounterLogic uses a logical coordinate system to store the
 * positions of in-flight beans.For example, for a 4-slot machine:
 *                      (0, 0)
 *               (0, 1)        (1, 1)
 *        (0, 2)        (1, 2)        (2, 2)
 *  (0, 3)       (1, 3)        (2, 3)       (3, 3)
 * [Slot0]       [Slot1]       [Slot2]      [Slot3]
 */

public class BeanCounterLogicImpl implements BeanCounterLogic {
	// TODO: Add member methods and variables as needed

	private int beanCount;
	private int slotCount;
	private BeanImpl[] beanTotal;
	private boolean isLuck;

	private int remainCount;
	private int[] xPosArray;
	private int[] slotBeanCount;
    private LinkedList<BeanImpl> beansInFlight;

	/**
	 * Constructor - creates the bean counter logic object that implements the core
	 * logic with the provided number of slots.
	 * 
	 * @param slotCount the number of slots in the machine
	 */
	BeanCounterLogicImpl(int slotCount) {
		// TODO: Implement
		this.slotCount = slotCount;
 		beansInFlight = new LinkedList<BeanImpl>();//the normal size of the list will be slotCount(eg. 10)
 		slotBeanCount = new int[slotCount];
 		xPosArray = new int[slotCount];

	}

	/**
	 * Returns the number of slots the machine was initialized with.
	 * 
	 * @return number of slots
	 */
	public int getSlotCount() {
		// TODO: Implement

		return slotCount;
	}
	
	/**
	 * Returns the number of beans remaining that are waiting to get inserted.
	 * 
	 * @return number of beans remaining
	 */
	public int getRemainingBeanCount() {
		// TODO: Implement
		
		return remainCount;

		//return 0;
	}

	/**
	 * Returns the x-coordinate for the in-flight bean at the provided y-coordinate.
	 * 
	 * @param yPos the y-coordinate in which to look for the in-flight bean
	 * @return the x-coordinate of the in-flight bean; if no bean in y-coordinate, return NO_BEAN_IN_YPOS
	 */
	public int getInFlightBeanXPos(int yPos) {
		// TODO: Implement
		if(yPos>beansInFlight.size()-1)
			return NO_BEAN_IN_YPOS;// = -1
		else
			return xPosArray[yPos];

	}

	/**
	 * Returns the number of beans in the ith slot.
	 * 
	 * @param i index of slot
	 * @return number of beans in slot
	 */
	public int getSlotBeanCount(int i) {
		// TODO: Implement
		return slotBeanCount[i];
	}

	/**
	 * Calculates the average slot number of all the beans in slots.
	 * 
	 * @return Average slot number of all the beans in slots.
	 */
	public double getAverageSlotBeanCount() { //WHAT DECIMAL PLACE????
		// TODO: Implement

		// the sum of slot values/ number of slots
		int sum =0;
		for(int i = 0;i<slotCount;i++){
			int num = getSlotBeanCount(i);
			sum += num*i;
		}
		double sumD = (double)sum;
		double totalNum = (double)(beanCount-remainCount-beansInFlight.size());
		double val = sumD/totalNum;

		val = val*100;
		val = Math.round(val);
		val = val /100;
		
		return val;
	}

	/**
	 * Removes the lower half of all beans currently in slots, keeping only the
	 * upper half. If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void upperHalf() {
		// TODO: Implement

		//current beans count in the slots
		int currCount = (beanCount-remainCount);
		currCount -= beansInFlight.size();
		int toRemove = 0;
		//int removed = 0;

		//if((currCount%2)==1){// if currCount is odd
		// toRemove =	(currCount-1)/2;
		//}
		toRemove = currCount/2;
		for(int i=0;i<slotCount;i++){
			if(toRemove==0)
				break;
			if(slotBeanCount[i]>toRemove){
				slotBeanCount[i] -= toRemove;
				break;
			}else{
				toRemove -=slotBeanCount[i];
				slotBeanCount[i] = 0;
			}


		}

	}

	/**
	 * Removes the upper half of all beans currently in slots, keeping only the
	 * lower half.  If there are an odd number of beans, remove (N-1)/2 beans, where
	 * N is the number of beans. So, if there are 3 beans, 1 will be removed and 2
	 * will be remaining.
	 */
	public void lowerHalf() {
		// TODO: Implement
		//current beans count in the slots
		//int currCount = (beanCount-remainCount)-beansInFlight.size();
		int currCount =0;
		for(int i=0;i<slotCount;i++){
			currCount+=slotBeanCount[i];
		}

		int toRemove;
		//int removed = 0;

		//if((currCount%2)==1){// if currCount is odd
		// toRemove =	(currCount-1)/2;
		//}
		toRemove = currCount/2;
		for(int i=slotCount-1;i>=slotCount;i--){
			if(toRemove==0)
				break;
			if(slotBeanCount[i]>toRemove){
				slotBeanCount[i] -= toRemove;
				break;
			}else{//if bean count less or equal to toRemove, decrease toRemove by the number of beans in this slot; and remove all the beans in this slot
				toRemove -=slotBeanCount[i];
				slotBeanCount[i] = 0;
			}

		}


		//the number of beans in some upper slots will decrease

	}

	/**
	 * A hard reset. Initializes the machine with the passed beans. The machine
	 * starts with one bean at the top. Note: the Bean interface does not have any
	 * methods except the constructor, so you will need to downcast the passed Bean
	 * objects to BeanImpl objects to be able to work with them. This is always safe
	 * by construction (always, BeanImpl objects are created with
	 * BeanCounterLogicImpl objects and BeanBuggy objects are created with
	 * BeanCounterLogicBuggy objects according to the Config class).
	 * 
	 * @param beans array of beans to add to the machine
	 */
	public void reset(Bean[] beans) {
		// TODO: Implement
		beanCount = beans.length;

		beanTotal = new BeanImpl[beanCount];


		if(beans.length>0){
			//downcast
			BeanImpl bi =(BeanImpl)beans[0];
			//get the mode(luck or skill) of this game run
			isLuck = bi.getLuck();

			//initialize the inpflight beans
			beansInFlight.add(bi);
			//initialize the first bean's coordinate (0,0)
			xPosArray[0]=0;

			//initialize the remain to be the total beancount-1 ??? or not -1???
			remainCount = beanCount-1;

			//copy the array
			for(int i =0;i<beans.length;i++){
				beanTotal[i]=(BeanImpl)beans[i];
			}
		}

	}

	/**
	 * Repeats the experiment by scooping up all beans in the slots and all beans
	 * in-flight and adding them into the pool of remaining beans. As in the
	 * beginning, the machine starts with one bean at the top.
	 */
//will this use same beans?
// or do the randomization again???

	public void repeat() {
		// TODO: Implement
		beansInFlight = new LinkedList<BeanImpl>();

		beansInFlight.add(beanTotal[0]);
		slotBeanCount = new int[beanCount];
		remainCount =beanCount-1;
		xPosArray = new int[beanCount];

	}

	/**
	 * Advances the machine one step. All the in-flight beans fall down one step to
	 * the next peg. A new bean is inserted into the top of the machine if there are
	 * beans remaining.
	 * 
	 * @return whether there has been any status change. If there is no change, that
	 *         means the machine is finished.
	 */
	public boolean advanceStep() {
		// TODO: Implement
		if(remainCount==0&&beansInFlight.size()==0)//machine shall be finished
			return false;
		else{
			//fall down one step
			//beansInFlight.size()>slotCount-1){
			

			if(beansInFlight.get(0).getYpos()==slotCount-1){
				//TEST
				int dropTo;
				//if(!isLuck) {
					//dropTo = beansInFlight.get(0).getSkillLevel();
				//}else
					dropTo = xPosArray[slotCount-1];				
				//UPDATE SLOT
				slotBeanCount[dropTo]++;
				beansInFlight.removeFirst();
			}

			boolean newlyAdd = false;
			if(remainCount>0){
				beansInFlight.add(beanTotal[beanCount-remainCount]);
				//xPosArray[0]=0;
				remainCount--;
				newlyAdd=true;
			}
			//update the x and y coordinate
			for(int i=0;i< beansInFlight.size();i++){
				//if(i==slotCount-1) {// do not touch the one in (0,0) if there is one
				//	break;
				//}
				if(i==beansInFlight.size()-1){
					if(newlyAdd)
						break;
				}
				int size = beansInFlight.size();
				int yPos = beansInFlight.get(i).getYpos()+1;	//consider using another way to get yPos
				//int yPos = size-1-i;  //translate the index in the linked list to the yPos coordinate
				if(yPos<slotCount){
					xPosArray[yPos] = beansInFlight.get(i).setNewXpos();
				}
				beansInFlight.get(i).setNewYpos();

				//beansInFlight.get(i).setNewXpos();
				//beansInFlight.get(i).setNewYpos();
			}

			return true;
		}		//if there is no change in status, return false
		
	}
	
	/**
	 * Number of spaces in between numbers when printing out the state of the machine.
	 * Make sure the number is odd (even numbers don't work as well).
	 */
	private int xspacing = 3;

	/**
	 * Calculates the number of spaces to indent for the given row of pegs.
	 * 
	 * @param yPos the y-position (or row number) of the pegs
	 * @return the number of spaces to indent
	 */
	private int getIndent(int yPos) {
		int rootIndent = (getSlotCount() - 1) * (xspacing + 1) / 2 + (xspacing + 1);
		return rootIndent - (xspacing + 1) / 2 * yPos;
	}

	/**
	 * Constructs a string representation of the bean count of all the slots.
	 * 
	 * @return a string with bean counts for each slot
	 */
	public String getSlotString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		String format = "%" + (xspacing + 1) + "d";
		for (int i = 0; i < getSlotCount(); i++) {
			fmt.format(format, getSlotBeanCount(i));
		}
		fmt.close();
		return bld.toString();
	}

	/**
	 * Constructs a string representation of the entire machine. If a peg has a bean
	 * above it, it is represented as a "1", otherwise it is represented as a "0".
	 * At the very bottom is attached the slots with the bean counts.
	 * 
	 * @return the string representation of the machine
	 */
	public String toString() {
		StringBuilder bld = new StringBuilder();
		Formatter fmt = new Formatter(bld);
		for (int yPos = 0; yPos < getSlotCount(); yPos++) {
			int xBeanPos = getInFlightBeanXPos(yPos);
			for (int xPos = 0; xPos <= yPos; xPos++) {
				int spacing = (xPos == 0) ? getIndent(yPos) : (xspacing + 1);
				String format = "%" + spacing + "d";
				if (xPos == xBeanPos) {
					fmt.format(format, 1);
				} else {
					fmt.format(format, 0);
				}
			}
			fmt.format("%n");
		}
		fmt.close();
		return bld.toString() + getSlotString();
	}

	/**
	 * Prints usage information.
	 */
	public static void showUsage() {
		System.out.println("Usage: java BeanCounterLogic slot_count bean_count <luck | skill> [debug]");
		System.out.println("Example: java BeanCounterLogic 10 400 luck");
		System.out.println("Example: java BeanCounterLogic 20 1000 skill debug");
	}
	
	/**
	 * Auxiliary main method. Runs the machine in text mode with no bells and
	 * whistles. It simply shows the slot bean count at the end.
	 * 
	 * @param args commandline arguments; see showUsage() for detailed information
	 */
	public static void main(String[] args) {
		boolean debug;
		boolean luck;
		int slotCount = 0;
		int beanCount = 0;

		if (args.length != 3 && args.length != 4) {
			showUsage();
			return;
		}

		try {
			slotCount = Integer.parseInt(args[0]);
			beanCount = Integer.parseInt(args[1]);
		} catch (NumberFormatException ne) {
			showUsage();
			return;
		}
		if (beanCount < 0) {
			showUsage();
			return;
		}

		if (args[2].equals("luck")) {
			luck = true;
		} else if (args[2].equals("skill")) {
			luck = false;
		} else {
			showUsage();
			return;
		}
		
		if (args.length == 4 && args[3].equals("debug")) {
			debug = true;
		} else {
			debug = false;
		}

		// Create the internal logic
		BeanCounterLogicImpl logic = new BeanCounterLogicImpl(slotCount);
		// Create the beans (in luck mode)
		BeanImpl[] beans = new BeanImpl[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = new BeanImpl(slotCount, luck, new Random());
		}
		// Initialize the logic with the beans
		logic.reset(beans);

		if (debug) {
			System.out.println(logic.toString());
		}

		// Perform the experiment
		while (true) {
			if (!logic.advanceStep()) {
				break;
			}
			if (debug) {
				System.out.println(logic.toString());
			}
		}
		// display experimental results
		System.out.println("Slot bean counts:");
		System.out.println(logic.getSlotString());
	}
}
