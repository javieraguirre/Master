using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
using System.Xml;


namespace ServidorLogica
{

    public class Cadena : System.MarshalByRefObject
    {
        public String cadena1 = "Conectado";

        public String cadena()
        {
            Console.WriteLine("Devolviendo cadena");
            return cadena1;
        }
    }

    [SecurityPermission(SecurityAction.Demand)]
    public class ServidorLogicaJuego : System.MarshalByRefObject
    {
        /// Funcion principal.

        [STAThread]
        static void Main()
        {
            Console.WriteLine("SERVIDOR DE LOGICA DE JUEGO\n");

            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");
            TablaNombres tablaNombres = (TablaNombres)Activator.GetObject(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");
            int puertoLogica = tablaNombres.puertoLogica;
            int puertoNiveles = tablaNombres.puertoNiveles;
            String nombreLevel = tablaNombres.nombreLevel;
            String nombreLevelSet = tablaNombres.nombreLevelSet;
            String nombreNiveles = tablaNombres.nombreNiveles;

            Console.WriteLine("Conectando con servidor de niveles\n\npuerto: " + puertoNiveles);
            Console.WriteLine("Estableciendo direccion de servicio de logica:\n\npuerto: " + puertoLogica + "\nnombres de los objetos remotos: " + nombreLevel + ", y " + nombreLevelSet);

            HttpChannel chnlBoard = new HttpChannel(puertoLogica);
            ChannelServices.RegisterChannel(chnlBoard);
            RemotingConfiguration.RegisterWellKnownServiceType(typeof(SokobanURJC.Level), nombreLevel, WellKnownObjectMode.Singleton);
            RemotingConfiguration.RegisterWellKnownServiceType(typeof(SokobanURJC.LevelSet), nombreLevelSet, WellKnownObjectMode.Singleton);

            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.ColeccionNiveles), "http://localhost:" + puertoNiveles + "/" + nombreNiveles);

            Console.WriteLine("\n\nAtendiendo las peticiones. Pulse Enter para salir.");
            Console.ReadLine();

            ChannelServices.UnregisterChannel(chnlBoard);
        }
    }
}
