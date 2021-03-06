package edu.eci.cvds.beans;

import edu.eci.cvds.samples.entities.Prestamo;
import edu.eci.cvds.samples.entities.Recurso;
import edu.eci.cvds.samples.entities.RecursoTipo;
import edu.eci.cvds.samples.services.ServiciosBiblioteca;
import edu.eci.cvds.samples.services.ServiciosBibliotecaException;
import edu.eci.cvds.samples.services.ServiciosBibliotecaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author 2LateTeam
 *
 */
@ManagedBean(name = "recursoBean")
@ViewScoped
public class RecursoBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ShiroLoginBean.class);

    private ServiciosBiblioteca serviciosBiblioteca;

    private String nombre;
    private String ubicacion;
    private RecursoTipo recursoTipo;
    private int capacidad;
    private String identificadorInterno;
    private String estado;
    private List<Recurso> recursosList;
    private static ScheduleModel eventModel = new DefaultScheduleModel();



    public RecursoBean(){
        serviciosBiblioteca = ServiciosBibliotecaFactory.getInstance().getServiciosBiblioteca();
        recursosList = consultarRecursos();

        cargarEventos();
    }


    public List<Recurso> getRecursosList() {

        return recursosList;
    }


    public void cargarEventos(){
        for (int i = 0; i<recursosList.size(); i++){
            List<Prestamo> prestamosReserva = null;
            try {
                prestamosReserva = serviciosBiblioteca.consultarPrestamosRecurso(recursosList.get(i));

            } catch (ServiciosBibliotecaException e) {
                e.printStackTrace();
            }

            SimpleDateFormat formatter=new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
            for (int j = 0; j<prestamosReserva.size(); j++) {
                Date fin = null;
                Date inicio = null;
                try {
                    inicio = formatter.parse(prestamosReserva.get(j).getFechaInicio());
                    fin = formatter.parse(prestamosReserva.get(j).getFechaFin());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DefaultScheduleEvent event = new DefaultScheduleEvent("Prestamo recurso " + recursosList.get(i).getIdentificadorInterno(),inicio,fin);

                System.out.println(inicio);
                System.out.println(fin);

                eventModel.addEvent(event);

            }

        }

    }


    public static void añadirEvento(DefaultScheduleEvent ev){
        eventModel.addEvent(ev);
    }
    public ScheduleModel getEventModel(){
        return eventModel;
    }



    public void setRecursoList(List<Recurso> recursosList) {
        this.recursosList = recursosList;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getIdentificadorInterno() {
        return identificadorInterno;
    }

    public void setIdentificadorInterno(String identificadorInterno) {
        this.identificadorInterno = identificadorInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RecursoTipo getRecursoTipo() {
        return recursoTipo;
    }

    public RecursoTipo[] recursoTipos(){
        return RecursoTipo.values();
    }

    public void setRecursoTipo(RecursoTipo recursoTipo) {
        this.recursoTipo = recursoTipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }
    
    public String getEstado(){
        return estado;
    }

    public void setEstado(String est){
        estado = est;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void registrarRecurso() {
        try{
            Recurso recurso = new Recurso(nombre, ubicacion, recursoTipo, capacidad);

            serviciosBiblioteca.registrarRecurso(recurso);

            facesError("Registro exitoso");

        } catch (ServiciosBibliotecaException e) {
            facesError(e.getMessage());
        }
    }

    public void modificarRecurso(){
        try{
            serviciosBiblioteca.modificarRecurso(identificadorInterno, estado);
        }catch (ServiciosBibliotecaException e) {
            facesError(e.getMessage());
        }
        
    }
    public List<Recurso> consultarRecursos(){
        List<Recurso> recs = null;
        try {
            recs = serviciosBiblioteca.consultarRecursos();

            facesError("Consulta exitosa");
        }catch (ServiciosBibliotecaException e) {
            facesError(e.getMessage());
        }
        return recs;
    }

/*    public List<Prestamo> consultarReservas(){
        List<Prestamo> resul = null;
        try{
            resul = serviciosBiblioteca.
        }catch (ServiciosBibliotecaException e){

        }
    }


    /**
     * Adds a new SEVERITY_ERROR FacesMessage for the ui
     * @param message Error Message
     */
    private void facesError(String message) {
        FacesContext.getCurrentInstance().addMessage("Registro: ", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "error"));
    }

}
