package abraham.daymanager

import java.io.Serializable

class EventosPOJO : Serializable{

    var titulo : String = ""
    var descripcion : String = ""
    var fecha : String = ""
    var hora : String = ""

    constructor()

    constructor(titulo: String, descripcion: String, fecha: String, hora: String) {
        this.titulo = titulo
        this.descripcion = descripcion
        this.fecha = fecha
        this.hora = hora
    }
}





