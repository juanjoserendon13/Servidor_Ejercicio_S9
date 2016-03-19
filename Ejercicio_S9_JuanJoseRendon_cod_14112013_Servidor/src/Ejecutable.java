import processing.core.PApplet;

public class Ejecutable extends PApplet {
	Logica logica;

	public void setup() {
		logica = new Logica(this);
		size(200, 200);
	}

	public void draw() {
		background(255);
	}

	public void stop() {
		super.stop();

		logica.guardar();
	}

}
