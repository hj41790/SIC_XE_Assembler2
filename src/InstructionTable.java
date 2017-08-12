import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class InstructionTable {

	private static InstructionTable table = null;
	
	private HashMap<String, Instruction> map = null;
	private String filename = "inst.data";
	private String[] reg_table;
	
	public static InstructionTable getInstance(){
		
		if(table == null)
			table = new InstructionTable();
		
		return table;
		
	}
	
	public boolean isInstruction(String _key){
		
		String key = _key;
		if(_key.charAt(0)=='+') key = _key.substring(1);
		
		return map.containsKey(key);
	}
	
	public int getInstCode(String _key){
		
		String key = _key;
		if(_key.charAt(0)=='+') key = _key.substring(1);
		
		return ((Instruction)map.get(key)).getOpcode();
	}
	
	public int getOperandNum(String _key){
		
		String key = _key;
		if(_key.charAt(0)=='+') key = _key.substring(1);
		
		return ((Instruction)map.get(key)).getNum_operand();
	}
	
	public int getFormat(String _key){
		
		String key = _key;
		if(_key.charAt(0)=='+') key = _key.substring(1);
		
		return ((Instruction)map.get(key)).getFormat();
	}
	
	public int getRegisterNum(String r){
		
		for(int i=0; i<reg_table.length; i++)
			if(reg_table[i].equals(r)) return i;
		
		return -1;
	}
	
	private InstructionTable(){
		
		map = new HashMap<String, Instruction>();
		reg_table = new String[]{
				"A", "X", "L", "B", "S", "T", "F", "PC", "SW"
		};
		
		// file read
		
		try {
			
			File file = new File(filename);
			Scanner scanner = new Scanner(file);

			while(scanner.hasNext()){
				String name = scanner.next();
				int format = scanner.nextInt();
				int opcode = scanner.nextInt(16);
				int num_operand = scanner.nextInt();
				
				Instruction tmp = new Instruction(name, opcode, format, num_operand);
//				tmp.print();

				map.put(name, tmp);

			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
}
