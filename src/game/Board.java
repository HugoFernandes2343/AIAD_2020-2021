package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Board extends JPanel {

	private ArrayList<Square> allSquares = new ArrayList<Square>();
	private ArrayList<Square> unbuyableSquares = new ArrayList<Square>(); // squares like "Go", "Chances" etc...
	
	public ArrayList<Square> getUnbuyableSquares(){
		return unbuyableSquares;
	}
	
	public ArrayList<Square> getAllSquares(){
		return allSquares;
	}
	
	public Square getSquareAtIndex(int location) {
		return allSquares.get(location);
	}

	public Board(int xCoord, int yCoord, int width, int height) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(xCoord, yCoord, width, height);
		this.setLayout(null);
		initializeSquares();
	}

	private void initializeSquares() {
		// TODO Auto-generated method stub
		String[] squareNames = {
				"Partida",
				"Campo Grande",
				"Caixa da Comunidade",
				"R. Faria Guimarães",
				"Av. Fernão Magalhães",
				"Al. Linhas de Torres",
				"Sorte",
				"Av. Nações Unidas",
				"Av. 24 de Julho",
				"Cadeia",
				"Av. Central",
				"Comp. de Electricidade",
				"R. Ferreira Borges",
				"Av. de Roma",
				"Av. da Boavista",
				"Caixa da Comunidade",
				"Av. da República",
				"R. Mouzinho da Silveira",
				"Estacionamento Livre",
				"R. de Santa Catarina",
				"Sorte",
				"Av. Infante Santo",
				"R. Júlio Diniz",
				"Praça da República",
				"Av. Fontes de Melo",
				"Comp. das Águas",
				"Rot. da Boavista",
				"Vá para a cadeia",
				"Av. da Liberdade",
				"R. dos Clérigos",
				"Caixa da Comunidade",
				"Av. Parque das Nações",
				"R. das Amoreiras ",
				"R. Augusta",
				"Sorte",
				"Rossio"
		};
		

		// squares on the top
		Square square00 = new Square(6,6,100,100,squareNames[0],135);
		this.add(square00);
		allSquares.add(square00);
		unbuyableSquares.add(square00);
		
		Square square01 = new Square(106,6,100,100,squareNames[1],180);
		this.add(square01);
		allSquares.add(square01);
		
		Square square02 = new Square(206,6,100,100,squareNames[2],180);
		this.add(square02);
		allSquares.add(square02);
		unbuyableSquares.add(square02);
		
		Square square03 = new Square(306,6,100,100,squareNames[3],180);
		this.add(square03);
		allSquares.add(square03);
		
		Square square04 = new Square(406,6,100,100,squareNames[4],180);
		this.add(square04);
		allSquares.add(square04);

		Square square05 = new Square(506,6,100,100,squareNames[5],180);
		this.add(square05);
		allSquares.add(square05);
		Square square06 = new Square(606,6,100,100,squareNames[6],180);
		this.add(square06);
		allSquares.add(square06);
		Square square07 = new Square(706,6,100,100,squareNames[7],180);
		this.add(square07);
		allSquares.add(square07);

		Square square08 = new Square(806,6,100,100,squareNames[8],180);
		this.add(square08);
		allSquares.add(square08);
		
		Square square09 = new Square(906,6,100,100,squareNames[9],-135);
		this.add(square09);
		allSquares.add(square09);
		unbuyableSquares.add(square09);

		Square square10 = new Square(906,106,100,100,squareNames[10],-135);
		this.add(square10);
		allSquares.add(square10);

		// squares on the right
		Square square11 = new Square(906,206,100,100,squareNames[11],-90);
		this.add(square11);
		allSquares.add(square11);
		
		Square square12 = new Square(906,306,100,100,squareNames[12],-90);
		this.add(square12);
		allSquares.add(square12);
		unbuyableSquares.add(square07);
		
		Square square13 = new Square(906,406,100,100,squareNames[13],-90);
		this.add(square13);
		allSquares.add(square13);
		
		Square square14 = new Square(906,506,100,100,squareNames[14],-90);
		this.add(square14);
		allSquares.add(square14);
		
		Square square15 = new Square(906,606,100,100,squareNames[15],-45);
		this.add(square15);
		allSquares.add(square15);
		unbuyableSquares.add(square15);

		Square square16 = new Square(906,706,100,100,squareNames[16],-135);
		this.add(square16);
		allSquares.add(square16);
		unbuyableSquares.add(square16);

		Square square17 = new Square(906,806,100,100,squareNames[17],-135);
		this.add(square17);
		allSquares.add(square17);
		unbuyableSquares.add(square17);

		// squares on the bottom
		Square square18 = new Square(906,906,100,100,squareNames[18],0);
		this.add(square18);
		allSquares.add(square18);
		
		Square square19 = new Square(806,906,100,100,squareNames[19],0);
		this.add(square19);
		allSquares.add(square19);
		unbuyableSquares.add(square19);
		
		Square square20 = new Square(706,906,100,100,squareNames[20],0);
		this.add(square20);
		allSquares.add(square20);
		
		Square square21 = new Square(606,906,100,100,squareNames[21],0);
		this.add(square21);
		allSquares.add(square21);
		
		Square square22 = new Square(506,906,100,100,squareNames[22],45);
		this.add(square22);
		allSquares.add(square22);
		unbuyableSquares.add(square22);

		Square square23 = new Square(406,906,100,100,squareNames[23],0);
		this.add(square23);
		allSquares.add(square23);

		Square square24 = new Square(306,906,100,100,squareNames[24],0);
		this.add(square24);
		allSquares.add(square24);

		Square square25 = new Square(206,906,100,100,squareNames[25],0);
		this.add(square25);
		allSquares.add(square25);

		Square square26 = new Square(106,906,100,100,squareNames[26],0);
		this.add(square26);
		allSquares.add(square26);
		
		// squares on the left
		Square square27 = new Square(6,906,100,100,squareNames[27],90);
		this.add(square27);
		allSquares.add(square27);
		
		Square square28 = new Square(6,806,100,100,squareNames[28],90);
		this.add(square28);
		allSquares.add(square28);
		
		Square square29 = new Square(6,706,100,100,squareNames[29],90);
		this.add(square29);
		allSquares.add(square29);
		unbuyableSquares.add(square29);
		
		Square square30 = new Square(6,606,100,100,squareNames[30],90);
		this.add(square30);
		allSquares.add(square30);

		Square square31 = new Square(6,506,100,100,squareNames[31],90);
		this.add(square31);
		allSquares.add(square31);

		Square square32 = new Square(6,406,100,100,squareNames[32],90);
		this.add(square32);
		allSquares.add(square32);

		Square square33 = new Square(6,306,100,100,squareNames[33],90);
		this.add(square33);
		allSquares.add(square33);

		Square square34 = new Square(6,206,100,100,squareNames[34],90);
		this.add(square34);
		allSquares.add(square34);
		unbuyableSquares.add(square34);

		Square square35 = new Square(6,106,100,100,squareNames[35],90);
		this.add(square35);
		allSquares.add(square35);

		// setting prices
		square01.setPrice(100);
		square03.setPrice(100);
		square04.setPrice(120);
		
		square06.setPrice(140);
		square08.setPrice(140);
		square09.setPrice(160);
		
		square11.setPrice(180);
		square13.setPrice(180);
		square14.setPrice(200);
		
		square16.setPrice(300);
		square17.setPrice(300);
		square19.setPrice(320);
		
		// setting rent prices
		square01.setRentPrice(6);
		square03.setRentPrice(6);
		square04.setRentPrice(8);
		
		square06.setRentPrice(10);
		square08.setRentPrice(10);
		square09.setRentPrice(12);
		
		square11.setRentPrice(14);
		square13.setRentPrice(14);
		square14.setRentPrice(16);
		
		square16.setRentPrice(26);
		square17.setRentPrice(26);
		square19.setRentPrice(28);
		
		
		

		JLabel lblMonopoly = new JLabel("MONOPOLY"){
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				AffineTransform aT = g2.getTransform();
				Shape oldshape = g2.getClip();
				double x = getWidth()/2.0;
				double y = getHeight()/2.0;
				aT.rotate(Math.toRadians(-35), x, y);
				g2.setTransform(aT);
				g2.setClip(oldshape);
				super.paintComponent(g);
			}
		};
		lblMonopoly.setForeground(Color.WHITE);
		lblMonopoly.setBackground(Color.RED);
		lblMonopoly.setOpaque(true);
		lblMonopoly.setHorizontalAlignment(SwingConstants.CENTER);
		lblMonopoly.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
		lblMonopoly.setBounds(179, 277, 263, 55);
		this.add(lblMonopoly);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}




}
