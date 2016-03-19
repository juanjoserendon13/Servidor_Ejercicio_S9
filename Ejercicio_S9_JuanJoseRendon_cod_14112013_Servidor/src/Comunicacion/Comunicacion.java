package Comunicacion;

import processing.core.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;

import processing.data.XML;
import Memoria.Usuario;

//import Helados.Helado;

public class Comunicacion extends Thread {

	private DatagramSocket socket;

	private LinkedList<Usuario> usuarios;

	private int puerto;
	// private Helado helado;
	private Mensaje msj = null;
	private DatagramPacket pack;
	private boolean agregarUsuario = false;
	XML root;
	PApplet app;

	public Comunicacion(PApplet p) {
		this.app = p;
		puerto = 5100;
		// helado = new Helado();
		usuarios = new LinkedList<Usuario>();
		// -----

		// xml=p.loadXML("../data/backup.xml");
		///// creación de archivo xml si este no existe

		File binFolder = new File(app.sketchPath);
		File proyecto = binFolder.getParentFile();
		File datos = new File(proyecto.getAbsolutePath() + "\\data\\backup.xml");
		System.out.println(datos.getAbsolutePath());
		if (datos.exists()) {
			root = app.loadXML(datos.getAbsolutePath());
			System.out.print("Archivo cargado");
		} else {
			System.out.print("el archivo no existia ...");
			root = app.parseXML("<usuarios></usuarios>");
			app.saveXML(root, datos.getAbsolutePath());
			System.out.println("... fue creado");
		}

		try {
			socket = new DatagramSocket(puerto);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		while (true) {
			try {
				recibir();
				sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void enviar(String o, int puerto, InetAddress direccion) {
		byte[] datos = o.getBytes();

		try {
			DatagramPacket enviar = new DatagramPacket(datos, datos.length, direccion, puerto);
			socket.send(enviar);
			pack = null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	public void recibir() {
		byte[] buzon = new byte[1024];

		pack = new DatagramPacket(buzon, buzon.length);

		try {
			System.out.println("esperando");
			socket.receive(pack);
			int purtoTemporal = pack.getPort();
			InetAddress direccionTemporal = pack.getAddress();
			System.out.println("recibi");
			// ------Deserializar
			if (pack.getData() != null) {
				msj = deserializar(pack.getData());
				System.out.println(msj.getTipo());
			}

			// ----Tipo de mensaje Registro o Ingreso
			if (msj.getTipo().equals("Registro")) {
				if (usuarios.size() < 1) {
					agregarUsuario = true;
				}
				int temp = 0;
				for (int i = 0; i < usuarios.size(); i++) {
					Usuario u = usuarios.get(i);

					if (u.getUsuario().equals(msj.getNombre())) {
						// enviar("false");
						temp = i;
						agregarUsuario = false;
						System.out.println("Ya estoy");
					} else if (!u.getUsuario().equals(msj.getNombre()) && temp == 0) {
						agregarUsuario = true;
						System.out.println("soy nuevo");
					}
				}
				// / define el agregar un nuevo usuario
				if (agregarUsuario == true) {
					usuarios.add(new Usuario(msj.getNombre(), msj.getContra()));
					guardarUsuarios(msj);
					agregarUsuario = false;
				}
				System.out.println("cantidadUsuarios: " + usuarios.size());

			}
			// -------Fin registro
			if (msj.getTipo().equals("Ingreso")) {
				if (usuarios.size() < 1) {
					enviar("false", purtoTemporal, direccionTemporal);
					System.out.println("no hay usuarios registrados");
				}
				for (Usuario u : usuarios) {
					if (u.getUsuario().equals(msj.getNombre()) && u.getContra().equals(msj.getContra())) {
						enviar("true", purtoTemporal, direccionTemporal);
						System.out.println("Registrado");
					} else {
						enviar("false", purtoTemporal, direccionTemporal);
						System.out.println("No registrado");
					}
				}
			}

			// ----------------guardar XML

			// ---------Fin ingreso

			// ----Validacion Tipo Helado

			// helado.tipoHelado(msj.getTipo());
			// System.out.println("mi helado es" + " " + msj.getTipo());
			//
			// if (helado.getPrecio() != null) {
			// System.out.println("precio es" + " " + helado.getPrecio());
			// enviar(helado.getPrecio(), purtoTemporal, direccionTemporal);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void guardarUsuarios(Mensaje ms) {

		// String nombre =ms.getNombre();
		XML newChild = root.addChild("usuario");
		newChild.setString("nombre", ms.getNombre());
		newChild.setString("contraseña", ms.getContra());

	}

	// ///////////////////

	public Mensaje deserializar(byte[] bytes) {
		ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
		Mensaje aux = null;
		try {
			ObjectInputStream is = new ObjectInputStream(byteArray);
			aux = (Mensaje) is.readObject();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aux;
	}

	public void guardar() {

		app.saveXML(root, "../data/backup.xml");
		System.out.println("ACCIONES GUARDAS");

	}

}