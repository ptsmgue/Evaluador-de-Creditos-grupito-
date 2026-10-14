-- create a table
create database EvaluadorCrediticio;
use EvaluadorCrediticio;
-- create
CREATE TABLE Usuario (
    idUsuario int not null auto_increment,
    nombreUsuario varchar(100),
    contrasena varchar(100),
    Primary key(idUsuario)
);

Create table Cliente(
  idCliente int not null auto_increment,
  nombreCliente varchar(100),
  ingresos Decimal(10,2),
  gastos Decimal(10,2),
  historial varchar(255),
  Primary key(idCliente)
);

Create table Empresa(
  idEmpresa int not null auto_increment,
  rubro varchar(150),
  director varchar(150),
  idCliente int,
  Primary key(idEmpresa),
  Foreign key(idCliente) references Cliente(idCliente)
);

Create table Persona(
  idPersona int not null auto_increment,
  nombrePersona varchar(50),
  apellidoPersona varchar(50),
  dni varchar(8),
  idCliente int,
  Primary key(idPersona),
  Foreign key(idCliente) references Cliente(idCliente)
);



insert into Cliente (nombreCliente,ingresos,gastos,historial) values ("Santinoti",2.67,67,"Deudor(apostador)");
insert into Cliente (nombreCliente,ingresos,gastos,historial) values ("santiEmpresa",5000.67,67000,"Deudor(MASIVO bro)");
insert into Empresa (rubro,director,idCliente) values ("Bar","santiEmpresa",2);
insert into Persona (nombrePersona,apellidoPersona,dni,idCliente) values ("Nanti","Goldstein","67676767",1);

delimiter //
CREATE PROCEDURE historialPersona();
BEGIN
	IF EXISTS (select c.nombreCliente,p.nombrePersona,p.apellidoPersona,c.ingresos,c.gastos,c.historial from Cliente as c join Persona as p on c.idCliente = p.idCliente);
		IF exists (select c.nombreCliente,p.nombrePersona,p.apellidoPersona from Cliente as c join Persona as p on c.idCliente = p.idCliente where c.gastos > c.ingresos);
    update Cliente set historial = "Deudor" where Cliente.gastos > Cliente.ingresos;
    End if;
  end if;
END //
delimiter ;

call historialPersona();

