/*
 * Name: Daniel He
 * Solar ID: 114457594
 * Homework #6
 * Email: daniel.he@stonybrook.edu
 * Course: CSE214
 * Recitation #: R01 TA: Ulfeen Ayevan & Wesley Mui  
 */

import java.io.Serializable;

/*
 *Auction represents an active auction currently in the database. The Auction class contain member variables for the 
 *seller's name, the current bid, the time remaining (in hours), current bidder's name, information about the item, 
 *and the unique ID for the auction. The class implements a toString() method, which should print all of the data 
 *members in a neat tabular form. The Auction class can only be altered by a single member method called newBid(), 
 *which takes in the name of a bidder and their bid value. This method checks to see if the bid value is greater 
 *than the current bid, and if it is, replaces the current bid and buyer name. There are getter methods for 
 *each member variable, however, there are no setters.
 * 
 * @author Daniel He
 * email: daniel.he@stonybrook.edu
 * 114457594
 */
public class Auction implements Serializable{
	private String auctionID;
	private String sellerName;
	private String buyerName;
	private String itemInfo;
	private double currentBid;
	private int timeRemaining;

	/*
	 * Constructs an Auction objects with null or 0 fields.
	 *
	 * <dt> Postconditions:
	 * 	<dd> Auction was constructed.
	 * 
	 */
	public Auction() {
	}

	/*
	 * Constructs an Auction objects with user inputted fields
	 * 
	 * @param auctionID
	 * 		auctionID of the Auction.
	 * @param sellerName
	 *  	sellerName of the Auction.
	 *  @param buyerName
	 *  	buyerName of the Auction.
	 *  @param itemInfo
	 *  	itemInfo of the Auction.
	 *  @param currentBid
	 *  	currentBid of the Auction.
	 *  @param timeRemaining
	 *  	timeRemaining of the Auction.
	 *  
	 * <dt> Postconditions:
	 * 	<dd> Auction was constructed.
	 * 
	 */
	public Auction(String auctionID, String sellerName, String buyerName, String itemInfo, double currentBid, int timeRemaining) {
		this.auctionID = auctionID;
		this.sellerName = sellerName.replaceAll("\\r|\\n", "").replaceAll("( )+", " ");
		this.itemInfo = itemInfo;
		this.buyerName = buyerName;
		this.currentBid = currentBid;
		this.timeRemaining = timeRemaining;
	}

	/*
	 * Constructs an Auction objects with user inputted fields
	 * 
	 * @param auctionID
	 * 		auctionID of the Auction.
	 * @param sellerName
	 *  	sellerName of the Auction.
	 *  @param itemInfo
	 *  	itemInfo of the Auction.
	 *  @param timeRemaining
	 *  	timeRemaining of the Auction.
	 *  
	 * <dt> Postconditions:
	 * 	<dd> Auction was constructed.
	 * 
	 */
	public Auction(String auctionID, String sellerName, String itemInfo, int timeRemaining) {
		this.auctionID = auctionID;
		this.sellerName = sellerName.replaceAll("\\r|\\n", "").replaceAll("( )+", " ");
		this.itemInfo = itemInfo;
		this.buyerName = "";
		this.currentBid = 0;
		this.timeRemaining = timeRemaining;
	}

	/*
	 * Decreases the time remaining for this auction by the specified amount. If time is 
	 * greater than the current remaining time for the auction, then the time remaining 
	 * is set to 0 (i.e. no negative times).
	 * 
	 * @param time
	 * 		time to decrease all auctions.
	 *  
	 * <dt> Postconditions:
	 * 	<dd> timeRemaining has been decremented by the indicated amount and is greater 
	 * 		than or equal to 0.

	 * 
	 */
	public void decrementTimeRemaining(int time) {
		if(time > timeRemaining) {
			timeRemaining = 0;
		}
		else {
			timeRemaining = timeRemaining - time;
		}
	}

	/*
	 * Makes a new bid on this auction. If bidAmt is larger than currentBid, then the 
	 * value of currentBid is replaced by bidAmt and buyerName is is replaced by bidderName.
	 * 
	 * @param bidderName
	 * 		name of the bidder trying to place a new bid.
	 * @param bidAmt
	 * 		amount of the new bid.
	 *  
	 *  <dt> Preconditions:
	 * 	<dd> The auction is not closed (i.e. timeRemaining > 0).
	 * 
	 * <dt> Postconditions:
	 * 	<dd> currentBid Reflects the largest bid placed on this object. If the auction is closed, throw a ClosedAuctionException.
	 * 
	 * @throws ClosedAuctionException
	 * 		Thrown if the auction is closed and no more bids can be placed (i.e. timeRemaining == 0).
	 * 
	 */
	public void newBid(String bidderName, double bidAmt) throws ClosedAuctionException{
		if(bidAmt <= 0) {
			throw new IllegalArgumentException("Bid amount can not be 0 or negative.");
		} else if(!(timeRemaining > 0)) {
			throw new ClosedAuctionException("Auction is closed.");
		} else if(bidAmt > currentBid) {
			currentBid = bidAmt;
			buyerName = bidderName;
		}
	}

	/*
	 * returns the String auctionID
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction auctionID
	 * 
	 */
	public String getAuctionID() {
		return auctionID;
	}

	/*
	 * returns the String sellerName
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction sellerName
	 * 
	 */
	public String getSellerName() {
		return sellerName;
	}

	/*
	 * returns the String buyerName
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction buyerName
	 * 
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/*
	 * returns the String itemInfo
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction itemInfo
	 * 
	 */
	public String getItemInfo() {
		return itemInfo;
	}

	/*
	 * returns the double currentBid
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction currentBid
	 * 
	 */
	public double getCurrentBid() {
		return currentBid;
	}

	/*
	 * returns the int timeRemaining
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The Auction timeRemaining
	 *
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}

	/*
	 * Returns string of data members in tabular form.
	 * 
	 * <dt> Preconditions:
	 * 	<dd> This Auction object has been instantiated.
	 * 
	 * @return
	 * 	The string of data members in tabular form.
	 */
	public String toString() {
		String bid = "";
		String s = "";
		String trim = itemInfo;

		if(currentBid == 0) {
			bid = "        ";
		} else {
			bid = String.format("%,.2f", currentBid);
		}

		if(trim.length() > 41) {
			trim = trim.substring(0, 42);
		}

		String temp =  " $";
		if(bid.equals("        "))
			temp = "  ";
		s = String.format("%11s |%s %8s | %-22s|  %-22s | %3s hours | %3s", 
				auctionID, temp, bid, sellerName, buyerName, timeRemaining, trim);
		return s;
	}
}
