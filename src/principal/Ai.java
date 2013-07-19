package principal;

/**
 * Classe que cont�m a intelig�ncia artificial do jogo
 */
public class Ai {
	/**
	 * Tabuleiro do jogo
	 */
	private Tabuleiro tabuleiroAtual;

	/**
	 * Cor usada pelo computador
	 */
	private int cor;

	/**
	 * Profundidade m�xima para o minimax
	 */
	private static final int profundidadeMaxima = 6;

	/**
	 * Peso das casas do tabuleiro
	 */
	private static final int valores[] = {  4, 4, 4, 4, 
						4, 3, 3, 3, 
						3, 2, 2, 4, 
						4, 2, 1, 3, 
						3, 1, 2, 4, 
						4, 2, 2, 3, 
						3, 3, 3, 4, 
						4, 4, 4, 4 };

	public Tabuleiro getTabuleiroAtual() {
		return tabuleiroAtual;
	}

	public void setTabuleiroAtual(Tabuleiro tabuleiroAtual) {
		this.tabuleiroAtual = tabuleiroAtual;
	}

	/**
	 * Construtor.
	 * 
	 * @param tabuleiro
	 *            Tabuleiro que o Ai deve usar para fazer as jogadas.
	 */
	Ai(Tabuleiro tabuleiro) {
		tabuleiroAtual = tabuleiro;
	}

	/**
	 * Define a cor que o Ai deve jogar
	 * 
	 * @param cor
	 */
	public void setCor(int cor) {
		this.cor = cor;
	}

	/**
	 * Busca a cor que o Ai esta jogando
	 * 
	 * @return cor do Ai
	 */
	public int getCor() {
		return this.cor;
	}

	/**
	 * Executa uma jogada.
	 */
	public Lista joga() {
		try {
			Lista movimentos = minimax(tabuleiroAtual);

			if (!movimentos.taVazio()) {
				tabuleiroAtual.movimenta(movimentos);
				return movimentos;
			} else {
				return null;
			}
		} catch (ExcecaoDeMovimentos edm) {
			edm.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Muda o tabuleiro associado
	 */
	public void mudaTabuleiro(Tabuleiro tabuleiro) {
		tabuleiroAtual = tabuleiro;
	}

	/**
	 * Indica se a jogada n�o � nula
	 */
	private boolean possoJogar(Lista movimentos) {
		return !movimentos.taVazio() && !((Lista) movimentos.pegaCabeca()).taVazio();
	}

	/**
	 * Implementa o algoritmo minimax
	 */
	private Lista minimax(Tabuleiro tabuleiro) throws ExcecaoDeMovimentos {
		Lista sucessores;
		Lista movimento, melhorMovimento = null;
		Tabuleiro proximoTabuleiro;
		int valor, valorMaximo = Integer.MIN_VALUE;

		sucessores = tabuleiro.movimentosLegais();
		while (possoJogar(sucessores)) {
			movimento = (Lista) sucessores.removeInicio();
			proximoTabuleiro = (Tabuleiro) tabuleiro.copiar();

			proximoTabuleiro.movimenta(movimento);
			valor = movimentacaoMinima(proximoTabuleiro, 1, valorMaximo, Integer.MAX_VALUE);

			if (valor > valorMaximo) {
				valorMaximo = valor;
				melhorMovimento = movimento;
			}
		}
		return melhorMovimento;
	}

	/**
	 * Implementa a avali��o da jogada do ponto de vista do jogador m�ximo
	 */
	private int movimentacaoMaxima(Tabuleiro tabuleiro, int profundidade, int alfa, int beta) throws ExcecaoDeMovimentos {
		if (cortaArvore(tabuleiro, profundidade))
			return forcaDoJogador(tabuleiro);

		Lista sucessores;
		Lista movimentacao;
		Tabuleiro proximoTabuleiro;
		int valor;

		sucessores = tabuleiro.movimentosLegais();
		while (possoJogar(sucessores)) {
			movimentacao = (Lista) sucessores.removeInicio();
			proximoTabuleiro = (Tabuleiro) tabuleiro.copiar();
			proximoTabuleiro.movimenta(movimentacao);
			valor = movimentacaoMinima(proximoTabuleiro, profundidade + 1, alfa, beta);

			if (valor > alfa) {
				alfa = valor;
			}

			if (alfa > beta) {
				return beta;
			}
		}
		return alfa;
	}

	/**
	 * Implementa a avalia��o da jogada do ponto de vista do jogador m�nimo
	 */
	private int movimentacaoMinima(Tabuleiro tabuleiro, int profundidade, int alfa, int beta) throws ExcecaoDeMovimentos {
		if (cortaArvore(tabuleiro, profundidade))
			return forcaDoJogador(tabuleiro);

		Lista sucessores;
		Lista movimentacao;
		Tabuleiro proximoTabuleiro;
		int valor;

		sucessores = (Lista) tabuleiro.movimentosLegais();
		while (possoJogar(sucessores)) {
			movimentacao = (Lista) sucessores.removeInicio();
			proximoTabuleiro = (Tabuleiro) tabuleiro.copiar();
			proximoTabuleiro.movimenta(movimentacao);
			valor = movimentacaoMaxima(proximoTabuleiro, profundidade + 1, alfa, beta);

			if (valor < beta) {
				beta = valor;
			}

			if (beta < alfa) {
				return alfa;
			}
		}
		return beta;
	}

	/**
	 * Retorna a for�a do jogador
	 */
	private int forcaDoJogador(Tabuleiro tabuleiro) {
		int dama;
		@SuppressWarnings("unused")
		int inimigo, damaInimigo;

		if (cor == Tabuleiro.BRANCA) {
			dama = Tabuleiro.DAMA_BRANCA;
			inimigo = Tabuleiro.VERMELHA;
			damaInimigo = Tabuleiro.DAMA_VERMELHA;
		} else {
			dama = Tabuleiro.DAMA_VERMELHA;
			inimigo = Tabuleiro.BRANCA;
			damaInimigo = Tabuleiro.DAMA_BRANCA;
		}

		int forca = 0;
		int forcaDoInimigo = 0;
		int peca;

		try {
			for (int i = 0; i < 32; i++) {
				peca = tabuleiro.getPeca(i);

				if (peca != Tabuleiro.VAZIA)
					if (peca == cor || peca == dama)
						forca += calculaValor(peca, i);
					else
						forcaDoInimigo += calculaValor(peca, i);
			}
		} catch (ExcecaoDeCoordenadas edc) {
			edc.printStackTrace();
			System.exit(-1);
		}

		return forca - forcaDoInimigo;
	}

	/**
	 * Calcula a for�a de uma pe�a
	 */
	private int calculaValor(int peca, int pos) {
		int valor;

		if (peca == Tabuleiro.BRANCA) { // Peca simples
			if (pos >= 4 && pos <= 7) {
				valor = 7;
			} else {
				valor = 5;
			}
		} else if (peca != Tabuleiro.VERMELHA) { // Peca simples
			if (pos >= 24 && pos <= 27) {
				valor = 7;
			} else {
				valor = 5;
			}
		} else { // dama
			valor = 10;
		}
		return valor * valores[pos];
	}

	/**
	 * Indica se se pode cortar a �rvore
	 * 
	 * @param tabuleiro
	 *            tabuleiro do jogo
	 * @param profundidade
	 *            profundidade do corte
	 * 
	 */
	private boolean cortaArvore(Tabuleiro tabuleiro, int profundidade) {
		return profundidade > profundidadeMaxima || tabuleiro.terminou();
	}

}
