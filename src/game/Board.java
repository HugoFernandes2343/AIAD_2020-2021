package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Board extends JPanel {

	private final ArrayList<Square> allSquares = new ArrayList<>();
	private final ArrayList<Square> unbuyableSquares = new ArrayList<>();
	
	public List<Square> getUnbuyableSquares(){
		return unbuyableSquares;
	}
	
	public List<Square> getAllSquares(){
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
		String sorte = "Sorte";
		String caixa = "Caixa da Comunidade";

		Square square00 = new Square(6,6,100,100,"Partida",135);
		this.add(square00);
		allSquares.add(square00);
		unbuyableSquares.add(square00);

		Square square01 = new Square(106,6,100,100,"Campo Grande",180,100,90,0.68);
		this.add(square01);
		allSquares.add(square01);
		
		Square square02 = new Square(206,6,100,100,caixa,180);
		this.add(square02);
		allSquares.add(square02);
		unbuyableSquares.add(square02);
		
		Square square03 = new Square(306,6,100,100,"R. Faria Guimarães",180,100,90,0.68);
		this.add(square03);
		allSquares.add(square03);
		
		Square square04 = new Square(406,6,100,100,"Av. Fernão Magalhães",180,120,100,0.7);
		this.add(square04);
		allSquares.add(square04);

		Square square05 = new Square(506,6,100,100,"Al. Linhas de Torres",180,140,120,0.73);
		this.add(square05);
		allSquares.add(square05);

		Square square06 = new Square(606,6,100,100,sorte,180);
		this.add(square06);
		allSquares.add(square06);
		unbuyableSquares.add(square06);

		Square square07 = new Square(706,6,100,100,"Av. Nações Unidas",180,140,120,0.73);
		this.add(square07);
		allSquares.add(square07);

		Square square08 = new Square(806,6,100,100,"Av. 24 de Julho",180,160,130,0.75);
		this.add(square08);
		allSquares.add(square08);

		Square square09 = new Square(906,6,100,100,"Cadeia",-135);
		this.add(square09);
		allSquares.add(square09);
		unbuyableSquares.add(square09);

		Square square10 = new Square(906,106,100,100,"Av. Central",-135,180,150,0.78);
		this.add(square10);
		allSquares.add(square10);

		// squares on the right
		Square square11 = new Square(906,206,100,100,"Imposto Capitais",-90);
		this.add(square11);
		allSquares.add(square11);
		unbuyableSquares.add(square11);
		
		Square square12 = new Square(906,306,100,100,"R. Ferreira Borges",-90,180,150,0.78);
		this.add(square12);
		allSquares.add(square12);
		
		Square square13 = new Square(906,406,100,100,"Av. de Roma",-90,200,160,0.80);
		this.add(square13);
		allSquares.add(square13);

		Square square14 = new Square(906,506,100,100,"Av. da Boavista",-90,300,180,0.83);
		this.add(square14);
		allSquares.add(square14);
		
		Square square15 = new Square(906,606,100,100,caixa,-45);
		this.add(square15);
		allSquares.add(square15);
		unbuyableSquares.add(square15);

		Square square16 = new Square(906,706,100,100,"Av. da República",-135,300,180,0.83);
		this.add(square16);
		allSquares.add(square16);
		unbuyableSquares.add(square16);

		Square square17 = new Square(906,806,100,100,"R. Mouzinho da Silveira",-135,320,190,0.85);
		this.add(square17);
		allSquares.add(square17);

		// squares on the bottom
		Square square18 = new Square(906,906,100,100,"Estacionamento Livre",0);
		this.add(square18);
		allSquares.add(square18);
		unbuyableSquares.add(square18);
		
		Square square19 = new Square(806,906,100,100,"R. de Santa Catarina",0,340,210,0.88);
		this.add(square19);
		allSquares.add(square19);

		Square square20 = new Square(706,906,100,100,sorte,0);
		this.add(square20);
		allSquares.add(square20);
		unbuyableSquares.add(square20);
		
		Square square21 = new Square(606,906,100,100,"Av. Infante Santo",0,340,210,0.88);
		this.add(square21);
		allSquares.add(square21);
		
		Square square22 = new Square(506,906,100,100,"R. Júlio Diniz",45,360,220,0.90);
		this.add(square22);
		allSquares.add(square22);

		Square square23 = new Square(406,906,100,100,"Praça da República",0,400,240,0.93);
		this.add(square23);
		allSquares.add(square23);

		Square square24 = new Square(306,906,100,100,"Av. Fontes de Melo",0,400,240,0.93);
		this.add(square24);
		allSquares.add(square24);

		Square square25 = new Square(206,906,100,100,"Imposto de Luxo",0);
		this.add(square25);
		allSquares.add(square25);
		unbuyableSquares.add(square25);

		Square square26 = new Square(106,906,100,100, "Rot. da Boavista",0,420,250,0.95);
		this.add(square26);
		allSquares.add(square26);
		
		// squares on the left
		Square square27 = new Square(6,906,100,100,"Vá para a cadeia",90);
		this.add(square27);
		allSquares.add(square27);
		unbuyableSquares.add(square27);
		
		Square square28 = new Square(6,806,100,100,	"Av. da Liberdade",90,440,270,0.78);
		this.add(square28);
		allSquares.add(square28);
		
		Square square29 = new Square(6,706,100,100,"R. dos Clérigos",90,440,270,0.78);
		this.add(square29);
		allSquares.add(square29);
		
		Square square30 = new Square(6,606,100,100,caixa,90);
		this.add(square30);
		allSquares.add(square30);
		unbuyableSquares.add(square30);

		Square square31 = new Square(6,506,100,100, "Av. Parque das Nações",90,460,280,0.80);
		this.add(square31);
		allSquares.add(square31);

		Square square32 = new Square(6,406,100,100,"R. das Amoreiras",90,500,300,0.83);
		this.add(square32);
		allSquares.add(square32);

		Square square33 = new Square(6,306,100,100,"R. Augusta",90,500,300,0.83);
		this.add(square33);
		allSquares.add(square33);

		Square square34 = new Square(6,206,100,100,sorte,90);
		this.add(square34);
		allSquares.add(square34);
		unbuyableSquares.add(square34);

		Square square35 = new Square(6,106,100,100,"Rossio",90,600,350,0.85);
		this.add(square35);
		allSquares.add(square35);


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
		lblMonopoly.setFont(new Font("Lucida Grande", Font.PLAIN, 72));
		lblMonopoly.setBounds(253, 400, 500, 200);
		this.add(lblMonopoly);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}




}
