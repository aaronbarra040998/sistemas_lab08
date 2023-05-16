/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import modelos.Alumnos;
import modelos.Conexion;
import modelos.Cursos;
import modelos.Matriculas;

/**
 *
 * @author arondarkas
 */
public class MatriculaDAOImpl implements IMatriculaDAO{

    @Override
    public List<Alumnos> buscarAlumnos(Alumnos alumno) {
        Connection co =null;
        Statement stm= null;
        ResultSet rs=null;
        String sql="SELECT * FROM alumnos WHERE nombre LIKE'%" + alumno.getNombre() + "%'";
        List<Alumnos> listaAlumnos= new ArrayList<Alumnos>();

        try {			
                Conexion con = new Conexion();
                co=con.Conectar();
                stm=co.createStatement();
                rs=stm.executeQuery(sql);
                while (rs.next()) {
                        Alumnos nalumno=new Alumnos();
                        nalumno.setCodigo(rs.getInt(1));
                        nalumno.setNombre(rs.getString(2));
                        nalumno.setDireccion(rs.getString(3));
                        nalumno.setEmail(rs.getString(4));
                        nalumno.setTelefono(rs.getString(5));
                        nalumno.setCelular(rs.getString(6));
                        nalumno.setSexo(rs.getString(7));
                        nalumno.setFec_nac(rs.getDate(8));
                        nalumno.setEstado(rs.getString(9));
                        listaAlumnos.add(nalumno);
                }
                stm.close();
                rs.close();
                co.close();
        } catch (SQLException e) {
                System.out.println("Error:Clase MatriculaDaoImpl,"
                        + "método buscarAlumnos");
        }
        return listaAlumnos;
    }
    @Override
 public List<Cursos> buscarCursos() {
        Connection co =null;
        Statement stm= null;
        ResultSet rs=null;
        String sql="SELECT * FROM cursos ORDER BY codigo";
        List<Cursos> listaCursos= new ArrayList<Cursos>();

        try {			
                Conexion con = new Conexion();
                co=con.Conectar();
                stm=co.createStatement();
                rs=stm.executeQuery(sql);
                while (rs.next()) {
                        Cursos curso=new Cursos();
                        curso.setCodigo(rs.getInt(1));
                        curso.setNombre(rs.getString(2));
                        curso.setCosto(rs.getDouble(3));
                        curso.setFec_ini(rs.getDate(4));
                        curso.setFec_fin(rs.getDate(5));
                        curso.setDuracion(rs.getInt(6));
                        curso.setSesiones(rs.getInt(7));
                        curso.setCapacidad(rs.getInt(8));
                        curso.setInscritos(rs.getInt(9));
                        curso.setEstado(rs.getString(10));
                        listaCursos.add(curso);
                }
                stm.close();
                rs.close();
                co.close();
        } catch (SQLException e) {
                System.out.println("Error:Clase MatriculaDaoImpl,"
                        + "método obtener");
        }
        return listaCursos;
    }
 public String getFecha() {
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        DateFormat formato = new SimpleDateFormat( "yyyy-MM-dd" );
        return formato.format( date ) ;
    }

    @Override
    public boolean grabarMatricula(String[] datosMatricula, String[] codigoCursos, String[] montos) {
        Conexion co = new Conexion();
        String xcodm = co.generarCodigo("matriculas","codigo");
        boolean registrar = true;
	Statement stm= null;
        Connection con=null;      
        
        String sql = "insert into matriculas (codigo,fecha,nro_doc," +
                 "codigo_alumno,total,estado) values (?,?,?,?,?,'A') ";
        String xfech = this.getFecha();
        String xndoc = datosMatricula[0];
        String xcoda = datosMatricula[1];
        String xtota = datosMatricula[2];

        try {			    
            con=co.Conectar();
            stm= con.createStatement();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, xcodm);
            ps.setString(2, xfech);
            ps.setString(3, xndoc);
            ps.setString(4, xcoda);
            ps.setString(5, xtota);
            ps.executeUpdate();
            
            for( int xc=0 ; xc < codigoCursos.length ; xc++ ){
                String xcodCurso = codigoCursos[xc];
                this.grabarNuevoDetalle(con,xcodm,xcodCurso,montos[xc]);
            }

            
            stm.close();
            con.close();
        } catch (SQLException e) {
                System.out.println("Error: Clase MatriculaDaoImpl, "
                        + "método grabarMatricula");
                e.printStackTrace();
                return false;
        }
        return registrar;
    }
    
    public void grabarNuevoDetalle( Connection xcon,String xcodm, String xcodc,String xmonto ) 
        throws SQLException {
        String sql = "insert into detalles (codigo_matricula,codigo_curso," +
                     "monto,asistencias,nota,estado) values (?,?,?,0,0,'A') ";

        PreparedStatement ps = xcon.prepareStatement(sql);
        ps.setString(1, xcodm);
        ps.setString(2, xcodc);
        ps.setString(3, xmonto);
        ps.executeUpdate();

        // actualizar nro de inscritos en curso
        sql = "update cursos set inscritos=inscritos+1 where codigo=?";
        PreparedStatement psc = xcon.prepareStatement(sql);
        psc.setString(1, xcodc);
        psc.executeUpdate();
    }

    @Override
    public List<Matriculas> obtener() {
        Connection co = null;
        Statement stm = null;
        ResultSet rs = null;
        String sql = "select m.codigo, m.fecha, m.nro_doc, a.codigo, m.total, m.estado "
                + "from matriculas m "
                + "join alumnos a on m.codigo_alumno = a.codigo ";
        List<Matriculas> listaMatriculas = new ArrayList<Matriculas>();
        try {
            Conexion con = new Conexion();
            co = con.Conectar();
            stm = co.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                Matriculas matricula = new Matriculas();
                matricula.setCodigo(rs.getInt(1));
                matricula.setFecha(rs.getDate(2));
                matricula.setNro_doc(rs.getString(3));
                matricula.setCodigo_alumno(rs.getInt(4));
                matricula.setTotal(rs.getDouble(5));
                matricula.setEstado(rs.getString(6));
                listaMatriculas.add(matricula);
            }
        } catch (SQLException e) {
            System.out.println("Error:clase MatriculaDaoImpl, método obtener");
        }
        return listaMatriculas;
    }

    @Override
    public List<Matriculas> buscarPorNroDoc(String nro_doc) {
        Connection co = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        String sql = "SELECT m.codigo, m.fecha, m.nro_doc, a.codigo, m.total, m.estado "
                + "FROM matriculas m "
                + "JOIN alumnos a ON m.codigo_alumno = a.codigo "
                + "WHERE m.nro_doc = ?";
        List<Matriculas> listaMatriculas = new ArrayList<>();
        try {
            Conexion con = new Conexion();
            co = con.Conectar();
            stm = co.prepareStatement(sql);
            stm.setString(1, nro_doc);
            rs = stm.executeQuery();
            while (rs.next()) {
                Matriculas matricula = new Matriculas();
                matricula.setCodigo(rs.getInt(1));
                matricula.setFecha(rs.getDate(2));
                matricula.setNro_doc(rs.getString(3));
                matricula.setCodigo_alumno(rs.getInt(4));
                matricula.setTotal(rs.getDouble(5));
                matricula.setEstado(rs.getString(6));
                listaMatriculas.add(matricula);
            }
        } catch (SQLException e) {
            System.out.println("Error: clase MatriculaDAOImpl, método buscarPorNroDoc");
        }
        return listaMatriculas;
    }

}

