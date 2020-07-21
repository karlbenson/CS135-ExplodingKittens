import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class client {
	static Socket s;
	static DataInputStream dis;
	static DataOutputStream dos;
	static String username;
	static gameWindow GUI;
	static ArrayList<Card> hand=new ArrayList<Card>();
	static ArrayList<JLabel> card=new ArrayList<JLabel>();
	static Integer currPlayer=0;
	static Integer playnum=null;
	static String[] droppedEarlier;
	static Integer maxPlayers=0;
	static ArrayList<String> droppedActions=new ArrayList<String>();
	
	static int counter = 7; //setting the counter to 10 sec
	static boolean diffused=false;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*String ip= JOptionPane.showInputDialog("Please input server IP: ");
		String port= JOptikonPane.showInputDialog("Please input port: ");
		*/
		try {
			s=new Socket();
			//s.connect(new InetSocketAddress(ip,Integer.parseInt(port)), 5000);
			s.connect(new InetSocketAddress("localhost",7777), 5000);
			System.out.println("Socket "+s.getInetAddress()+" has connected.");
			username= JOptionPane.showInputDialog("Please input username: ");
			while(username.isEmpty()) {
				username= JOptionPane.showInputDialog("Please input username: ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot connect to server. Either the Server was not established or maximum number of players was reached.");
		}
		
		if(s.isConnected()){
			try {
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				dos.writeUTF(username);
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Scanner sc=new Scanner(System.in);
			
			////GUI PART////
			GUI=new gameWindow();
			GUI.frame.setVisible(true);
			GUI.status.setText("status: waiting for other players...");
			GUI.console.setText(GUI.console.getText()+"\nwaiting for other players...\n----\n");
			GUI.diffuseBtn.setEnabled(false);
			GUI.dropBtn.setEnabled(false);
			
			/*GUI.frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent arg0) {
					try {
						dos.writeUTF("exit");
						dos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});*/
			GUI.drawPile.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(GUI.dropBtn.isEnabled()) {
						GUI.drawPile.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
					}
				}
				public void mouseReleased(MouseEvent arg0) {
					if(GUI.dropBtn.isEnabled()) {
						GUI.drawPile.setBorder(null);
						GUI.dropBtn.setEnabled(false);
						
						try {
							dos.writeUTF("getCard#");
							dos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			
			GUI.diffuseBtn.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if(GUI.diffuseBtn.isEnabled()) {
						int ctr=0;
						int index=0;
						for(int i=0; i<card.size(); i++) {
							LineBorder border=(LineBorder) card.get(i).getBorder();
							if(border !=null && border.getLineColor().equals(Color.GREEN)) {
								ctr++; //gets index number of green cards
								index=i;
							}
						}
						if(ctr!=1 && !hand.get(index).getFunc().equalsIgnoreCase("diffuse")) {
							GUI.console.setText(GUI.console.getText()+"\n--\nINVALID MOVE! Try Again.\n--\n");
						}else if(ctr==1 && hand.get(index).getFunc().equalsIgnoreCase("diffuse")) {
							diffused=true;
							discard();
						}
					}
				}
			});
			
			GUI.chatsms.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e2) {
					try {
						if(e2.getKeyCode()==KeyEvent.VK_ENTER) {
							if(GUI.chatsms.getText().equalsIgnoreCase("clear#")) {
								GUI.console.setText("");
								GUI.status.setText("status: Console cleared.");
							}else {
								dos.writeUTF("message#"+GUI.chatsms.getText());
								GUI.status.setText("status: Message sent.");
								dos.flush();
							}
							GUI.chatsms.setText("");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				public void keyReleased(KeyEvent e2) {
					if(e2.getKeyCode()==KeyEvent.VK_ENTER) {
						GUI.chatsms.setText("");
					}
				}
			});
			
			GUI.dropBtn.addMouseListener(new MouseAdapter() {
				@Override
				
				public void mousePressed(MouseEvent arg0) {
					int ctr=0;
					for(int i=0; i<card.size(); i++) {
						LineBorder border=(LineBorder) card.get(i).getBorder();
						if(border !=null && border.getLineColor().equals(Color.GREEN)) {
							ctr++; //gets index number of green cards
						}
					}
					if(GUI.dropBtn.isEnabled() && ctr!=0) {
						discard();
					}
				}
				
			});
			
			Thread sendMessage = new Thread(new Runnable()  
	        { 
	            @Override
	            public void run() { 
	                while (true) { 
	  
	                    // read the message to deliver. 
	                    String msg = sc.nextLine(); 
	                      
	                    try { 
	                        // write on the output stream 
	                        dos.writeUTF(msg); 
	                        dos.flush();
	                    } catch (IOException e) { 
	                        e.printStackTrace(); 
	                    } 
	                } 
	            } 
	        }); 
	          
	        // readMessage thread 
	        Thread readMessage = new Thread(new Runnable()  
	        { 
	        	String msg;
	            @Override
	            public void run() { 
	  
	                while (true) { 
	                    try { 
	                        // read the message sent to this client 
	                        msg = dis.readUTF();
	                        System.out.println("--------");
	                        System.out.println(msg);
	                        System.out.println("--------");
	                        if(msg.equalsIgnoreCase("exit granted")) {
	                        	System.out.println("Socket "+s.getInetAddress()+" is closed.");
	        					close();
	        					break;
	                        }else if(!msg.contentEquals("")) {
	                        	if(msg.contains("hand#")) {
	                        		String[] tmp=msg.split("#");
	                        		
	                        		msg=tmp[tmp.length-1];
	                        		tmp=msg.split(",");
	                        		
	                        		for(int i=0; i<tmp.length; i++) {
	                        			String[] tmp2=tmp[i].split(":");
	                        			hand.add(new Card(tmp2[0],tmp2[1],tmp2[2]));
	                        		}
	                        		for(int i=0; i<hand.size(); i++) {
	                        			System.out.println("["+hand.get(i).getFunc()+","+hand.get(i).getOther()+","+hand.get(i).getDir()+"]");
	                        		}
	                        		msg="";
	                        		
	                        		//JLabel[] card = new JLabel[8];
	                        		for(int i=0; i<8; i++) {
	                        			if(hand.get(i).getFunc().equalsIgnoreCase("cat")) {
	                        				card.add(new JLabel(hand.get(i).getOther()));
	                        			}else {
	                        				card.add(new JLabel(hand.get(i).getFunc()));
	                        			}
	                        			
	                        			card.get(i).setPreferredSize(new Dimension(130,172));
	                        			card.get(i).setHorizontalAlignment(SwingConstants.CENTER);
	                        			//card[i]=new JLabel(hand.get(i).getFunc());
	                        			//card[i].setPreferredSize(new Dimension(130,172));
	                        			//card[i].setHorizontalAlignment(SwingConstants.CENTER);
	                        			final int a=i;
	                        			card.get(a).addMouseListener(new MouseAdapter() {
	                        				@Override
	                        				public void mouseEntered(MouseEvent arg0) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border==null) {
	                        						if (border==null || !border.getLineColor().equals(Color.GREEN)) {
	                        							card.get(a).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
	                        						}
	                        					}
	                        				}
	                        				
	                        				public void mouseExited(MouseEvent e) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border!=null) {
	                        						if(!border.getLineColor().equals(Color.GREEN)) {
		                        						card.get(a).setBorder(null);
		                        					}
	                        					}
	                        					
	                        				}
	                        				
	                        				public void mousePressed(MouseEvent e) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border==null || border.getLineColor().equals(Color.RED)) {
	                        						card.get(a).setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
	                        					}else {
	                        						card.get(a).setBorder(null);
	                        					}
	                        					
	                        				}
	                        			});
	                        			GUI.cardPane.add(card.get(a));
	                        		}
	                        		GUI.cardPane.revalidate();
                        			GUI.cardPane.repaint();
                        			checkDiffuse();
	                        		//get Users into the JList
                        			dos.writeUTF("getUsers");
                        			dos.flush();
                        			
	                        	}else if (msg.contains("maxPlayers#")) {
	                        		String[] tmp=msg.split("#");
	                        		maxPlayers=Integer.parseInt(tmp[tmp.length-1]);
	                        		msg="";
	                        	}else if (msg.contains("winner#")) {
	                        		String[] tmp=msg.split("#");
	                        		GUI.frame.setVisible(false);
	                        		JOptionPane.showMessageDialog(GUI.frame, tmp[1]);
	                        		close();
	                        		System.exit(0);
	                        		msg="";
	                        	}else if (msg.contains("receiveCard#")) {
	                        		String[] tmp=msg.split("#");
	                        		String[] tmp2=tmp[tmp.length-1].split(",");
	                        		
	                        		hand.add(0, new Card(tmp2[0],tmp2[1],tmp2[2]));
	                        		if(tmp2[0].equalsIgnoreCase("cat")) {
	                        			card.add(0, new JLabel(tmp2[1]));
	                        		}else {
	                        			card.add(0, new JLabel(tmp2[0]));
	                        		}
	                        		
	                        		/*if (tmp2[0].equalsIgnoreCase("exploding")) {
	                        			GUI.explodesign.setBackground(Color.RED);
	                        			GUI.timer.setForeground(Color.WHITE);
	                        			GUI.dropBtn.setEnabled(false);
	                        			GUI.diffuseBtn.setEnabled(true);
	                        			GUI.console.setText("");
	                        			GUI.console.setText("**WARNING: You have drawn an EXPLODING CARD!**");
	                        			dos.writeUTF("warnOthers#0#"+playnum);
	                        			
	                        			Timer timer = new Timer(); //new timer
	                        	        
	                        	        TimerTask task = new TimerTask() {         
	                        	            public void run() {                
	                        	                GUI.timer.setText("00:0"+Integer.toString(counter)+":00"); //the timer lable to counter.
	                        	                counter--;
	                        	                if (counter == -1){
	                        	                    timer.cancel();
	                        	                    GUI.timer.setText("DEAD");
	                        	                    GUI.cardPane.removeAll();
	                        	                    GUI.cardPane.revalidate();
	                        	                    GUI.cardPane.repaint();
	                        	                    GUI.diffuseBtn.setEnabled(false);
	                        	                    GUI.diffuseCtr.setText(null);
	                        	                    GUI.console.setText(GUI.console.getText()+"\n\n**You have died**\n\n");
	                        	                    try {
														dos.writeUTF("dead#");
														dos.flush();
														 try {
				                        	                    String droppedcards="";
				        	        							for(int i=0; i<droppedActions.size(); i++) {
				        	        								droppedcards=droppedcards+droppedActions.get(i)+",";
				        	        							}
				                    							if(playnum>=maxPlayers) {
																	dos.writeUTF("endTurn#"+droppedcards+"#0");
				                    							}else {
				                    								dos.writeUTF("endTurn#"+droppedcards+"#"+(playnum));
				                    							}
				                    							droppedActions.clear();
				                    							dos.flush();
			                        	                    } catch (IOException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
													} catch (IOException e1) {
														// TODO Auto-generated catch block
														e1.printStackTrace();
													}
	                        	                    
	                        	                    try {
														dos.writeUTF("warnOthers#2#"+playnum);
														dos.flush();
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
	                        	                }else if(counter==0) {
	                        	                	GUI.timer.setForeground(Color.RED);
	                        	                }else if(diffused){
	                        	                	GUI.timer.setText("00:00:00");
	                        	                	GUI.timer.setForeground(Color.GRAY);
	                        	                	GUI.console.setText(GUI.console.getText()+"\n\n**You have diffused the exploding card!**\n\n");
	                        	                	discardExploding();
	                        	                	try {
														dos.writeUTF("warnOthers#1#"+playnum);
														dos.flush();
													} catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
	                        	                	
	                        	                    timer.cancel();
	                        	                    counter=7;
	                        	                    diffused = false;
	                        	                    
	                        	                    try {
		                        	                    String droppedcards="";
		        	        							for(int i=0; i<droppedActions.size(); i++) {
		        	        								droppedcards=droppedcards+droppedActions.get(i)+",";
		        	        							}
		                    							if(playnum>=maxPlayers) {
															dos.writeUTF("endTurn#"+droppedcards+"#0");
		                    							}else {
		                    								dos.writeUTF("endTurn#"+droppedcards+"#"+(playnum));
		                    							}
		                    							droppedActions.clear();
		                    							dos.flush();
	                        	                    } catch (IOException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
	                        	                }
	                        	            }
	                        	        };
	                        	        timer.scheduleAtFixedRate(task, 1000, 1000); // =  timer.scheduleAtFixedRate(task, delay, period);
	                        		}else {
	                        			String droppedcards="";
	        							for(int i=0; i<droppedActions.size(); i++) {
	        								droppedcards=droppedcards+droppedActions.get(i)+",";
	        							}
	        							
	        							if(playnum>=maxPlayers) {
	        								dos.writeUTF("endTurn#"+droppedcards+"#0");
	        							}else {
	        								dos.writeUTF("endTurn#"+droppedcards+"#"+(playnum));
	        							}
	        							droppedActions.clear();
	        							dos.flush();
	                        		}
	                        		*/
	                        			
	                        		/*String droppedcards="";
        							for(int i=0; i<droppedActions.size(); i++) {
        								droppedcards=droppedcards+droppedActions.get(i)+",";
        							}
        							
        							if(playnum>=maxPlayers) {
        								dos.writeUTF("endTurn#"+droppedcards+"#0");
        							}else {
        								dos.writeUTF("endTurn#"+droppedcards+"#"+(playnum));
        							}*/
        							dos.writeUTF("checkAlivePeople#");
        							dos.flush();
        							
	                        		//clear current draw pile
	                        		
	                        		for(int i=0; i<card.size(); i++){
	                					while(card.get(i).getMouseListeners().length!=0){
	                						card.get(i).removeMouseListener(card.get(i).getMouseListeners()[0]);
	                					}
	                				}
	                        		GUI.drawPile.removeAll();
	                        		
	                        		//re-populate card Pane
	                        		for(int i=0; i<card.size(); i++) {
	                        			card.get(i).setPreferredSize(new Dimension(130,172));
	                        			card.get(i).setHorizontalAlignment(SwingConstants.CENTER);
	                        			System.out.print(hand.get(i).getFunc()+",");
	                        			GUI.cardPane.add(card.get(i));
	                        			final int a=i;
	                        			card.get(a).addMouseListener(new MouseAdapter() {
	                        				@Override
	                        				public void mouseEntered(MouseEvent arg0) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border==null) {
	                        						if (border==null || !border.getLineColor().equals(Color.GREEN)) {
	                        							card.get(a).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
	                        						}
	                        					}
	                        				}
	                        				
	                        				public void mouseExited(MouseEvent e) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border!=null) {
	                        						if(!border.getLineColor().equals(Color.GREEN)) {
		                        						card.get(a).setBorder(null);
		                        					}
	                        					}
	                        					
	                        				}
	                        				
	                        				public void mousePressed(MouseEvent e) {
	                        					LineBorder border=(LineBorder) card.get(a).getBorder();
	                        					if(border==null || border.getLineColor().equals(Color.RED)) {
	                        						card.get(a).setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
	                        					}else {
	                        						card.get(a).setBorder(null);
	                        					}
	                        					
	                        				}
	                        			});
	                        		}
	                        		System.out.println("\n");
	                        		GUI.cardPane.revalidate();
	                    			GUI.cardPane.repaint();
	                    			checkDiffuse();
	                        		msg="";
	                        	}else if (msg.contains("yourTurn#")) {
	                        		System.out.println("pasuk**");
	                        		System.out.println("ikaw na memsh");
	                        		droppedEarlier=msg.split("#");
	                        		if(droppedEarlier.length>1) { //meaning may some stuff
	                        			droppedEarlier=droppedEarlier[droppedEarlier.length-1].split(",");
		                        		System.out.println("drpearl**"+java.util.Arrays.toString(droppedEarlier));
		                        		
	                        		}
	                        		
	                        		boolean flag=true;
	                        		switch(droppedEarlier[droppedEarlier.length-1].toLowerCase()) {
	                        		case "double slap": 
	                        			dos.writeUTF("getDouble#");
                        				dos.flush();
	                        			break;
	                        		case "triple slap":
	                        			dos.writeUTF("getTriple#");
	                        			dos.flush();
	                        			break;
	                        		case "skip":
	                        			dos.writeUTF("checkAlivePeople#");
	                        			dos.flush();
	                        			flag=false;
	                        			break;
	                        		}
	                        		droppedEarlier=null;
	                        		if(flag) {
	                        			GUI.console.setText(GUI.console.getText()+"\n----\nYOUR TURN.\n----");
		                        		GUI.status.setText("status: Your Turn.");
	                        		}else {
	                        			GUI.console.setText(GUI.console.getText()+"\n----\nYOU WERE SKIPPED!\n----");
		                        		GUI.status.setText("status: skipped.");
	                        		}
	                        		
	                        		msg="";
	                        		GUI.dropBtn.setEnabled(true);
	                        	}else if(msg.contains("userList#")) {
	                        		String[] userListh=msg.split("#"); //"horizontal User List w/ commas and all"
                					String users=userListh[userListh.length-1];
                        			String[] userListV=users.split(","); //"horizontal" turned "vertical"
                        			DefaultListModel listmodel=new DefaultListModel();
                        			for(int i=0; i<userListV.length; i++) {
                        				if((i+1)==playnum) {
                        					listmodel.addElement("Player "+(i+1)+": "+userListV[i]+" (YOU)");
                        				}else {
                        					listmodel.addElement("Player "+(i+1)+": "+userListV[i]);
                        				}
                        			}
                        			GUI.playerList.setModel(listmodel);;
                        			GUI.playerList.revalidate();
                        			GUI.playerList.repaint();
                        			msg="";
	                        	}else if (msg.contains("highlight#")) {
	                        		String tmp[]=msg.split("#");
	                        		Integer index=Integer.parseInt(tmp[tmp.length-1]);
	                        		if((index+1)!=playnum) {
	                        			GUI.status.setText("status: Player "+(index+1)+"\'s turn.");
	                        		}
	                        		msg="";
	                        		GUI.playerList.setSelectedIndex(index);
	                        	}else if (msg.contains("receiveDouble#")) {
	                        		String tmp[]=msg.split("#");
	                        		
	                        		for(int i=1; i<3; i++) {
	                        			String[] tmp2=tmp[i].split(",");
	                        			System.out.println(java.util.Arrays.toString(tmp2));
	                        			hand.add(0, new Card(tmp2[0],tmp2[1],tmp2[2]));
		                        		if(tmp2[0].equalsIgnoreCase("cat")) {
		                        			card.add(0, new JLabel(tmp2[1]));
		                        		}else {
		                        			card.add(0, new JLabel(tmp2[0]));
		                        		}
	                        		}
	                        		repaintrepopulate();
	                        		msg="";
	                        	}else if (msg.contains("receiveTriple#")) {
	                        		String tmp[]=msg.split("#");
	                        		for(int i=1; i<4; i++) {
	                        			String[] tmp2=tmp[i].split(",");
	                        			hand.add(0, new Card(tmp2[0],tmp2[1],tmp2[2]));
		                        		if(tmp2[0].equalsIgnoreCase("cat")) {
		                        			card.add(0, new JLabel(tmp2[1]));
		                        		}else {
		                        			card.add(0, new JLabel(tmp2[0]));
		                        		}
	                        		}
	                        		repaintrepopulate();
	                        		msg="";
	                        	}else if(msg.contains("status#")){
	                        		String[] tmp= msg.split("#");
	                        		GUI.status.setText("status: "+tmp[tmp.length-1]);
	                        		msg="";
	                        	}else if (msg.contains("theFuture#")) {
	                        		String tmp[]=msg.split("#");
	                        		System.out.println("line 598***");
	                        		String[] firstThree=tmp[tmp.length-1].split(",");
	                        		JOptionPane.showMessageDialog(GUI.frame, "The first three cards are the following:\n "+java.util.Arrays.toString(firstThree));
	                        		msg="";
	                        	}else if(msg.contains("playerNum#")){
	                        		String[] hatiin=msg.split("#");
	                        		playnum=Integer.parseInt(hatiin[hatiin.length-1]);
	                        		System.out.println("I AM PLAYER NUMBER: "+playnum);
	                        		msg="";
	                        	}else if(msg.contains("sendAliveList#")){
	                        		String[] alive=msg.split("#");
	                        		String list=alive[alive.length-1];
	                        		list=list.replaceAll("\\p{P}", "");
	                        		
	                        		alive=list.split(",");
	                        		System.out.println(java.util.Arrays.toString(alive));
	                        		
	                        		String droppedcards="";
	        						for(int i=0; i<droppedActions.size(); i++) {
	        							droppedcards=droppedcards+droppedActions.get(i)+",";
	        						}
	        						
	        						//NOT FINISHED: CHECK WHEN DEAD PEOPLE ARISE
	        						
	        						
	                        		if(playnum>=maxPlayers) {
	        							dos.writeUTF("endTurn#"+droppedcards+"#0");
	        						}else {
	        							dos.writeUTF("endTurn#"+droppedcards+"#"+(playnum));
	        						}
	        						droppedActions.clear();
	        						dos.flush();
	        						msg="";
	                        	}else if (msg.contains("buhayList#")) {
	                        		System.out.println("line 300**");
	                        		String[] tmp=msg.split("#");
	                        		String[] alivepeople=tmp[tmp.length-1].split(",");
	                        		for(int i=0; i<alivepeople.length; i++) {
	                        			int tmpint=Integer.parseInt(alivepeople[i])+1;
	                        			alivepeople[i]= Integer.toString(tmpint);
	                        		}
	                        		String s = (String)JOptionPane.showInputDialog(
            			                    GUI.frame,
            			                    "Choose player to get card:\n",
            			                    "Favor",
            			                    JOptionPane.PLAIN_MESSAGE,null,
            			                    alivepeople,
            			                    alivepeople[0]);
	                        		
	                        		int tmpint=Integer.parseInt(s)-1;
			            			dos.writeUTF("favor#"+(playnum-1)+"#"+tmpint);
			            			dos.flush();
	                        	}else if (msg.contains("stealCard#")) {
	                        		String[] tmp=msg.split("#");
	                        		int rand=(int) (Math.random()*(((hand.size()-1)-0)+1))+0;
	                        		
	                        		Card card2=hand.get(rand);
	                        		hand.remove(rand);
	                        		card.remove(rand);
	                        		
	                        		dos.writeUTF("stolenCard#"+tmp[1]+"#"+card2.getFunc()+","+card2.getOther()+","+card2.getDir()+",");
	                        		dos.flush();
	                        		
	                        		repaintrepopulate();
	                        		
	                        		msg="";
	                        	}else if(msg.contains("discarded#")){
	                        		if(GUI.status.getText().contains("Your Turn.")) {
	                        			GUI.cardPane.removeAll();
	                        			
	                        			//re-populate card Pane
		                        		for(int i=0; i<card.size(); i++) {
		                        			System.out.print(hand.get(i).getFunc()+",");
		                        			GUI.cardPane.add(card.get(i));
		                        			final int a=i;
		                        			card.get(a).addMouseListener(new MouseAdapter() {
		                        				@Override
		                        				public void mouseEntered(MouseEvent arg0) {
		                        					LineBorder border=(LineBorder) card.get(a).getBorder();
		                        					if(border==null) {
		                        						if (border==null || !border.getLineColor().equals(Color.GREEN)) {
		                        							card.get(a).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		                        						}
		                        					}
		                        				}
		                        				
		                        				public void mouseExited(MouseEvent e) {
		                        					LineBorder border=(LineBorder) card.get(a).getBorder();
		                        					if(border!=null) {
		                        						if(!border.getLineColor().equals(Color.GREEN)) {
			                        						card.get(a).setBorder(null);
			                        					}
		                        					}
		                        					
		                        				}
		                        				
		                        				public void mousePressed(MouseEvent e) {
		                        					LineBorder border=(LineBorder) card.get(a).getBorder();
		                        					if(border==null || border.getLineColor().equals(Color.RED)) {
		                        						card.get(a).setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		                        					}else {
		                        						card.get(a).setBorder(null);
		                        					}
		                        					
		                        				}
		                        			});
		                        		}
		                        		System.out.println("\n");
		                        		GUI.cardPane.revalidate();
		                    			GUI.cardPane.repaint();
		                    			checkDiffuse();
	                        		}
	                        		
	                    			//to change look of discard pile
	                    			String[] tmp=msg.split("#");
	                    			tmp=tmp[tmp.length-1].split(":");
	                    			System.out.println("*discarded array: "+java.util.Arrays.toString(tmp));
	                    			GUI.discard.setText(null);
	                    			ImageIcon origimg=new ImageIcon(getClass().getResource(tmp[tmp.length-1]));
	                    			
	                    			Image scaled=origimg.getImage();
	                    			scaled=scaled.getScaledInstance(GUI.discard.getWidth(), GUI.discard.getHeight(), Image.SCALE_DEFAULT);
	                    			GUI.discard.setIcon(new ImageIcon(scaled));
	                    			GUI.discard.setVisible(true);
	                    			GUI.discard.setBorder(null);
	                    			
	                    			if(tmp[0].contains("Cat")) {
	                    				GUI.console.setText(GUI.console.getText()+"\n**A pair of "+tmp[1]+" is dropped!**\n");
	                    			}else {
	                    				if(tmp[0].substring(0, 0).equalsIgnoreCase("a")||tmp[0].substring(0, 0).equalsIgnoreCase("e")||tmp[0].substring(0, 0).equalsIgnoreCase("i")||tmp[0].substring(0, 0).equalsIgnoreCase("o")||tmp[0].substring(0, 0).equalsIgnoreCase("u")) {
	                    					GUI.console.setText(GUI.console.getText()+"\n**An "+tmp[0]+" card is dropped!**\n");
	                    				}else {
	                    					GUI.console.setText(GUI.console.getText()+"\n**A "+tmp[0]+" card is dropped!**\n");
	                    				}
	                    			}
	                    			msg="";
	                        	}
	                        	
	                        	//NO PROTOCOL MESSAGE: GENERAL
	                        	if(!msg.equals("")) {
	                        		System.out.println(msg);
		                        	GUI.console.setText(GUI.console.getText()+"\n"+msg);
	                        	}
	                        	
	                        }
	                        msg="";
	                    } catch (IOException e) {
	                    	System.out.println(msg);
	                        e.printStackTrace(); 
	                    } 
	                } 
	                System.out.println("dito nagclose");
	                System.exit(0);
	            } 
	        }); 
	  
	        sendMessage.start(); 
	        readMessage.start();
		}
		
	}
	
	protected static void discardExploding() {
		ArrayList<Integer> indices=new ArrayList<Integer>();
		
		for(int i=0; i<card.size(); i++) {
			LineBorder border=(LineBorder) card.get(i).getBorder();
			if(border !=null && border.getLineColor().equals(Color.GREEN)) {
				indices.add(i); //gets index number of green card
			}else if(hand.get(i).getFunc().equalsIgnoreCase("exploding")) {
				indices.add(i);
			}
		}
		
		//unfinished: continue
	}
	
	protected static void discard() {
		// TODO Auto-generated method stub
		ArrayList<Integer> indices=new ArrayList<Integer>();
		String tmp="";
		for(int i=0; i<card.size(); i++) {
			LineBorder border=(LineBorder) card.get(i).getBorder();
			if(border !=null && border.getLineColor().equals(Color.GREEN)) {
				indices.add(i); //gets index number of green cards
			}
		}
		System.out.print("\nHighlighted Cards are in index: "+indices.toString());
		
		System.out.println();
		if(indices.size()>2) {
			GUI.console.setText(GUI.console.getText()+"\n--\nINVALID MOVE! Try Again.\n--\n");
		}else {
			if(checkCard(indices)) {
				System.out.println("handed: ");
				for(int i=0; i<hand.size(); i++) {
					System.out.println("["+hand.get(i).getFunc()+","+hand.get(i).getOther()+","+hand.get(i).getDir()+"]");
				}
				
				
				for(int i=0; i<indices.size(); i++) {
					int index=indices.get(i);
					tmp=tmp+hand.get(index).getFunc()+":"+hand.get(index).getOther()+":"+hand.get(index).getDir()+",";
					if(hand.get(index).getOther().equals("endturn")) {
						droppedActions.add(hand.get(index).getFunc());
					}
					
				}
				
				
				
				for(int i=0; i<card.size(); i++){
					while(card.get(i).getMouseListeners().length!=0){
						card.get(i).removeMouseListener(card.get(i).getMouseListeners()[0]);
					}
				}
				
				try {
					dos.writeUTF("discardPile#"+tmp);
					dos.flush();
					//server: add to discard pile, send to all clients to be shown on discard pile label
					//repaint revalidate check diffusectr
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(indices.size()==1 && hand.get(indices.get(0)).getOther().equalsIgnoreCase("endturn")) {
					try {
						GUI.drawPile.setBorder(null);
						GUI.dropBtn.setEnabled(false);
						
						//System.out.println("**MaxPlayers: "+maxPlayers);
						
						if(hand.get(indices.get(0)).getFunc().equalsIgnoreCase("draw from the bottom")) {
							dos.writeUTF("bottomDraw#");
							dos.flush();
						}
						
						dos.writeUTF("checkAlivePeople#");
						dos.flush();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(indices.size()==1 && !hand.get(indices.get(0)).getOther().equalsIgnoreCase("endturn")) { 
					if(hand.get(indices.get(0)).getFunc().toLowerCase().equalsIgnoreCase("favor")) {
						System.out.println("*pumasok ples line 833 anuba*");
						try {
							dos.writeUTF("checkBuhay#");
							dos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			
					}
				}else if(indices.size()==1) {
					try {
						switch(hand.get(indices.get(0)).getFunc().toLowerCase()) {
						case "reverse":
							
						case "shuffle":
							System.out.println("line 794");
							dos.writeUTF("reshuffleCards#");
							dos.flush();
							break;
						case "see the future":
							dos.writeUTF("seeTheFuture#");
							dos.flush();
							break;
						}
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				while(indices.size()!=0) {
					int index=indices.get(0);
					hand.remove(index);
					card.remove(index);
					indices.remove(0);
					
					if(indices.size()==1){
						int tmpi=indices.get(0)-1;
						indices.remove(0);
						indices.add(tmpi);
					}
					
				}
				
			}else {
				GUI.console.setText(GUI.console.getText()+"\n--\nINVALID MOVE! Try Again.\n--\n");
			}
			
		}
		for(int i=0; i<hand.size(); i++){
			System.out.println(hand.get(i).getFunc());
		}
	}

	private static boolean checkCard(ArrayList<Integer> indices) {
		boolean flag=true;
		
		if(indices.size()==2) {
			//cat but different type
			//cat + attack
			//attack + cat
			//same cat same type is true
			if(hand.get(indices.get(0)).getFunc().equalsIgnoreCase("cat") && hand.get(indices.get(1)).getFunc().equalsIgnoreCase("cat")) {
				if (!hand.get(indices.get(0)).getOther().equalsIgnoreCase(hand.get(indices.get(1)).getOther())) {
					flag=false;
				}
			}else {
				flag=false;
			}
		}else {
			if(hand.get(indices.get(0)).getFunc().equalsIgnoreCase("cat")) {
				flag=false;
			}
		}
		
		return flag;
	}
	
	protected static void repaintrepopulate() {
		//clear current draw pile
		
		for(int i=0; i<card.size(); i++){
			while(card.get(i).getMouseListeners().length!=0){
				card.get(i).removeMouseListener(card.get(i).getMouseListeners()[0]);
			}
		}
		GUI.drawPile.removeAll();
		
		//re-populate card Pane
		for(int i=0; i<card.size(); i++) {
			card.get(i).setPreferredSize(new Dimension(130,172));
			card.get(i).setHorizontalAlignment(SwingConstants.CENTER);
			System.out.print(hand.get(i).getFunc()+",");
			GUI.cardPane.add(card.get(i));
			final int a=i;
			card.get(a).addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent arg0) {
					LineBorder border=(LineBorder) card.get(a).getBorder();
					if(border==null) {
						if (border==null || !border.getLineColor().equals(Color.GREEN)) {
							card.get(a).setBorder(BorderFactory.createLineBorder(Color.RED, 2));
						}
					}
				}
				
				public void mouseExited(MouseEvent e) {
					LineBorder border=(LineBorder) card.get(a).getBorder();
					if(border!=null) {
						if(!border.getLineColor().equals(Color.GREEN)) {
    						card.get(a).setBorder(null);
    					}
					}
					
				}
				
				public void mousePressed(MouseEvent e) {
					LineBorder border=(LineBorder) card.get(a).getBorder();
					if(border==null || border.getLineColor().equals(Color.RED)) {
						card.get(a).setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
					}else {
						card.get(a).setBorder(null);
					}
					
				}
			});
		}
		System.out.println("\n");
		GUI.cardPane.revalidate();
		GUI.cardPane.repaint();
		checkDiffuse();
	}

	protected static void checkDiffuse() {
		// TODO Auto-generated method stub
		Integer tmp=0;
		for(int i=0; i<hand.size(); i++) {
			if(hand.get(i).getFunc().equalsIgnoreCase("diffuse")) {
				tmp++;
			}
		}
		GUI.diffuseCtr.setText(Integer.toString(tmp));
	}

	private static void close() {
		// TODO Auto-generated method stub
		try {
			dos.writeUTF("exit#");
			dos.flush();
			dis.close();
			dos.close();
			s.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

}
