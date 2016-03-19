import Comunicacion.Comunicacion;
import processing.core.*;

public class Logica {
	private PApplet app;
	private Comunicacion com;

	public Logica(PApplet app) {
		this.app = app;
		com = new Comunicacion(app);
		com.start();
	}

	public void guardar() {
	
		com.guardar();
		
	}
}
