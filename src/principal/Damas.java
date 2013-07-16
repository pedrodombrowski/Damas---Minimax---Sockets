package principal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

/**
 * Janela principal da aplicação
 */
public class Damas extends JFrame implements ActionListener {
//teste de git
	/**
	 * ID Serial da classe
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Botões do menu
	 */
	private JMenuItem novo, sair;

	/**
	 * Inputs do ip e da porta do cliente
	 */
	private JTextField ip, cliente;

	/**
	 * Labels da janela
	 */
	private JLabel iplbl, clilbl, eulbl, oponentelbl;

	/**
	 * Tabuleiro do jogo
	 */
	private VisaoDoTabuleiro visao;

	/**
	 * Construtor
	 */
	public Damas() {
		visao = new VisaoDoTabuleiro(this, new Tabuleiro());
		setTitle("Damas - Servidor: " + visao.porta);
		getContentPane().add(visao, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JMenu menu = new JMenu("Arquivo");
		novo = new JMenuItem("Novo Jogo");
		novo.addActionListener(this);
		menu.addSeparator();
		sair = new JMenuItem("Sair");
		sair.addActionListener(this);
		JMenuBar bar = new JMenuBar();
		iplbl = new JLabel("IP: ");
		ip = new JTextField(visao.endereco);
		clilbl = new JLabel("Porta: ");
		cliente = new JTextField(visao.portaCliente + "");
		eulbl = new JLabel("   Eu: 0   ");
		oponentelbl = new JLabel("  Oponente: 0   ");

		ip.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				try {
					visao.endereco = ip.getText();
				} catch (Exception ex) {
					visao.endereco = "localhost";
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					visao.endereco = ip.getText();
				} catch (Exception ex) {
					visao.endereco = "localhost";
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				try {
					visao.endereco = ip.getText();
				} catch (Exception ex) {
					visao.endereco = "localhost";
				}
			}
		});

		cliente.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				try {
					visao.portaCliente = Integer.parseInt(cliente.getText());
				} catch (Exception ex) {
					visao.portaCliente = 8080;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					visao.portaCliente = Integer.parseInt(cliente.getText());
				} catch (Exception ex) {
					visao.portaCliente = 8080;
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				try {
					visao.portaCliente = Integer.parseInt(cliente.getText());
				} catch (Exception ex) {
					visao.portaCliente = 8080;
				}
			}
		});

		menu.add(novo);
		menu.add(sair);
		bar.add(menu);
		bar.add(iplbl);
		bar.add(ip);
		bar.add(clilbl);
		bar.add(cliente);
		bar.add(eulbl);
		bar.add(oponentelbl);
		setJMenuBar(bar);
	}

	/**
	 * Método main da aplicação
	 */
	public static void main(String args[]) {
		Damas c = new Damas();

		c.setSize(600, 645);
		c.setVisible(true);
	}

	/**
	 * Processa as mensagens do menu
	 */
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == sair) {
			System.exit(0);
		} else if (event.getSource() == novo) {
			visao.novoJogo();
		}
	}

	/**
	 * Retorna o label eu
	 * 
	 * @return label eu
	 */
	public JLabel getEulbl() {
		return eulbl;
	}

	/**
	 * Retorna o label Oponente
	 * 
	 * @return label oponente
	 */
	public JLabel getOponentelbl() {
		return oponentelbl;
	}
}