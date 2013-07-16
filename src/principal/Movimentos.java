package principal;

/**
 * Serve para guardar os movimentos
 */
class Movimentos {
	/**
	 * Casa de partida
	 */
	private int de;

	/**
	 * Casa destino
	 */
	private int para;

	/**
	 * Inicializa uma jogada.
	 */
	Movimentos(int de, int para) {
		this.de = de;
		this.para = para;
	}

	/**
	 * Devolve a casa de partida
	 */
	public int getDe() {
		return de;
	}

	/**
	 * Devolve a casa destino
	 */
	public int getPara() {
		return para;
	}

	/**
	 * Devolve uma representação string da jogada
	 */
	public String toString() {
		return "(" + de + "," + para + ")";
	}
}
