package principal;

import java.io.*;
import java.net.*;

/**
 * Classe que contém os métodos de comunicação socket do cliente
 * 
 */
public class Cliente {

	static int BUFF = 128000; // tamanho do buffer

	byte data[];
	byte buff[];

	int porta;
	String endereco;
	Socket socket;
	DatagramSocket socket_entrada, socket_saida;

	BufferedInputStream entrada;
	BufferedOutputStream saida;

	/**
	 * Construtor que inicia o socket cliente
	 * 
	 * @param p
	 *            porta do servidor
	 * @param endereco
	 *            endereço do servidor
	 * @throws IOException
	 */

	public Cliente(int p, String endereco) throws IOException {
		porta = p;
		this.endereco = endereco;

		try {
			socket = new Socket(InetAddress.getByName(endereco), porta);
			entrada = new BufferedInputStream(socket.getInputStream(), BUFF);
			saida = new BufferedOutputStream(socket.getOutputStream(), BUFF);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// inicializa os buffers

		buff = new byte[BUFF];
		data = new byte[BUFF];

		System.out.println("Cliente: abrindo socket para " + endereco + " na porta " + porta);

	}

	/**
	 * Envia um array de bytes via socket
	 * 
	 * @param vals
	 *            array a ser enviado
	 * @param len
	 *            tamanho do array
	 * @throws IOException
	 */
	public void EnviaBytes(byte vals[], int len) throws IOException {
		System.out.print("Cliente: enviando " + len + " bytes: ");
		for (int i = 0; i < len; i++)
			System.out.print(vals[i] + " ");
		System.out.println("");

		saida.write(vals, 0, len);
		saida.flush();
	}

	/**
	 * Recebe um array de bytes via socket
	 * 
	 * @param val
	 *            array de bytes a ser usado para armazenar os bytes recebidos
	 * @param maxlen
	 *            retorno número de elementos do array
	 * @return retorno
	 * @throws IOException
	 */
	public int RecebeBytes(byte val[], int maxlen) throws IOException {
		int i;
		int totalbytes = 0;
		int numbytes;

		if (maxlen > BUFF)
			System.out.println("Cliente: Muitos bytes para serem recebidos!");

		while (totalbytes < maxlen) {
			numbytes = entrada.read(data);

			// copia os bytes para o buffer de resultado
			for (i = totalbytes; i < totalbytes + numbytes; i++)
				val[i] = data[i - totalbytes];

			totalbytes += numbytes;
		}

		System.out.print("Cliente: recebeu " + maxlen + " bytes - ");
		for (i = 0; i < maxlen; i++)
			System.out.print(val[i] + " ");
		System.out.println("");

		return maxlen;
	}

	/**
	 * Fecha o socket
	 * 
	 * @throws IOException
	 */
	public void Fecha() throws IOException {
		socket.close();

		System.out.println("Cliente: fechando socket");
	}
}
