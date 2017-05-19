import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MyAssembler {
	
	private String inputfile = "input.txt";
	private String outputfile = "output_144.txt";
		
	private ArrayList<String> 	input_data;
	private TokenTable			token_table;
	
	private InstructionTable 	INSTTAB;
	private DirectiveTable 		DIRTAB;
	private SymbolTable 		SYMTAB;
	private LiteralTable 		LITTAB;
	private SectionTable 		SECTAB;
	private ModifyTable			MODTAB;

	public MyAssembler(){

		init();
		read_input_file();
		
		pass1();
		pass2();
		
		make_object_file();
	}
	
	public void pass1(){
		
		try{
			
			for(String s : input_data){
				Token t = new Token();
				token_table.addToken(t);
				t.makeToken(s);
			}
			
		}catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pass2(){
		
		token_table.intermediate_print();
		System.out.println("\n");
		
	}
	
	public void make_object_file(){
		
		
	}
	
	
	public void init(){
		
		token_table = TokenTable.getInstance();
		input_data = new ArrayList<String>();
		
		INSTTAB = InstructionTable.getInstance();
		DIRTAB = DirectiveTable.getInstance();
		SYMTAB = SymbolTable.getInstance();
		LITTAB = LiteralTable.getInstance();
		SECTAB = SectionTable.getInstance();
		MODTAB = ModifyTable.getInstance();
	}
	
	public void read_input_file(){
		
		try {
			FileReader fr = new FileReader(inputfile);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			while((line=br.readLine())!=null){
				input_data.add(line);
//				System.out.println(line);
			}
			
			br.close();
			fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new MyAssembler();

	}

}





































