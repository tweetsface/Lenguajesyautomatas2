import java.awt.*;
import java.io.PrintStream;
import java.util.Hashtable;
import java.lang.String;
import java.util.ArrayList;

class NotToken{
	public int typ;public String tok;
	NotToken(int t, String ty) {
		typ = t; tok = ty;
	}
}

class TokenAsignaciones
{
	
	  //Tabla que almacenara los tokens declarados
	  public static Hashtable tabla = new Hashtable();

	  //String que almacenara los tokens para imprimir 
	  public static ArrayList<NotToken> ListaTokens = new ArrayList<NotToken>();
	  
	  //Listas que guardaran los tipos compatibles de las variablesja
	  private static ArrayList<Integer> intComp = new ArrayList();
	  private static ArrayList<Integer> boolComp = new ArrayList();
	  private static ArrayList<Integer> strComp = new ArrayList();

	                                      // variable		 // tipoDato
	public static void InsertarSimbolo(Token identificador, int tipo)
    {
		tabla.put(identificador.image, tipo);
		
	}
	public static void SetTables()
	{
		intComp.add(25);
		intComp.add(28);

		boolComp.add(26);
		boolComp.add(29);
		boolComp.add(30);

		strComp.add(27);
		strComp.add(31);
	}
 
	public static String checkAsing(Token TokenIzq, Token TokenAsig)
	{
	    int tipoIdent1;
		int tipoIdent2;		

		if(TokenIzq.kind != 28)		
		{
			try 
			{
			    tipoIdent1 = (Integer)tabla.get(TokenIzq.image);
			}
			catch(Exception e)
			{
                GUILayer.resultadoAnalisis.setForeground(Color.RED);
                GUILayer.resultadoAnalisis.setText("Error: El identificador " + TokenIzq.image + " No ha sido declarado \r\nLinea: " + TokenIzq.beginLine);
                return "Error: El identificador " + TokenIzq.image + " No ha sido declarado \r\nLinea: " + TokenIzq.beginLine;
            }
		}
		else 		
			tipoIdent1 = 0;

		if(TokenAsig.kind == 32)	
		{
			try
			{
				tipoIdent2 = (Integer)tabla.get(TokenAsig.image);
			}
			catch(Exception e)
			{
                GUILayer.resultadoAnalisis.setForeground(Color.RED);
                GUILayer.resultadoAnalisis.setText("Error: El identificador " + TokenIzq.image + " No ha sido declarado \r\nLinea: " + TokenIzq.beginLine);
				return "Error: El identificador " + TokenAsig.image + " No ha sido declarado \r\nLinea: " + TokenIzq.beginLine;
			}
		}
		else if(TokenAsig.kind == 28 || TokenAsig.kind == 29 || TokenAsig.kind == 30 || TokenAsig.kind == 31)
			tipoIdent2 = TokenAsig.kind;
		else
		    tipoIdent2 = 0;

			
	  
		
		if(tipoIdent1 == 25) //Int
        {
            if (intComp.contains(tipoIdent2))
                return " ";
            else {
                GUILayer.resultadoAnalisis.setForeground(Color.RED);
                GUILayer.resultadoAnalisis.setText("Error: No se puede convertir " + TokenAsig.image + " a Entero \r\nLinea: " + TokenIzq.beginLine);
                return "Error: No se puede convertir " + TokenAsig.image + " a Entero \r\nLinea: " + TokenIzq.beginLine;
            }
        }
		else if(tipoIdent1 == 26) //boolean
		{
			if(boolComp.contains(tipoIdent2))
				return " ";
			else{
                GUILayer.resultadoAnalisis.setForeground(Color.RED);
                GUILayer.resultadoAnalisis.setText("Error: No se puede convertir " + TokenAsig.image + " a Entero \r\nLinea: " + TokenIzq.beginLine);
				return "Error: No se puede convertir " + TokenAsig.image + " a boolean \r\nLinea: " + TokenIzq.beginLine;
		    }
		}
		else if(tipoIdent1 == 27) //string
        {
            if (strComp.contains(tipoIdent2))
                return " ";
            else {
                GUILayer.resultadoAnalisis.setForeground(Color.RED);
                GUILayer.resultadoAnalisis.setText("Error: No se puede convertir " + TokenAsig.image + " a Entero \r\nLinea: " + TokenIzq.beginLine);
                return "Error: No se puede convertir " + TokenAsig.image + " a Cadena \r\nLinea: " + TokenIzq.beginLine;
            }
        }
		else
		{
            GUILayer.resultadoAnalisis.setForeground(Color.RED);
            GUILayer.resultadoAnalisis.setText("Error: No se puede convertir " + TokenAsig.image + " a Entero \r\nLinea: " + TokenIzq.beginLine);
			return "El Identificador " + TokenIzq.image + " no ha sido declarado" + " Linea: " + TokenIzq.beginLine;
		}
		
	}	  
 }
  
  
  
  
  
  
  