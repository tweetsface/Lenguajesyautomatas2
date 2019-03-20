import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Vector;

public class GUILayer extends JPanel implements ActionListener {

    // Tabla

    private static DefaultTableModel tablaTokens;
    private JTable tablaInformacion;
    private JScrollPane scrollTable;
    private static String [] nombresToken = {"ASIGNACIONES","PLUS","MINUS","MULTIPLY","PUBLIC",					    //0-4
            "PRIVATE","STATIC","VOID","MAIN","CLASS","IF","ELSE",					                                //5-11
            "PRINT","LPAREN","RPAREN","LBRACE","RBRACE","LBRACKET",				                                    //12-17
            "RBRACKET","SEMICOLON","COMMA","EQ","MN","ER","INT","BOOLEAN"	                                        //18-25
            ,"STR","INTEGER","STRUE","SFALSE","USESTRING","IDENTIFIER"};	                                        //26-31

    // Paneles
    private JPanel zonaEste, zonaCentro, codigoIM;

    // TextArea
   
    public static JTextArea programa;
    private JScrollPane scrollPrograma, scrollCodigoIntermedio, scrollCodigoMaquina;

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

        String [] nombreColumnas = {"Tokens","Indetificador"};
        Object [][] registros = null;

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
            }
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
}
