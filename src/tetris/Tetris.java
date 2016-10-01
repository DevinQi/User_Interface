package tetris;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.MouseInputAdapter;

/**
 * Created by bwbecker on 2016-09-19.
 */
public class Tetris extends JFrame {
    @Override
    public synchronized void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
    }

    private int t = 0;

    public Tetris(int fps, double speed, String sequence) {


        Tetrispiece t = new Tetrispiece(fps,speed,sequence);
        addMouseListener(t);
        addMouseWheelListener(t);
        addMouseMotionListener(t);
        addKeyListener(t);
        this.add(t);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 520);
        SwingUtilities.updateComponentTreeUI(this);
        this.setVisible(true);
        this.setResizable(false);


    }

}
class Tetrispiece extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
    int fps;
    double speed;
    String sequence = "";
    private Timer ddd = new Timer();
    private Timer fpss = new Timer();


    Tetrispiece(int fps,double speed,String Sequence){
        this.fps = fps;
        this.speed = speed;
        this.sequence = Sequence;
        newpiece();
        initialmap();
        ddd.schedule(new move_down(),(long)0,(long)(1000*speed/24));
        fpss.schedule(new fps_p(),0,(long)(1000/fps));


    }
    private class move_down extends TimerTask{
        @Override
        public void run() {
            down();
        }
    };

    private class fps_p extends TimerTask{
        @Override
        public void run() {
            repaint();
        }
    };

    //paint way
    private int way;

    private int beforeM;

    //Cheat
    private int cheatfactor = 0;

    //score
    private int score;
    //x,y represent the coordinate in the map
    private int x,y;

    //piecetype
    private int piecetype = 0;
    private int nextpiecetype = -1;

    //piece direction
    private int piecedir = 0;
    private int nextpiecedir = -1;

    // piece's color
    private  String  piececolor;

    //pause?
    private int paused = 1;

    //selected?
    private int select = 0;

    // block already fixed
    int[][] map = new int[12][25]; //with borders + 2

    //  random color arry
//    String[] colora = {
//        "#4169E1","#20B2AA","#F0E68C","#FF69B4","#FF6347","#6A5ACD","#FF7F50","#778899"
//    };


    /*//a piece
    int[][]p = new int[][] {       //initially -1 means haven't been showed on map
        {-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}
    };  */

    // all kinds of pieces
    final int[][][] piece= new int[][][]{
            //turn left - turn right +
            //I   0
            {{0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0},{0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0},
                    {0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0},{0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0}},
            //L   1
            {{0,1,0,0,0,1,0,0,0,1,1,0,0,0,0,0},{0,0,0,0,0,1,1,1,0,1,0,0,0,0,0,0},
                    {0,0,0,0,0,1,1,0,0,0,1,0,0,0,1,0},{0,0,0,0,0,0,1,0,1,1,1,0,0,0,0,0}},
            //J    2
            {{0,0,1,0,0,0,1,0,0,1,1,0,0,0,0,0},{0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,1,1,0,0,1,0,0,0,1,0,0},{0,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0}},
            //T    3
            {{0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,0},{0,0,0,0,0,0,1,0,0,1,1,0,0,0,1,0},
                    {0,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0},{0,1,0,0,0,1,1,0,0,1,0,0,0,0,0,0}},
            //O    4
            {{0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0},{0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0},
                    {0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0},{0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0}},
            //S    5
            {{0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0},{0,0,0,0,0,1,0,0,0,1,1,0,0,0,1,0},
                    {0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0},{0,1,0,0,0,1,1,0,0,0,1,0,0,0,0,0}},
            //Z    6
            {{0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0},{0,0,0,0,0,0,1,0,0,1,1,0,0,1,0,0},
                    {0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0},{0,0,1,0,0,1,1,0,0,1,0,0,0,0,0,0}}

    };


    //produce a new piece with random type
    public void newpiece(){
        select = 0;
        //System.out.println(this.sequence);
        if(this.sequence == "") {
            if(nextpiecetype == -1) {
                piecetype = (int) (Math.random() * 10000) % 7;
                nextpiecetype = (int) (Math.random() * 10000) % 7;
                System.out.println("Count");
            }else{
                piecetype = nextpiecetype;
                nextpiecetype = (int) (Math.random() * 10000) % 7;
            }

        }else{
            char c = this.sequence.charAt(0);
            switch (c){
                case 'I':
                    piecetype = 0;
                    break;
                case 'L':
                    piecetype = 1;
                    break;
                case 'J':
                    piecetype = 2;
                    break;
                case 'T':
                    piecetype = 3;
                    break;
                case 'O':
                    piecetype = 4;
                    break;
                case 'S':
                    piecetype = 5;
                    break;
                case 'Z':
                    piecetype = 6;
                    break;
                default:
                    System.out.println("Invalid type of piece " + c + "! Skiped ");
            }
            this.sequence = this.sequence.substring(1,this.sequence.length());
            char c1 = this.sequence.charAt(0);
            switch (c1){
                case 'I':
                    nextpiecetype = 0;
                    break;
                case 'L':
                    nextpiecetype = 1;
                    break;
                case 'J':
                    nextpiecetype = 2;
                    break;
                case 'T':
                    nextpiecetype = 3;
                    break;
                case 'O':
                    nextpiecetype = 4;
                    break;
                case 'S':
                    nextpiecetype = 5;
                    break;
                case 'Z':
                    nextpiecetype = 6;
                    break;
                default:
                    System.out.println("Invalid type of piece " + c + "! Skiped ");
            }

        }
        if(nextpiecedir == -1) {
            piecedir = (int) (Math.random() * 10000) % 4;
            nextpiecedir = (int) (Math.random() * 10000) % 4;
        }else{
            piecedir =nextpiecedir;
            nextpiecedir = (int) (Math.random() * 10000) % 4;
            //System.out.println("piecedir:" + piecedir +"   nextpiecedir: "+nextpiecedir);
        }
        //System.out.println("A : "+nextpiecedir);
        piececolor = "#696969";
        x=4;
        y=0;

        if(gameover()){
            initialmap();
            score = 0;
            JOptionPane.showMessageDialog(null, "GAME OVER");
        }
    }

    //initial map
    public void initialmap(){
        for(int i = 0;i<12;i++){
            for(int j =0; j<24;j++){
                map[i][j] = 0;  // 0 means white
            }
            map[i][24] = 2;
        }
        for(int i = 0; i <24; i++ ) {
            map[0][i] = 2;
            map[11][i] = 2;
        }
    }
    //check wetheris a valid move
    public boolean isvalid(int x,int y){
        //System.out.println(map[0][1]);
        for(int i = 0;i < 4;i++){
            for(int j = 0; j< 4; j++){
                //System.out.println("x :" + x + "    y:" + y);
                if((piece[piecetype][piecedir][i*4+j] == 1 && map[x+j][y+i]== 1 )||
                        (piece[piecetype][piecedir][i*4+j] == 1 && map[x+j][y+i]== 2 )){

                    return false;
                }
            }
        }
        return true;
    }

    //remove a line of block
    public void bingo(){
        int count = 0;
        for(int j = 0; j < 24; j++){
            for(int i = 1; i < 11; i++){
                if(map[i][j] == 1){
                    count++;
                    if(count == 10){
                        score += 10;
                        for (int b = j; b > 0 ;b--){
                            for (int a = 1; a < 10; a++){
                                map[a][b] = map[a][b-1];
                            }
                        }
                    }
                }
            }
            count = 0;
        }
    }

    //check wether gameover
    public boolean gameover(){
        if(!isvalid(x,y)) {
            return true;
        }else{
            return false;
        }
    }

    //////////////////////////////////////move methord/////////////////////////////////////////////

    //move left
    public void left(){
        //System.out.println("enter left");
        if(isvalid(x-1,y)){
            x--;

        }

        repaint();
    }

    //move right
    public void right(){
        if(paused == 1){
            return;
        }
        //System.out.println("enter right");
        if(isvalid(x+1,y)){
            //System.out.println(x);
            x++;
        }
        //System.out.println("????"+x);
        repaint();
    }

    //turn left
    public void tleft(){
        if(paused == 1){
            return;
        }
        int temp = piecedir;
        piecedir = ((piecedir-1) + 4) % 4;
        if(!isvalid(x,y)) {
            piecedir = temp;
        }
        repaint();
    }

    //turn right
    public void tright(){
        if(paused == 1){
            return;
        }
        int temp = piecedir;
        piecedir = (piecedir+1) % 4;
        if(!isvalid(x,y)) {
            piecedir = temp;
        }
        repaint();
    }

    //move down
    public void down(){
        if(paused == 1){
            return;
        }
        if(isvalid(x,y+1)){
            y++;
            repaint();
        }else {

            for(int i =0;i < 4;i++){
                for(int j =0 ; j <4; j++){
                    //System.out.println("x: "+ x + "j:" + j+"    y:"+ y + ", i:" + i + " , piece:" + piece[piecetype][piecedir][i*4+j]);
                    if( j+x < 0){
                        continue;
                    }
                    if(j+x > 10){
                        continue;
                    }
                    if(y + i >24){
                        continue;
                    }
                    if(map[x+j][y+i] == 0 && map[x+j][y+i] != 2){
                        map[x+j][y+i] = piece[piecetype][piecedir][i*4+j];
                    }
                }

            }
            /*for(int i =0;i < 4;i++){
                for(int j =0 ; j <4; j++){
                    if(map[x+i][y+j] != 0){
                        System.out.println("i:" + i + ", j:" + j + " , piece:" + piece[piecetype][piecedir][i*4+j]);

                    }
                }
            }*/
            bingo();
            repaint();
            newpiece();
        }
    }
    public void drop(){
        if(paused == 1){
            return;
        }
        while (isvalid(x,y+1)){
            y++;
        }

    }
    public void pause(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if(paused == 1){
            paused = 0;
            topFrame.setResizable(false);
        }else{
            paused = 1;
            topFrame.setResizable(true);
        }
    }


    ///////////////////////////////////////////Paint Methord//////////////////////////////////////

    public void paintComponent(Graphics g){
        if(way == 0){
            super.paintComponent(g);
            super.setBackground(Color.decode("#B0C4DE"));
            Font trb = new Font("TimesRoman", Font.BOLD, 25);
            g.setFont(trb);
            g.setColor(Color.decode("#00008B"));
            g.drawString("Tetrix Game",180,200);
            Font trb1 = new Font("TimesRoman", Font.BOLD, 18);
            g.setFont(trb1);
            g.drawString("Press Enter to Start",170,250);
            g.drawString("Press M to Open Menu",160,274);
        }
        else if(way == 1){
            super.paintComponent(g);
            super.setBackground(Color.decode("#FFFFE0"));
            //g.drawOval(x*20, y*20, 3, 3);
            for (int j = 0; j < 16; j++) {
                if (piece[piecetype][piecedir][j] == 1) {
                    g.setColor(Color.decode(piececolor));
                    g.fillRoundRect((j % 4 + x) * 20, (j / 4 + y) * 20, 19, 19, 5, 5);
                    //g.fillRect((j % 4 + x +1) * 20, (j / 4 + y) * 20, 19, 19);
                }
            }
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < 25; j++) {
                    if (map[i][j] == 2) {
                        g.setColor(Color.decode("#B0C4DE"));
                        g.fillRoundRect(i * 20, j * 20, 19, 19, 5, 5);
                    }
                    if (map[i][j] == 1) {
                        //g.setColor(Color.decode("#8B4513"));
                        g.setColor(Color.decode(piececolor));
                        g.fillRoundRect(i * 20, j * 20, 19, 19, 5, 5);
                    }
                }
            }
            g.setColor(Color.decode("#4B0082"));
            Font trb = new Font("TimesRoman", Font.BOLD, 22);
            g.setFont(trb);
            String S = "Score:   " + Integer.toString(score);
            g.drawString(S, 330, 100);

            Font trb2 = new Font("TimesRoman", Font.BOLD, 20);
            g.setFont(trb2);
            if(cheatfactor == 0){
                g.drawString("Press S to open Cheat", 270,260);
                g.drawString("Press M to open Menu",270,290);
            }else {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (piece[nextpiecetype][nextpiecedir][i * 4 + j] == 1) {
                            g.fillRoundRect(j * 20 + 330, i * 20 + 230, 19, 19, 10, 10);
                        }
                    }
                }
            }




//        g.drawString("抵制不良游戏，拒绝盗版游戏。",280,200);
//        g.drawString("注意自我保护，谨防上当受骗。",280,220);
//        g.drawString("适度游戏益脑，沉迷游戏伤身",280,240);
//        g.drawString("合理安排时间，享受健康生活。",280,260);
        }else if(way == 2){
            super.paintComponent(g);
            super.setBackground(Color.decode("#B0C4DE"));

            Font L = new Font("TimesRoman", Font.BOLD, 20);
            g.setFont(L);
            g.setColor(Color.decode("#DC143C"));
            g.drawString("KeyBoard:",40,20);
            Font trb = new Font("TimesRoman", Font.BOLD, 17);
            g.setFont(trb);
            g.setColor(Color.decode("#00008B"));

            g.drawString("Move left:     Left Arrow  or Numpad 4",50,50);
            g.drawString("Move right:    Right Arrow or Numpad 6",50,80);
            g.drawString("Drop:          Space Bar   or Numpad 8",50,110);
            g.drawString("Rotate Right:  Up Arrow, X or Numpad 1,5,9",50,140);
            g.drawString("Rotate Left:   Control, Z  or Numpad 3,7",50,170);
            g.drawString("Pause          P",50,200);

            g.setFont(L);
            g.setColor(Color.decode("#DC143C"));
            g.drawString("Mouse:",40,230);
            g.setFont(trb);
            g.setColor(Color.decode("#00008B"));
            g.drawString("MousePress(unselect): Select for further manipulation",50,260);
            g.drawString("MouseMotion:          Selected piece follows the mouse",50,290);
            g.drawString("MouseWheel:           Rotate the selected piece.",50,320);
            g.drawString("MousePress (selected):Rotate the selected piece.",50,350);

            g.setFont(L);
            g.setColor(Color.decode("#DC143C"));
            g.drawString("Cheating:",40,380);
            g.setFont(trb);
            g.setColor(Color.decode("#00008B"));
            g.drawString("Press S to open Cheating:  Showing next piece",50,410);



            //g.drawString("");



        }


    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getExtendedKeyCode()){
            case KeyEvent.VK_DOWN:
                down();
                break;
            case KeyEvent.VK_LEFT:
                left();
                break;
            case KeyEvent.VK_RIGHT:
                right();
                break;
            case KeyEvent.VK_UP:
                tright();
                break;
            case KeyEvent.VK_CONTROL:
                tleft();
                break;
            case KeyEvent.VK_NUMPAD4:
                left();
                break;
            case KeyEvent.VK_NUMPAD6:
                right();
                break;
            case KeyEvent.VK_NUMPAD1:
                tright();
                break;
            case KeyEvent.VK_NUMPAD5:
                tright();
                break;
            case KeyEvent.VK_NUMPAD9:
                tright();
                break;
            case KeyEvent.VK_NUMPAD3:
                tleft();
                break;
            case KeyEvent.VK_NUMPAD7:
                tleft();
                break;
            case KeyEvent.VK_Z:
                tleft();
                break;
            case KeyEvent.VK_SPACE:
                drop();
                break;
            case KeyEvent.VK_NUMPAD8:
                drop();
                break;
            case KeyEvent.VK_P:
                pause();
                break;
            case KeyEvent.VK_ENTER:
                if(paused == 1) {
                    pause();
                }
                way =1;
                break;
            case KeyEvent.VK_M:
                if(way == 0 ) {
                    beforeM = 0;
                    way = 2;
                }else if(way == 1){
                    beforeM = 1;
                    way =2;
                    pause();
                    break;
                }else{
                    way = 0;
                    if(beforeM ==1){
                        pause();
                    }
                    way = beforeM;
                }
                break;
            case KeyEvent.VK_S:
                if(cheatfactor == 0){
                    cheatfactor =1;
                }else {
                    cheatfactor = 0;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int xp = e.getX();
        int yp = e.getY();
        if(select == 0){
            //System.out.println("xp is"+xp+"  x is" + x+"  yp is"+yp+"   y is" + y );
            if((xp - x*20 > 0 && xp-x*20 < 80)&&(yp-y*20 > 0 && yp-y*20 <80)){
                select =1;
                System.out.println("selected !");
            }
        }else {
            if ((xp - x*20 > 0 && xp - x*20 < 80) && (yp - y*20 > 0 && yp - y*20 < 80)) {
                drop();
            } else {
                select = 0;
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(select == 1){
            java.awt.Point p = getMousePosition();
            int cx = (x + 2)*20;
            if(p.getX() > cx+20){
                right();
            }else if(p.getX()< cx-20){
                left();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            tright();
        }else if(notches > 0){
            tleft();
        }
    }



    /*private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            down();
        }
    }*/
}
