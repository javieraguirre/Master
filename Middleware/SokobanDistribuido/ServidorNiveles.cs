using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using System.Runtime.Remoting.Channels.Tcp;
using System.Drawing;
using System.Net;
using System.Net.Sockets;
using System.Security.Permissions;

using SokobanURJC;
using Conexiones;
using System.IO;


namespace SokobanURJC
{

    [Serializable]
    public class ColeccionNiveles : MarshalByRefObject
    {
        public String[] arrayNombresNiveles = { "boxworld1.xml", "boxworld2.xml", "boxworld3.xml", "boxworld4.xml" };

        public byte[] ficheroXMLRemoto(int indice)
        {

            XmlDocument doc = new XmlDocument();
            doc.Load(arrayNombresNiveles[indice]);

            byte[] byteArray = new byte[0];
            using (MemoryStream stream = new MemoryStream())
            {
                doc.Save(stream);
                stream.Close();
                byteArray = stream.ToArray();
            }
            return byteArray;
        }
    }
}


namespace ServidorNiveles
{

    [SecurityPermission(SecurityAction.Demand)]
    public class ServidorLogicaJuego : System.MarshalByRefObject
    {
        /// Funcion principal.
        [STAThread]
        static void Main()
        {
            Console.WriteLine("SERVIDOR DE NIVELES\n"); // Console.WriteLine("Pulse cualquier tecla para comenzar"); Console.ReadLine();

            // Recuperar numeros de puerto desde el servidor de nombres:
            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");
            TablaNombres tablaNombres = (TablaNombres)Activator.GetObject(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");
            int puertoNiveles = tablaNombres.puertoNiveles;
            String nombreNiveles = tablaNombres.nombreNiveles;

            // Establecer comunicaciones: configurarse como servidor.
            Console.WriteLine("Estableciendo direccion de servicio de niveles:\n\npuerto: " + puertoNiveles + "\nnombre del objeto remoto: " + nombreNiveles);

            Conexion cnxServLogica = new Conexion();
            IChannel canalServLogica = cnxServLogica.Conectar("servidor", "http", puertoNiveles, typeof(SokobanURJC.ColeccionNiveles), nombreNiveles);

            Console.WriteLine("\n\nAtendiendo las peticiones. Pulse Enter para salir.");
            Console.ReadLine();
        }
    }
}
