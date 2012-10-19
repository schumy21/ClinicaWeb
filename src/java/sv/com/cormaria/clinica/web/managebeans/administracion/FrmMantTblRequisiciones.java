/* To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.cormaria.clinica.web.managebeans.administracion;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.richfaces.component.UIDataTable;
import sv.com.cormaria.clinica.web.managebeans.base.PageBase;
import sv.com.cormaria.servicios.entidades.administracion.TblDetalleRequisicion;
import sv.com.cormaria.servicios.entidades.administracion.TblRequisiciones;
import sv.com.cormaria.servicios.entidades.catalogos.CatAreas;
import sv.com.cormaria.servicios.entidades.catalogos.CatTipoRequisicion;
import sv.com.cormaria.servicios.entidades.administracion.TblProducto;
import sv.com.cormaria.servicios.enums.EstadoRequisicion;
import sv.com.cormaria.servicios.facades.administracion.TblDetalleRequisicionFacadeLocal;
import sv.com.cormaria.servicios.facades.administracion.TblRequisicionesFacadeLocal;
import sv.com.cormaria.servicios.facades.catalogos.CatAreasFacadeLocal;
import sv.com.cormaria.servicios.facades.catalogos.CatTipoRequisicionFacadeLocal;
import sv.com.cormaria.servicios.facades.administracion.TblProductoFacadeLocal;

/**
 *
 * @author Mackk
 */
@ManagedBean (name = "frmMantTblRequisiciones")
@ViewScoped
public class FrmMantTblRequisiciones extends PageBase{
    private TblRequisiciones tblRequisiciones = new TblRequisiciones();
    private TblDetalleRequisicion tblDetalleReq = new TblDetalleRequisicion();
    
    @EJB
    private TblRequisicionesFacadeLocal facade;

    @EJB
    private TblDetalleRequisicionFacadeLocal detalleReqFacade;

    @EJB
    private CatTipoRequisicionFacadeLocal tipoReqFacade;

    @EJB
    private TblProductoFacadeLocal productoFacade;    
    
    @EJB
    private CatAreasFacadeLocal catAreaFacade;
 
    private List<CatAreas> catAreaList = new ArrayList<CatAreas>();

    private List<CatTipoRequisicion> catTipoReqList = new ArrayList<CatTipoRequisicion>();
   
    private List<TblDetalleRequisicion> detalleReqList = new ArrayList<TblDetalleRequisicion>();
    
    private List<TblProducto> productosList = new ArrayList<TblProducto>();
    
    private Integer numRequisicion;

    public TblDetalleRequisicion getTblDetalleReq() {
        return tblDetalleReq;
    }

    public void setTblDetalleReq(TblDetalleRequisicion tblDetalleReq) {
        this.tblDetalleReq = tblDetalleReq;
    }

    public CatTipoRequisicionFacadeLocal getTipoReqFacade() {
        return tipoReqFacade;
    }

    public void setTipoReqFacade(CatTipoRequisicionFacadeLocal tipoReqFacade) {
        this.tipoReqFacade = tipoReqFacade;
    }

    public List<TblProducto> getProductosList() {
        if (productosList.isEmpty()){
            try{
                productosList = productoFacade.findActive();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return productosList;
    }

    public void setProductosList(List<TblProducto> productosList) {
        this.productosList = productosList;
    }

    public List<TblDetalleRequisicion> getDetalleReqList() {
        if (detalleReqList.isEmpty()){
            if (this.getTblRequisiciones()!=null && this.getTblRequisiciones().getNumRequisicion()!=null){
                try{
                    detalleReqList = detalleReqFacade.findByNumRequisicion(this.getTblRequisiciones().getNumRequisicion());
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return detalleReqList;
    }

    public void setDetalleReqList(List<TblDetalleRequisicion> detalleReqList) {
        this.detalleReqList = detalleReqList;
    }

    public List<CatAreas> getCatAreaList() {
        if (catAreaList.isEmpty()){
            try{
                catAreaList = catAreaFacade.findActive();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return catAreaList;
    }

    public void setCatAreaList(List<CatAreas> catAreaList) {
        this.catAreaList = catAreaList;
    }

    public List<CatTipoRequisicion> getCatTipoReqList() {
        if (catTipoReqList.isEmpty()){
            try{
                catTipoReqList = tipoReqFacade.findActive();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return catTipoReqList;
    }

    public void setCatTipoReqList(List<CatTipoRequisicion> catTipoReqList) {
        this.catTipoReqList = catTipoReqList;
    }
    
    public Integer getNumRequisicion() {
        return numRequisicion;
    }

    public void setNumRequisicion(Integer numRequisicion) {
        this.numRequisicion = numRequisicion;
    }
    
    public TblRequisiciones getTblRequisiciones() {
        return tblRequisiciones;
    }

    public void setTblRequisiciones(TblRequisiciones tblRequisiciones) {
        this.tblRequisiciones = tblRequisiciones;
    }
    
    public void guardar(ActionEvent ae){
        try{
            if(tblRequisiciones.getNumRequisicion() != null && tblRequisiciones.getNumRequisicion()>0){
                facade.edit(tblRequisiciones);
            }else{
                if (this.getSessionBean().getUsuario()==null){
                    this.addError("Es necesario que este autenticado para poder ingresar una requisicion", "Es necesario que este autenticado para poder ingresar una requisicion");
                }else if(this.getSessionBean().getUsuario().getNumEmpleado()==null){
                    this.addError("El usuario no tiene un codigo de empleado asociado", "El usuario no tiene un codigo de empleado asociado");
                }else{
                    tblRequisiciones.setNumEmpleado(this.getSessionBean().getUsuario().getNumEmpleado());
                    tblRequisiciones.setEstRequisicion(EstadoRequisicion.CREADA);
                    facade.create(tblRequisiciones);
                }
            }
        }catch(Exception x){
            x.printStackTrace();
            this.addError(x.getMessage(), x.getMessage());
        }
    }
    
    public void agregarDetalleReq(ActionEvent ae){
        try{
            if (this.getTblDetalleReq().getTblDetalleRequisicionPK().getNumRequisicion()!=null && this.getTblDetalleReq().getTblDetalleRequisicionPK().getNumRequisicion()>0){
                System.out.println("Edit");
                this.tblDetalleReq = detalleReqFacade.edit(tblDetalleReq);
            }else{
                System.out.println("Create: "+this.getTblRequisiciones().getNumRequisicion());
                tblDetalleReq.getTblDetalleRequisicionPK().setNumRequisicion(this.getTblRequisiciones().getNumRequisicion());
                this.tblDetalleReq = detalleReqFacade.create(tblDetalleReq);
            }
            this.tblDetalleReq = new TblDetalleRequisicion();
            this.detalleReqList.clear();
        }catch(Exception ex){
            ex.printStackTrace();
            this.addError(ex.getMessage(), ex.getMessage());
        }
    }
    
    public void nuevo(ActionEvent ae){
        this.tblRequisiciones = new TblRequisiciones();
        this.detalleReqList.clear();
    }
    
    public void init(){
        if (this.getNumRequisicion()!=null && this.getNumRequisicion()>0){
            try{
                this.tblRequisiciones = this.facade.find(this.getNumRequisicion());
            }catch(Exception ex){
                this.addError(ex.getMessage(), ex.getMessage());
            }
        }
    }
    
    public void deleteDetalle(ActionEvent ae){
        try{
            UIDataTable table = (UIDataTable) ae.getComponent().getParent().getParent();
            TblDetalleRequisicion detalle = (TblDetalleRequisicion) table.getRowData();
            detalleReqFacade.remove(detalle);
            detalleReqList.clear();
        }catch(Exception x){
            x.printStackTrace();
            this.addError(x.getMessage(), x.getMessage());
        }
    }    
}