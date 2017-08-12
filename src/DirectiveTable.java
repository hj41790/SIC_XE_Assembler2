import java.util.regex.Pattern;

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

		SectionTable SECTAB = SectionTable.getInstance();	
		SymbolTable SYMTAB = SymbolTable.getInstance();
		
		
		try{
			
			if(operator.equals("START")){
				String operand;
				
				// only one operand
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				
				if((operand=token.getOperand(0))==null)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				
				// check if section name exist
				if(token.getLabel()==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				// add section
				Section section = new Section(token.getLabel());			
				SECTAB.addSection(section);
				
				// set locctr
				if(isNumeric(operand))
					SECTAB.setLocctr(Integer.parseInt(operand));
				else
					throw new CustomException(CustomException.WRONG_FORMAT);
				
				// modify token
				token.setAddr(SECTAB.getLocctr());
				token.setSection(SECTAB.getSectionNum());
				
				// register global symbol
				Symbol s = SYMTAB.addSymbol(token.getLabel(), token);
				s.setEXIST();
				s.setEXTDEF();
				
			}
			else if(operator.equals("EXTDEF")){
				
				for(int i=0; i<Token.MAX_OPERAND; i++){
					
					String operand = token.getOperand(i);
					if(operand == null) break;
					
					Symbol s = SYMTAB.search_globalSymbol(operand);
					
					if(s==null){
						
						// global symbol need no duplication in all symbol
						if(SYMTAB.search_allSymbol(operand)!=null)
							throw new CustomException(CustomException.SYMBOL_DUPLICATION);
						
						s = SYMTAB.addSymbol(operand, token);
						s.setEXTDEF();
						
					}
					else if(s.getSection()<0){
						// already defined from EXTREF
						s.setSection(token.getSection());
					}
					else{
						// symbol duplication
						throw new CustomException(CustomException.SYMBOL_DUPLICATION);
					}
				}
				
			}
			else if(operator.equals("EXTREF")){
				
				for(int i=0; i<Token.MAX_OPERAND; i++){

					String operand = token.getOperand(i);
					if(operand == null) break;
					
					Symbol s = SYMTAB.search_globalSymbol(operand);
					
					if(s==null){
						
						// global symbol need no duplication in all symbol
						if(SYMTAB.search_allSymbol(operand)!=null)
							throw new CustomException(CustomException.SYMBOL_DUPLICATION);
						
						// delete section information & set reference
						s = SYMTAB.addSymbol(operand, token);
						s.setSection(-1);
						s.setEXTDEF();
						s.setRefSection(token.getSection());
					}
					else{
						// set reference
						s.setRefSection(token.getSection());
					}	
					
				}

				
				
			}
			else if(operator.equals("RESW") || operator.equals("RESB")){
				
				String name = token.getLabel();
				if(name==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				
				String operand = token.getOperand(0);
				if(operand==null)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				
				int size;
				if(isNumeric(operand))
					size = Integer.parseInt(operand);
				else
					throw new CustomException(CustomException.WRONG_FORMAT);
				
				// register symbol
				Symbol s = SYMTAB.search_globalSymbol(name);
				if(s==null){
					
					if(SYMTAB.search_localSymbol(name, token.getSection())!=null)
						throw new CustomException(CustomException.SYMBOL_DUPLICATION);
					
					s = SYMTAB.addSymbol(name, token);
					s.setEXIST();
					
				}
				else if(s.getSection()==token.getSection() && !s.isEXIST()){
					s.setAddr(token.getAddr());
					s.setEXIST();
				}
				else{
					// symbol duplication
					throw new CustomException(CustomException.SYMBOL_DUPLICATION);
				}
				
				// location counter calculation
				if(operator.equals("RESW"))	SECTAB.addLocctr(size*3);
				else SECTAB.addLocctr(size);
				
			}
			else if(operator.equals("BYTE")){
				
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				else if(token.getOperandNum()==0)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				else if(token.getLabel()==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				String operand = token.getOperand(0);
				String pattern1 = "^C\'(\\s|\\S)*\'$";			// C'__'
				String pattern2 = "^X\'([0-9]|[A-F]){2}\'$";	// X'__'
				
				int size = 0;
				
				// pattern matching
				if(Pattern.matches(pattern1, operand)){
					size = operand.length()-3;
				}
				else if(Pattern.matches(pattern2, operand)){
					size = (int) Math.ceil((operand.length()-3)/2.0);
				}
				else
					throw new CustomException(CustomException.WRONG_FORMAT);
				
				
				// symbol table registration
				Symbol s = SYMTAB.search_globalSymbol(token.getLabel());
				if(s==null){
					
					if(SYMTAB.search_localSymbol(token.getLabel(), token.getSection())!=null)
						throw new CustomException(CustomException.SYMBOL_DUPLICATION);
					
					s = SYMTAB.addSymbol(token.getLabel(), token);
					s.setEXIST();
					
				}
				else if(s.getSection()==token.getSection() && !s.isEXIST()){
					s.setAddr(token.getAddr());
					s.setEXIST();
				}
				else
					throw new CustomException(CustomException.SYMBOL_DUPLICATION);
				
				// location counter calculation
				SECTAB.addLocctr(size);
				
			}
			else if(operator.equals("WORD")){
				
				// operand check
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				else if(token.getOperandNum()==0)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				else if(token.getLabel()==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				// symbol table registration
				Symbol s = SYMTAB.search_globalSymbol(token.getLabel());
				if(s==null){
					
					if(SYMTAB.search_localSymbol(token.getLabel(), token.getSection())!=null)
						throw new CustomException(CustomException.SYMBOL_DUPLICATION);
					
					s = SYMTAB.addSymbol(token.getLabel(), token);
					s.setEXIST();
					
				}
				else if(s.getSection()==token.getSection() && !s.isEXIST()){
					s.setAddr(token.getAddr());
					s.setEXIST();
				}
				else
					throw new CustomException(CustomException.SYMBOL_DUPLICATION);
				
				// location counter calculation
				SECTAB.addLocctr(3);
				
			}
			else if(operator.equals("LTORG")){
				
				LiteralTable.getInstance().LTORG();
				
			}
			else if(operator.equals("EQU")){
				
				// operand check
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				else if(token.getOperandNum()==0)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				else if(token.getLabel()==null)
					throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
				
				// symbol table registration
				Symbol s = SYMTAB.search_globalSymbol(token.getLabel());
				if(s==null){
					
					if(SYMTAB.search_localSymbol(token.getLabel(), token.getSection())!=null)
						throw new CustomException(CustomException.SYMBOL_DUPLICATION);
					
					s = SYMTAB.addSymbol(token.getLabel(), token);
					s.setEXIST();
					s.setEQU();
					
				}
				else if(s.getSection()==token.getSection() && !s.isEXIST()){
					s.setEXIST();
					s.setEQU();
				}
				else
					throw new CustomException(CustomException.SYMBOL_DUPLICATION);
		
				// calculation
				String operand = token.getOperand(0);
				Symbol tmp = SYMTAB.search_localSymbol(operand, token.getSection());
				
				if(operand.equals("*")){
					s.setAddr(SECTAB.getLocctr());
				}
				else if(isExpression(operand)){
					
					int count = -1;
					int result = 0;
					char op = operand.charAt(0);
					String substr = operand;
					if(op=='+' || op=='-'){
						substr = operand.substring(1);
					}
					else
						op = '+';
					
					String[] arr = substr.split("[+|-]");
					
					for(int i=0; i<arr.length; i++){
						tmp = SYMTAB.search_localSymbol(arr[i], token.getSection());
						if(tmp==null)
							throw new CustomException(CustomException.SYMBOL_UNDEFINED);
						
						if(tmp.isEXIST()){
							
							if(op=='+') result += tmp.getAddr();
							else result -= tmp.getAddr();
							
							count += arr[i].length() + 1;
							
							if(count<operand.length())
								op = operand.charAt(count);
							
						}
						else
							throw new CustomException(CustomException.SYMBOL_UNDEFINED);
						
					}
					
					s.setAddr(result);
					
				}
				else if(tmp!=null && tmp.isEXIST()){
					// local symbol & value exist
					s.setAddr(tmp.getAddr());
				}
				else if(isNumeric(operand)){
					s.setAddr(Integer.parseInt(operand));
				}
				else
					throw new CustomException(CustomException.WRONG_FORMAT);
				
				
				// token address = value
				token.setAddr(s.getAddr());
				
			}
			else if(operator.equals("CSECT")){
				
				// LTORG
				LiteralTable.getInstance().LTORG();
				
				// section length
				SECTAB.setLength();
				SECTAB.setLocctr(0);
				
				// register new section
				SECTAB.addSection(new Section(token.getLabel()));
				
				// modify token information
				token.setAddr(0);
				token.setSection(SECTAB.getSectionNum());
				
				// regist global symbol
				Symbol s = SYMTAB.search_globalSymbol(token.getLabel());
				if(s==null){
					s = SYMTAB.addSymbol(token.getLabel(), token);
					s.setEXIST();
					s.setEXTDEF();
				}
				else{
					s.setEXIST();
					s.setSection(token.getSection());
					s.setAddr(token.getAddr());
				}
				
			}
			else if(operator.equals("END")){
				
				if(token.getOperandNum()>1)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				else if(token.getOperandNum()==0)
					throw new CustomException(CustomException.OPERAND_SHORTAGE);
				
				String operand = token.getOperand(0);
				
				// LTORG
				LiteralTable.getInstance().LTORG();
				
				// section length
				SECTAB.setLength();
				SECTAB.setLocctr(0);
				
				// set return address
				Symbol s = SYMTAB.search_allSymbol(operand);
				if(s==null)
					throw new CustomException(CustomException.SYMBOL_UNDEFINED);
				
				SECTAB.setReturn_addr(s.getAddr());
				
			}
			
		}catch(CustomException e){
			e.printStackTrace();
		}
		
		
		
	}
	
	public int getIndex(String name){
		for(int i=0; i<dir_table.length; i++)
			if(dir_table[i].equals(name)) return i;
		
		return -1;
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
	
	private boolean isExpression(String s){
		
		String pattern = "^[A-Z]{1}([0-9]|[A-Z])+$";
		String[] arr = s.split("[+|-]");
		
		for(String str : arr){
			if(!Pattern.matches(pattern, str)) return false;
		}
		
		return true;
	}
	
	private boolean isNumeric(String s){

		String pattern = "^[0-9]+$";
		return Pattern.matches(pattern, s);
		
	}

}
