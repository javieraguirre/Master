using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using System.Security.Permissions;
using System.Text;
using System.Threading.Tasks;

namespace ServidorNombres
{
    [SecurityPermission(SecurityAction.Demand)]
    public class ServNombres : MarshalByRefObject
    {
        const int puertoNombres = 1232;
        const String nombreTabla = "TablaNombres.remoto";

        [STAThread]
        static void Main(string[] args)
        {
            Console.WriteLine("SERVIDOR DE NOMBRES");

            Console.WriteLine("Estableciendo direccion de servicio de nombres:\n\npuerto: " + puertoNombres + "\nnombre del objeto remoto: " + nombreTabla);

            HttpChannel chnlServNombres = new HttpChannel(puertoNombres);
            ChannelServices.RegisterChannel(chnlServNombres);
            RemotingConfiguration.RegisterWellKnownServiceType(typeof(SokobanURJC.TablaNombres), nombreTabla, WellKnownObjectMode.Singleton);

            Console.WriteLine("\n\nAtendiendo las peticiones. Pulse cualquier tecla para terminar.");
            Console.Read();
        }
    }
}
