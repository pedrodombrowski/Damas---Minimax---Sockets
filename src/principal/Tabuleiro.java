package principal;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * Tabuleiro de damas
 */
public class Tabuleiro implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Representação do tabuleiro. É baseado na representação padrão.
	 */
	private byte pecas[];

	/**
	 * Peças usadas no jogo.
	 */
	public static final byte VAZIA = 0;
	public static final byte BRANCA = 2;
	public static final byte DAMA_BRANCA = 3;
	public static final byte VERMELHA = 4;
	public static final byte DAMA_VERMELHA = 5;

	/**
	 * Serve para saber o tipo de peça mais facilmente
	 */
	private static final byte DAMA = 1;

	/**
	 * Contadores do número de peças de cada jogador
	 */
	private int pecasBrancas;
	private int pecasVermelhas;

	/**
	 * Indica qual o jogador atual.
	 */
	private int jogadorAtual;

	/**
	 * Constructor
	 */
	public Tabuleiro() {
		pecas = new byte[32];
		limpaTabuleiro();
	}

	/**
	 * Retorna o jogador atual
	 */
	public int getJogadorAtual() {
		return jogadorAtual;
	}

	/**
	 * Muda o jogador atual
	 */
	public void setJogadorAtual(int jogador) {
		jogadorAtual = jogador;
	}

	/**
	 * Devolve o número de peças do jogador branco.
	 */
	public int getPecasBrancas() {
		return pecasBrancas;
	}

	/**
	 * Devolve o numero de pecas do jogador vermelho.
	 */
	public int getPecasVermelhas() {
		return pecasVermelhas;
	}

	/**
	 * Cria uma copia da classe
	 */
	public Object copiar() {
		Tabuleiro board = new Tabuleiro();
		board.jogadorAtual = jogadorAtual;
		board.pecasBrancas = pecasBrancas;
		board.pecasVermelhas = pecasVermelhas;
		System.arraycopy(pecas, 0, board.pecas, 0, 32);
		return board;
	}

	/**
	 * Devolve uma lista com todas as jogadas válidas para o jogador atual
	 */
	public Lista movimentosLegais() {
		int cor;
		int oponente;
		cor = jogadorAtual;
		if (cor == BRANCA)
			oponente = VERMELHA;
		else
			oponente = BRANCA;

		if (temQueAtacar())
			return gerarAtaques(cor, oponente);
		else
			return gerarMovimentos(cor, oponente);
	}

	/**
	 * Gera as jogadas para os movimentos que são de ataque
	 */
	private Lista gerarAtaques(int cor, int oponente) {
		Lista movimentos = new Lista();
		Lista movimentosTemp;
		for (int k = 0; k < 32; k++)
			if ((pecas[k] & ~DAMA) == jogadorAtual) {
				if ((pecas[k] & DAMA) == 0) // Peca simples
					movimentosTemp = ataqueSimples(k, cor, oponente);
				else { // E' uma dama
					Lista ultimaPos = new Lista();
					ultimaPos.push_tras(new Integer(k));
					movimentosTemp = ataqueDeDama(ultimaPos, k, NENHUMA, cor, oponente);
				}
				if (naoNull(movimentosTemp))
					movimentos.adicionar(movimentosTemp);
			}
		return movimentos;
	}

	/**
	 * Gera as jogadas para ataques com peças simples
	 */
	private Lista ataqueSimples(int pos, int cor, int oponente) {
		int x = colunaPos(pos);
		int y = linhaPos(pos);
		int i;
		Lista movimentos = new Lista();
		Lista movimentosTemp;
		int posOponente, proximaPos;

		i = (cor == BRANCA) ? -1 : 1;

		// Ve as diagonais /^ e \v
		if (x < 6 && y + i > 0 && y + i < 7) {
			posOponente = colLinhaPos(x + 1, y + i);
			proximaPos = colLinhaPos(x + 2, y + 2 * i);
			if ((pecas[posOponente] & ~DAMA) == oponente && pecas[proximaPos] == VAZIA) {
				movimentosTemp = ataqueSimples(proximaPos, cor, oponente);
				movimentos.adicionar(addMovimento(new Movimentos(pos, proximaPos), movimentosTemp));
			}
		}
		// Ve as diagonais v/ e ^\
		if (x > 1 && y + i > 0 && y + i < 7) {
			posOponente = colLinhaPos(x - 1, y + i);
			proximaPos = colLinhaPos(x - 2, y + 2 * i);

			if ((pecas[posOponente] & ~DAMA) == oponente && pecas[proximaPos] == VAZIA) {
				movimentosTemp = ataqueSimples(proximaPos, cor, oponente);
				movimentos.adicionar(addMovimento(new Movimentos(pos, proximaPos), movimentosTemp));
			}
		}
		if (movimentos.taVazio())
			movimentos.push_tras(new Lista());
		return movimentos;
	}

	/**
	 * Constantes para a ultima direção
	 */
	private static final int NENHUMA = 0; // Primeira vez
	private static final int ESQUERDA_BAIXO = 1; // Diagonal v/
	private static final int ESQUERDA_CIMA = 2; // Diagonal ^\
	private static final int DIREITA_BAIXO = 3; // Diagonal \v
	private static final int DIREITA_CIMA = 4; // Diagonal /^

	/**
	 * Gera as jogadas para as damas
	 */
	private Lista ataqueDeDama(Lista ultimaPos, int pos, int dir, int cor, int oponente) {
		Lista movimentosTemp, movimentos = new Lista();
		if (dir != DIREITA_BAIXO) {
			movimentosTemp = ataqueDeDama(ultimaPos, pos, cor, oponente, 1, 1);
			if (naoNull(movimentosTemp))
				movimentos.adicionar(movimentosTemp);
		}
		if (dir != ESQUERDA_CIMA) {
			movimentosTemp = ataqueDeDama(ultimaPos, pos, cor, oponente, -1, -1);
			if (naoNull(movimentosTemp))
				movimentos.adicionar(movimentosTemp);
		}
		if (dir != DIREITA_CIMA) {
			movimentosTemp = ataqueDeDama(ultimaPos, pos, cor, oponente, 1, -1);
			if (naoNull(movimentosTemp))
				movimentos.adicionar(movimentosTemp);
		}
		if (dir != ESQUERDA_BAIXO) {
			movimentosTemp = ataqueDeDama(ultimaPos, pos, cor, oponente, -1, 1);
			if (naoNull(movimentosTemp))
				movimentos.adicionar(movimentosTemp);
		}
		return movimentos;
	}

	/**
	 * Gera as jogadas para ataques com peças de dama para uma diagonal
	 */
	private Lista ataqueDeDama(Lista ultimaPos, int pos, int cor, int oponente, int incX, int incY) {
		int x = colunaPos(pos);
		int y = linhaPos(pos);
		int i, j;
		Lista movimentos = new Lista();
		Lista movimentosTemp, posTemp;

		int posInicial = ((Integer) ultimaPos.pegaCabeca()).intValue();

		i = x + incX;
		j = y + incY;

		// Procura o inimigo
		while (i > 0 && i < 7 && j > 0 && j < 7 && (pecas[colLinhaPos(i, j)] == VAZIA || colLinhaPos(i, j) == posInicial)) {
			i += incX;
			j += incY;
		}

		if (i > 0 && i < 7 && j > 0 && j < 7 && (pecas[colLinhaPos(i, j)] & ~DAMA) == oponente && !ultimaPos.tem(new Integer(colLinhaPos(i, j)))) {

			ultimaPos.push_tras(new Integer(colLinhaPos(i, j)));

			i += incX;
			j += incY;

			int salvaI = i;
			int salvaJ = j;
			while (i >= 0 && i <= 7 && j >= 0 && j <= 7 && (pecas[colLinhaPos(i, j)] == VAZIA || colLinhaPos(i, j) == posInicial)) {
				int dir;
				if (incX == 1 && incY == 1) {
					dir = ESQUERDA_CIMA;
				} else if (incX == -1 && incY == -1) {
					dir = DIREITA_BAIXO;
				} else if (incX == -1 && incY == 1) {
					dir = DIREITA_CIMA;
				} else {
					dir = ESQUERDA_BAIXO;
				}
				posTemp = (Lista) ultimaPos.clone();
				movimentosTemp = ataqueDeDama(posTemp, colLinhaPos(i, j), dir, cor, oponente);
				if (naoNull(movimentosTemp))
					movimentos.adicionar(addMovimento(new Movimentos(pos, colLinhaPos(i, j)), movimentosTemp));
				i += incX;
				j += incY;
			}
			ultimaPos.pop_tras();
			if (movimentos.taVazio()) {
				i = salvaI;
				j = salvaJ;
				while (i >= 0 && i <= 7 && j >= 0 && j <= 7 && (pecas[colLinhaPos(i, j)] == VAZIA || colLinhaPos(i, j) == posInicial)) {
					movimentosTemp = new Lista();
					movimentosTemp.push_tras(new Movimentos(pos, colLinhaPos(i, j)));
					movimentos.push_tras(movimentosTemp);
					i += incX;
					j += incY;
				}
			}
		}
		return movimentos;
	}

	/**
	 * Indica se a lista de movimentos nao é nula
	 */
	private boolean naoNull(Lista movimentos) {
		return !movimentos.taVazio() && !((Lista) movimentos.pegaCabeca()).taVazio();
	}

	/**
	 * Acrescenta um movimento à cabeca de todas as listas de movimentos
	 * 
	 * @param movimento
	 *            jogada a acrescentar
	 * @param movimentos
	 *            lista de listas com jogadas, no fim fica vazia
	 */
	private Lista addMovimento(Movimentos movimento, Lista movimentos) {
		if (movimento == null)
			return movimentos;
		Lista atual, temp = new Lista();
		while (!movimentos.taVazio()) {
			atual = (Lista) movimentos.removeInicio();
			atual.push_frente(movimento);
			temp.push_tras(atual);
		}
		return temp;
	}

	/**
	 * Gera as jogadas para os movimentos que não são de ataque
	 */
	private Lista gerarMovimentos(int cor, int oponente) {
		Lista moves = new Lista();
		Lista movimentoTemp;

		for (int k = 0; k < 32; k++)
			if ((pecas[k] & ~DAMA) == jogadorAtual) {
				int x = colunaPos(k);
				int y = linhaPos(k);
				int i, j;

				if ((pecas[k] & DAMA) == 0) { // Peca simples
					i = (cor == BRANCA) ? -1 : 1;

					// Ve as diagonais /^ e \v
					if (x < 7 && y + i >= 0 && y + i <= 7 && pecas[colLinhaPos(x + 1, y + i)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(x + 1, y + i)));
						moves.push_tras(movimentoTemp);
					}

					// Ve as diagonais ^\ e v/
					if (x > 0 && y + i >= 0 && y + i <= 7 && pecas[colLinhaPos(x - 1, y + i)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(x - 1, y + i)));
						moves.push_tras(movimentoTemp);
					}
					;
				} else { // É uma dama
					// Ve a diagonal \v
					i = x + 1;
					j = y + 1;

					while (i <= 7 && j <= 7 && pecas[colLinhaPos(i, j)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(i, j)));
						moves.push_tras(movimentoTemp);

						i++;
						j++;
					}

					// Ve a diagonal ^\
					i = x - 1;
					j = y - 1;
					while (i >= 0 && j >= 0 && pecas[colLinhaPos(i, j)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(i, j)));
						moves.push_tras(movimentoTemp);

						i--;
						j--;
					}

					// Ve a diagonal /^
					i = x + 1;
					j = y - 1;
					while (i <= 7 && j >= 0 && pecas[colLinhaPos(i, j)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(i, j)));
						moves.push_tras(movimentoTemp);

						i++;
						j--;
					}

					// Ve a diagonal v/
					i = x - 1;
					j = y + 1;
					while (i >= 0 && j <= 7 && pecas[colLinhaPos(i, j)] == VAZIA) {
						movimentoTemp = new Lista();
						movimentoTemp.push_tras(new Movimentos(k, colLinhaPos(i, j)));
						moves.push_tras(movimentoTemp);

						i--;
						j++;
					}
				}
			}
		return moves;
	}

	/**
	 * Indica se a jogada e válida
	 */
	public boolean movimentoValido(int de, int para) {
		// Se o valor da peca for invalido a jogada não é valida
		if (de < 0 || de > 32 || para < 0 || para > 32)
			return false;
		// Se a casa origem estiver vazia ou destino nao estiver vazia a jogada
		// nao é valida
		if (pecas[de] == VAZIA || pecas[para] != VAZIA)
			return false;
		// Verifica se estamos tentando mover uma peca do jogador atual
		if ((pecas[de] & ~DAMA) != jogadorAtual)
			return false;
		int cor;
		int oponente;
		cor = pecas[de] & ~DAMA;
		if (cor == BRANCA)
			oponente = VERMELHA;
		else
			oponente = BRANCA;
		int daLinha = linhaPos(de);
		int daColuna = colunaPos(de);
		int paraLinha = linhaPos(para);
		int paraColuna = colunaPos(para);
		int incX, incY;
		// Calcula incrementos
		if (daColuna > paraColuna)
			incX = -1;
		else
			incX = 1;
		if (daLinha > paraLinha)
			incY = -1;
		else
			incY = 1;
		int x = daColuna + incX;
		int y = daLinha + incY;
		if ((pecas[de] & DAMA) == 0) { // Peça simples
			boolean boaDir;
			if ((incY == -1 && cor == BRANCA) || (incY == 1 && cor == VERMELHA))
				boaDir = true;
			else
				boaDir = false;
			if (x == paraColuna && y == paraLinha) // Jogada simples
				return boaDir && !temQueAtacar();
			// Se nao se executou uma jogada simples só pode ser uma jogada de
			// conquista
			return boaDir && x + incX == paraColuna && y + incY == paraLinha && (pecas[colLinhaPos(x, y)] & ~DAMA) == oponente;
		} else { // É uma dama
			// boolean encontrada = false;

			while (x != paraColuna && y != paraLinha && pecas[colLinhaPos(x, y)] == VAZIA) {
				x += incX;
				y += incY;
			}
			// Jogada simples com dama
			if (x == paraColuna && y == paraLinha)
				return !temQueAtacar();
			if ((pecas[colLinhaPos(x, y)] & ~DAMA) == oponente) {
				x += incX;
				y += incY;
				while (x != paraColuna && y != paraLinha && pecas[colLinhaPos(x, y)] == VAZIA) {
					x += incX;
					y += incY;
				}
				if (x == paraColuna && y == paraLinha)
					return true;
			}
		}
		return false;
	}

	/**
	 * Indica se o jogador atual é obrigado a atacar
	 */
	public boolean temQueAtacar() {
		for (int i = 0; i < 32; i++)
			if ((pecas[i] & ~DAMA) == jogadorAtual && podeAtacar(i))
				return true;

		return false;
	}

	/**
	 * Indica se a casa indicada ataca alguma posição
	 * 
	 * @param pos
	 *            casa em questão
	 */
	public boolean podeAtacar(int pos) {
		if (pecas[pos] == VAZIA)
			return false;

		int cor;
		int oponente;

		cor = pecas[pos] & ~DAMA;
		if (cor == BRANCA)
			oponente = VERMELHA;
		else
			oponente = BRANCA;

		int x = colunaPos(pos);
		int y = linhaPos(pos);

		if ((pecas[pos] & DAMA) == 0) { // E uma peça simples
			int i;

			i = (cor == BRANCA) ? -1 : 1;

			// Analisa as diagonais /^ e \v
			if (x < 6 && y + i > 0 && y + i < 7 && (pecas[colLinhaPos(x + 1, y + i)] & ~DAMA) == oponente
					&& pecas[colLinhaPos(x + 2, y + 2 * i)] == VAZIA)
				return true;

			// Analisa as diagonais ^\ e v/
			if (x > 1 && y + i > 0 && y + i < 7 && (pecas[colLinhaPos(x - 1, y + i)] & ~DAMA) == oponente
					&& pecas[colLinhaPos(x - 2, y + 2 * i)] == VAZIA)
				return true;

		} else { // É uma dama
			int i, j;

			// Ve a diagonal \v
			i = x + 1;
			j = y + 1;
			while (i < 6 && j < 6 && pecas[colLinhaPos(i, j)] == VAZIA) {
				i++;
				j++;
			}

			if (i < 7 && j < 7 && (pecas[colLinhaPos(i, j)] & ~DAMA) == oponente) {
				i++;
				j++;

				if (i <= 7 && j <= 7 && pecas[colLinhaPos(i, j)] == VAZIA)
					return true;
			}

			// Ve a diagonal ^\
			i = x - 1;
			j = y - 1;
			while (i > 1 && j > 1 && pecas[colLinhaPos(i, j)] == VAZIA) {
				i--;
				j--;
			}

			if (i > 0 && j > 0 && (pecas[colLinhaPos(i, j)] & ~DAMA) == oponente) {
				i--;
				j--;

				if (i >= 0 && j >= 0 && pecas[colLinhaPos(i, j)] == VAZIA)
					return true;
			}

			// Ve a diagonal /^
			i = x + 1;
			j = y - 1;
			while (i < 6 && j > 1 && pecas[colLinhaPos(i, j)] == VAZIA) {
				i++;
				j--;
			}

			if (i < 7 && j > 0 && (pecas[colLinhaPos(i, j)] & ~DAMA) == oponente) {
				i++;
				j--;

				if (i <= 7 && j >= 0 && pecas[colLinhaPos(i, j)] == VAZIA)
					return true;
			}

			// Ve a diagonal v/
			i = x - 1;
			j = y + 1;
			while (i > 1 && j < 6 && pecas[colLinhaPos(i, j)] == VAZIA) {
				i--;
				j++;
			}

			if (i > 0 && j < 7 && (pecas[colLinhaPos(i, j)] & ~DAMA) == oponente) {
				i--;
				j++;

				if (i >= 0 && j <= 7 && pecas[colLinhaPos(i, j)] == VAZIA)
					return true;
			}
		}
		return false;
	}

	/**
	 * Faz uma jogada simples
	 */
	public void move(int from, int to) throws ExcecaoDeMovimentos {
		boolean temQueAtacar = temQueAtacar();

		aplicaMovimento(from, to);

		if (!temQueAtacar)
			mudaLado();
		else if (!podeAtacar(to))
			mudaLado();
	}

	/**
	 * Faz uma jogada multipla
	 */
	public void movimenta(Lista movimentos) throws ExcecaoDeMovimentos {
		Movimentos movimento;
		@SuppressWarnings("rawtypes")
		Enumeration e = movimentos.elementos();
		while (e.hasMoreElements()) {
			movimento = (Movimentos) e.nextElement();
			aplicaMovimento(movimento.getDe(), movimento.getPara());
		}
		mudaLado();
	}

	/**
	 * Muda o jogador atual
	 */
	private void mudaLado() {
		if (jogadorAtual == BRANCA)
			jogadorAtual = VERMELHA;
		else
			jogadorAtual = BRANCA;
	}

	/**
	 * Faz o movimento
	 */
	private void aplicaMovimento(int de, int para) throws ExcecaoDeMovimentos {
		if (!movimentoValido(de, para)) {
			System.out.println("de: " + de + ", para:" + para);
			throw new ExcecaoDeMovimentos();
		}

		limpaPeca(de, para);
		// Faz o movimento
		if (para < 4 && pecas[de] == BRANCA) {
			pecas[para] = DAMA_BRANCA;
		} else if (para > 27 && pecas[de] == VERMELHA) {
			pecas[para] = DAMA_VERMELHA;
		} else {
			pecas[para] = pecas[de];
		}
		pecas[de] = VAZIA;
	}

	/**
	 * Devolve a peça desejada.
	 * 
	 * @param pos
	 *            posição da peça
	 */
	public byte getPeca(int pos) throws ExcecaoDeCoordenadas {
		if (pos < 0 || pos > 32)
			throw new ExcecaoDeCoordenadas();

		return pecas[pos];
	}

	/**
	 * Indica se o jogo já terminou.
	 */
	public boolean terminou() {
		return pecasBrancas == 0 || pecasVermelhas == 0 || !naoNull(movimentosLegais());
	}

	/**
	 * Indica quem ganhou o jogo
	 */
	public int ganhador() {
		if (jogadorAtual == BRANCA) {
			if (movimentosLegais().taVazio()) {
				return VERMELHA;
			}else{
				if(pecasVermelhas == 0){
					return BRANCA;
				}
			}
		} else {
			if (movimentosLegais().taVazio()) {
				return BRANCA;
			}else{
				if(pecasBrancas == 0){
					return VERMELHA;
				}
			}
		}
		return 0;
	}

	/**
	 * Elimina uma peça do tabuleiro entre de e para
	 */
	private void limpaPeca(int de, int para) {
		int deLinha = linhaPos(de);
		int deColuna = colunaPos(de);
		int paraLinha = linhaPos(para);
		int paraColuna = colunaPos(para);

		int i, j;

		if (deColuna > paraColuna)
			i = -1;
		else
			i = 1;

		if (deLinha > paraLinha)
			j = -1;
		else
			j = 1;

		deColuna += i;
		deLinha += j;

		while (deLinha != paraLinha && deColuna != paraColuna) {
			int pos = colLinhaPos(deColuna, deLinha);
			int peca = pecas[pos];

			if ((peca & ~DAMA) == BRANCA)
				pecasBrancas--;
			else if ((peca & ~DAMA) == VERMELHA)
				pecasVermelhas--;

			pecas[pos] = VAZIA;
			deColuna += i;
			deLinha += j;
		}
	}

	/**
	 * Reseta o tabuleiro
	 */
	public void limpaTabuleiro() {
		int i;

		pecasBrancas = 12;
		pecasVermelhas = 12;

		jogadorAtual = VERMELHA;

		for (i = 0; i < 12; i++)
			pecas[i] = VERMELHA;

		for (i = 12; i < 20; i++)
			pecas[i] = VAZIA;

		for (i = 20; i < 32; i++)
			pecas[i] = BRANCA;
	}

	/**
	 * Indica se o valor é par
	 */
	private boolean ePar(int value) {
		return value % 2 == 0;
	}

	/**
	 * Indica a posição que corresponde ao par linha coluna.
	 * 
	 * @param coluna
	 *            coluna do tabuleiro (entre 0 e 7)
	 * @param linha
	 *            linha do tabuleiro (entre 0 e 7)
	 * @returns posição (entre 0 e 31)
	 */
	private int colLinhaPos(int coluna, int linha) {
		if (ePar(linha))
			return linha * 4 + (coluna - 1) / 2;
		else
			return linha * 4 + coluna / 2;
	}

	/**
	 * Devolve a linha correspondente à posição
	 */
	private int linhaPos(int valor) {
		return valor / 4;
	}

	/**
	 * Devolve a coluna correspondente à posição
	 */
	private int colunaPos(int valor) {
		return (valor % 4) * 2 + ((valor / 4) % 2 == 0 ? 1 : 0);
	}

}
