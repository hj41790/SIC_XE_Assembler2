
public class DirectiveTable {
	
	private static DirectiveTable table = null;
	
	private String[] dir_table = null;
	private int[]	 operandNum = null;
	
	public static DirectiveTable getInstance(){
		
		if(table == null)
			table = new DirectiveTable();
		
		return table;
	}
	
	private DirectiveTable(){
		dir_table = new String[]{
				"START", "EXTDEF", "EXTREF", "RESW", "RESB", "BYTE", 
				"WORD", "LTORG", "EQU", "ORG", "CSECT", "USE", "END"
		};
		
		operandNum = new int[]{
				1,1,1,1,1,1,1,0,1,1,0,1,1
		};
	}
	
	public void execute(Token token){
		
		String operator = token.getOperator();
		
		try{
			
			if(operator.equals("START")){
				String operand;
				
				// only one operand
				if(token.getOperand(1)!=null)
					throw new CustomException(CustomException.OPERAND_TOO_MUCH);
				
				if((operand=token.getOperand(0))==null)
					throw new CustomException(CustomException.NO_OPERAND);
				
				// check if section name exist
				if(token.getLabel()==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				Section section = new Section(token.getLabel());
				SectionTable SECTAB = SectionTable.getInstance();
				
				
			}
			
		}catch(CustomException e){
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	public int getOperandNum(String name){
		
		for(int i=0; i<dir_table.length; i++){
			if(dir_table[i].equals(name)) return operandNum[i];
		}
		
		return -1;
	}
	
	public boolean isDirective(String name){
		for(String s : dir_table){
			if(s.equals(name)) return true;
		}
		return false;
	}

}

