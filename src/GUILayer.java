import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
public class GUILayer extends JPanel implements ActionListener {
	char inicial;
	ArrayList<String> linea = new ArrayList<String>();
	private ArrayList<String> listaErroresSemanticos = new ArrayList<String>();
	private ArrayList<String> variables = new ArrayList<>();
	private HashMap<String, TablaSimbolos> TablaSimbolos = new HashMap<String, TablaSimbolos>();
	private ArrayList<String> operaciones = new ArrayList<String>();
	private ArrayList<Datos> t = new ArrayList<Datos>();
    private static DefaultTableModel tablaTokens;
    private JTable tablaInformacion;
    private JTable tss;
    private static DefaultTableModel ts;
    private JScrollPane scrollTable;
    private JScrollPane scrollTables;

    private static String [] nombresToken = {"ASIGNACIONES","PLUS","MINUS","MULTIPLY","PUBLIC",					    //0-4
            "PRIVATE","STATIC","VOID","MAIN","CLASS","IF","ELSE",					                                //5-11
            "PRINT","LPAREN","RPAREN","LBRACE","RBRACE","LBRACKET",				                                    //12-17
            "RBRACKET","SEMICOLON","COMMA","EQ","MN","ER","INT","BOOLEAN"	                                        //18-25
            ,"STR","INTEGER","STRUE","SFALSE","USESTRING","IDENTIFIER"};	                                        //26-31

    // Paneles
    private JPanel zonaEste, zonaCentro,zonabaja;
    // TextArea
   
    public static JTextArea programa;
    private JScrollPane scrollPrograma;

    // Barra de herramientas
    private JToolBar jTool;
    private JButton lexico, semantico;

    // Resultado
    public static JLabel resultadoAnalisis;
    private JScrollPane scrollResultado;

    public GUILayer(){
        super();
        setLocation(200,100);
        setSize(900,400);
        setLayout(new BorderLayout());

        initComponents();
    }
    public void initComponents(){
        zonaEste();
        zonaCentro();
        zonaNorte();
    }
    private void zonaNorte(){
        jTool = new JToolBar("barra de herramientas");

        lexico = new JButton("Analisis Lexico");
        lexico.addActionListener(this);

   
        semantico = new JButton("Analisis Semantico");
        semantico.addActionListener(this);

        jTool.add(lexico);
        jTool.add(new JToolBar.Separator());
        jTool.add(semantico);

        add(jTool, BorderLayout.NORTH);

    }
    private void zonaEste() {
        zonaEste = new JPanel();
        zonaEste.setLayout(new GridLayout(2,1));

        String [] nombreColumnas = {"Tokens","Identificador"};
        Object [][] registros = null;
        
        String [] nombreColumnass = {"Variable","Tipo de dato","Valor","Posicion","Alcance"};
        Object [][] registross = null;
        ts = new DefaultTableModel(registross, nombreColumnass);
        tss = new JTable(ts);
        tablaTokens = new DefaultTableModel(registros, nombreColumnas);
        
        tablaInformacion = new JTable(tablaTokens);
        scrollTable = new JScrollPane();
        scrollTable.setViewportView(tablaInformacion);
        scrollResultado = new JScrollPane();
        scrollResultado.createVerticalScrollBar();
        resultadoAnalisis = new JLabel("Resultado");
        resultadoAnalisis.setFont(new Font(resultadoAnalisis.getFont().getName(),resultadoAnalisis.getFont().getStyle() ,20));
        scrollResultado.setViewportView(resultadoAnalisis);

        zonaEste.add(scrollTable);
        zonaEste.add(scrollResultado);
        add(zonaEste, BorderLayout.EAST);
    }
    private void zonaCentro() {
        zonaCentro = new JPanel();
        zonaCentro.setLayout(new GridLayout(2,1));

       
        scrollPrograma = new JScrollPane();

        programa = new JTextArea("Codigo del programa");

        scrollPrograma.setViewportView(programa);

        zonaCentro.add(scrollPrograma);
        // zonaCentro.add(codigoIM);

        add(zonaCentro, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            JButton objeto = (JButton) e.getSource();
            String text = objeto.getText();
            if(text.equals("Analisis Lexico")){
                System.out.println("LEXICO");
                analizarLexico();
            }else if(text.equals("Analisis Semantico")){
                analizadorSemantico();
                AnalisisSemantico(programa.getText());
            }
        }
    }
    public void AnalisisSemantico(String programa) {
		String codigo = programa;
		recorreCodigo(codigo);
		LlenaTabla();
		if (listaErroresSemanticos.isEmpty()) {
			Agregar(); // si no hay errores semanticos procedemos a hacer los triplos
		}
	

		System.out.println("\t" + "TABLA DE SIMBOLOS" + "\t");
		System.out.println("Variable\t" + "Tipo de dato\t" + "Valor\t" + "Posici�n\t" + "Alcance\t");
		imprimeTabla();
		System.out.println();
	}
    
    public void imprimeTabla() {
		for (int i = 0; i < variables.size(); i++) {
			System.out.println(variables.get(i) + "\t\t" +  TablaSimbolos.get(variables.get(i)).getTipoDato() + "\t\t"
					+ TablaSimbolos.get(variables.get(i)).getValor() + "\t\t"
					+  TablaSimbolos.get(variables.get(i)).getPosicion() + "\t"
					+  TablaSimbolos.get(variables.get(i)).getAlcance());
		}

	}
	public void recorreCodigo(String programa) {
		String parrafo = "";
		for (int i = 0; i < programa.length(); i++) {
			if (programa.charAt(i) == '{' || programa.charAt(i) == '}' || programa.charAt(i) == ';') {
				linea.add(parrafo);
				parrafo = "";
			} else
				parrafo += Character.toString(programa.charAt(i));
		}
	}
	
	

	public void LlenaTabla() {
		String parrafo;
		CharSequence sInt = "int", sIgual = "=";
		CharSequence sString = "String";
		CharSequence sDouble = "double";
		CharSequence sBool = "boolean";
		for (int i = 0; i < linea.size(); i++) {
			parrafo = linea.get(i);
			if (parrafo.contains(sInt) || parrafo.contains(sDouble) || parrafo.contains(sBool)
					|| parrafo.contains(sString)) {
				AgregaVariable(parrafo, sInt, i + 1);
				AgregaVariable(parrafo, sString, i + 1);
				AgregaVariable(parrafo, sDouble, i + 1);
				AgregaVariable(parrafo, sBool, i + 1);
			} else {
				if (parrafo.contains(sIgual)) {
					if (parrafo.contains("+") || parrafo.contains("-") || parrafo.contains("/")
							|| parrafo.contains("+"))
						operaciones.add(parrafo);
					if (parrafo.contains("(") && parrafo.contains(")")) {
						parrafo = parrafo.replaceAll("\\(", "");
						parrafo = parrafo.replaceAll("\\)", "");
					}
					Operaciones(parrafo, i + 1);
					System.out.println(parrafo);
					
				}
				
			}
			}
		}

	public void AgregaVariable(String parrafo, CharSequence Tipo, int pos) {
		String parrafoAux = "", variable = "", valor = "";
		CharSequence sIgual = "=", sPublic = "public", sPrivate = "private";
		if (parrafo.contains(Tipo)) {
			for (int j = 0; j < parrafo.length(); j++) {
				if (parrafoAux.contains(Tipo)) {
					if (parrafoAux.contains(sIgual)) {
						valor += Character.toString(parrafo.charAt(j));
					} else {
						if (parrafo.charAt(j) == ' ') {
							variable = Character.toString(parrafo.charAt(j + 1));
						}
					}
				}
				parrafoAux += Character.toString(parrafo.charAt(j));
			}
			// Validar si la variable ya esta declarada
			if (TablaSimbolos.containsKey(variable))
				listaErroresSemanticos
						.add("La variable " + "'" + variable + "'" + " ya se encuentra declarada en el renglon "
								+ TablaSimbolos.get(variable).getPosicion() + ".");
			else {
				// Verificando el valor asignado con el tipo
				if (!verificaTipoConValor(Tipo.toString(), valor)) {
					listaErroresSemanticos.add("Imposible asignar a variable " + "'" + variable + "' Tipo ("
							+ Tipo.toString() + ") valor:  " + valor + " renglon " + pos + ".");
					return;
				}
				if (parrafo.contains(sPublic) || parrafo.contains(sPrivate)) {
					TablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "global"));
					variables.add(variable);
				} else {
					TablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "local"));
					variables.add(variable);
				}
			}
		}

	}
	
	private boolean verificaTipoConValor(String tipo, String valor) {
		if (valor.length() == 0) {
			return true;
		}
		switch (tipo) {
		case "boolean":
			return (valor.equals("true") || valor.equals("false"));
		case "int":
			try {
				Integer.parseInt(valor);
				return true;
			} catch (Exception e) {
				return false;
			}
		default:
			return false;
		}

	}
    private void analizadorSemantico(){

        while(tablaTokens.getRowCount() > 0){
            tablaTokens.removeRow(0);
        }

        String textoAnalizar= programa.getText(), listTemp = "";                                //Recobra el texto del JTextArea
        ByteArrayInputStream baits = new ByteArrayInputStream(textoAnalizar.getBytes());        //Los convierte en en un Arreglo de Bytes
        MininiJava ae = new MininiJava(baits);                                                  //Se envían al Analizador Léxico/Sintáctico

        resultadoAnalisis.setForeground(Color.GREEN);
        resultadoAnalisis.setText("El analisis Semantico se ha completado exitosamente");
        try {
            ae.MainClass();                                                                     //Se ejecuta el Análisis Léxico/Sintáctico/Semántico
        } catch (ParseException ex) {
            resultadoAnalisis.setForeground(Color.YELLOW);
            resultadoAnalisis.setText("El analisis Semantico no se completo");
        } catch (TokenMgrError tme){
            resultadoAnalisis.setForeground(Color.YELLOW);
            resultadoAnalisis.setText("El analisis Semantico no se completo");
        }

        //Ciclo que recorre la Lista de Tokens:

        for(int i = 0;i<TokenAsignaciones.ListaTokens.size()-1; i++){
            String tipo = nombresToken[TokenAsignaciones.ListaTokens.get(i).typ-1];
            String token = TokenAsignaciones.ListaTokens.get(i).tok;
            String [] s = {tipo,token};
            tablaTokens.addRow(s);
        }

        //Se reinicia la Lista estática de TokenAsignaciones

        TokenAsignaciones.ListaTokens = new ArrayList<NotToken>();
        tablaTokens.addRow(new Vector());

    }
    public void Agregar() { 
		String operacion, operacionAux;
		int pos = 0, cont = 1;
		String agg = "";
		for (int i = 0; i < operaciones.size(); i++) {
			operacion = operaciones.get(i).replaceAll("\\s", "");
			operacionAux = quitaIgual(operacion);
			while (!operacionAux.isEmpty()) {
				//verificamos si contiene un numero negativo
				if((operacionAux.contains("-")) && esOperador(operacionAux.charAt(operacionAux.indexOf("-") - 1))) {
							pos=operacionAux.indexOf("-");
							t.add(new Datos("T"+cont,Character.toString(operacionAux.charAt(pos))+Character.toString(operacionAux.charAt(pos+1))));
							operacionAux = operacionAux.replace(Character.toString(operacionAux.charAt(pos))+Character.toString(operacionAux.charAt(pos+1)), "");
							cont++;
				}
				// Agregamos primero lo que se encuentra en parentesis
				if (operacionAux.contains("(")) {
					pos = operacionAux.indexOf("(") + 1;
					while (operacionAux.charAt(pos) != ')') {
						agg = agg + operacionAux.charAt(pos);
						pos++;
					}
					operacionAux = operacionAux.replace("(", "");
					operacionAux = operacionAux.replace(")", "");
					t.add(new Datos("T" + cont, agg));
					cont++;
					operacionAux = operacionAux.replace(agg, "");
				} else {
					// cuando la longitud es de 1, se realiza la operacion con los dos ultimos
					// triplos
					if (operacionAux.length() == 1) {
						agg = t.get(cont-3).getNum() + operacionAux.charAt(0) + t.get(cont-2).getNum();
						t.add(new Datos("T" + cont, agg));
						operacionAux = operacionAux.replace(Character.toString(operacionAux.charAt(0)), "");
					} else {
						// Cuando la longitud es 2, se realiza la operacion con el operando y el ultimo
						// triplo
						if (operacionAux.length() == 2) {
							System.out.println(cont);
							agg = Character.toString(operacionAux.charAt(0)) + Character.toString(operacionAux.charAt(1)) + t.get(cont - 2).getNum();
							t.add(new Datos("T" + cont, agg));
							operacionAux = operacionAux.replace(Character.toString(operacionAux.charAt(0))
									+ Character.toString(operacionAux.charAt(1)), "");
							cont++;
						} else {
							// Si es un operando se hace un triplo
							if (!esOperador(operacionAux.charAt(operacionAux.length() - 1))) {
								agg = Character.toString(operacionAux.charAt(operacionAux.length() - 1));
								t.add(new Datos("T" + cont, agg));
								operacionAux = operacionAux.replace(agg, "");
								cont++;
							} else {
								// Si es un operador y su antecesor es un operador se realiza la operacion con
								// los dos ultimos triplos
								if (esOperador(operacionAux.charAt(operacionAux.length() - 2))) {
									agg = t.get(cont - 3).getNum() + Character.toString(operacionAux.charAt(operacionAux.length() - 1))
											+ t.get(cont - 2).getNum();
									t.add(new Datos("T" + cont, agg));
									operacionAux = operacionAux.substring(0, operacionAux.length() - 1);
									/*operacionAux = operacionAux.replace(
											Character.toString(operacionAux.charAt(operacionAux.length() - 1)), "");*/
									cont++;
								} else {
									// Si es un operando entonces se agrega un nuevo triplo
									if (!esOperador(operacionAux.charAt(operacionAux.length() - 2))) {
										agg= Character.toString(operacionAux.charAt(0))
												+ Character.toString(operacionAux.charAt(1))
												+ Character.toString(operacionAux.charAt(2));
										t.add(new Datos("T" + cont, agg));
										operacionAux = operacionAux.replace(agg, "");
										cont++;
									}
								}

							}

						}
					}
				}
			}

		}
	}
    
	public boolean esOperador(String operador) {
		return (operador.equals("+") || operador.equals("*") || operador.equals("/") || operador.equals("-"))
				|| (operador.equals("&") || (operador.equals("&&") || operador.equals("|") || operador.equals("||")
						|| operador.equals("!=") || operador.equals("==")));
	}
	String quitaIgual(String operacion) {
		int indice;
		indice = operacion.indexOf("=");
		inicial= operacion.charAt(indice-1);
		operacion = operacion.replace(Character.toString(operacion.charAt(indice - 1)), "");
		operacion = operacion.replace("=", "");
		return operacion;
	}

	boolean esOperador(char c) {
		if (c == '+' || c == '-' || c == '*' || c == '/')
			return true;
		return false;
	}


   
    private static void analizarLexico(){

        while(tablaTokens.getRowCount() > 0){
            tablaTokens.removeRow(0);
        }
        String textoAnalizar= programa.getText(), listTemp = "";                                //Recobra el texto del JTextArea
        ByteArrayInputStream baits = new ByteArrayInputStream(textoAnalizar.getBytes());        //Los convierte en en un Arreglo de Bytes
        MininiJava ae = new MininiJava(baits);                                                  //Se envían al Analizador Léxico/Sintáctico

        try {
            //Se ejecuta el Análisis Léxico/Sintáctico/Semántico
            ae.MainClass();
            resultadoAnalisis.setForeground(Color.GREEN);
            resultadoAnalisis.setText("El análisis Lexico se ha completado exitosamente");
        } catch (ParseException ex) {
            resultadoAnalisis.setForeground(Color.YELLOW);
            resultadoAnalisis.setText("El analisis lexico no se completo");
        } catch (TokenMgrError tme){
            resultadoAnalisis.setForeground(Color.RED);
            resultadoAnalisis.setText(tme.getMessage());
        }

        //Ciclo que recorre la Lista de Tokens:

        for(int i = 0;i<TokenAsignaciones.ListaTokens.size()-1; i++){
            String tipo = nombresToken[TokenAsignaciones.ListaTokens.get(i).typ-1];
            String token = TokenAsignaciones.ListaTokens.get(i).tok;
            String [] s = {tipo,token};
            tablaTokens.addRow(s);
        }

        //Se reinicia la Lista estática de TokenAsignaciones

        TokenAsignaciones.ListaTokens = new ArrayList<NotToken>();
        tablaTokens.addRow(new Vector());
    }
    public void Operaciones(String parrafo, int pos) {
		String parrafoAux = "", variable = "", operandoAux = "";
		boolean variableEncontrada = false;
		// Variables usadas y no defindas
		// Se recorre el parrafo para obtener la variable de la operaci�n
		for (int j = 0; j < parrafo.length(); j++) {
			if (!variableEncontrada && parrafo.charAt(j) == '=' && (parrafo.charAt(j + 1) != '=')) {// Encontramos
																									// lavariable, lo
																									// que sigue es la
																									// operacion
				variable = parrafoAux;
				parrafoAux = "";
				variableEncontrada = true;
				if (!TablaSimbolos.containsKey(variable)) {
					listaErroresSemanticos
							.add("La variable " + "'" + variable + "' en la posici�n " + pos + " no ha sido definida.");
					break;
				}
			} else { // se eliminan los espacios en blanco

				if (Character.isWhitespace(parrafo.charAt(j)))
					continue;
				if (!Character.isWhitespace(parrafo.charAt(j))
						&& (!esOperador(parrafo.charAt(j) + "") && !esOperador(parrafoAux))) {
					parrafoAux += Character.toString(parrafo.charAt(j));

				} else {
					// Verificamos si es una variable
					if (TablaSimbolos.containsKey(parrafoAux)) { // Es variable
						if (!TablaSimbolos.get(parrafoAux).getTipoDato()
								.equals(TablaSimbolos.get(variable).getTipoDato())) {
							listaErroresSemanticos.add("Tipo de operacion incorrecta variable " + variable + " Tipo ("
									+ TablaSimbolos.get(variable).getTipoDato() + ") con variable " + parrafoAux
									+ " Tipo ( " + TablaSimbolos.get(parrafoAux) + ")");
							break;
						}
					} else {// Es operando u constante
						if (esOperador(parrafoAux)) {// Es un operador
							if (!verificarTipoConOperando(TablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {// No
																													// es
																													// un
																													// tipo
																													// de
																													// operador
																													// correco
								listaErroresSemanticos.add("Tipo de operacion incorrecta variable " + variable
										+ " Tipo (" + TablaSimbolos.get(variable).getTipoDato() + ") con operador "
										+ parrafoAux);
								break;
							}
						} else {// Es una constante
							if (!verificaTipoConValor(TablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {
								listaErroresSemanticos
										.add("Tipo de operacion incorrecta variable " + variable + " Tipo ("
												+ TablaSimbolos.get(variable).getTipoDato() + ") con " + parrafoAux);
								break;
							}
						}
					}
					parrafoAux = "";
					if (parrafoAux.length() == 0) {// verificamos si necesitamos meter el operador
						parrafoAux += parrafo.charAt(j);
					}
				}
			}
		}
		if (parrafoAux.length() > 0) {// Quedo algo en los operandos/operadores
			// Verificamos si es una variable
			if (TablaSimbolos.containsKey(parrafoAux)) { // Es variable
				if (!TablaSimbolos.get(parrafoAux).getTipoDato().equals(TablaSimbolos.get(variable).getTipoDato())) {
					listaErroresSemanticos.add("Tipo de operacion incorrecta variable " + variable + " Tipo ("
							+ TablaSimbolos.get(variable).getTipoDato() + ") con variable " + parrafoAux + " Tipo ( "
							+ TablaSimbolos.get(parrafoAux) + ")");
				}
			} else {// Es operando u constante
				if (esOperador(parrafoAux)) {// Es un operador
					if (!verificarTipoConOperando(TablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {// No es un
																											// tipo de
																											// operador
																											// correco
						listaErroresSemanticos.add("Tipo de operacion incorrecta variable " + variable + " Tipo ("
								+ TablaSimbolos.get(variable).getTipoDato() + ") con operador " + parrafoAux);
					}
				} else {// Es una constante
					if (!verificaTipoConValor(TablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {
						listaErroresSemanticos.add("Tipo de operacion incorrecta variable " + variable + " Tipo ("
								+ TablaSimbolos.get(variable).getTipoDato() + ") con " + parrafoAux);
					}
				}
			}
			parrafoAux = "";

		}
	}
    public boolean verificarTipoConOperando(String tipo, String operando) {
		switch (tipo) {
		case "int":
			return (operando.equals("+") || operando.equals("*") || operando.equals("/") || operando.equals("-"));
		case "boolean":
			return (operando.equals("&") || operando.equals("&&") || operando.equals("|") || operando.equals("||")
					|| operando.equals("!=") || operando.equals("=="));
		}
		return false;
	}

}
