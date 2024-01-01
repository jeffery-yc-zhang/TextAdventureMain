package gitHub;

import java.util.*;

public class TextAdventureMain {
	static Scanner sc = new Scanner(System.in);

	HashMap<String, Room> roomList = new HashMap<String, Room>();
	HashMap<String, Item> itemList = new HashMap<String, Item>(); // list of all item objects
	HashMap<String, Integer> inventory = new HashMap<>();
	public String currentRoom = "";
	Player p = new Player();
	static boolean alive = true;
	static boolean won = false;
	static boolean sacrifice= false;
	boolean sword = false;

	public static void main(String[] args) {

		TextAdventureMain t = new TextAdventureMain();
		t.AdventureMain();
	}

	void AdventureMain() {

		boolean playing = true;
		String command = "";
		setup(); // create all objects needed, including map; print intro. message
		Room.updateRooms(roomList);
		System.out.println();
		lookAtRoom(0);

		/***** MAIN GAME LOOP *****/
		while (playing) {
			Room.updateRooms(roomList);
			if (currentRoom.equals("Lava")) {
				System.out.println("You took a bath in lava and died after burning in lava and dying.");
				break;
			}

			if (!alive) {
				System.out.println("You lost, you suck, poor you.");
				break;
			}
			if (won) {
				break;
			}
			System.out.println();
			command = getCommand();
			playing = parseCommand(command);

		}
		System.out.println();
		if (won) {
			System.out.println("Congrats! You Beat the legendary flying turtle and beat the game!");
		} else {
			System.out.println(" YOU LOSE ");
		}

	}

	void lookAtRoom(int n) {
		Room r = roomList.get(currentRoom);
		if (n == 0) {
			if (r.isVisited) {
				System.out.println(r.displayName);
			} else {
				r.isVisited = true;
				System.out.println(r.displayName);
				System.out.println();
				System.out.println(r.description);
				if (r.itemsHere.length() > 20) {
					System.out.println();
					System.out.println(r.itemsHere);
				}
			}
		} else {
			System.out.println(r.displayName);
			System.out.println();
			System.out.println(r.description);
			if (r.itemsHere.length() > 20) {
				System.out.println();
				System.out.println(r.itemsHere);
			}
		}
	}

	void moveToRoom(char c) {
		Room r = roomList.get(currentRoom);
		String room = r.getExits(c);
		if (room.equals("")) {
			System.out.println("You cant go there.");
		} else {
			currentRoom = r.getExits(c);
			lookAtRoom(0);
		}
	}

	void showInventory() {
		System.out.println(" INVENTORY: ");
		for (Map.Entry item : inventory.entrySet()) {
			System.out.println(item.getKey() + " : " + item.getValue());
		}
	}

	void setup() {
		System.out.println("Welcome to the game! You are not gonna have a good time! Good luck on your adventure!");
		Room.setupRooms(roomList);
		Item.setupItems(itemList, roomList);
		// ... more stuff ...
		currentRoom = "Cave entrance";
	}

	String getCommand() {
		String text = sc.nextLine();
		if (text.length() == 0)
			text = "qwerty"; // default command
		// sc.close();
		return text;
	}

	boolean parseCommand(String text) {

		text = text.toLowerCase().trim(); // the complete string BEFORE parsing
		text = text.replaceAll(" into ", " in ");
		text = text.replaceAll(" rocks", " rock");
		text = text.replaceAll("pick up", "pickup");
		text = text.replaceAll("look at", "lookat");
		text = text.replaceAll("climb up", "climbup");
		text = text.replaceAll("healing pot", "healingpot");
		text = text.replaceAll("atk pot", "atkpot");
		text = text.replaceAll("def pot", "defpot");
		text = text.replaceAll("life pot", "lifepot");
		text = text.replaceAll("dex pot", "dexpot");

		String words[] = text.split(" ");

		ArrayList<String> wordlist = new ArrayList<String>(Arrays.asList(words)); // array list of words
		for (int i = 0; i < wordlist.size(); i++) {
			if (wordlist.get(i).equals("the"))
				wordlist.remove(i--);
		}

		String word1 = words[0];
		String word2 = "";
		String word3 = "";
		if (words.length > 1) {
			word2 = words[1];
		}
		// if (words.length>2) {
		// word3=words[2];
		// }
		/***** MAIN PROCESSING *****/
		if (word2.equals("")) {
			switch (word1) {

			case "quit":
				System.out.print("Are you really a loser? You wanna quit now?");
				String ans = getCommand().toUpperCase();
				if (ans.equals("YES") || ans.equals("Y")) {
					alive = false;
				}
				break;

			case "hp":
				System.out.println(p.health+"/"+p.maxHp);
				break;
				
			case "balance": case "bal":
				System.out.println("Current balance: "+p.money);
				break;

			case "stats":
				stats();
				break;
			case "sacrifice":
				sacrifice();
				break;

			case "n":
			case "s":
			case "w":
			case "e":
			case "north":
			case "south":
			case "west":
			case "east":
				moveToRoom(word1.charAt(0));
				break;

			case "i":
			case "inventory":
				showInventory();
				break;

			case "sleep":
				sleep();
				break;

			case "help":
				printHelp();
				break;
			case "buy":
			case "shop":
				buy();
				break;
			case "look":
				lookAtRoom(1);
				break;
			case "die":
				die();
				break;
			case "attack":
			case "fight":
			case "kill":
				attack();
				break;

			default:
				System.out.println("Sorry, I don't understand that command");
			}
		}
		if (!word2.equals("")) {

			switch (word1) {

			case "observe":
				observeItem(word2);
				break;

			case "take":
			case "grab":
			case "pickup":
				takeItem(word2);
				break;

			case "eat":
			case "drink":
				eatItem(word2);
				break;

			case "drop":
				dropItem(word2);
				break;

			default:
				System.out.println("Sorry, I don't understand that command");
			}
		}
		return true;
	}
	// tons of other methods go here ...

	void observeItem(String word) {
		if (!inventory.containsKey(word)) {
			System.out.println("You dont have that?");
			return;
		}
		if (itemList.get(word).writing.equals("")) {
			System.out.println("No writings.");
			return;
		}
		System.out.println(itemList.get(word).writing);
	}
	void sacrifice() {
		if (!currentRoom.equals("Altar")) {
			System.out.println("Can't do anything here");
			return;
		}
		if (p.health<150) {
			System.out.println("You do not have enough health to sacrifice");
			return;
		}
		System.out.println("You are about to sell your soul.");
		System.out.print("Set HP to 1, ATK to 10000?: (Y/N): ");
		String s=sc.nextLine().toLowerCase();
		if (s.equals("n")) {
			return;
		}
		if (s.equals("y")) {
			sacrifice=true;
			System.out.println("You have sold your soul.");
			p.atk=10000;
			p.maxHp=1;
			p.health=1;
		}
		
	}
	void buy() {
		Item i = new Item("", "");
		if (!currentRoom.equals("Merchant")) {
			System.out.println("You can't buy anything here");
			return;
		}
		while (true) {
			System.out.println();
			System.out.println("\t\tSHOP\t\t");
			System.out.println("\t Current Balance: $" + p.money);
			int shopItems = 0;
			for (Item it:Item.shop) {
					System.out.printf("%d)     %-14s\t  $%d\n", it.shopIndex, it.name, it.price);
			}
			System.out.println("4)     Sharpen Sword      $40");
			System.out.println("5)     God potion         $120");
			System.out.print("type 'Q' to leave shop or type a # to buy item: ");
			String a = sc.nextLine();
	
			if (a.equals("q") || a.equals("Q")) {
				System.out.println("You have exited the shop");
				return;
			}
			System.out.println();
	
			for (Map.Entry item : itemList.entrySet()) {
				if (itemList.get(item.getKey()).shopIndex != 0) {
					if (itemList.get(item.getKey()).shopIndex == Integer.parseInt(a)) {
						i = itemList.get(item.getKey());
					}
				}
			}
			switch (a) {
			case "1": case "2": case "3":
				System.out.print("How many " + i.name + "s do you want? ");
				int amount = sc.nextInt();
				sc.nextLine();
				if (p.money < amount * i.price) {
					System.out.println("Not enough money");
					System.out.println();
				} else {
					p.money -= amount * i.price;
					System.out.println("You have bought " + amount + " " + i.name + "s.");
					if (!inventory.containsKey(i.name)) {
						inventory.put(i.name, amount);
					} else {
						inventory.put(i.name, inventory.get(i.name) + amount);
					}
				}
				continue;
			case "5":
				System.out.print("How many potions do you want: ");
				int ans1=sc.nextInt();
				sc.nextLine();
				if (p.money<120*ans1) {
					System.out.println("Not enough money");
					System.out.println();
				} else {
					p.money-=120*ans1;
					System.out.println("You have bought the God potions");
					if (!inventory.containsKey("godpotion")) {
						inventory.put("godpotion",ans1);
					} else {
						inventory.put("godpotion", inventory.get("godpotion") + ans1);
					}
				}
				continue;
				
				
			case "4":
				if (sword) {
					System.out.println("You cant upgrade your sword again.");
					break;
				}
				System.out.print("Do you want to upgrade your sword? (Y/N): ");
				String ans = sc.nextLine();
				System.out.println();
				if (ans.equals("Y") || ans.equals("y")) {
					System.out.println("You upgraded your sword +10 atk");
					p.money -= 40;
					p.atk += 10;
				}
				continue;
				
			
	
			default:
				System.out.println("Not a valid response.");
				continue;
			}
		}
	}

	void stats() {
		System.out.println(" STATS: ");
		System.out.println("Max hp: " + p.maxHp);
		System.out.println("Hp: " + p.health);
		System.out.println("Attack: " + p.atk);
		System.out.println("Defense: " + p.def);
		System.out.println("Money: " + p.money);
		System.out.println("Dexterity: " + p.dex);
	}
	void die() {
		System.out.println("k.");
		alive=false;
	}
 	void sleep() {
		int rand = (int) (Math.random() * 9);
		if (rand == 1) {
			System.out.println("Unlucky. While you were sleeping, you were brutally murdered by a chicken.");
			alive=false;
		} else {
			if (p.health < p.maxHp - 15) {
				p.health += 15;
				System.out.println("You wake up with some good rest +15hp");
			} else {
				p.health = p.maxHp;
				System.out.println("You wake up with full hp");
			}
		}

	}

	void printHelp() {
		System.out.println("Move around with directions: n,w,s,e,up,down");
		System.out.println(
				"Common commands include : kill (enemy), eat (item), drop (item), take (item), i (inventory), sleep, observe (item)");
	}

	void dropItem(String word2) {
		if (!inventory.containsKey(word2)) {
			System.out.println("Cant drop what you dont have bozo.");
		} else {
			int amount = inventory.get(word2) - 1;
			System.out.println("You dropped the " + word2);
			System.out.println("You now have " + amount + " " + word2 + "s left.");
			inventory.put(word2, amount);
			if (amount == 0) {
				inventory.remove(word2);
			}
			roomList.get(currentRoom).items.add(word2);
		}
	}

	public void takeItem(String item) {
		if (roomList.get(currentRoom).items.contains(item)) {

			if (inventory.containsKey(item)) { // check for existing items
				inventory.put(item, inventory.get(item) + 1);
				System.out
						.println("You took the " + item + ". Now you have " + inventory.get(item) + " " + item + "s.");
			} else {
				inventory.put(item, 1); // add first item
				System.out.println("You took the " + item + ".");
			}
			roomList.get(currentRoom).items.remove(item);
		} else {
			System.out.println("There are none of those in this room.");
		}
	}

	void eatItem(String item) {
		if (!inventory.containsKey(item)) { // check if you have item
			System.out.println("You have no " + item + "s.");
			return;
		}
		if (item.equals("healingpot")) {
			if (p.health == p.maxHp) {
				System.out.println("You are at full hp");
				return;
			} else if (p.health < p.maxHp - 50) {
				p.health += 50;
				System.out.println("You are now at " + p.health + " hp.");
			} else {
				p.health = p.maxHp;
				System.out.println("You are now at full hp");
			}
		}

		else if (item.equals("lifepot")) {
			if (sacrifice) {
				System.out.println("You cannot change your hp. You have sacrificed.");
				return;
			}
			p.maxHp += 15;
			System.out.println("Your max health is now " + p.maxHp);
		} else if (item.equals("defpot")) {
			p.def += 5;
			System.out.println("Your defense is now " + p.def);
		} else if (item.equals("dexpot")) {
			p.dex += 0.2;
			System.out.println("Your dexterity is now " + p.dex);
		} else if (item.equals("atkpot")) {
			p.atk += 3;
			System.out.println("Your attack is now " + p.atk);
		} else if (item.equals("godpotion")) {
			p.atk+=15;
			p.def+=20;
			p.dex+=1.5;
			System.out.println("+15 atk +20 def +1.5 dex");
		} else {
			System.out.println("You cant consume that...");
			return;
		}

		int itemLeft = inventory.get(item) - 1;
		inventory.put(item, itemLeft);
		if (itemLeft == 1) {
			System.out.println("You only have one " + item + " left.");
		} else if (itemLeft == 0) {
			inventory.remove(item);
		} else {
			System.out.println("You now have " + inventory.get(item) + " " + item + "s left.");
		}

	}


	boolean attackFirst(double yourDex, double enemyDex) {
		double rand = Math.random() * (yourDex + enemyDex);
		if (rand < yourDex) {
			return true;
		}
		return false;
	}
	void attackSeq(Enemy e) {
		int crit = (int) (Math.random() * 9);
		if (attackFirst(p.dex, e.dex)) {
			
			if (crit == 1) {
				System.out.println("You crit him for " + p.atk * 2 + " damage.");
				e.health = e.health - p.atk * 2;
				System.out.println("It now has " + e.health + " hp.\n");
			} else {
				e.health = e.health - p.atk;
				System.out.println("It now has " + e.health + " hp.\n");
			}
			if (e.health<=0) {
				return;
			}
			
			int edmg = (int) (e.atk * (1 - (p.def / 100.0)));
			crit = (int) (Math.random() * 11);
			if (crit == 1) {
				System.out.println("It crit you for " + edmg * 2 + " damage.");
				p.health = p.health - edmg * 2;
				System.out.println("You now have " + p.health + " hp.\n");
			} else {
				p.health = (p.health - edmg);
				System.out.println("You now have " + p.health + " hp.\n");
			}
			if (p.health<=0) {
				return;
			}
	
		} else {
			int edmg = (int) (e.atk * (1 - (p.def / 100.0)));
			crit = (int) (Math.random() * 11);
			if (crit == 1) {
				System.out.println("It crit you for " + edmg * 2 + " damage.");
				p.health = p.health - edmg * 2;
				System.out.println("You now have " + p.health + " hp.\n");
			} else {
				p.health = (p.health - edmg);
				System.out.println("You now have " + p.health + " hp.\n");
			}
			if (p.health<=0) {
				return;
			}
			
			crit = (int) (Math.random() * 9);
			if (crit == 1) {
				System.out.println("You crit him for " + p.atk * 2 + " damage.");
				e.health = e.health - p.atk * 2;
				System.out.println("It now has " + e.health + " hp.\n");
			} else {
				e.health = e.health - p.atk;
				System.out.println("It now has " + e.health + " hp.\n");
			}
			if (e.health<=0) {
				return;
			}
		}
		
	}

	void attack() {
		if (!inventory.containsKey("sword")) {
			System.out.println("You have nothing to fight with.");
			return;
		}
		if (roomList.get(currentRoom).enemy.equals("")) {
			System.out.println("There is no enemy in this room");
			return;
		}
		
		Enemy e=new Enemy(roomList.get(currentRoom).enemy);
		
		if (!e.isAlive) {
			System.out.println("There is no enemy in this room");
			return;
		}
		
		while (true) {
			System.out.print("Attack, Heal or Run? (a/h/r): ");
			String r=sc.nextLine().toLowerCase();
			System.out.println();
			if (r.equals("a")) {
				attackSeq(e);
				if (e.health<=0) {
					System.out.println("You killed it.");
					if (currentRoom.equals("Boss Room")) {
						won=true;
					}
					p.money+=e.money;
					System.out.println("You got "+e.money+" coins.");
					e.isAlive=false;
					return;
				}
				if (p.health<=0) {
					System.out.println("It killed you.");
					alive=false;
					return;
				}
				continue;
				
			} 
			if (r.equals("h")) {
				eatItem("healingpot");
				continue;
			}
			if (r.equals("r")) {
				return;
			}
			System.out.println("Not a valid command");
		}
	}
}

class Player {
	int health = 100;
	int maxHp = 100;
	double def = 5.0f;
	double dex = 1.0f;
	int atk = 17;
	int money = 100;

	Player() {
	}
}

class Room {
	String displayName;
	String description;
	String itemsHere = "";
	String enemy="";
	String N = "";
	String W = "";
	String S = "";
	String E = "";

	boolean isVisited = false;

	ArrayList<String> items = new ArrayList<String>();

	Room(String displayName) {
		this.displayName = displayName;
	}

	void setExits(String N, String S, String E, String W) {
		this.N = N; // Cave
		this.S = S;
		this.E = E;
		this.W = W;
	}

	String getExits(char dir) {
		switch (dir) {
		case 'N':
		case 'n':
			return this.N;
		case 'S':
		case 's':
			return this.S;
		case 'W':
		case 'w':
			return this.W;
		case 'E':
		case 'e':
			return this.E;
		default:
			return "";
		}
	}

	static String giveItems(Room r) {
		String s = "Items in this room: |";
		for (int i = 0; i < r.items.size(); i++) {
			s += r.items.get(i) + "| ";
		}
		if (s.length() < 22) {
			return "";
		}
		return s.trim();
	}

	static void updateRooms(HashMap<String, Room> roomList) {
		for (String room : roomList.keySet()) {
			roomList.get(room).itemsHere = giveItems(roomList.get(room));
		}
	}

	static void setupRooms(HashMap<String, Room> roomList) {
		// 			Mini Boss
//	Lava  Mineshaft Cave 3 		Merchant
		// 			Cave 2		
		// 	Altar	Cave 		Loot room
		// 			Cave entrance

		// cave entrance
		Room r = new Room("Cave entrance");
		r.description = ("You are at the cave entrance. This cave is said to be full of legendary treasures, but also monsters!\n"
				+ "Move north to enter the cave...");
		r.setExits("Cave", "", "", "");
		roomList.put("Cave entrance", r);

		// cave
		r = new Room("Cave");
		r.enemy="Goblin";
		r.description = ("Oh crap! There is a goblin here! He doesn't seem to notice us yet...\n"
				+ "There seems to be a loot room to the East with weapons and potions...\n"
				+ "You could sneak past and advance further into the cave North...\n"
				+ "There is also this weird crack in the wall to the West that you can slide into.");
		r.setExits("Cave 2", "Cave entrance", "Loot room", "Altar");
		roomList.put("Cave", r);
		
		//Altar
		r=new Room("Altar");
		r.description=("A strange altar that looks like sacrificial temple...\n"
				+ "I wonder, what do you get for your sacrifice?");
		r.setExits("", "", "Cave", "");
		roomList.put("Altar", r);

		// loot room
		r = new Room("Loot room");
		r.description = ("You have entered a cool room with a nice golden chest inside. Whats in the chest?");
		r.setExits("", "", "", "Cave");
		roomList.put("Loot room", r);

		// cave 2
		r = new Room("Cave 2");
		r.enemy="Goblin";
		r.description = ("Further down the cave, the only thing you can see is a bright light further down the cave.");
		r.setExits("Cave 3", "Cave", "", "");
		roomList.put("Cave 2", r);

		// cave 3
		r = new Room("Cave 3");
		r.enemy="Demon";
		r.description = ("To the North there is a spooky chamber, to the East there seems to be a merchant.\n"
				+ "To the West, there is a mineshaft. You can always turn back!");
		r.setExits("Mini Boss", "Cave 2", "Merchant", "Mineshaft");
		roomList.put("Cave 3", r);

		// merchant
		r = new Room("Merchant");
		r.description = ("A cozy room with a kind merchant running a shop.\n"
				+ "Type 'shop'  or 'buy' to purchase something!");
		r.setExits("", "", "", "Cave 3");
		roomList.put("Merchant", r);

		// mineshaft
		r = new Room("Mineshaft");
		r.description = ("You enter a small mineshaft and you see a golden glowing further in. I could be gold!\n"
				+ "... but it seems quite unsafe. Travel west to venture deeper.");
		r.setExits("", "", "Cave 3", "Lava");
		roomList.put("Mineshaft", r);

		// lava
		r = new Room("Lava");
		r.description = ("You finally find out what it is... It is a pool of lava.\n"
				+ "Unfortunately, you are clumsy and fell into the lava.");
		roomList.put("Lava", r);

		// mini boss
		r = new Room("Mini Boss");
		r.enemy="Flying turtle";
		r.description = ("A huge chamber with a monstrous flying turtle.");
		roomList.put("Mini Boss", r);

	}

}

class Item {
	String name;
	String writing = "";
	int shopIndex = 0;
	int price = 0;
	static ArrayList<Item> shop = new ArrayList<>();

	Item(String name, String writing) {
		this.name = name;
		this.writing = writing;
	}

	static void setupItems(HashMap<String, Item> itemList, HashMap<String, Room> roomList) {
		Item z = new Item("sword", "An ancient sword used to slay evil. Effective against monsters.");
		itemList.put("sword", z);
		roomList.get("Loot room").items.add("sword");

		z = new Item("healingpot", "Healing Potion. Drinking it will give you up to 50 HP");
		shop.add(z);
		z.shopIndex = 1;
		z.price = 10;
		itemList.put("healingpot", z);
		roomList.get("Loot room").items.add("healingpot");

		z = new Item("atkpot", "Attack Potion. Drinking it gives tremendous strength for battle.");
		shop.add(z);
		z.shopIndex = 2;
		z.price = 15;
		itemList.put("atkpot", z);
		roomList.get("Loot room").items.add("atkpot");

		z = new Item("lifepot", "Life Potion. Drinking it will increase max HP.");
		shop.add(z);
		z.shopIndex = 3;
		z.price = 15;
		itemList.put("lifepot", z);
		roomList.get("Loot room").items.add("lifepot");

	}

}

class Enemy {

	int atk;
	double dex;
	int health;
	boolean isAlive=true;
	int money=0;

	Enemy(String name) {
		if (name.equals("Goblin")) {
			atk = 15;
			dex = 0.9;
			health = 50;
			money=30;
		}
		if (name.equals("Demon")) {
			atk = 20;
			dex = 1.8;
			health = 150;
			money=60;
		}
		if (name.equals("Flying turtle")) {
			atk = 35;
			dex = 0.7;
			health = 300;
			money = 130;
		}
		if ()
	}

}
