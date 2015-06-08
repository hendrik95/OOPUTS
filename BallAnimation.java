import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import java.awt.geom.Line2D;
import java.awt.image.*;
import java.awt.*;
import javax.swing.JFrame;
import java.util.*;
import javax.swing.*;
//import java.awt.geom.AffineTransform;
import java.awt.geom.*;
//import java.applet.*;



public class BallAnimation extends JFrame implements Runnable
{
	private Thread animator;
	private Graphics g;
	private BufferedImage dbImage;
	
	private Wall wall1, wall2, wall3, wall4;
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	
	private int jumlahBola = 16;
	
	private int mainBallIndex;
	private double v,vMax;
	private double incV;
	private int mousePressedX;
	private int mousePressedY;
	private Line2D lineDirection;
	private boolean isPressed;//,isMoving;
	private int s = 0;
	private int boardWidth = 1010;
	private int boardHeight = 400;
	private int pos;
	private double[] posX = new double[16];
	private double[] posY = new double[16];
	private double r;
	private double powerWidth;
	private int point1 = 0;
	private int point2 = 0;
	private int temp = 0;
	private int pos1x = 570;
	private int pos2x = 583;
	private boolean isPlayer1 = true;
	
	AffineTransform backup;
	AffineTransform trans;
	private double angle;
	
	private Hole hole1,hole2,hole3,hole4,hole5,hole6;
	
	private int FPS = 50;
	private int targetTime = 1000 / FPS;
	
	public static HashMap<String, AudioPlayer> sfx;
	public boolean game = true;
	
	public BallAnimation()
	{
		//configuring the main frame
		//setExtendedState(MAXIMIZED_BOTH);         // full screen
		setSize(1150,800);
		setResizable(false);
		setVisible(true);                         // can be seen
		setDefaultCloseOperation(EXIT_ON_CLOSE);  // end by exit
		//isMoving = false;
		//image where everything is drawn
		dbImage = (BufferedImage) createImage(getWidth(), getHeight());
		g = dbImage.getGraphics();
						
		//creating balls on the canvas with random position and color
		r = (500/18.772/2)+0.5;
		Random randomObj = new Random();
		pos = randomObj.nextInt(15) + 0;
		posX[15] = 147+boardWidth/4;
		posY[15] = 240+boardHeight/2;
		
		posX[1] = posX[15]-r*5/3;
		posY[1] = posY[15]-r;
		posX[2] = posX[1];
		posY[2] = posY[1]+r*2;
		
		posX[3] = posX[1]-r*5/3;
		posY[3] = posY[1]-r;
		posX[4] = posX[3];
		posY[4] = posY[3]+r*2;
		posX[5] = posX[4];
		posY[5] = posY[4]+r*2;
		
		posX[6] = posX[3]-r*5/3;
		posY[6] = posY[3]-r;
		posX[7] = posX[6];
		posY[7] = posY[6]+r*2;
		posX[8] = posX[7];
		posY[8] = posY[7]+r*2;
		posX[9] = posX[8];
		posY[9] = posY[8]+r*2;
		
		posX[10] = posX[6]-r*5/3;
		posY[10] = posY[6]-r;
		posX[11] = posX[10];
		posY[11] = posY[10]+r*2;
		posX[12] = posX[11];
		posY[12] = posY[11]+r*2;
		posX[13] = posX[12];
		posY[13] = posY[12]+r*2;
		posX[14] = posX[13];
		posY[14] = posY[13]+r*2;
		
		posX[0] = 147 + (2*boardWidth/3);
		posY[0] = posY[15];
		balls.add(new Ball(posX[0] ,
							posY[0], 
							500/18.772/2, 
							0, 
							0, 
							new Color(255,255,255), 
							5));	
		for(int i=1; i<jumlahBola;i++)
		{	
			balls.add(new Ball(posX[i] ,
							posY[i], 
							500/18.772/2, 
							0, 
							0, 
							new Color(randomObj.nextInt(256),randomObj.nextInt(256),randomObj.nextInt(256)), 
							5));	
		
		}
		
		//creating a line which will be used to indicate the direction
		lineDirection = new Line2D.Double(balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY(),
									balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY());

		
		
		//adding listener to listen and react when the mouse is moved (adjusting lineDirection position)		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub				
				lineDirection.setLine(lineDirection.getX1(), lineDirection.getY1(), arg0.getX(), arg0.getY());
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//initializing the velocity (and its increment) used when the user click the mouse 
		v = 0;
		vMax = 25;
		incV = 0.3;
		isPressed = false;
		addMouseListener(new MouseListener() {			
			@Override			
			public void mouseReleased(MouseEvent arg0) {
	
				if(!isMoving()) {
					sfx.get("hit").play();
					isPressed = false;
					//animator.sleep(10);
					//calculate new vx and vy for the main ball (indicated by mainBallIndex)
					int centerX, centerY;
					double centerV;
					
					//calculate the vector between the main ball and mouse pointer
					centerX = mousePressedX - (int)balls.get(mainBallIndex).getX();
					centerY = mousePressedY - (int)balls.get(mainBallIndex).getY();
					centerV = Math.sqrt(centerX*centerX + centerY*centerY);
					
					//calculate the velocity of the main ball based on how long the user pressed the mouse
					double vx, vy;
					vx = v/centerV*centerX;
					vy = v/centerV*centerY;
					balls.get(mainBallIndex).setDx(vx);
					balls.get(mainBallIndex).setDy(-vy);
					
					//reset the v for the next input
					v = 0;
					
					//if(!isMoving()) isPlayer1 = !isPlayer1;
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(!isMoving()) {
					mousePressedX = arg0.getX();
					mousePressedY = arg0.getY();				
					isPressed = true;
					lineDirection.setLine(lineDirection.getX1(), lineDirection.getY1(), arg0.getX(), arg0.getY());
					
					
					
					//if(isPlayer1) point1++;
					//else point2++;
					//isPlayer1 = !isPlayer1;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		double x3, y3 , x4, y4;
		//int sizewidth = 500;
		wall1 = new Wall(147, 240, boardWidth, 240);
		x4 = wall1.getX1() + boardHeight * wall1.normalX();
		y4 = wall1.getY1() - boardHeight * wall1.normalY();
	
		x3 = wall1.getX2() + boardHeight * wall1.normalX();
		y3 = wall1.getY2() - boardHeight * wall1.normalY();
		
		wall2 = new Wall(wall1.getX2(), wall1.getY2(), x3, y3);
		wall3 = new Wall(x3, y3, x4, y4);
		wall4 = new Wall(x4,y4,wall1.getX1(), wall1.getY1());	
		
		hole1 = new Hole(577,147+90,(500/18.772/2)*1.51);
		hole2 = new Hole(137,140+90,(500/18.772/2)*1.51);
		hole3 = new Hole(1018,140+90,(500/18.772/2)*1.51);
		hole4 = new Hole(137,559+90,(500/18.772/2)*1.51);
		hole5 = new Hole(577,554+90,(500/18.772/2)*1.51);
		hole6 = new Hole(1018,559+90,(500/18.772/2)*1.51);
		////////////sound
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("wall", new AudioPlayer("/sound/wall.wav"));
		sfx.put("hit", new AudioPlayer("/sound/hit.wav"));
		sfx.put("hole", new AudioPlayer("/sound/hole.wav"));
		sfx.put("ball", new AudioPlayer("/sound/ball.wav"));
		
		
		animator = new Thread(this);
		animator.start();
	}
	
	public void run() {
		long startTime;
		long urdTime;
		long waitTime;
		while(true)
		{	
			startTime = System.nanoTime();
			if(game) update();
			render();
			paintScreen();
			urdTime = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - urdTime;
			
			try
			{
				animator.sleep(waitTime);
			}
			catch(Exception ex)
			{
			}
		}
	}
	
	public boolean isMoving()
	{
		for(int i=0; i<jumlahBola; i++)
		{
			if(!balls.get(i).checkStop()) return true;
		}
		return false;
	}
	
	
	public void detectHoles(int i)
	{
		if(balls.get(i).getV() < 12)
		{
			if(i == 0)
			{
				sfx.get("hole").play();
				//balls.get(i).setX(posX[0]);
				//balls.get(i).setY(posY[0]);
				balls.get(i).setX(pos1x+8);
				balls.get(i).setY(120);
				balls.get(i).setDx(0);
				balls.get(i).setDy(0);
			}
			else
			{
				sfx.get("hole").play();
				//if(isPlayer1) point1++;
				//else point2++;
				
				if(isPlayer1) {
					point1++;
					balls.get(i).setX(pos1x-point1*2*(r+2));
					balls.get(i).setY(159);
				}
				else {
					point2++;
					balls.get(i).setX(pos2x+point2*2*(r+2));
					balls.get(i).setY(159);
				}
				temp = 0;
				//isPlayer1 = !isPlayer1;
				balls.get(i).setDx(0);
				balls.get(i).setDy(0);
			}
		}
		
		if(balls.get(i).getY() <= wall1.getY1()) {
			if(hole1.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole1.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() <= hole2.getX()  && balls.get(i).getY() <= hole2.getY() ) {
			if(hole2.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole2.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() >= hole3.getX()  && balls.get(i).getY() <= hole3.getY()) {
			if(hole3.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole3.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() <= hole4.getX()  && balls.get(i).getY() >= hole4.getY() ) {
			if(hole4.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole4.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getY() >= wall3.getY1()) {
			if(hole5.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole5.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		else if(balls.get(i).getX() >= hole6.getX()  && balls.get(i).getY() >= hole6.getY() ) {
			if(hole6.distance(balls.get(i).getX(),balls.get(i).getY()) + balls.get(i).getR() + 2>= hole6.getR())
			{
					balls.get(i).setDy(-(balls.get(i).getDy()));
					balls.get(i).setDx(-(balls.get(i).getDx()));
			}
		}
		
	}
	
	public void update()
	{		
		
		if(isPressed)
		{	
			if(temp == 0) temp = 1;
		}
		if(temp == 1){
			if(isMoving()) temp = 2;
		}
		if(temp == 2){
			if(!isMoving()){
				if(balls.get(0).getY() < wall1.getX1()) {
					balls.get(0).setX(posX[0]);
					balls.get(0).setY(posY[0]);
				}
				temp = 3;
			}
		}
		if(temp == 3) {
			isPlayer1 = !isPlayer1;
			temp = 0;
		}
		
		
		
		for(int i=0; i<balls.size(); i++)
		{
			balls.get(i).speed();
			if(balls.get(i).getX() >= hole1.getX() - hole1.getR() && balls.get(i).getX() <= hole1.getX() + hole1.getR()
				&& balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR() && balls.get(i).getY() >= hole1.getY() - hole1.getR())
			{
				detectHoles(i);
			}
			else if(balls.get(i).getX() <= wall1.getX1() + 2*balls.get(i).getR() && balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR()
				&& balls.get(i).getY() >= hole2.getY() - hole2.getR() )
			{
				detectHoles(i);
			}
			else if(balls.get(i).getX() >= wall1.getX2() - 2*balls.get(i).getR() && balls.get(i).getY() <= wall1.getY1() + 2*balls.get(i).getR()
					&& balls.get(i).getY() >= hole3.getY() - hole3.getR() )
			{
				detectHoles(i);
			}
			else if(balls.get(i).getX() <= wall4.getX1() + 2*balls.get(i).getR() && balls.get(i).getY() >= wall4.getY1() - 2*balls.get(i).getR()
					&& balls.get(i).getY() <= hole4.getY() + hole4.getR() )
			{
				detectHoles(i);
			}
			else if(balls.get(i).getX() >= hole5.getX() - hole5.getR() && balls.get(i).getX() <= hole5.getX() + hole5.getR() 
					&& balls.get(i).getY() >= wall3.getY1() - 2*balls.get(i).getR() && balls.get(i).getY() >= hole5.getY() - hole5.getR() )
			{
				detectHoles(i);
			}
			else if(balls.get(i).getX() >= wall3.getX1() - 2*balls.get(i).getR() && balls.get(i).getY() >= wall3.getY1() - 2*balls.get(i).getR()
					&& balls.get(i).getY() <= hole6.getY() + hole6.getR())
			{
				detectHoles(i);
			}
			else balls.get(i).detect(wall1, wall2, wall3, wall4);
			balls.get(i).detectBall(balls);
		}
		lineDirection.setLine(balls.get(mainBallIndex).getX(), balls.get(mainBallIndex).getY(), lineDirection.getX2(), lineDirection.getY2());
	
		if(isPressed)
		{
			v += incV;
			if(v >= vMax) v = vMax;
		}	
		
		powerWidth = v/vMax * 390;
		double deltaX = lineDirection.getX2() - lineDirection.getX1();
		double deltaY = lineDirection.getY2() - lineDirection.getY1();
		angle = Math.atan2(deltaY,deltaX) * 180 / Math.PI;
		
		if(point1 + point2 == 15) game = false;
		
		
	}
	public void render()
	{
		g.setColor(Color.WHITE);
		g.fillRect(0,0, getWidth(), getHeight());
		
		ImageIcon background = new ImageIcon("pic/background.png");
		g.drawImage(background.getImage(), 0, 0, 1150, 800, null);
		
		ImageIcon logo = new ImageIcon("pic/logo.png");
		g.drawImage(logo.getImage(), 502, 31, 150, 150, null);
		
		
		ImageIcon table = new ImageIcon("pic/table.png");
		g.drawImage(table.getImage(), 79, 82+90, 1000, 535, null);
		
		ImageIcon bar = new ImageIcon("pic/bar.png");
		g.drawImage(bar.getImage(), pos1x+10, 130, -500, 60, null);
		g.drawImage(bar.getImage(), pos2x-10, 130, 500, 60, null);
		
		ImageIcon player = new ImageIcon("pic/player.png");
		g.drawImage(player.getImage(), pos1x-130, 58, -200, 70, null);
		g.drawImage(player.getImage(), pos2x+130, 58, 200, 70, null);
		
		g.setColor(new Color(255,255,10,200));
		g.setFont(new Font("Verdana",Font.PLAIN, 25));
		//if(isPlayer1)
		g.drawString("Player 1",pos1x-281,100);
		//else
		g.drawString("Player 2",pos2x+179,100);
		
		ImageIcon black = new ImageIcon("pic/black.png");
		if(isPlayer1) g.drawImage(black.getImage(), pos2x+130, 58, 200, 70, null);
		else g.drawImage(black.getImage(), pos1x-130, 58, -200, 70, null);
		
		ImageIcon power = new ImageIcon("pic/power.png");
		g.drawImage(power.getImage(), 140, 710, 400, 40, null);
		
		g.setColor(new Color(0,0,0,180));
		g.fillRect(545 - 390+ (int) powerWidth, 716, 368-(int) powerWidth, 27);
		
		g.setColor(new Color(255,255,100,200));
		g.setFont(new Font("Razer Header Regular",Font.BOLD, 35));
		g.drawString("POWER",165,780);
		
		/*hole1.draw((Graphics2D)g);
		hole2.draw((Graphics2D)g);
		hole3.draw((Graphics2D)g);
		hole4.draw((Graphics2D)g);
		hole5.draw((Graphics2D)g);
		hole6.draw((Graphics2D)g);*/
		
		for(int i=0; i<balls.size(); i++)
		{
			balls.get(i).draw(g);
		}
		//wall1.draw(g);
		//wall2.draw(g);
		//wall3.draw(g);
		//wall4.draw(g);
		
		
		
		
		
		
		
		int stick1x = 70;
		int stick2x = 1120;
		int stickx;
		if(isPlayer1) stickx = stick2x;
		else stickx = stick1x;
		
		Graphics2D g2 = (Graphics2D) g;
		ImageIcon stick = new ImageIcon("pic/stick.png");
		backup = g2.getTransform();
		trans = new AffineTransform();
		trans.rotate( Math.toRadians(90), stickx,155);

		g2.transform( trans );
		g2.drawImage(stick.getImage(), stickx, 155, 500, 35, null);
		g2.setTransform( backup );
		
		if(!isMoving()){
			drawStick((Graphics2D)g);
		}
		
	
		//if(isPressed) 
		//g.fillRect(147, 150, boardWidth,boardHeight);
		
		if(game == false){
			//g.setColor(new Color(0,0,0,160));
			//g.fillRect(0, 290, 1200, 150);
			//ImageIcon black = new ImageIcon("pic/black.png");
			g.drawImage(black.getImage(), -23, 295, 1200, 150, null);
			g.setColor(new Color(255,100,60,190));
			g.setFont(new Font("Razer Header Regular", Font.BOLD  , 100));
			FontMetrics font = g.getFontMetrics();
			String s;
			if(point1 > point2) s = new String("Player 1 wins!!");
			else if(point1 == point2) s = new String("Draw!!");
			else s = new String("Player 2 wins!!");
			g.drawString(s, (1200 - font.stringWidth(s))/2, 800/2);
		}
	}
	
	public void drawStick(Graphics2D g)
	{
		ImageIcon stick = new ImageIcon("pic/stick.png");
		backup = g.getTransform();
		trans = new AffineTransform();
		trans.rotate( Math.toRadians(180+angle), (int)balls.get(0).getX(),(int)balls.get(0).getY());

		g.transform( trans );
		g.drawImage(stick.getImage(), (int)(balls.get(0).getX()+ r + powerWidth/2), (int)(balls.get(0).getY() - 18), 500, 35, null);
		g.setTransform( backup );
		
		if(lineDirection != null)
			{
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(new Color(255,255,255,200));
				g2.setStroke(new BasicStroke(3));
				g.drawLine((int)lineDirection.getX1(), 
						(int)lineDirection.getY1(), 
						(int)lineDirection.getX2(), 
						(int)lineDirection.getY2());	
			}
	}
	
	public void paintScreen()
	{
		Graphics frameGraphics = getGraphics();
		frameGraphics.drawImage(dbImage, 0, 0, null);
	}
	
	public static void main(String[] args)
	{
		BallAnimation animasi = new BallAnimation();
	}
	
}
