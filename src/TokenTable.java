import java.util.ArrayList;

public class TokenTable {
	
	private static TokenTable table = null;

	private ArrayList<Token> array = null;

	public static TokenTable getInstance(){

		if(table == null)
			table = new TokenTable();

		return table;
	}

	private TokenTable(){

		array = new ArrayList<Token>();

	}	
	
	public void addToken(Token t){
		
		try{
			if(t==null)
				throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
			
			array.add(t);
			
		}catch(CustomException e){
			e.printStackTrace();
		}
	}
	
	public void intermediate_print(){
		
		for(Token t : array){
			System.out.println(t.toString());
		}
		
	}
	
	public String getObjectCode() throws CustomException{
		
		ModifyTable MODTAB = ModifyTable.getInstance();
		SectionTable SECTAB = SectionTable.getInstance();
		SymbolTable SYMTAB = SymbolTable.getInstance();
		
		int optype;
		String operator;
		String value;
		
		int section = 0;
		int start = 0;
		int count = 0;
		String text = "";
		String result = "";
		
		Token end_token = null;
		
		
		for(Token t : array){
			
			optype = t.getOptype();
			operator = t.getOperator();
			value = t.getValue();
			
			if(optype==Token.TYPE_INSTRUCTION || optype==Token.TYPE_LITERAL){
				
				// set location colum of modification record
				MODTAB.setLocation(t, start+count+1);
				
				// check text record
				if(start+count != t.getAddr() || count + (value.length()/2) >30){
					
					result += String.format("T%06X%02X%s\n", start, count, text);
					start = t.getAddr();
					count = value.length()/2;
					text = "" + value;
					
				}
				else{
					count += value.length()/2;
					text += value;
				}
				
			}
			else if(optype==Token.TYPE_DIRECTIVE){
				
				if(operator.equals("START")){
					start = t.getAddr();
					result += String.format("H%-6s%06X%06X\n", 
							t.getLabel(), t.getAddr(), SECTAB.getLength(0));
				}
				else if(operator.equals("EXTDEF")){
					
					result += "D";
					
					for(int i=0; i<Token.MAX_OPERAND; i++){
						if(t.getOperand(i)==null) break;
						Symbol s = SYMTAB.search_globalSymbol(t.getOperand(i));
						result += String.format("%-6s%06X", t.getOperand(i), s.getAddr());
					}
					
					result += "\n";
				}
				else if(operator.equals("EXTREF")){
					
					result += "R";
					for(int i=0; i<Token.MAX_OPERAND; i++){
						if(t.getOperand(i)==null) break;
						result += String.format("%-6s", t.getOperand(i));
					}
					result += "\n";
					
				}
				else if(operator.equals("BYTE") || operator.equals("WORD")){

					// set location colum of modification record
					MODTAB.setLocation(t, start+count);
					
					// check text record
					if(start+count != t.getAddr() || count + (value.length()/2) >30){
						
						result += String.format("T%06X%02X%s\n", start, count, text);
						start = t.getAddr();
						count = value.length()/2;
						text = "" + value;
						
					}
					else{
						count += value.length()/2;
						text += value;
					}
					
				}
				else if(operator.equals("CSECT")){
					
					// check Text record
					if(count>0){
						result += String.format("T%06X%02X%s\n", start, count, text);
						start = t.getAddr();
						count = 0;
						text = "";
					}
					
					// print modification record
					result += MODTAB.toString(section);
					
					// print end record
					if(t.getSection()<1)
						result += String.format("E%06x\n\n", SECTAB.getReturn_addr());
					else
						result += "E\n\n";

					section++;
					
					if(operator.equals("CSECT")){
						start = t.getAddr();
						result += String.format("H%-6s%06X%06X\n", 
								t.getLabel(), start, SECTAB.getLength(section));
					}
				}
				else if(operator.equals("END")){
					end_token = t;
				}
				
			}

		}

		
		// last End record
		
		// check Text record
		if(count>0){
			result += String.format("T%06X%02X%s\n", start, count, text);
			start = end_token.getAddr();
			count = 0;
			text = "";
		}
		
		// print modification record
		result += MODTAB.toString(section);
		
		// print end record
		if(end_token.getSection()<1)
			result += String.format("E%06x\n\n", SECTAB.getReturn_addr());
		else
			result += "E\n\n";
		
		
		return result;
	}
}

