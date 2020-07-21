import java.io.*;
import java.net.*;
import java.util.*;

////////TO DO/////////
//  Send distributed cards per player
//  Update GUI after distribution
//  Run Game per queue in Vector
//  Implement Action Cards

///////////////////KNOWN ERROR
/*
 * 1. somewhere in drawing an exploding kitten and dying after: next player becomes iffy
 */

public class server {
	static ServerSocket ss;
	static Socket s;
	static DataInputStream dis;
	static DataOutputStream dos;
	static Integer maxNumofUsers=3;
	static boolean[] alive=new boolean[maxNumofUsers];
	static Vector<UserHandler> users;
	static Integer currentPlayer=0;
	static boolean ended=false;
	static volatile boolean movenext=true;
	public static int currNumofUsers;
	static String dropped="";
	
	static ArrayList<Card> tmpcards=new ArrayList<Card>();
	static ArrayList<Card> diffuse=new ArrayList<Card>();
	static ArrayList<Card> exploding=new ArrayList<Card>();
	static ArrayList<Card> deck=new ArrayList<Card>();
	static ArrayList<Card> discard=new ArrayList<Card>();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		currNumofUsers=0;
		ss=new ServerSocket(7777);
		users=new Vector<UserHandler>();
		
		while(currNumofUsers<maxNumofUsers) {
			s=ss.accept();
			UserHandler uh= new UserHandler(s, Integer.toString(currNumofUsers+1));
			users.add(uh);
			Thread thread= new Thread(users.get(users.size()-1));//start a thread
			thread.start();
			currNumofUsers++;
			for(int i=0; i<users.size(); i++) { //ito hindi ko sure if tama sya in networking //updates number of users present pero userhandler
				users.get(i).updateUserSet(users);
			}
		}
		
		System.out.println("Maximum number of players reached.");
		for(int i=0; i<alive.length; i++) {
			alive[i]=true;
		}
		while(true) { //wait till last user to enter has a name already then distribute the cards
			if(!users.get(users.size()-1).name.isEmpty()) {
				prepCards(users);
				break;
			}
		}
		startgame(); //to start the game ano pa nga ba
		
		//check winner
		int winner=0;
		for(int i=0; i<users.size(); i++) {
			if(users.get(i).isAlive==true) {
				winner=i+1;
				break;
			}
		}
		try {
			for(int i=0; i<users.size(); i++) {
				users.get(i).dos.writeUTF("winner#Player "+winner+" has won the game!");
				users.get(i).dos.flush();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void startgame() {
		// TODO Auto-generated method stub
		while(ended!=true) {
				if(currentPlayer==0){
					for(int j=0; j<users.size(); j++) {
						users.get(j).myTurn=false;
					}
				}
				
				if(currentPlayer<users.size() && movenext==true && users.get(currentPlayer).myTurn==false && users.get(currentPlayer).isAlive==true) {
					System.out.print(currentPlayer);
					try {
						users.get(currentPlayer).dos.writeUTF("yourTurn#"+dropped);
						dropped="";
						users.get(currentPlayer).dos.flush();
						for(int k=0; k<users.size(); k++) {
							users.get(k).dos.writeUTF("highlight#"+currentPlayer);
							users.get(k).dos.flush();
						}
						users.get(currentPlayer).myTurn=true;
						movenext=false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		
	}

	private static void prepCards(Vector<UserHandler> users) {
		// TODO Auto-generated method stub
		/*for(int i=0; i<5; i++) {
			tmpcards.add(new Card("Alter the Future","","img/alter-min.png")); //5 Alter the Future
		}*/
		
		for(int i=0; i<8; i++) {
			tmpcards.add(new Card("Double Slap","endturn","img/attack-min.png")); //8 Double Slaps
		}
		
		for(int i=0; i<2; i++) {
			tmpcards.add(new Card("Triple Slap","endturn","img/attack3-min.png")); //2 Triple Slaps
		}
		
		for(int i=0; i<7; i++) {
			tmpcards.add(new Card("Cat","Toilet Paper Cat","img/cat1-min.png")); //7 Toilet Paper Cat
		}
		
		for(int i=0; i<7; i++) {
			tmpcards.add(new Card("Cat","Singing Cat","img/cat2-min.png")); //7 Singing Cat
		}
		
		for(int i=0; i<7; i++) {
			tmpcards.add(new Card("Cat","Peek-a-boo Cat","img/cat3-min.png")); //7 Singing Cat
		}
		
		for(int i=0; i<7; i++) {
			tmpcards.add(new Card("Cat","Tangled Cat","img/cat5-min.png")); //7 Cat in a Basket
		}
		
		for(int i=0; i<5; i++) {
			tmpcards.add(new Card("Cat","Feral Cat","img/feral-min.png")); //5 Feral Cat
		}
		
		for(int i=0; i<6; i++) {
			tmpcards.add(new Card("Draw From The Bottom","endturn","img/draw-min.png")); //6 Draw From The Bottom
		}
		
		/*for(int i=0; i<10; i++) {
			tmpcards.add(new Card("Nope","","img/nope-min.png")); //10 Nope
		}*/
		
		for(int i=0; i<6; i++) {
			tmpcards.add(new Card("Favor","","img/favor-min.png")); //6 Favor
		}
		
		for(int i=0; i<6; i++) {
			tmpcards.add(new Card("See The Future","","img/see-min.png")); //6 See the Future
		}
		
		/*for(int i=0; i<4; i++) {
			tmpcards.add(new Card("Reverse","endturn","img/reverse-min.png")); //4 Reverse
		}*/
		
		for(int i=0; i<6; i++) {
			tmpcards.add(new Card("Shuffle","","img/shuffle-min.png")); //6 Shuffle
		}
		
		for(int i=0; i<10; i++) {
			tmpcards.add(new Card("Skip","endturn","img/skip-min.png")); //10 Skips
		}
		
		//end tmp pile
		
		//start diffuse pile
		for(int i=0; i<5; i++) {
			diffuse.add(new Card("Diffuse","","img/diffuse-min.png")); //10 Diffuse
			diffuse.add(new Card("Diffuse","","img/diffuse1-min.png"));
		}
		//end diffuse pile
		
		//start exploding pile
		for(int i=0; i<9; i++) {
			exploding.add(new Card("Exploding","","img/explode-min.png"));//9 exploding
		}
		//end exploding pile
		
		//shuffle tmp
		Collections.shuffle(tmpcards);
		Collections.shuffle(tmpcards);
		//initialize to distribute pile
		ArrayList<Card> todistrib=new ArrayList<Card>();
		for(int i=0; i<users.size(); i++) {
			todistrib.add(diffuse.get(0));
			diffuse.remove(0);
			for(int j=0; j<7; j++) {
				todistrib.add(tmpcards.get(0));
				tmpcards.remove(0);
			}
		}
		
		System.out.println("Distribute pile ready.");
		System.out.println("\nPreparing draw pile...");
		System.out.println("Adding remaining diffuse and playing cards...");
		
		deck.addAll(tmpcards);
		deck.addAll(diffuse);
		/*for(int i=0; i<maxNumofUsers-1; i++) {
			deck.add(exploding.get(i));
		}*/
		
		System.out.println("Shuffling cards...");
		//Shuffling cards
		Collections.shuffle(deck);
		Collections.shuffle(deck);
		
		for(int i=0; i<maxNumofUsers-1; i++) {
			deck.add(0,exploding.get(i));
		}
		
		for(int i=0; i<users.size(); i++) {
			try {
				users.get(i).dos.writeUTF("Draw pile ready. Let's Start.");
				users.get(i).dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println();
		
		for(int i=0; i<users.size(); i++) {
			try {
				users.get(i).dos.writeUTF("maxPlayers#"+maxNumofUsers);
				users.get(i).dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i=0; i<users.size(); i++) { //to send distributed cards to each player
			String tmp="hand#";
			for (int j=0; j<8;j++) {
				tmp=tmp+todistrib.get(0).getFunc()+":"+todistrib.get(0).getOther()+":"+todistrib.get(0).getDir()+",";
				todistrib.remove(0);
			}
			try {
				users.get(i).dos.writeUTF(tmp);
				users.get(i).dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

class UserHandler implements Runnable{
	Socket s;
	DataInputStream dis;
	DataOutputStream dos;
	Vector<UserHandler> users=new Vector<UserHandler>();
	String username;
	String name="";
	boolean isAlive=true;
	volatile boolean myTurn=false;
	boolean dead=false;
	public UserHandler(Socket s, String username) {
		// TODO Auto-generated constructor stub
		this.s=s;
		this.username=username;
	}

	public void run(){
		try {
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			dos.writeUTF("playerNum#"+username);
			dos.flush();
			name=dis.readUTF();
			while(true) {
				String message=dis.readUTF();
				if(message.contentEquals("exit#")) {
					for(int i=0; i<users.size(); i++) {
						if (server.users.get(i).name.equals(this.name)) {
							users.remove(i);
							server.users=users;
							updateUserSet(users);
						}
					}
					dos.writeUTF("exit granted");
					dos.flush();
					close();
					break;
				}else if(message.contentEquals("getUsers")) {
					String tmp="userList#";
					for(int i=0; i<users.size(); i++) {
						tmp=tmp+users.get(i).name+",";
					}
					dos.writeUTF(tmp); //fake array pa lang hehez
					dos.flush();
				}else if(message.contains("message#")){//group sms
					String[] tmp=message.split("#");
            		message=tmp[tmp.length-1];
            		
					for(int i=0; i<users.size(); i++) {
						users.get(i).dos.writeUTF(name + " : "+message);
						users.get(i).dos.flush();
					}
				}else if(message.contains("endTurn#")){
					String[] tmp=message.split("#");
					
					server.currentPlayer=Integer.parseInt(tmp[tmp.length-1]);
					while(users.get(server.currentPlayer).isAlive==false) {
						server.currentPlayer++;
						if(server.currentPlayer>=server.maxNumofUsers) {
							server.currentPlayer=0;
						}
					}
					server.dropped=tmp[tmp.length-2];
					myTurn=true;
					server.movenext=true;
				}else if(message.contains("dead#")){
					isAlive=false;
					server.alive[Integer.parseInt(username)-1]=false;
					int alive=0;
					for(int i=0; i<users.size(); i++) {
						if(users.get(i).isAlive==true) {
							alive++;
						}
					}
					if(alive==1) {
						server.ended=true;
					}
				}else if(message.contains("checkAlivePeople#")){
					int ctr=0;
					for(int i=0; i<server.alive.length; i++) {
						if(server.alive[i]==true) {
							ctr++;
						}
					}
					System.out.println("Still alive: "+ctr);
					dos.writeUTF("sendAliveList#"+java.util.Arrays.toString(server.alive));
					dos.flush();
					
				}else if(message.contains("getCard#")){
					Card card=server.deck.get(0);
					dos.writeUTF("receiveCard#"+card.getFunc()+","+card.getOther()+","+card.getDir());
					dos.flush();
					server.deck.remove(0);
				}else if(message.contains("getCard#")){
					Card card=server.deck.get(0);
					dos.writeUTF("receiveCard#"+card.getFunc()+","+card.getOther()+","+card.getDir());
					dos.flush();
					server.deck.remove(0);
				}else if(message.contains("reshuffleCards#")){
					Collections.shuffle(server.deck);
					for(int i=0; i<users.size(); i++) {
						users.get(i).dos.writeUTF("*The deck has been re-shuffled*");
						users.get(i).dos.flush();
					}
				}else if(message.contains("seeTheFuture#")){
					String theFuture="theFuture#";
					for(int i=0; i<3; i++) {
						if(i==server.deck.size()) {
							break; 
						}
						if(server.deck.get(i).getFunc().equalsIgnoreCase("cat")) {
							theFuture=theFuture+server.deck.get(i).getOther()+",";
						}else {
							theFuture=theFuture+server.deck.get(i).getFunc()+",";
						}
					}
					dos.writeUTF(theFuture);
					dos.flush();
				}else if(message.contains("getDouble#")){
					Card[] cards=new Card[2];
					cards[0]=server.deck.get(0);
					cards[1]=server.deck.get(1);
					
					if(cards[0].getFunc().equalsIgnoreCase("exploding")&&cards[0].getFunc().equalsIgnoreCase("exploding")) {
						for(int i=1; i<server.deck.size(); i++) {
							Collections.swap(server.deck, i, i+1);
							if(!server.deck.get(i+1).getFunc().equalsIgnoreCase("exploding")) {
								cards[1]=server.deck.get(1);
								break;
							}
						}
						
					}
					
					dos.writeUTF("receiveDouble#"+cards[0].getFunc()+","+cards[0].getOther()+","+cards[0].getDir()+"#"+cards[1].getFunc()+","+cards[1].getOther()+","+cards[1].getDir());
					dos.flush();
					
					server.deck.remove(0);
					server.deck.remove(0);
				}else if(message.contains("getTriple#")){
					Card[] cards=new Card[3];
					cards[0]=server.deck.get(0);
					cards[1]=server.deck.get(1);
					cards[2]=server.deck.get(2);
					
					dos.writeUTF("receiveTriple#"+cards[0].getFunc()+","+cards[0].getOther()+","+cards[0].getDir()+"#"+cards[1].getFunc()+","+cards[1].getOther()+","+cards[1].getDir()+"#"+cards[2].getFunc()+","+cards[2].getOther()+","+cards[2].getDir());
					dos.flush();
					
					server.deck.remove(0);
					server.deck.remove(0);
					server.deck.remove(0);
				}else if(message.contains("favor#")){
					String[] tmp=message.split("#");
					String toborrow=tmp[1];
					String tobeborrowed=tmp[2];
					
					users.get(Integer.parseInt(tobeborrowed)).dos.writeUTF("stealCard#"+toborrow);
					users.get(Integer.parseInt(tobeborrowed)).dos.flush();
				}else if(message.contains("bottomDraw#")){ 
					dos.writeUTF("receiveCard#"+server.deck.get(server.deck.size()-1).getFunc()+","+server.deck.get(server.deck.size()-1).getOther()+","+server.deck.get(server.deck.size()-1).getDir()+",");
					dos.flush();
					server.deck.remove(server.deck.get(server.deck.size()-1));
				}else if(message.contains("checkBuhay#")){ 
					String alivepeople="buhayList#";
					for(int i=0; i<users.size(); i++) {
						if(users.get(i).isAlive==true) {
							alivepeople=alivepeople+i+",";
						}
					}
					dos.writeUTF(alivepeople);
					dos.flush();
				}else if(message.contains("warnOthers#")){
					String[] tmp=message.split("#");
					
					if(tmp[1].equals("0")) {
						int playerwiththeexplodingcard=Integer.parseInt(tmp[2])-1;
						for(int i=0; i<users.size(); i++) {
							if(i!=playerwiththeexplodingcard) {
								users.get(i).dos.writeUTF("**Player "+tmp[2]+" just drew an EXPLODING CARD!");
								users.get(i).dos.flush();
							}
						}
					}else if(tmp[1].equals("1")){
						int playerwiththeexplodingcard=Integer.parseInt(tmp[2])-1;
						for(int i=0; i<users.size(); i++) {
							if(i!=playerwiththeexplodingcard) {
								users.get(i).dos.writeUTF("**Player "+tmp[2]+" has diffused the exploding card");
								users.get(i).dos.flush();
							}
						}
					}else {
						int playerwiththeexplodingcard=Integer.parseInt(tmp[2])-1;
						for(int i=0; i<users.size(); i++) {
							if(i!=playerwiththeexplodingcard) {
								users.get(i).dos.writeUTF("**Player "+tmp[2]+" has died.");
								users.get(i).dos.flush();
							}
						}
					}
					
				}else if(message.contains("discardPile#")){
					String[] tmp=message.split("#");
					System.out.println(java.util.Arrays.toString(tmp));
					String msg=tmp[tmp.length-1];
            		tmp=msg.split(",");
            		
            		for(int i=0; i<tmp.length; i++) {
            			String[] tmp2=tmp[i].split(":");
            			server.discard.add(new Card(tmp2[0],tmp2[1],tmp2[2]));
            			System.out.println(java.util.Arrays.toString(tmp2));
            			
            		}
            		for(int i=0; i<server.discard.size(); i++) {
            			System.out.print("received "+server.discard.get(i).getFunc()+" ,");
            		}
            		
            		
            		System.out.println();
            		for(int i=0; i<users.size(); i++) { //to change the discard top card
            			users.get(i).dos.writeUTF("discarded#"+server.discard.get(server.discard.size()-1).getFunc()+":"+server.discard.get(server.discard.size()-1).getOther()+":"+server.discard.get(server.discard.size()-1).getDir());
            			users.get(i).dos.flush();
            		}
				}else {
					//for(UserHandler u: users) {
						dos.writeUTF("Some String");
						dos.flush();
					//}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		try {
			for(int i=0; i<users.size(); i++) {
				if(users.get(i).username==this.username) {
					users.remove(i);
					System.out.println("out");
					for(int j=0; j<users.size(); j++) {
						users.get(j).updateUserSet(users);
					}
					server.currNumofUsers=users.size();
					server.users=users;
					break;
				}
			}
			dis.close();
			dos.close();
			s.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateUserSet(Vector<UserHandler> users) {
		// TODO Auto-generated method stub
		this.users=users;
	}

}