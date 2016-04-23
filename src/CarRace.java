
import java.awt.*;
import java.awt.event.*;
import static java.lang.String.format;
import javax.swing.*;

public class CarRace extends JFrame {

	Car car; 
	SlowCar slowCar; 
	CarOnComing bigTruck;
	int[] keyControls = { 0, 0, 0, 0 };
	Timer timer;
	double timeRem;
	double gameLength = 4800;
	int penaltyTime = 120;
	int gameEnd = 0;


	public static void main(String[] args) {
		new CarRace();

	}




	public CarRace() {
		Road road = new Road();
		car = new Car();
		slowCar = new SlowCar();
		bigTruck = new CarOnComing();
		add(road, BorderLayout.CENTER);
		setSize(600, 720);
		setTitle("Car Racing");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		initialiseApp();

		class KeyChecker implements KeyListener {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					keyControls[0] = 1;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					keyControls[1] = 1;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					keyControls[2] = 1;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					keyControls[3] = 1;
				}
				if (e.getKeyCode() == KeyEvent.VK_Y) {
					initialiseApp();
				}
				if (e.getKeyCode() == KeyEvent.VK_N) {
					System.exit(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					keyControls[0] = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					keyControls[1] = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					keyControls[2] = 0;
				} 
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					keyControls[3] = 0;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		}
		KeyChecker keyListener = new KeyChecker();
		addKeyListener(keyListener);
	}

	class Road extends JPanel {

		Image carDriver;
		ImageIcon pngDriver = new ImageIcon(this.getClass().getResource("carb75.png"));
		Image carSlow;
		ImageIcon pngSlow = new ImageIcon(this.getClass().getResource("slow75.png"));
		Image truck;
		ImageIcon pngTruck = new ImageIcon(this.getClass().getResource("truck90.png"));
		Image bg;
		ImageIcon jpgBg = new ImageIcon(this.getClass().getResource("bg2.jpg"));
		Image explode;
		ImageIcon pngEx = new ImageIcon(this.getClass().getResource("explode.png"));
		Image goAgain;
		ImageIcon jpgGo = new ImageIcon(this.getClass().getResource("go.jpg"));

		public Road() {
			setDoubleBuffered(true);
			TimerListener animate = new TimerListener();
			timer = new Timer(16, animate);
			timer.start();
		}

		private class TimerListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timeRem > 0)
					timeRem--;
				if (car.hit > 0) {
					penaltyTime--;
					if (penaltyTime == 0) {
						car.hit = 0;
						penaltyTime = 100;
						slowCar.y = -200;
						slowCar.setSpeed();
						bigTruck.setSpeed();
						bigTruck.y = -bigTruck.yStart;
						car.x = 195;
					}
				}
				if (timeRem > 0 && car.hit == 0) {
					car.move(getHeight());
					slowCar.move(getHeight());
					bigTruck.move(getHeight());
					car.checkHit();
				} else if (timeRem <= 0) {
					gameEnd = 1;
				}
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			carDriver = pngDriver.getImage();
			carSlow = pngSlow.getImage();
			truck = pngTruck.getImage();
			bg = jpgBg.getImage();
			explode = pngEx.getImage();
			goAgain = jpgGo.getImage();
			g.drawImage(bg, 0, 0, getWidth() + 1, getHeight(), this);
			g.drawImage(carDriver, car.x, car.y, this);
			g.drawImage(carSlow, slowCar.x, (int) Math.round(slowCar.y), this);
			g.drawImage(truck, bigTruck.x, bigTruck.y, this);
			if (car.hit == 1)
				g.drawImage(explode, car.x, car.y - 20, this);
			if (car.hit == 2)
				g.drawImage(explode, car.x, car.y + 130, this);
			if (gameEnd == 1) {
				g.drawImage(goAgain, 157, 250, this);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", Font.BOLD, 26));
				g.drawString("Your score: " + format("%.0f", car.score), 190, 335);
			}

			g.setColor(Color.BLACK);
			g.fillRect(5, 5, 130, 50);
			g.fillRect(getWidth() - 125, 5, 120, 50);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.BOLD, 16));
			g.drawString(" GEAR : " + car.gear, 10, 45);
			g.drawString(" SPEED: " + format("%.0f", car.speed), 10, 25);
			g.drawString(" SCORE: " + format("%.0f", car.score), getWidth() - 125, 25);
			g.drawString(" TIME : " + format("%.0f", timeRem / 40), getWidth() - 125, 45);
		}

	}

	class Car {
		int gear = 1;
		double accel = 0;
		int x;
		int y = 0;
		double speed = 0;
		double score = 0;
		int hit = 0;

		public Car() {
		}

		public void move(int height) {
			if (speed > 0) {
				if (x < 330 && keyControls[1] == 1)
					x += 4;
				if (x > 195 && keyControls[0] == 1)
					x -= 4;
			}
			gearCheck();
			speed += accel;
			if (speed > 100)
				speed = 100;
			if (speed < 0)
				speed = 0;
			score += (speed / 100);
			y = height - 200;
		}

		public void gearCheck() {
			if (gear == 1 && speed > 20)
				gear = 2;
			if (gear == 2 && speed > 40)
				gear = 3;
			if (gear == 3 && speed > 70)
				gear = 4;
			if (gear == 4 && speed < 60)
				gear = 3;
			if (gear == 3 && speed < 40)
				gear = 2;
			if (gear == 2 && speed < 20)
				gear = 1;
			if (keyControls[2] == 1)
				accel = (5 - (gear / 2.0)) / 2.0;
			if (keyControls[3] == 1)
				accel = (-(5 - gear));
			if (keyControls[2] == 0 && keyControls[3] == 0)
				accel = -0.05;
		}

		public void checkHit() {
			if (x < slowCar.x + 65) {
				if (y > slowCar.y + 140 || y < slowCar.y - 150)
					hit = 0;
				else {
					if (y < slowCar.y + 45)
						hit = 2;
					else
						hit = 1;
					speed = 0;
					slowCar.speed = 0;
				}
			}
			if (x + 65 > bigTruck.x) {
				if (y > bigTruck.y + 190 || y < bigTruck.y - 170)
					hit = 0;
				else {
					hit = 1;
					speed = 0;
					bigTruck.speed = 0;
				}
			}
		}
	}

	class SlowCar {
		int brake;
		int x = 195;
		double y;
		double speed;
		double yStart = 200;

		public SlowCar() {
			setSpeed();
			this.y = -yStart;
		}

		public void move(int height) {
			y += (car.speed - speed) / 10.0;
			if (y >= height + yStart) {
				y = -yStart;
				setSpeed();
			}
			if (y < -yStart) {
				y = height + yStart;
				setSpeed();
			}
		}

		public void setSpeed() {
			speed = 20 + Math.random() * 20;
		}
	}

	class CarOnComing {
		int x = 325;
		int y = 0;
		double speed;
		int yStart = 300;

		public CarOnComing() {
			setSpeed();
			this.y = -300;
		}

		public void move(int height) {

			y += (car.speed + speed) / 20.0;

			if (y > (height + yStart)) {
				y = -yStart - (int) (Math.random() * (yStart / 2));
				setSpeed();
			}
			if (y < -yStart) {
				y = height + yStart;
				setSpeed();
			}
		}

		public void setSpeed() {
			speed = 60 + (Math.random() * 20.0);
			y = -yStart;
		}
	}

	public void initialiseApp() {
		timeRem = gameLength;
		car.x = 195;
		penaltyTime = 100;
		slowCar.yStart = 200;
		bigTruck.yStart = 300;
		slowCar.y = slowCar.yStart;
		bigTruck.y = bigTruck.yStart;
		gameEnd = 0;
		car.score = 0;
		car.speed = 0;
	}
}