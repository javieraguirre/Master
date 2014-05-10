using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SokobanURJC
{
    [Serializable]
    public class TablaNombres : MarshalByRefObject
    {
        int ptoLogica = 1230;
        int ptoNiveles = 1231;
        String nomNiveles = "ColeccionNiveles.remoto";
        String nomLevel = "Level.remoto";
        String nomLevelSet = "LevelSet.remoto";

        public int puertoLogica
        {
            get { return ptoLogica; }
            set { ptoLogica = puertoLogica; }
        }

        public int puertoNiveles
        {
            get { return ptoNiveles; }
            set { ptoNiveles = puertoNiveles; }
        }

        public String nombreNiveles { get { return nomNiveles; } }
        public String nombreLevel { get { return nomLevel; } }
        public String nombreLevelSet { get { return nomLevelSet; } }
    }
}
