package tesis.compraventa;

public class Model {
    private int id;
    private String titulo;
    private String descripcion;
    private String valor;
    private String comuna;
    private String categoria;
    private byte[] image;

    public Model(int id, String titulo, String descripcion, String valor, String comuna, String categoria, byte[] image) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.valor = valor;
        this.comuna = comuna;
        this.categoria = categoria;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
