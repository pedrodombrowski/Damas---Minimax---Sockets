package principal;

import java.io.*;
import java.net.*;

/**
 * 
 * Classe que contém os métodos de comunicação socket do servidor
 * 
 */
public class Servidor {

	static int BUFF = 128000; // tamanho do nosso buffer

	static int INT = 4;
	static int DOUBLE = 8;
	static int FLOAT = 4;

	byte data[];
	byte buff[];

	int porta;
	boolean reverso = false;
	ServerSocket servidor;
	Socket sock;
	DatagramSocket recebe_sock, envia_sock;
	String endereco;

	BufferedInputStream entrada;
	BufferedOutputStream saida;

	/**
	 * Construtor que inicia o socket servidor
	 * 
	 * @param p
	 *            porta usada para o servidor
	 * @throws IOException
	 */
	public Servidor(int p) throws IOException {
		porta = p;

		try {
			servidor = new ServerSocket(porta, 100);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// inicializa os buffers

		buff = new byte[BUFF];
		data = new byte[BUFF];
	}

	// espera por conexões
	/**
	 * Bota o servidor para escutar por uma conexão
	 * 
	 * @throws IOException
	 */
	public void Connect() throws IOException {
		System.out.println("Servidor: Aguardando Conexão...");
		byte rev[] = new byte[1];
		rev[0] = 0;

		sock = servidor.accept();

		endereco = sock.getInetAddress().getHostName();

		System.out.println("Servidor: abrindo socket em " + endereco + " na porta " + porta);

		entrada = new BufferedInputStream(sock.getInputStream(), BUFF);
		saida = new BufferedOutputStream(sock.getOutputStream(), BUFF);

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
		System.out.print("Servidor: enviando " + len + " bytes: ");
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
			System.out.println("Servidor: Muitos bytes para serem recebidos!");

		while (totalbytes < maxlen) {
			numbytes = entrada.read(data);

			// copia os bytes para o buffer de resultado
			for (i = totalbytes; i < totalbytes + numbytes; i++)
				val[i] = data[i - totalbytes];

			totalbytes += numbytes;
		}

		System.out.print("Servidor: recebeu " + maxlen + " bytes - ");
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
		sock.close();
		System.out.println("Servidor: fechando socket");
	}

}
