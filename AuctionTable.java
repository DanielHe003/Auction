/*
 * Name: Daniel He
 * Solar ID: 114457594
 * Homework #6
 * Email: daniel.he@stonybrook.edu
 * Course: CSE214
 * Recitation #: R01 TA: Ulfeen Ayevan & Wesley Mui  
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import big.data.*;

/*
 * The database of open auctions will be stored in a hash table to 
 * provide constant time insertion and deletion.
 * 
 * @author Daniel He
 * email: daniel.he@stonybrook.edu
 * 114457594
 */
public class AuctionTable implements Serializable{

	Hashtable<String, Auction> auctions = new Hashtable<>();

	/*
	 * Uses the BigData library to construct an AuctionTable from a remote data source.
	 * 
	 * @param URL
	 * 		String representing the URL fo the remote data source.
	 * 
	 * <dt> Preconditions:
	 * 	<dd>URL represents a data source which can be connected to using the BigData library.
	 * 	<dd>The data source has proper syntax.
	 * 
	 * @returns 
	 * 		The AuctionTable constructed from the remote data source.
	 * 
	 * @throws IllegalArgumentException
	 * 	Thrown if the URL does not represent a valid datasource (can't connect or invalid syntax).
	 */

	public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException{
		AuctionTable table = new AuctionTable();
		try {
			DataSource ds = DataSource.connect(URL).load();
			String[] sellerName = ds.fetchStringArray("listing/seller_info/seller_name");

			String[] id = ds.fetchStringArray("listing/auction_info/id_num");
			String[] buyName = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
			String[] memory = ds.fetchStringArray("listing/item_info/memory");
			String[] hardDrive = ds.fetchStringArray("listing/item_info/hard_drive");
			String[] cpu = ds.fetchStringArray("listing/item_info/cpu");
			String[] bidHolder = ds.fetchStringArray("listing/auction_info/current_bid");
			Double[] curBid = new Double[bidHolder.length];

			for(int i = 0; i<bidHolder.length; i++) {
				bidHolder[i] = bidHolder[i].substring(1,bidHolder[i].length());
				bidHolder[i] = bidHolder[i].replaceAll(",","");
				curBid[i] = Double.parseDouble(bidHolder[i]);
			}

			String[] timeTemp = ds.fetchStringArray("listing/auction_info/time_left");
			int[] timeLeft = new int[timeTemp.length];
			int days = 0;
			int hours = 0;
			for(int i = 0; i < timeTemp.length; i++) {
				if(timeTemp[i].contains("days,") && timeTemp[i].contains("hours")) {
					String[] parts = timeTemp[i].split(",", 2);
					String a = parts[0];
					String b = parts[1];
					days = Integer.parseInt(a.replaceAll("[^0-9]", ""));
					hours = Integer.parseInt(b.replaceAll("[^0-9]", ""));
					hours += days*24;
				} else if(timeTemp[i].contains("days") && !timeTemp[i].contains("hours")) {
					hours = Integer.parseInt(timeTemp[i].replaceAll("[^0-9]", "")) * 24;
				} else if(!timeTemp[i].contains("days") && timeTemp[i].contains("hours")) {
					hours = Integer.parseInt(timeTemp[i].replaceAll("[^0-9]", ""));
				}
				timeLeft[i] = hours;
			}

			for(int i = 0; i < sellerName.length; i++) {
				String seltemp = sellerName[i];
				String buyNameTemp = buyName[i];
				String cpuTemp = cpu[i];
				String hardDriveTemp = hardDrive[i];
				String memoryTemp = memory[i];

				if(sellerName[i].equals(""))
					seltemp = "N/A";
				if(buyName[i].equals(""))
					buyNameTemp = "N/A";
				if(cpu[i].equals(""))
					cpuTemp = "N/A";
				if(hardDrive[i].equals(""))
					hardDriveTemp = "N/A";
				if(memory[i].equals(""))
					memoryTemp = "N/A";

				String trimmer = cpuTemp +" - "+ memoryTemp +" - "+ hardDriveTemp;

				Auction e = new Auction(id[i], seltemp, buyNameTemp, trimmer, curBid[i], timeLeft[i]);
				table.auctions.put(id[i], e);
			}
		} catch(Exception e) {
			System.out.println("Incorrect URL.");
		}
		return table;
	}

	/*
	 * Manually posts an auction, and add it into the table.
	 * 
	 * @param auctionID
	 * 		the unique key for this object
	 * @param auction
	 * 		The auction to insert into the table with the corresponding auctionID
	 * 
	 * <dt> Postconditions:
	 * 	<dd>The item will be added to the table if all given parameters are correct.
	 * 
	 * @throws IllegalArgumentException
	 * 		If the given auctionID is already stored in the table.
	 */
	public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException{
		if(auctions.containsKey(auctionID))
			throw new IllegalArgumentException("AuctionID is already stored in the table.");
		auctions.put(auctionID, auction);
	}

	/*
	 * Get the information of an Auction that contains the given ID as key
	 * 
	 * @param auctionID
	 * 		the unique key for this object.
	 * 
	 * @returns 
	 * 		An Auction object with the given key, null otherwise.
	 */
	public Auction getAuction(String auctionID) {
		if(auctions.containsKey(auctionID))
			return auctions.get(auctionID);
		else return null;
	}


	/*
	 * Simulates the passing of time. Decrease the timeRemaining of all 
	 * Auction objects by the amount specified. The value cannot go below 0.
	 * 
	 * @param numHours
	 * 		the number of hours to decrease the timeRemaining value by.
	 * 
	 * <dt> Postconditions:
	 * 	<dd>All Auctions in the table have their timeRemaining timer decreased. 
	 * 		If the original value is less than the decreased value, set the value to 0.
	 * 
	 * @throws IllegalArgumentException
	 * 		If the given numHours is non positive
	 */
	public void letTimePass(int numHours) throws IllegalArgumentException{
		if(numHours <0)
			throw new IllegalArgumentException("Hours passed can not be negative.");
		Enumeration<String> e = auctions.keys();
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			auctions.get(key).decrementTimeRemaining(numHours);
		}
	}

	/*
	 * Iterates over all Auction objects in the table and removes them if they are expired (timeRemaining == 0).
	 *
	 * <dt> Postconditions:
	 * 	<dd> Only open Auction remain in the table.
	 */
	public void removeExpiredAuctions() {
		Enumeration<String> e = auctions.keys();
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			if(auctions.get(key).getTimeRemaining() == 0)
				auctions.remove(key);
		}
	}

	/*
	 * Prints the AuctionTable in tabular form.
	 */
	public void printTable() {
		System.out.printf(" Auction ID |      Bid   |        Seller         |          Buyer          |    Time   |  Item Info \n"
				+ "===================================================================================================================================");
		Enumeration<String> e = auctions.keys();
		System.out.println();
		ArrayList<Auction> temp = new ArrayList<Auction>();
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			temp.add(auctions.get(key));
		}
		
		
		  Collections.sort(temp, new Comparator<Auction>(){ public int compare(Auction a, Auction b){ 
			  int x = Integer.parseInt(a.getAuctionID());
			  int y = Integer.parseInt(b.getAuctionID());
			  return y-x; 
		  }});
		 
		for(int i = 0; i < temp.size(); i++) {
			System.out.println(temp.get(i).toString());
		}  
	}
}

