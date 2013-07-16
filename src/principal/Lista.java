package principal;

import java.util.*;

/**
 * Nodos da lista
 */
class nodosLista {
	nodosLista anterior, proximo;
	Object valor;

	public nodosLista(Object elem, nodosLista nodoAnterior, nodosLista proximoNodo) {
		valor = elem;
		anterior = nodoAnterior;
		proximo = proximoNodo;
	}
}

/**
 * Classe para listas genéricas
 */
public class Lista implements Cloneable {
	private nodosLista cabeca;
	private nodosLista rabo;
	private int count;

	public Lista() {
		count = 0;
	}

	/**
	 * Adiciona um elemento no início da lista
	 */
	public void push_frente(Object elem) {
		nodosLista nodo = new nodosLista(elem, null, cabeca);

		if (cabeca != null)
			cabeca.anterior = nodo;
		else
			rabo = nodo;

		cabeca = nodo;
		count++;
	}

	/**
	 * Adiciona um elemento na cauda da lista
	 */
	public void push_tras(Object elem) {
		nodosLista nodo = new nodosLista(elem, rabo, null);

		if (rabo != null)
			rabo.proximo = nodo;
		else
			cabeca = nodo;

		rabo = nodo;
		count++;
	}

	/**
	 * Remove o elemento do início da lista e o devolve
	 */
	public Object removeInicio() {
		if (cabeca == null)
			return null;

		nodosLista node = cabeca;
		cabeca = cabeca.proximo;

		if (cabeca != null)
			cabeca.anterior = null;
		else
			rabo = null;

		count--;
		return node.valor;
	}

	/**
	 * Remove o elemento do fim da lista e o devolve
	 */
	public Object pop_tras() {
		if (rabo == null)
			return null;

		nodosLista nodo = rabo;
		rabo = rabo.anterior;

		if (rabo != null)
			rabo.proximo = null;
		else
			cabeca = null;

		count--;
		return nodo.valor;
	}

	/**
	 * Diz se a lista está vazia
	 */
	public boolean taVazio() {
		return cabeca == null;
	}

	/**
	 * Devolve o número de elementos na lista
	 */
	public int tamanho() {
		return count;
	}

	/**
	 * Acrescenta outra lista
	 * 
	 * @param outra
	 *            lista a ser adicionada
	 */
	public void adicionar(Lista outra) {
		nodosLista node = outra.cabeca;

		while (node != null) {
			push_tras(node.valor);
			node = node.proximo;
		}
	}

	/**
	 * Limpa a lista
	 */
	public void limpar() {
		cabeca = rabo = null;
	}

	/**
	 * Devolve o elemento à cabeca da lista sem o remover
	 */
	public Object pegaCabeca() {
		if (cabeca != null)
			return cabeca.valor;
		else
			return null;
	}

	/**
	 * Devolve o elemento à cauda da lista sem o remover
	 * 
	 */
	public Object peek_rabo() {
		if (rabo != null)
			return rabo.valor;
		else
			return null;
	}

	/**
	 * Verifica se o elemento existe na lista
	 */
	public boolean tem(Object elem) {
		nodosLista nodo = cabeca;

		while (nodo != null && !nodo.valor.equals(elem))
			nodo = nodo.proximo;

		return nodo != null;
	}

	/**
	 * Duplica a lista
	 */
	public Object clone() {
		Lista temp = new Lista();
		nodosLista nodo = cabeca;

		while (nodo != null) {
			temp.push_tras(nodo.valor);
			nodo = nodo.proximo;
		}

		return temp;
	}

	/**
	 * Devolve uma representação em string
	 */
	public String toString() {
		String temp = "[";
		nodosLista nodo = cabeca;

		while (nodo != null) {
			temp += nodo.valor.toString();
			nodo = nodo.proximo;
			if (nodo != null)
				temp += ", ";
		}
		temp += "]";

		return temp;
	}

	/**
	 * Classe para fazer enumerações
	 */
	@SuppressWarnings("rawtypes")
	class Enum implements Enumeration {
		/**
		 * Elemento corrente
		 */
		private nodosLista nodo;

		Enum(nodosLista iniciar) {
			nodo = iniciar;
		}

		/**
		 * Indica se ainda existem elementos
		 */
		public boolean hasMoreElements() {
			return nodo != null;
		}

		/**
		 * Devolve o próximo elemento
		 */
		public Object nextElement() throws NoSuchElementException {
			Object temp;

			if (nodo == null)
				throw new NoSuchElementException();

			temp = nodo.valor;
			nodo = nodo.proximo;

			return temp;
		}
	}

	/**
	 * Devolve uma enumeração dos elementos da lista
	 */
	@SuppressWarnings("rawtypes")
	public Enumeration elementos() {
		return new Enum(cabeca);
	}
}
