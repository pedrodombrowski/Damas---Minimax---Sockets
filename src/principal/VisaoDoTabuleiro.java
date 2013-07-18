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
		portaCliente = 7070;
		endereco = "localhost";
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
	 * Começa um novo jogo como as vermelhas
	 */
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
	}

	/**
	 * Muda o título para refletir o jogador corrente
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
	 *            Local onde o desenho é efetuado
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
	 *            Contexto onde desenha as peças
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
	 * Margem para as pecas que são damas
	 */
	private static final int TAMANHO_DAMA = 3;

	/**
	 * Desenha as pecas no tabuleiro
	 * 
	 * @param g
	 *            Contexto onde desenha as peças
	 * @param marginX
	 *            Margem horizontal do tabuleiro
	 * @param marginY
	 *            Margem vertical do tabuleiro
	 * @param incValue
	 *            Fator de incremento entre as casas do tabuleiro onde as peças
	 *            são desenhadas
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

	/**
	 * Inicia a thread principal do jogo
	 * 
	 * @param visao
	 *            instância do tabuleiro
	 */
	public void iniciaThreadPrincipal(VisaoDoTabuleiro visao) {
		this.serv = new Serv(visao);
		this.tServ = new Thread(this.serv);
		this.tServ.start();
	}

	/**
	 * Boolean sincronizada que faz a organização entre as duas threads
	 * informando se o servidor está conectado ou não
	 * 
	 * @return retorna estado do servidor
	 */
	public synchronized boolean isServidorConectado() {
		try {
			if (this.iniciado) {
				wait();
			}
		} catch (Exception e) {

		}
		return servidorConectado;
	}

	/**
	 * Método responsável pela atribuição da boolean que informa se o servidor
	 * está conectado e liberação das threads em estado de espera
	 * 
	 * @param servidorConectado
	 *            estado do servidor
	 */
	public synchronized void setServidorConectado(boolean servidorConectado) {
		this.servidorConectado = servidorConectado;
		notifyAll();
	}
}

/**
 * Classe do loop do servidor
 * 
 */
class ServStart implements Runnable {

	VisaoDoTabuleiro visao;

	/**
	 * Construtor do início do servidor
	 * 
	 * @param tabuleiro
	 *            Recebe uma instancia do jogo
	 */
	public ServStart(VisaoDoTabuleiro tabuleiro) {
		this.visao = tabuleiro;
	}

	/**
	 * Loop principal do servidor
	 */
	@Override
	public void run() {
		try {
			while (true) {
				if (!visao.isServidorConectado()) {
					visao.servidor.Connect();
					System.out.println("Conectou no servStart!");
					visao.servidorConectado = true;
					if (!visao.iniciado) {
						visao.iniciado = false;
						visao.iniciaThreadPrincipal(visao);
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
	VisaoDoTabuleiro visao;
	Lista lista;
	List<Conversor> conversores;
	int de = 0, para = 0;
	byte deIni, paraIni, deFin, paraFin;

	/**
	 * Construtor do início do jogo
	 * 
	 * @param tabuleiro
	 *            Recebe uma instancia do jogo
	 */
	public Serv(VisaoDoTabuleiro tabuleiro) {
		this.visao = tabuleiro;
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
				if (!visao.iniciado) {
					visao.ai.setCor(Tabuleiro.VERMELHA);
					visao.iniciado = true;
				} else {
					visao.ai.setCor(Tabuleiro.BRANCA);
				}
				System.out.println("cor do meu jogador: " + visao.ai.getCor());
				visao.tabuleiro.setJogadorAtual(Tabuleiro.BRANCA);
				visao.ai.getTabuleiroAtual().setJogadorAtual(Tabuleiro.BRANCA);
				cor = visao.ai.getCor();
				if (cor == Tabuleiro.VERMELHA) {
					boolean t1 = true;
					while (t1) {
						System.out.println("esperando...");
						if (!visao.servidorConectado) {
							visao.servidor.Connect();
							visao.servidorConectado = true;
						}
						visao.servidor.RecebeBytes(movimento, 6);
						if (movimento[0] == 6) {
							de = getPosicaoPorCoor(movimento[2], movimento[3]);
							para = getPosicaoPorCoor(movimento[4], movimento[5]);
							if (de > -1 && para > -1) {
								if (visao.tabuleiro.movimentoValido(de, para)) {
									visao.tabuleiro.move(de, para);
									visao.repaint();
									visao.ai.mudaTabuleiro(visao.tabuleiro);
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
					System.out.println("pensando...");
					Tabuleiro temp = visao.ai.getTabuleiroAtual();
					lista = visao.ai.joga();
					Movimentos mov;
					@SuppressWarnings("rawtypes")
					Enumeration e = lista.elementos();
					while (e.hasMoreElements()) {
						mov = (Movimentos) e.nextElement();
						int de = mov.getDe(), para = mov.getPara();
						byte tipo = 0;
						if (visao.ai.getTabuleiroAtual().temQueAtacar()) {
							if (e.hasMoreElements()) {
								tipo = 2;
							} else {
								tipo = 1;
							}
						}
						movimento[0] = 6;
						movimento[1] = tipo;
						setMovimentacao(de, para);
						if (!visao.clienteConectado) {
							Thread.sleep(300);
							visao.cliente = new Cliente(visao.portaCliente, visao.endereco);
							visao.clienteConectado = true;
						}
						visao.cliente.EnviaBytes(movimento, 6);
						Thread.sleep(100);
						visao.cliente.RecebeBytes(movimento, 6);
						if (movimento != null) {
							if (movimento[0] == 4) {
								visao.tabuleiro = visao.ai.getTabuleiroAtual();
								t2 = false;
								visao.repaint();
							} else {
								visao.tabuleiro = temp;
								visao.ai.mudaTabuleiro(temp);
							}
						}
					}
				}
				// loop principal do jogo
				while (visao.iniciado) {
					try {
						// RECEBENDO UMA JOGADA
						boolean t3 = true;
						while (t3) {
							if (visao.iniciado) {
								System.out.println("esperando...");
								if (!iniciando) {
									Thread.sleep(500);
									iniciando = true;
								}
								if (visao.servidorConectado == true) {
									if (visao.tabuleiro.ganhador() == 0) {
										visao.servidor.RecebeBytes(movimento, 6);
										if (movimento[0] == 6) {
											byte validador = movimento[1];
											de = getPosicaoPorCoor(movimento[2], movimento[3]);
											para = getPosicaoPorCoor(movimento[4], movimento[5]);
											if (de > -1 && para > -1) {
												if (visao.tabuleiro.movimentoValido(de, para)) {
													visao.tabuleiro.move(de, para);
													visao.repaint();
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
										if (visao.tabuleiro.ganhador() == Tabuleiro.VERMELHA) {
											w = "Vermelhas";
										} else {
											w = "Brancas";
										}
										if (!pontuou) {
											if (cor == visao.tabuleiro.ganhador()) {
												visao.eu++;
											} else {
												visao.oponente++;
											}
											pontuou = true;
											System.out.println("Ganhador: " + w);
											System.out.println("Pontuação: eu: " + visao.eu + ", oponente: " + visao.oponente);
											visao.pai.getEulbl().setText("   Eu: " + visao.eu + "   ");
											visao.pai.getOponentelbl().setText("   Oponente: " + visao.oponente + "   ");
											visao.pai.setTitle("Damas - As " + w + " ganharam!");
											visao.tabuleiro.limpaTabuleiro();
											visao.selecionada.limpar();
											visao.repaint();
											running = false;
											visao.setServidorConectado(false);
											visao.clienteConectado = false;
										}
										t3 = false;
										visao.iniciado = false;
									}
									visao.repaint();
								}
							} else {
								t3 = false;
							}
						}
						if (visao.tabuleiro.getJogadorAtual() != cor) {
							visao.tabuleiro.setJogadorAtual(cor);
						}
						// ENVIANDO UMA JOGADA
						boolean t4 = true;
						while (t4) {
							if (visao.iniciado) {
								System.out.println("pensando...");
								if (visao.tabuleiro.ganhador() == 0) {
									Tabuleiro temp = visao.ai.getTabuleiroAtual();
									lista = visao.ai.joga();
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
										if (!visao.clienteConectado) {
											visao.cliente = new Cliente(visao.portaCliente, visao.endereco);
											visao.clienteConectado = true;
										}
										visao.cliente.EnviaBytes(movimento, 6);
										Thread.sleep(300);
										visao.cliente.RecebeBytes(movimento, 6);
										if (movimento != null) {
											if (movimento[0] == 4) {
												visao.tabuleiro = visao.ai.getTabuleiroAtual();
												t4 = false;
												visao.repaint();
											} else {
												visao.tabuleiro = temp;
												visao.ai.mudaTabuleiro(visao.tabuleiro);
											}
										}
									}
								} else {
									String w = "";
									if (visao.tabuleiro.ganhador() == Tabuleiro.VERMELHA) {
										w = "Vermelhas";
									} else {
										w = "Brancas";
									}
									if (!pontuou) {
										if (cor == visao.tabuleiro.ganhador()) {
											visao.eu++;
										} else {
											visao.oponente++;
										}
										pontuou = true;
										System.out.println("Ganhador: " + w);
										System.out.println("Pontuação: eu: " + visao.eu + ", oponente: " + visao.oponente);
										visao.pai.getEulbl().setText("   Eu: " + visao.eu + "   ");
										visao.pai.getOponentelbl().setText("   Oponente: " + visao.oponente + "   ");
										visao.pai.setTitle("Damas - As " + w + " ganharam!");
										visao.servidorConectado = false;
										visao.tabuleiro.limpaTabuleiro();
										visao.selecionada.limpar();
										visao.setServidorConectado(false);
										visao.clienteConectado = false;
										visao.repaint();
										running = false;
									}
									t4 = false;
									visao.iniciado = false;
								}
								visao.repaint();
							} else {
								t4 = false;
							}
						}
						visao.repaint();
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Busca uma posição do meu protocolo dada uma coordenada do protocolo da
	 * turma
	 * 
	 * @param x
	 *            coordenada x
	 * @param y
	 *            coordenada y
	 * @return posição encontrada
	 */
	public int getPosicaoPorCoor(int x, int y) {
		for (Conversor c : conversores) {
			if (c.getX() == x && c.getY() == y) {
				return c.getPosicao();
			}
		}
		return -1;
	}

	/**
	 * Busca o 'x' e o 'y' corespondentes a duas posições do meu protocolo e
	 * coloca eles no array de movimento
	 * 
	 * @param de
	 *            identificador da posição de origem
	 * 
	 * @param para
	 *            identificador da posição de destino
	 */
	public void setMovimentacao(int de, int para) {
		for (Conversor c : conversores) {
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
			conversores = new ArrayList<Conversor>();
			for (int x = 0; x < 8; x++) {

				for (int j = 0; j < 8; j += 2) {
					linha++;
					int y = j + (x % 2);
					/*
					 * if ((i % 2) == 0) { y = j + 1; } else { y = j; }
					 */
					conversores.add(new Conversor(count, Byte.parseByte("" + x), Byte.parseByte("" + y)));
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
			System.out.println("Erro na função adicionaItems!!");
			e.printStackTrace();
		}
	}

	/**
	 * Gera e envia uma resposta via socket Cliente
	 * 
	 * @param ok
	 *            parametro que define se a resposta é positiva ou negativa
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
				visao.cliente.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			} else {
				movimento[0] = 5;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				visao.cliente.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			}
		} catch (Exception ex) {
			System.out.println("Erro na função responde!!");
			ex.printStackTrace();
		}
	}

	/**
	 * Gera e envia uma resposta via socket Servidor
	 * 
	 * @param ok
	 *            parametro que define se a resposta é positiva ou negativa
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
				visao.servidor.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			} else {
				movimento[0] = 5;
				movimento[1] = 0;
				movimento[2] = 0;
				movimento[3] = 0;
				movimento[4] = 0;
				movimento[5] = 0;
				visao.servidor.EnviaBytes(movimento, 6);
				Thread.sleep(100);
			}
		} catch (Exception ex) {
			System.out.println("Erro na função respondeServidor!!");
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

		/**
		 * Construtor
		 * 
		 * @param posicao
		 *            posição no meu protocolo
		 * @param x
		 *            posição de linha do protocolo da turma
		 * @param y
		 *            posição de coluna do protocolo da turma
		 */
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