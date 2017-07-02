package com.realsight.westworld.tsp.test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.realsight.westworld.tsp.lib.model.htm.DogHierarchy;
import com.realsight.westworld.tsp.lib.util.Entry;

public class MazeExample {
	
	public enum ThreeRealms {
		Devaloka, Earth, Hell;
    };
	
	public class World {
		private ThreeRealms status = null;
		private char[][] maze = null;
		private int[][] count = null;
		private Entry<Integer, Integer> entry = null;
		private int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
		
		public World(Path path) throws FileNotFoundException {
			Scanner sin = new Scanner(path.toFile());
			List<String> lines = new ArrayList<String>();
			while(sin.hasNextLine()) {
				lines.add(sin.nextLine());
			}
			sin.close();
			this.maze = new char[lines.size()][];
			this.count = new int[lines.size()][];
			for (int i = 0; i < lines.size(); i++) {
				this.maze[i] = new char[lines.get(i).length()];
				this.count[i] = new int[lines.get(i).length()];
				for (int j = 0; j < lines.get(i).length(); j++) {
					this.maze[i][j] = lines.get(i).charAt(j);
					if (this.maze[i][j] == 'S') {
						this.entry = new Entry<Integer, Integer>(i, j);
					}
				}
			}
			if (this.entry != null)
				this.status = ThreeRealms.Earth;
		}
		
		public boolean check(Entry<Integer, Integer> entry) {
			if (entry.getFirst() < 0) return false;
			if (entry.getSecond() < 0) return false;
			if (entry.getFirst() >= this.maze.length) return false;
			if (entry.getSecond() >= this.maze[entry.getFirst()].length) return false;
			int x = entry.getFirst();
			int y = entry.getSecond();
			if (this.maze[x][y] == '#')
				return false;
			return true;
		}
		
		public List<Character> getEnv() {
			List<Character> env = new ArrayList<Character>();
			if (this.status != ThreeRealms.Earth)
				return env;
			int x = this.entry.getFirst();
			int y = this.entry.getSecond();
			for (int i = 0; i < dir.length; i++) {
				int tx = x + dir[i][0];
				int ty = y + dir[i][1];
				if (check(new Entry<Integer, Integer>(tx, ty))) {
					env.add(this.maze[tx][ty]);
				} else {
					env.add('#');
				}
			}
			return env;
		}
		
		public void step(int x, int y) {
			if (status != ThreeRealms.Earth)
				return ;
			this.entry.setFirst(x);
			this.entry.setSecond(y);
			if (check(this.entry)) {
				if (this.maze[x][y] == 'E'){
					this.status = ThreeRealms.Devaloka;
					return ;
				}
				this.count[x][y] += 1;
				return ;
			}
			this.status = ThreeRealms.Hell;
		}
		
		public void up() {
			if (status != ThreeRealms.Earth)
				return ;
			int x = this.entry.getFirst() - 1;
			int y = this.entry.getSecond();
			step(x, y);
		}
		
		public void down() {
			if (status != ThreeRealms.Earth)
				return ;
			int x = this.entry.getFirst() + 1;
			int y = this.entry.getSecond();
			step(x, y);
		}
		
		public void left() {
			if (status != ThreeRealms.Earth)
				return ;
			int x = this.entry.getFirst();
			int y = this.entry.getSecond() - 1;
			step(x, y);
		}
		
		public void right() {
			if (status != ThreeRealms.Earth)
				return ;
			int x = this.entry.getFirst();
			int y = this.entry.getSecond() + 1;
			step(x, y);
		}
		
		public ThreeRealms getStatus() {
			return this.status;
		}
		
		public void show(){
			System.out.println("The World is followed as:");
			if (this.status != ThreeRealms.Earth) {
				if (this.status == ThreeRealms.Devaloka) {
					System.out.println("Devaloka.\n");
				} else if (this.status == ThreeRealms.Hell) {
					System.out.println("Hell.\n");
				}
				return ;
			}
			for (int i = 0; i < this.maze.length; i++) {
				for (int j = 0; j < this.maze[i].length; j++) {
					if (this.entry.equals(new Entry<Integer, Integer>(i, j))){
						System.out.print("D");
					} else {
						System.out.print(this.maze[i][j]);
					}
				}
				System.out.println("");
			}
			System.out.println("");
		}
	};
	
	private List<String> actions = new ArrayList<String> ();
	Scanner sin = new Scanner(System.in);
	
	public MazeExample() {
		this.actions.add("up");
		this.actions.add("down");
		this.actions.add("left");
		this.actions.add("right");
		this.actions.add("over");
	}
	
	public void teach(DogHierarchy dog, World world) {
		Scanner sin = new Scanner(System.in);
		while(true) {
			world.show();
			System.out.print("input: ");
			String walk = sin.next();
			
			dog.learnAction(walk, 1.0);
			if (walk.equals("up")) {
				world.up();
			} else if (walk.equals("down")) {
				world.down();
			} else if (walk.equals("left")) {
				world.left();
			} else if (walk.equals("right")) {
				world.right();
			} else if (walk.equals("q")) {
				System.out.println("Teaching Over.");
				break;
			} else {
				System.out.println("input error.");
				continue;
			}
			List<Character> env = world.getEnv();
			dog.learn(env);
			if (world.getStatus() == ThreeRealms.Hell) {
				System.out.println("Dog dead.");
				break;
			} else if (world.getStatus() == ThreeRealms.Devaloka) {
				System.out.println("Dog win.");
				break;
			}
//			System.out.println(dog.getNeuroGroup().getActiveNeuros());
		}
	}
	
	public void run(DogHierarchy dog, World world) {
		Scanner sin = new Scanner(System.in);
		while(true) {
			world.show();
			System.out.print("Choose status: 1. walk a step; 2. end.\ninput:");
			int status = sin.nextInt();
			if (status == 1) {
				String walk = dog.action();
				System.out.println("dog chosse " + walk);
				if (walk.equals("up")) {
					world.up();
				} else if (walk.equals("down")) {
					world.down();
				} else if (walk.equals("left")) {
					world.left();
				} else if (walk.equals("right")) {
					world.right();
				}
				dog.learn(world.getEnv());
				if (world.getStatus() == ThreeRealms.Hell) {
					System.out.println("Dog dead.");
					break;
				} else if (world.getStatus() == ThreeRealms.Devaloka) {
					System.out.println("Dog win.");
					break;
				}
			}
		}
	}
	
	public void main() throws FileNotFoundException {
		Path path = Paths.get(System.getProperty("user.dir"), "data", "maze", "maze1");
		try {
			
			DogHierarchy dog = new DogHierarchy(this.actions);
			while(true) {
				System.out.print("1. 遛狗; 2. 狗狗自己遛自己.\ninput:");
				int choose = sin.nextInt();
				if (choose == 1) {
					World world = new World(path);
					dog.learn(world.getEnv());
					dog.learn(world.getEnv());
					dog.learn(world.getEnv());
					dog.learn(world.getEnv());
					System.out.println("带狗来户外");
					teach(dog, world);
					dog.learnAction("over", 1.0);
					System.out.println("狗狗累了开始休息，睡觉了.");
					dog.sleep();
				} else if (choose == 2){
					break;
				} else {
					System.out.println("input error");
				}
			}
			System.out.println("狗狗到户外.");
			World world = new World(path);
			dog.learn(world.getEnv());
			dog.learn(world.getEnv());
			dog.learn(world.getEnv());
			dog.learn(world.getEnv());
			System.out.println("狗狗感受环境.");
			run(dog, world);
			System.out.println("狗狗累了开始休息，睡觉了.");
			System.out.println("演示结束.");
			sin.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws FileNotFoundException {
		new MazeExample().main();
	}
}
