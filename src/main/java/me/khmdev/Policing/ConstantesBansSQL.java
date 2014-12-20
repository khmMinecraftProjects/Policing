package me.khmdev.Policing;

import me.khmdev.APIAuxiliar.whereIs.ConstantesServerSQL;
import me.khmdev.APIBase.Almacenes.ConstantesAlmacen;

public class ConstantesBansSQL {
	 
	public final static String tablaBan = "Ban";
	public final static String banId = "id";
	public final static String banUsuario = "usuario";
	public final static String banRazon = "razon";
	public final static String banServidor = "servidor";
	public final static String banFecha = "caduca";
	public final static String banCaducado = "caducado";
	public final static String banTipo = "tipo";
	public final static String tipoBan="Ban";
	public final static String tipoKick="Kick";
	
	public final static String tablaDenuncia="denuncia";
	public final static String denId="id";
	public final static String denDenunciante="denunciante";
	public final static String denDenunciado="denunciado";
	public final static String denServidor="servidor";
	public final static String denRazon="razon";
	public final static String denFecha="fecha";
	public final static String denSolucionado="solucionado";

	public static final String[] sql = {
		"CREATE TABLE IF NOT EXISTS " +tablaBan+ " ("+
				 banId+" MEDIUMINT NOT NULL AUTO_INCREMENT,"+
				 banTipo + " ENUM('"+ tipoBan + "', '" + tipoKick + "') DEFAULT '" + tipoBan + "',"+
				 banUsuario+" varchar(16) NOT NULL,"+
				 banRazon+" varchar(300) NOT NULL,"+
				 banServidor+" varchar(22) Default null,"+ 
				 banFecha+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
				 banCaducado+" tinyint(1) NOT NULL DEFAULT 0,"+
				
				 
				 "FOREIGN KEY ( "+ banUsuario+" ) REFERENCES "+ ConstantesAlmacen.tabla+"( "+ ConstantesAlmacen.id+" ) ON DELETE CASCADE ON UPDATE CASCADE,"+
				 "FOREIGN KEY ( "+ banServidor+" ) REFERENCES "+ ConstantesServerSQL.tablaServ+"( "+ ConstantesServerSQL.ServidorID+" ) ON DELETE CASCADE ON UPDATE CASCADE,"+
				 "PRIMARY KEY ( "+ banId+" ) "+
				")",
				"CREATE TABLE IF NOT EXISTS " +tablaDenuncia+" ("+
						  denId+ " MEDIUMINT NOT NULL AUTO_INCREMENT,"+
						  denDenunciante+" varchar(16) NOT NULL,"+
						  denDenunciado+" varchar(16) NOT NULL,"+
						  denServidor+" varchar(22) NOT NULL, "+
						  denRazon+" varchar(300) default \"\","+
						  denFecha+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
						  denSolucionado+" tinyint(1) NOT NULL DEFAULT 0,"+
						  "FOREIGN KEY ( "+denDenunciante+" ) REFERENCES "+ConstantesAlmacen.tabla+" ( "+ConstantesAlmacen.id+" ) ON DELETE CASCADE ON UPDATE CASCADE,"+
						  "FOREIGN KEY ( "+denDenunciado+" ) REFERENCES "+ConstantesAlmacen.tabla+" ( "+ConstantesAlmacen.id+" ) ON DELETE CASCADE ON UPDATE CASCADE,"+
						  "FOREIGN KEY ( "+denServidor+" ) REFERENCES "+ConstantesServerSQL.tablaServ+" ( "+ConstantesServerSQL.ServidorID+" ) ON DELETE CASCADE ON UPDATE CASCADE,"+
						  "PRIMARY KEY ( "+denId+" )" +
						")"
	
	};

}
