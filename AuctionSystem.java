/*
 * Name: Daniel He
 * Solar ID: 114457594
 * Homework #6
 * Email: daniel.he@stonybrook.edu
 * Course: CSE214
 * Recitation #: R01 TA: Ulfeen Ayevan & Wesley Mui  
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

/*
 *This class will allow the user to interact with the database by listing open auctions, make bids on 
 *open auctions, and create new auctions for different items. In addition, the class should provide the 
 *functionality to load a saved (serialized) AuctionTable or create a new one if a saved table does not exist.
 *On startup, the AuctionSystem should check to see if the file auctions.obj exists in the current directory. 
 *If it does, then the file should be loaded and deserialized into an AuctionTable for new auctions/bids 
 *If the file does not exist, an empty AuctionTable object should be created and used instead. Next, the user 
 *should be prompted to enter a username to access the system. This is the name that will be used to create new 
 *auctions and bid on the open auctions available in the table.
 *When the user enters 'Q' to quit the program, the auction table should be serialized to the file auctions.obj. 
 *That way, the next time the program is run, the auctions will remain in the database and allow different users 
 *to make bids on items.
 * 
 * @author Daniel He
 * email: daniel.he@stonybrook.edu
 * 114457594
 */

public class AuctionSystem implements Serializable{

	/*
	 * The method should first prompt the user for a username. This should be stored in username 
	 * The rest of the program will be executed on behalf of this user. Options are printed after
	 * each input.
	 */
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String scan = "";
		AuctionTable list = new AuctionTable();
		System.out.println("Starting...");
		try {
			try {
				list = readFile();
				System.out.printf("Loading previous Auction Table...  \n\n");
			} catch (Exception e1) {
				System.out.println("No previous auction table detected.");
				System.out.println("Creating new table...");
				System.out.println();
			}
			System.out.print("Please select a username: ");
			String name = scanner.nextLine();
			System.out.println();
			while(scan != "Q") {
				int count = 0;
				printMenu();
				System.out.print("Please select an option: ");
				scan = scanner.nextLine();

				if(scan.toUpperCase().equals("D")) {
					System.out.print("Please enter a URL: ");
					scan = scanner.nextLine();
					try {
						list = AuctionTable.buildFromURL(scan);
					} catch (Exception e) {
						System.out.println("URL is wrong");
					}
					System.out.println();
					count++;
				}
				else if(scan.toUpperCase().equals("A")) {
					System.out.println();
					System.out.println("Creating new Auction as " + name);

					System.out.print("Please enter an Auction ID: ");
					String tempID = scanner.nextLine();

					System.out.print("Please enter an Auction time (hours): ");
					scan = scanner.nextLine();

					int tempTime = 0;
					try {
						tempTime = Integer.parseInt(scan);
						if(tempTime<=0)
							throw new IllegalArgumentException();
						System.out.print("Please enter some Item Info: ");
						String tempInfo = scanner.nextLine();

						Auction e = new Auction(tempID, name, tempInfo, tempTime);
						list.putAuction(tempID, e);

						System.out.println();
						System.out.println("Auction " + tempID + " inserted into the table.");
						System.out.println();
						count++;
					} catch(Exception e) {
						System.out.println("Time can not be 0 or negative and has to be a number.");
						System.out.println();
					}
				}
				else if(scan.toUpperCase().equals("B")) {
					System.out.println();
					System.out.print("Please enter an Auction ID: ");
					scan = scanner.nextLine();
					Auction temp = list.getAuction(scan);
					System.out.println();
					if(temp != null) {
						String s = temp.getTimeRemaining() != 0 ? "OPEN" : "CLOSED";
						System.out.println("Auction "+temp.getAuctionID()+" is " +s);
						float holder = (float) temp.getCurrentBid();
						String p = temp.getCurrentBid() != 0 ? "$ " + String.format("%.2f", holder) : "None";
						System.out.println("    Current Bid: "+p);
						System.out.println();
						if(s.equals("OPEN")) {
							System.out.print("What would you like to bid?: ");
							scan = scanner.nextLine();
							double number = Double.parseDouble(scan);
							if(number <= temp.getCurrentBid()) {
								System.out.println("Bid not accepted.");
							}
							else {
								System.out.println("Bid accepted.");
								try {
									temp.newBid(name, number);
								} catch (ClosedAuctionException e) {
									System.out.println("Bid is closed.");
								}
							}
						} else if(s.equals("CLOSED")) {
							System.out.println("You can no longer bid on this item.");
						}
					} else if(temp == null) {
						System.out.println("Wrong ID!");
					}
					System.out.println();
					count++;
				}
				else if(scan.toUpperCase().equals("I")) {
					try {
						System.out.println();
						System.out.println();
						System.out.print("Please enter an Auction ID: ");
						scan = scanner.nextLine();
						System.out.println();
						System.out.println("Auction "+scan+":");

						Auction temp = list.getAuction(scan);
						System.out.println("    Seller: "+temp.getSellerName());
						System.out.println("    Buyer: "+temp.getBuyerName());
						System.out.println("    Time: "+temp.getTimeRemaining());
						System.out.println("    Info: "+temp.getItemInfo());

						System.out.println();
						count++;
					} catch(Exception e) {
						System.out.println("ID was wrong");
						System.out.println();
					}
				}
				else if(scan.toUpperCase().equals("P")) {
					System.out.println();
					list.printTable();
					System.out.println();
					System.out.println();
					count++;
				}
				else if(scan.toUpperCase().equals("R")) {
					System.out.printf("\n"
							+ "Removing expired auctions...\n");
					list.removeExpiredAuctions();
					System.out.printf("All expired auctions removed.\n\n");
					count++;
				}
				else if(scan.toUpperCase().equals("T")) {
					System.out.println();
					System.out.print("How many hours should pass: ");
					scan = scanner.nextLine();
					int time = Integer.parseInt(scan);
					list.letTimePass(time);
					System.out.printf("\r\nTime passing...\nAuction times updated.\n\n");
					count++;
				}
				else if(scan.toUpperCase().equals("Q")) {
					System.out.println();
					try {
						writeToFile(list);
						System.out.println("Writing Auction Table to file...");
						System.out.println("Done!");
						System.out.println();
						System.out.println("Goodbye.");
						System.out.println();
					} catch (IOException e) {
						System.out.println("Input/Output exception when saving data.");
					}
					return;
				}
				else if(!scan.toUpperCase().equals("D") && !scan.toUpperCase().equals("A") && !scan.toUpperCase().equals("B") &&
						!scan.toUpperCase().equals("I") && !scan.toUpperCase().equals("P") && !scan.toUpperCase().equals("R") &&
						!scan.toUpperCase().equals("T") &&!scan.toUpperCase().equals("Q") && count == 0) {
					System.out.printf("Input is invalid please try again!\n\n");
				}
			}
		} catch(Exception e) {
			System.out.println("Input was wrong");
		}
	}

	public static void printMenu() {
		System.out.println("Menu:\r\n"
				+ "    (D) - Import Data from URL\r\n"
				+ "    (A) - Create a New Auction\r\n"
				+ "    (B) - Bid on an Item\r\n"
				+ "    (I) - Get Info on Auction\r\n"
				+ "    (P) - Print All Auctions\r\n"
				+ "    (R) - Remove Expired Auctions\r\n"
				+ "    (T) - Let Time Pass\r\n"
				+ "    (Q) - Quit\r\n"
				+ "\n"
				+ "");
	}

	/*
	 * Creates a object file and saves all the data on it.
	 * 
	 * @param AuctionTable
	 * 	The AuctionTable it is trying to save
	 * 
	 * <dt> Postconditions:
	 * 	<dd> AuctionTable is now saved as a file.
	 * 
	 * @throws IOException
	 * 		Throws the error when the input is incorrect.
	 * 
	 */
	public static void writeToFile(AuctionTable e) throws IOException{
		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream("auction.obj"));
		outStream.writeObject(e);
	}

	/*
	 * If there is a file it reads from it.
	 * 
	 * <dt> Postconditions:
	 * 	<dd> AuctionTable is now stored in the complier console.
	 * 
	 * @throws ClassNotFoundException
	 * 		The file can not be found.
	 * @throws IOException
	 * 		There was a issue with either the input or output of the file.
	 */
	public static AuctionTable readFile() throws ClassNotFoundException, IOException{
		ObjectInputStream inStream = new ObjectInputStream(new FileInputStream("auction.obj"));
		AuctionTable temp = (AuctionTable) inStream.readObject();
		return temp;
	}
}
