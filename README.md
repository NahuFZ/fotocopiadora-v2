1. Para armar la estructura de la base de datos en MySQL, hay un archivo "fotocopiadora.sql" dentro de la carpeta "fotocopiadora-v2/sql” que debe ser importado en MySQL.
2. Para establecer la primera cuenta de administrador y los roles necesarios, hay un archivo "setup.sql" en la misma carpeta. También hay que importarla.
3. Para iniciar sesión con el primer administrador, su usuario es "admin@fotocopiadora.com" 
y la contraseña "admin123".
4. Idealmente, se debe ejecutar el archivo "login.jsp" para iniciar sesión, pero aun así cualquier archivo JSP redirige al login si no tienen una sesión abierta.
5. La carpeta donde se guardan los archivos subidos es C:\Users\SuUsuario\archivosfotocopiadora
6. Testeado con Java 21, Dynamic Web Module 5.0, XAMPP v3.3.0, Tomcat v11.0 y JConnector v9.3.0.
