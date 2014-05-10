using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.Net.Sockets;
using System.Runtime.Remoting;
using System.Runtime.Remoting.Channels;
using System.Runtime.Remoting.Channels.Http;
using System.Security.Permissions;
using System.Runtime.Serialization.Formatters.Binary; // libreria usada para la Practica 2. Esta libreria permite serializar en binario.
using System.Runtime.Serialization.Formatters.Soap;
using System.Xml.Serialization; // libreria para serializar en XML.
using System.IO; // Contiene FileStream y FileMode.
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Conexiones;
using ServidorLogica;
using System.Xml;
using System.Web;

using WebApplicationTest;
using ServidorNombres;


namespace SokobanURJC
{
	/// <summary>
	/// This class draws everything on screen. It also handles user input.
	/// </summary>
    [Serializable]
    [SecurityPermission(SecurityAction.Demand)]
    public class Board : System.Windows.Forms.Form
	{
		private System.ComponentModel.Container components = null;

        // Initialize the required objects
        public const string protocolo = "http";
        public int puerto = 1230;

        // Declarar componentes remotos:
        public static LevelSet levelSet; // = (LevelSet)Activator.GetObject(typeof(LevelSet), "http" + "://localhost:" + 1230 + "/LevelSet.remoto");
        public static Level level; // = (Level)Activator.GetObject(typeof(Level), "http" + "://localhost:" + 1230 + "/Level.remoto");*/
        public static ColeccionNiveles coleccionNiveles;

        public static WebApplicationTest.WebServiceClient ProxyServJugadores = new WebServiceClient();




        // Objects for drawing graphics on screen
		private PictureBox screen;
		public Bitmap img;

        // Nivel por defecto desde donde se empieza el juego.
        public int nivel = 1;

        String[] fichaUsuario = new String[3];
        String usuario;
        String password;
        String[] niveles;
        
        public String pedirPassword()
        {
            string pass = "";
            ConsoleKeyInfo key;

            do
            {
                key = Console.ReadKey(true);

                // Backspace Should Not Work
                if ((key.Key != ConsoleKey.Backspace) && (key.Key != ConsoleKey.Enter))
                {
                    pass += key.KeyChar;
                    Console.Write("*");
                }
                else
                {
                    Console.Write("\b");
                }
            }
            // Stops Receving Keys Once Enter is Pressed
            while (key.Key != ConsoleKey.Enter);

            return pass;
        }


        /// <summary>
        /// Constructor
        /// </summary>
		public Board()
		{
			InitializeComponent();

			screen = new PictureBox();

			Controls.AddRange(new Control[] {screen});
            levelSet = new LevelSet();

            // Load the levels in the LevelSet object and set the current level
            String s;
            bool opcionValida = false;
            do
            {
                Console.Write("\n[I]dentificate o [R]egistrate: "); s = Console.ReadLine();

                if (s == "i") // Caso en el que nos estemos identificando como un usuario ya existente.
                {
                    Console.WriteLine("\nIdentificate:\n");
                    Console.Write("Nombre de usuario: "); usuario = Console.ReadLine();
                    Console.Write("Contraseña: "); password = pedirPassword(); // Console.ReadLine();

                    //fichaUsuario = ProxyServJugadores.obtenerFichaJugador(usuario);
                    fichaUsuario = ProxyServJugadores.recuperarJugadorSQL(usuario, password).Split('&');

                    if ((fichaUsuario.Length != 3) || (fichaUsuario[1] != password))
                    {
                        Console.WriteLine("\nContraseña o usuario incorrectos");
                        opcionValida = false;
                    }
                    else
                    {
                        Console.WriteLine("\n\nTe has identificado como: " + fichaUsuario[0]);
                        //Console.WriteLine("Tu contrasena es: " + fichaUsuario[1]);
                        Console.WriteLine("El ultimo nivel al que jugaste fue: " + fichaUsuario[2]);

                        opcionValida = true;
                    }
                }

                else if (s == "r") // Caso en el que nos estemos registrando como usuario nuevo.
                {
                    Console.Write("\n\nTu nombre de usuario: "); fichaUsuario[0] = Console.ReadLine();
                    Console.Write("Tu contrasena: "); fichaUsuario[1] = pedirPassword(); // Console.ReadLine();
                    fichaUsuario[2] = "0.1";

                    ProxyServJugadores.crearJugadorSQL(fichaUsuario[0], fichaUsuario[1]);

                    Console.WriteLine("\nNuevo usuario creado.");

                    opcionValida = true;
                }
            }
            while (opcionValida == false);

            niveles = fichaUsuario[2].Split('.');
            int indiceColeccionNiveles = Convert.ToInt32(niveles[0]);
            int nivel = Convert.ToInt32(niveles[1]);
            String coleccion = coleccionNiveles.arrayNombresNiveles[indiceColeccionNiveles];
            levelSet.SetLevelsInLevelSet(indiceColeccionNiveles);

            // Cargar niveles de juego y empezar:
            Console.WriteLine("\nIniciando juego.");
            InitializeGame(nivel);
        }
        
       
        /// <summary>
        /// Sets the data for PlayerData, LevelSet, etc..
        /// </summary>
        private void InitializeGame(int nivel)
        {
            level = (Level)levelSet[nivel - 1];

			// Draw the level on the screen
            DrawLevel();
		}

        public Bitmap StringToImage(string imageString)
        {
            if (imageString == null) throw new ArgumentNullException("imageString");
            byte[] array = Convert.FromBase64String(imageString);
            Bitmap image = (Bitmap)Bitmap.FromStream(new MemoryStream(array));
            return image;
        }

		
		/// <summary>
		/// This method sets the width and the height of the screen,
		/// according to the level width and height. It then lets the level
		/// itself. Lastly, we set the labels to display the player/level info.
		/// </summary>
		public void DrawLevel()
		{
            int levelWidth = (level.Width) * Level.ITEM_SIZE;
            int levelHeight = (level.Height) * Level.ITEM_SIZE;
            this.ClientSize = new Size(levelWidth, levelHeight);
            screen.Size = new System.Drawing.Size(levelWidth, levelHeight);
            img = StringToImage(level.DrawLevel());
			screen.Image = img;
		}
		
		/// <summary>
		/// After moving Sokoban we only draw the changes on the level, and not
		/// redraw the whole level
		/// </summary>
        private void DrawChanges()
        {
            img = StringToImage(level.DrawChanges()); // img como byte[].
            screen.Image = img;
        }
        	
		/// <summary>
		/// Reads input from the keyboard and does something depending on what
		/// key is pressed.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void AKeyDown(object sender, KeyEventArgs e)
		{
		    string result = e.KeyData.ToString();
		    
		    switch (result)
		    {
		        case "Up":
		            level.MoveSokoban(MoveDirection.Up);
		            break;
                case "Down":
                    level.MoveSokoban(MoveDirection.Down);
                    break;
                case "Right":
                    level.MoveSokoban(MoveDirection.Right);
                    break;
                case "Left":
                    level.MoveSokoban(MoveDirection.Left);
                    break;
		    }

		    // Draw the changes of the level
            DrawChanges();

            if (level.IsFinished())
            {
                Console.WriteLine("Nivel superado!");
                nivel++;
                if (nivel == 11) { niveles[0] = niveles[0] + 1; nivel = 1; }

                String nuevonivel = niveles[0] + "." + nivel;
                ProxyServJugadores.actualizarJugadorSQL(usuario, password, nuevonivel);

                InitializeGame(nivel);
            }
		}

			
		#region Windows Form Designer generated code

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Console.WriteLine("CLIENTE DE SOKOBANURJC");

            // Recuperar tabla de nombres remota:
            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");
            TablaNombres tablaNombres = (TablaNombres)Activator.GetObject(typeof(SokobanURJC.TablaNombres), "http://localhost:1232/TablaNombres.remoto");

            int puertoLogica = tablaNombres.puertoLogica;
            int puertoNiveles = tablaNombres.puertoNiveles;

            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.Level), "http" + "://localhost:" + puertoLogica + "/Level.remoto");
            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.LevelSet), "http" + "://localhost:" + puertoLogica + "/LevelSet.remoto");
            RemotingConfiguration.RegisterWellKnownClientType(typeof(SokobanURJC.ColeccionNiveles), "http" + "://localhost:" + puertoNiveles + "/ColeccionNiveles.remoto");

            levelSet = (LevelSet)Activator.GetObject(typeof(LevelSet), "http" + "://localhost:" + puertoLogica + "/LevelSet.remoto");
            level = (Level)Activator.GetObject(typeof(Level), "http" + "://localhost:" + puertoLogica + "/Level.remoto");
            coleccionNiveles = (ColeccionNiveles)Activator.GetObject(typeof(SokobanURJC.ColeccionNiveles), "http" + "://localhost:" + puertoNiveles + "/ColeccionNiveles.remoto");


            // Lanzar juego:
            Application.Run(new Board());
        }
        
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
            // 
            // Board
            // 
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.BackColor = System.Drawing.Color.FromArgb(((System.Byte)(84)), ((System.Byte)(48)), ((System.Byte)(12)));
            this.ClientSize = new System.Drawing.Size(446, 200);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
            this.Name = "Práctica";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Tablero de juego de sokoban";
            this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.AKeyDown);
            this.ResumeLayout(false);

        }
		
        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        protected override void Dispose( bool disposing )
        {
            if( disposing )
            {
                if (components != null) 
                {
                    components.Dispose();
                }
            }
            base.Dispose( disposing );
        }
        
		#endregion
	}
}
