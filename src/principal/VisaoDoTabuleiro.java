package principal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;

/**
 * Classe que representa o tabuleiro e processa os eventos do utilizador.
 */
public class VisaoDoTabuleiro extends JPanel {

	/**
	 * ID Serial da classe
	 */
	private static final long serialVersionUID = 1L;

	int porta, portaCliente, eu, oponente;
	byte movimento[];
	String endereco;
	Servidor servidor;
	Cliente cliente;
	Serv serv;
	ServStart servStart;
	boolean iniciado = false, servidorConectado = false, clienteConectado = false;

	/**
	 * Tabuleiro que contem os dados apresentados por BoardView
	 */
	public Tabuleiro tabuleiro;

	/**
	 * Posicao do canto superior esquerdo.
	 */
	int inicioX;
	int inicioY;

	/**
	 * Largura das casas
	 */
	int tamanhoDaCasa;

	/**
	 * Casas selecionadas, o primeiro elemento e' a peca selecionada
	 */
	Lista selecionada;

	/**
	 * Computador
	 */
	Ai ai;

	/**
	 * Dimensao da peca
	 */
	private static final int TAMANHO = 0;

	/**
	 * Janela onde o tabuleiro se encontra
	 */
	Damas pai;

	Thread tServStart, tServ;

	/**
	 * Construtor
	 * 
	 * @param pai
	 *            Componente onde o tabuleiro vai ser desenhado
	 * @param b
	 *            Classe que guarda o estado do tabuleiro
	 */
	public VisaoDoTabuleiro(Damas pai, Tabuleiro b) {
		movimento = new byte[6];
		porta = 8080;
		portaCliente = 8080;
		endereco = "192.168.1.161";
		// 200.160.138.81
		selecionada = new Lista();
		tabuleiro = b;
		this.pai = pai;
		ai = new Ai(tabuleiro);
		try {
			servidor = new Servidor(porta);
		} catch (Exception e) {

		}

		servStart = new ServStart(this);
		this.tServStart = new Thread(servStart);
		this.tServStart.start();
	}

	/**
	 * Devolve o tabuleiro associado
	 */
	public Tabuleiro getTabuleiro() {
		return tabuleiro;
	}

	/**
	 * Come�a um novo jogo como as vermelhas
	 */
	@SuppressWarnings("deprecation")
	public void novoJogo() {
		try {
			System.out.println("Novo Jogo!");
			this.iniciado = true;
			cliente = new Cliente(portaCliente, endereco);
			this.clienteConectado = true;
			iniciaThreadPrincipal(this);
		} catch (Exception e) {

		}
		tabuleiro.limpaTabuleiro();
		selecionada.limpar();
		repaint();
		// MudaTitulo();
	}

	/**
	 * Muda o t�tulo para refletir o jogador corrente
	 */
	public void MudaTitulo() {
		if (tabuleiro.ganhador() == 0) {
			if (tabuleiro.getJogadorAtual() == Tabuleiro.BRANCA)
				pai.setTitle("Damas - Brancas");
			else
				pai.setTitle("Damas - Vermelhas");
		} else {
			String w = "";
			if (tabuleiro.ganhador() == Tabuleiro.VERMELHA) {
				w = "Vermelhas";
			} else {
				w = "Brancas";
			}
			pai.setTitle("Ganhador: " + w);
		}
	}

	/**
	 * Desenha o tabuleiro.
	 * 
	 * @param g
	 *            Local onde o desenho � efetuado
	 */
	public void paintComponent(Graphics g) {
		Dimension d = getSize();
		int marginX;
		int marginY;
		int incValue;

		// Limpa o buffer

		g.setColor(Color.lightGray);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);

		// Calcula os incrementos de forma a obter um tabuleiro quadrado
		if (d.width < d.height) {
			marginX = 0;
			marginY = (d.height - d.width) / 2;

			incValue = d.width / 8;
		} else {
			marginX = (d.width - d.height) / 2;
			marginY = 0;

			incValue = d.height / 8;
		}

		inicioX = marginX;
		inicioY = marginY;
		tamanhoDaCasa = incValue;

		desenhaTabuleiro(g, marginX, marginY, incValue);
		desenhaPecas(g, marginX, marginY, incValue);
	}

	/**
	 * Desenha a o tabuleiro
	 * 
	 * @param g
	 *            Contexto onde desenha as pe�as
	 * @param marginX
	 *            Margem horizontal do tabuleiro
	 * @param marginY
	 *            Margem vertical do tabuleiro
	 * @param valorInc
	 *            Fator de incremento entre as casas do tabuleiro
	 */
	private void desenhaTabuleiro(Graphics g, int marginX, int marginY, int valorInc) {
		int pos;

		for (int y = 0; y < 8; y++)
			for (int x = 0; x < 8; x++) {
				if ((x + y) % 2 == 0)
					g.setColor(Color.white);
				else {
					pos = y * 4 + (x + ((y % 2 == 0) ? -1 : 0)) / 2;

					if (selecionada.tem(new Integer(pos)))
						g.setColor(Color.green);
					else
						g.setColor(Color.black);
				}

				g.fillRect(marginX + x * valorInc, marginY + y * valorInc, valorInc - 1, valorInc - 1);
			}
	}

	/**
	 * Margem para as pecas que s�o damas
	 */
	private static final int TAMANHO_DAMA = 3;

	/**
	 * Desenha as pecas no tabuleiro
	 * 
	 * @param g
	 *            Contexto onde desenha as pe�as
	 * @param marginX
	 *            Margem horizontal do tabuleiro
	 * @param marginY
	 *            Margem vertical do tabuleiro
	 * @param incValue
	 *            Fator de incremento entre as casas do tabuleiro onde as pe�as
	 *            s�o desenhadas
	 */
	private void desenhaPecas(Graphics g, int marginX, int marginY, int incValue) {
		int x, y;
		for (int i = 0; i < 32; i++)
			try {
				if (tabuleiro.getPeca(i) != Tabuleiro.VAZIA) {
					if (tabuleiro.getPeca(i) == Tabuleiro.VERMELHA || tabuleiro.getPeca(i) == Tabuleiro.DAMA_VERMELHA)
						g.setColor(Color.red);
					else
						g.setColor(Color.white);

					y = i / 4;
					x = (i % 4) * 2 + (y % 2 == 0 ? 1 : 0);
					g.fillOval(TAMANHO + marginX + x * incValue, TAMANHO + marginY + y * incValue, incValue - 1 - 2 * TAMANHO, incValue - 1 - 2
							* TAMANHO);

					if (tabuleiro.getPeca(i) == Tabuleiro.DAMA_BRANCA) {
						g.setColor(Color.black);
						g.drawOval(TAMANHO_DAMA + marginX + x * incValue, TAMANHO_DAMA + marginY + y * incValue, incValue - 1 - 2 * TAMANHO_DAMA,
								incValue - 1 - 2 * TAMANHO_DAMA);
					} else if (tabuleiro.getPeca(i) == Tabuleiro.DAMA_VERMELHA) {
						g.setColor(Color.white);
						g.drawOval(TAMANHO_DAMA + marginX + x * incValue, TAMANHO_DAMA + marginY + y * incValue, incValue - 1 - 2 * TAMANHO_DAMA,
								incValue - 1 - 2 * TAMANHO_DAMA);
					}

				}
			} catch (ExcecaoDeCoordenadas bad) {
				bad.printStackTrace();
				System.exit(1);
			}
	}

	public void iniciaThreadPrincipal(VisaoDoTabuleiro visao) {
		this.serv = new Serv(visao);
		this.tServ = new Thread(this.serv);
		this.tServ.start();
	}

	public boolean isIniciado() {
		return iniciado;
	}

	public void setIniciado(boolean iniciado) {
		this.iniciado = iniciado;
		notify();
	}

	public synchronized boolean isServidorConectado() {
		try {
			if (this.iniciado) {
				wait();
			}
		} catch (Exception e) {

		}
		return servidorConectado;
	}

	public synchronized void setServidorConectado(boolean servidorConectado) {
		this.servidorConectado = servidorConectado;
		notifyAll();
	}

	public boolean isClienteConectado() {
		return clienteConectado;
	}

	public void setClienteConectado(boolean clienteConectado) {
		this.clienteConectado = clienteConectado;
		notify();
	}

}

/**
 * Classe do loop do servidor
 * 
 */
class ServStart implements Runnable {

	VisaoDoTabuleiro tabuleiro;

	/**
	 * Construtor do in�cio do jogo
	 * 
	 * @param tabuleiro
	 *            Recebe uma instancia do jogo
	 */
	public ServStart(VisaoDoTabuleiro tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

	/**
	 * Loop principal do jogo
	 */
	@Override
	public void run() {
		try {
			while (true) {
				if (!tabuleiro.isServidorConectado()) {
					tabuleiro.servidor.Connect();
					System.out.println("Conectou no servStart!");
					tabuleiro.servidorConectado = true;
					if (!tabuleiro.iniciado) {
						tabuleiro.iniciado = false;
						// tabuleiro.novoJogo();
						tabuleiro.iniciaThreadPrincipal(tabuleiro);
					}
				}
			}
		} catch (Exception e) {

		}
	}
}

/**
 * Classe do loop do jogo
 * 
 */
class Serv implements Runnable {

	byte movimento[] = new byte[6];
	VisaoDoTabuleiro tabuleiro;
	Lista lista;
	List<Conversor> traducao;
	int de = 0, para = 0;
	byte deIni, paraIni, deFin, paraFin;

	/**
	 * Construtor do in�cio do jogo
	 * 
	 * @param tabuleiro
	 *            Recebe uma instancia do jogo
	 */
	public Serv(VisaoDoTabuleiro tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

	/**
	 * Loop principal do jogo
	 */
	@Override
	public void run() {
		int cor = 0;
		adicionaItems();
		boolean running = true, iniciando = false;
		try {
			while (running) {
				boolean pontuou = false;
				if (!tabuleiro.iniciado) {
					tabuleiro.ai.setCor(Tabuleiro.VERMELHA);
					tabuleiro.iniciado = true;
				} else {
					tabuleiro.ai.setCor(Tabuleiro.BRANCA);
				}
				System.out.println("cor do meu jogador: " + tabuleiro.ai.getCor());
				tabuleiro.tabuleiro.setJogadorAtual(Tabuleiro.BRANCA);
				tabuleiro.ai.getTabuleiroAtual().setJogadorAtual(Tabuleiro.BRANCA);
				cor = tabuleiro.ai.getCor();
				if (cor == Tabuleiro.VERMELHA) {
					boolean t1 = true;
					while (t1) {
						System.out.println("esperando... 1");
						if (!tabuleiro.servidorConectado) {
							tabuleiro.servidor.Connect();
							tabuleiro.servidorConectado = true;
						}
						tabuleiro.servidor.RecebeBytes(movimento, 6);
						if (movimento[0] == 6) {
							de = getPosicaoPorCoor(movimento[2], movimento[3]);
							para = getPosicaoPorCoor(movimento[4], movimento[5]);
							if (de > -1 && para > -1) {
								if (tabuleiro.tabuleiro.movimentoValido(de, para)) {
									tabuleiro.tabuleiro.move(de, para);
									tabuleiro.repaint();
									tabuleiro.ai.mudaTabuleiro(tabuleiro.tabuleiro);
									Thread.sleep(100);
									respondeServidor(true);
									t1 = false;
								} else {
									respondeServidor(false);
								}
							}
						}
					}
				}
				boolean t2 = true;
				while (t2) {
					System.out.println("pensando... 2");
					Tabuleiro temp = tabuleiro.ai.getTabuleiroAtual();
					lista = tabuleiro.ai.joga();
					Movimentos mov;
					@SuppressWarnings("rawtypes")
					Enumeration e = lista.elementos();
					while (e.hasMoreElements()) {
						mov = (Movimentos) e.nextElement();
						int de = mov.getDe(), para = mov.getPara();
						byte tipo = 0;
						if (tabuleiro.ai.getTabuleiroAtual().temQueAtacar()) {
							if (e.hasMoreElements()) {
								tipo = 2;
							} else {
								tipo = 1;
							}
						}
						movimento[0] = 6;
						movimento[1] = tipo;
						setMovimentacao(de, para);
						if (!tabuleiro.clienteConectado) {
							Thread.sleep(300);
							tabuleiro.cliente = new Cliente(tabuleiro.portaCliente, tabuleiro.endereco);
							tabuleiro.clienteConectado = true;
						}
						tabuleiro.cliente.EnviaBytes(movimento, 6);
						Thread.sleep(100);
						tabuleiro.cliente.RecebeBytes(movimento, 6);
						if (movimento != null) {
							if (movimento[0] == 4) {
								tabuleiro.tabuleiro = tabuleiro.ai.getTabuleiroAtual();
								t2 = false;
								tabuleiro.repaint();
							} else {
								tabuleiro.tabuleiro = temp;
								tabuleiro.ai.mudaTabuleiro(temp);
							}
						}
					}
				}
				// loop principal do jogo
				while (tabuleiro.iniciado) {
					try {
						// RECEBENDO UMA JOGADA
						boolean t3 = true;
						while (t3) {
							if (tabuleiro.iniciado) {
								System.out.println("esperando...");
								if(!iniciando){
									Thread.sleep(500);
									iniciando = true;
								}
								if (tabuleiro.servidorConectado == true) {
									if (tabuleiro.tabuleiro.ganhador() == 0) {
										tabuleiro.servidor.RecebeBytes(movimento, 6);
										if (movimento[0] == 6) {
											byte validador = movimento[1];
											de = getPosicaoPorCoor(movimento[2], movimento[3]);
											para = getPosicaoPorCoor(movimento[4], movimento[5]);
											if (de > -1 && para > -1) {
												if (tabuleiro.tabuleiro.movimentoValido(de, para)) {
													tabuleiro.tabuleiro.move(de, para);
													tabuleiro.repaint();
													Thread.sleep(300);
													respondeServidor(true);
													if (validador == 1 || validador == 0) {
														t3 = false;
													}
												} else {
													respondeServidor(false);
												}
											}
										}
									} else {
										String w = "";
										if (tabuleiro.tabuleiro.ganhador() == Tabuleiro.VERMELHA) {
											w = "Vermelhas";
										} else {
											w = "Brancas";
										}
										if (!pontuou) {
											if (cor == tabuleiro.tabuleiro.ganhador()) {
												tabuleiro.eu++;
											} else {
												tabuleiro.oponente++;
											}
											pontuou = true;
											System.out.println("Ganhador: " + w);
											System.out.println("Pontua��o: eu: " + tabuleiro.eu + ", oponente: " + tabuleiro.oponente);
											tabuleiro.pai.getEulbl().setText("   Eu: " + tabuleiro.eu + "   ");
											tabuleiro.pai.getOponentelbl().setText("   Oponente: " + tabuleiro.oponente + "   ");
											tabuleiro.pai.setTitle("Damas - As " + w + " ganharam!");
											tabuleiro.tabuleiro.limpaTabuleiro();
											tabuleiro.selecionada.limpar();
											tabuleiro.repaint();
											running = false;
											tabuleiro.setServidorConectado(false);
										}
										t3 = false;
										tabuleiro.iniciado = false;
									}
									tabuleiro.repaint();
								}
							} else {
								t3 = false;
							}
						}
						if (tabuleiro.tabuleiro.getJogadorAtual() != cor) {
							tabuleiro.tabuleiro.setJogadorAtual(cor);
						}
						// ENVIANDO UMA JOGADA
						boolean t4 = true;
						while (t4) {
							if (tabuleiro.iniciado) {
								System.out.println("pensando...");
								if (tabuleiro.tabuleiro.ganhador() == 0) {
									Tabuleiro temp = tabuleiro.ai.getTabuleiroAtual();
									lista = tabuleiro.ai.joga();
									Movimentos mov;
									@SuppressWarnings("rawtypes")
									Enumeration e = lista.elementos();
									while (e.hasMoreElements()) {
										mov = (Movimentos) e.nextElement();
										int de = mov.getDe(), para = mov.getPara();
										movimento[0] = 6;
										setMovimentacao(de, para);
										byte tipo = 0;
										int teste = movimento[4] - movimento[2];
										if (teste < 0) {
											teste = teste * -1;
										}
										if (teste > 1) {
											if (e.hasMoreElements()) {
												tipo = 2;
											} else {
												tipo = 1;
											}
										} else if (e.hasMoreElements()) {
											tipo = 2;
										}
										movimento[1] = tipo;
										if (!tabuleiro.clienteConectado) {
											tabuleiro.cliente = new Cliente(tabuleiro.portaCliente, tabuleiro.endereco);
											tabuleiro.clienteConectado = true;
										}
										tabuleiro.cliente.EnviaBytes(movimento, 6);
										Thread.sleep(300);
										tabuleiro.cliente.RecebeBytes(movimento, 6);
										if (movimento != null) {
											if (movimento[0] == 4) {
												tabuleiro.tabuleiro = tabuleiro.ai.getTabuleiroAtual();
												t4 = false;
												tabuleiro.repaint();
											} else {
												tabuleiro.tabuleiro = temp;
												tabuleiro.ai.mudaTabuleiro(tabuleiro.tabuleiro);
											}
										}
									}
								} else {
									String w = "";
									if (tabuleiro.tabuleiro.ganhador() == Tabuleiro.VERMELHA) {
										w = "Vermelhas";
									} else {
										w = "Brancas";
									}
									if (!pontuou) {
										if (cor == tabuleiro.tabuleiro.ganhador()) {
											tabuleiro.eu++;
										} else {
											tabuleiro.oponente++;
										}
										pontuou = true;
										System.out.println("Ganhador: " + w);
										System.out.println("Pontua��o: eu: " + tabuleiro.eu + ", oponente: " + tabuleiro.oponente);
										tabuleiro.pai.getEulbl().setText("   Eu: " + tabuleiro.eu + "   ");
										tabuleiro.pai.getOponentelbl().setText("   Oponente: " + tabuleiro.oponente + "   ");
										tabuleiro.pai.setTitle("Damas - As " + w + " ganharam!");
										tabuleiro.servidorConectado = false;
										tabuleiro.tabuleiro.limpaTabuleiro();
										tabuleiro.selecionada.limpar();
										tabuleiro.setServidorConectado(false);
										tabuleiro.repaint();
										running = false;
									}
									t4 = false;
									tabuleiro.iniciado = false;
								}
								tabuleiro.repaint();
							} else {
								t4 = false;
							}
						}
						tabuleiro.repaint();
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Busca uma posi��o do meu protocolo dada uma coordenada do protocolo da
	 * turma
	 * 
	 * @param x
	 *            coordenada x
	 * @param y
	 *            coordenada y
	 * @return posi��o encontrada
	 */
	public int getPosicaoPorCoor(int x, int y) {
		for (Conversor c : traducao) {
			if (c.getX() == x && c.getY() == y) {
				return c.getPosicao();
			}
		}
		return -1;
	}

	/**
	 * Busca o 'x' e o 'y' corespondentes a duas posi��es do meu protocolo e
	 * coloca eles no array de movimento
	 * 
	 * @param de
	 *            identificador da posi��o de origem
	 * 
	 * @param para
	 *            identificador da posi��o de destino
	 */
	public void setMovimentacao(int de, int para) {
		for (Conversor c : traducao) {
			if (c.getPosicao() == de) {
				movimento[2] = c.getX();
				movimento[3] = c.getY();
			}
			if (c.getPosicao() == para) {
				movimento[4] = c.getX();
				movimento[5] = c.getY();
			}
		}
	}

	/**
	 * Gera a lista com os valores correspondentes de meu protocolo para o
	 * protocolo da turma
	 **/
	public void adicionaItems() {
		try {
			int count = 28, linha = 0;
			traducao = new ArrayList<Conversor>();
			for (int x = 0; x < 8; x++) {

				for (int j = 0; j < 8; j += 2) {
					linha++;
					int y = j + (x % 2);
					/*
					 * if ((i % 2) == 0) { y = j + 1; } else { y = j; }
					 */
					traducao.add(new Conversor(count, Byte.parseByte("" + x), Byte.parseByte("" + y)));
					// System.out.println("Pos:"+count+", x: "+i+", y: "+y);
					if (linha < 4) {
						count++;
					} else {
						count -= 7;
						linha = 0;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Erro na fun��o adicionaItems!!");
			e.printStackTrace();
		}
	}

	/**
	 * Gera e envia uma resposta via socket Cliente
	 * 
	 * @param ok
	 *            parametro que define se a resposta � positiva ou negativa
	 **/
	public void responde(boolean ok) {
		try {
			if (ok) {
				movimento[0] = 4;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				tabuleiro.cliente.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			} else {
				movimento[0] = 5;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				tabuleiro.cliente.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			}
		} catch (Exception ex) {
			System.out.println("Erro na fun��o responde!!");
			ex.printStackTrace();
		}
	}

	/**
	 * Gera e envia uma resposta via socket Servidor
	 * 
	 * @param ok
	 *            parametro que define se a resposta � positiva ou negativa
	 **/
	public void respondeServidor(boolean ok) {
		try {
			if (ok) {
				movimento[0] = 4;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				tabuleiro.servidor.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			} else {
				movimento[0] = 5;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				tabuleiro.servidor.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			}
		} catch (Exception ex) {
			System.out.println("Erro na fun��o respondeServidor!!");
			ex.printStackTrace();
		}
	}

	/**
	 * Classe interna que serve como tradutora do meu protocolo para o protocolo
	 * da turma
	 * 
	 **/
	public class Conversor {

		int posicao;
		byte x, y;

		public Conversor(int posicao, byte x, byte y) {
			this.posicao = posicao;
			this.x = x;
			this.y = y;
		}

		public int getPosicao() {
			return posicao;
		}

		public void setPosicao(int posicao) {
			this.posicao = posicao;
		}

		public byte getX() {
			return x;
		}

		public void setX(byte x) {
			this.x = x;
		}

		public byte getY() {
			return y;
		}

		public void setY(byte y) {
			this.y = y;
		}

	}
}