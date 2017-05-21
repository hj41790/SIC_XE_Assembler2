import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Token {
	
	private String		label;
	private String 		operator;
	private String[] 	operand;
	private String 		comment;
	private String 		value;

	private int 	addr;
	private int 	section;
	private int 	optype;
	private int 	nixbpe;
	
	private InstructionTable 	INSTTAB;
	private DirectiveTable 		DIRTAB;
	private SymbolTable 		SYMTAB;
	private LiteralTable 		LITTAB;
	private SectionTable 		SECTAB;
	private ModifyTable			MODTAB;
	

	public Token(){
		label = null;
		operator = null;
		comment = null;
		value = null;
		
		addr = 0;
		section = -1;
		optype = -1;
		nixbpe = 0;

		operand = new String[MAX_OPERAND];
		for(int i=0; i<MAX_OPERAND; i++)
			operand[i] = null;
	
		INSTTAB = InstructionTable.getInstance();
		DIRTAB = DirectiveTable.getInstance();
		SYMTAB = SymbolTable.getInstance();
		LITTAB = LiteralTable.getInstance();
		SECTAB = SectionTable.getInstance();
		MODTAB = ModifyTable.getInstance();
	}
	
	public void makeToken(String line) throws CustomException{
		
		addr = SECTAB.getLocctr();
		section = SECTAB.getSectionNum();
		
		// 1. comment line check
		if(line.charAt(0)=='.' || line.equals("")){
			// comment
			comment = line;
			optype = TYPE_COMMENT;
			return;
		}
		
		// 탭, 개행, 공백문자로 tokenize
		StringTokenizer tokenizer = new StringTokenizer(line, "\n|\t| ");
		
		// 2. label + operator check
		String token_next = tokenizer.nextToken();		
		if(INSTTAB.isInstruction(token_next)){
			operator = token_next;
			optype = TYPE_INSTRUCTION;
		}
		else if(DIRTAB.isDirective(token_next)){
			operator = token_next;
			optype = TYPE_DIRECTIVE;
		}
		else{
			// label + instuction/directive

			label = token_next;
			token_next = tokenizer.nextToken();

			if(INSTTAB.isInstruction(token_next)){
				operator = token_next;
				optype = TYPE_INSTRUCTION;
			}
			else if(DIRTAB.isDirective(token_next)){
				operator = token_next;
				optype = TYPE_DIRECTIVE;
			}
			else{
				throw new CustomException(CustomException.OPERATOR_UNDEFINED);
			}
		}

		// 3. operand + comment
		if((optype==TYPE_INSTRUCTION && INSTTAB.getOperandNum(operator)>0) ||
		   (optype==TYPE_DIRECTIVE && DIRTAB.getOperandNum(operator)>0)){
			
			if(!tokenizer.hasMoreTokens())
				throw new CustomException(CustomException.OPERAND_SHORTAGE);
			
			token_next = tokenizer.nextToken();
			
			int i=0;
			StringTokenizer st = new StringTokenizer(token_next, ",");
			while(st.hasMoreTokens()){
				operand[i] = st.nextToken();
				
				// literal check
				if(operand[i].charAt(0)=='='){
					if(i>0) throw new CustomException(CustomException.OPERAND_EXCEED);
					LITTAB.addLiteral(operand[i], section);
				}
				
				i++;
				// maximum operand = 3
				if(i==3 && st.hasMoreTokens())
					throw new CustomException(CustomException.OPERAND_EXCEED);
			}
			
			// comment
			if(tokenizer.hasMoreTokens()){			
				comment = "";
				while(tokenizer.hasMoreTokens()){
					comment += " "+tokenizer.nextToken();	
				}
			}
			
		}
		else{
			if(tokenizer.hasMoreTokens()){			
				comment = "";
				while(tokenizer.hasMoreTokens()){
					comment += " "+tokenizer.nextToken();	
				}
			}
		}
		
		
		// execute directive
		if(optype==TYPE_DIRECTIVE){
			 DIRTAB.execute(this);
		}
		
		// label symbol table registration
		if(optype==TYPE_INSTRUCTION && label != null){
			
			Symbol sym;
			if((sym = SYMTAB.search_globalSymbol(label)) != null){
				if(sym.getSection()<0 && !sym.isEXIST()){
					sym.setAddr(addr);
					sym.setEXIST();
					sym.setSection(section);
				}
				else
					throw new CustomException(CustomException.SYMBOL_DUPLICATION);
			}
			else if(SYMTAB.search_localSymbol(label, section) == null){
				sym = SYMTAB.addSymbol(label, this);
				sym.setAddr(addr);
				sym.setEXIST();
				sym.setSection(section);
			}
			else
				throw new CustomException(CustomException.SYMBOL_DUPLICATION);
			
		}
		
		
		// location counter calculation
		if(optype==TYPE_INSTRUCTION){
			if(operator.charAt(0)=='+') SECTAB.addLocctr(4);
			else SECTAB.addLocctr(INSTTAB.getFormat(operator));
		}
		
	}
	
	public void makeMachineCode() throws CustomException{
		
		int address = 0;
		int code = 0;
		String result = "";
		
		if(optype==TYPE_INSTRUCTION){
			
			int opcode = INSTTAB.getInstCode(operator);
			int format = INSTTAB.getFormat(operator);
			int param = INSTTAB.getOperandNum(operator);
			
			if(format==1){
				if(getOperandNum()!=0)
					throw new CustomException(CustomException.OPERAND_EXCEED);
				
				result += String.format("%02X", opcode);
				
			}
			else if(format==2){
				
				int p1=0, p2=0;
				
				if(param==1 && getOperandNum()==1){
					if((p1 = INSTTAB.getRegisterNum(operand[0]))<0)
						throw new CustomException(CustomException.REGISTER_UNDEFINED);
				}
				else if(param==2 && getOperandNum()==2){
					if((p1 = INSTTAB.getRegisterNum(operand[0]))<0)
						throw new CustomException(CustomException.REGISTER_UNDEFINED);
					if((p2 = INSTTAB.getRegisterNum(operand[1]))<0)
						throw new CustomException(CustomException.REGISTER_UNDEFINED);
				}
				else
					throw new CustomException(CustomException.OPERAND_ERR);
				
				result += String.format("%02X%01X%01X", opcode,p1,p2);
			}
			else{
				
				if(operator.charAt(0)=='+'){
					
					nixbpe |= BIT_E;
					
					if(param>0){
						
						// operand number check
						if(getOperandNum()==0)
							throw new CustomException(CustomException.OPERAND_SHORTAGE);
						
						// addressing mode check
						if(operand[0].charAt(0)=='@') nixbpe |= BIT_N;
						else if(operand[0].charAt(0)=='#') nixbpe |= BIT_I;
						else nixbpe |= BIT_N|BIT_I;
						
						// operand check
						Symbol s = SYMTAB.search_globalSymbol(operand[0]);
						if(s==null) {
							
							// #(immediate)
							if(Pattern.matches("^#?[0-9]+$", operand[0])){
								String str = operand[0];
								if(str.charAt(0)=='#') str = str.substring(1);
								
								address = Integer.parseInt(str);
							}
							else
								throw new CustomException(CustomException.OPERATOR_UNDEFINED);
						}
						else if(s.isReference(section) && s.isEXIST()){
							
							Modification m = new Modification(section, 5, 1, operand[0], this);
							MODTAB.addModifyTable(m);
							
						}
						else
							throw new CustomException(CustomException.SYMBOL_NO_REFERENCE);

						// X bit check
						if(operand[1]!=null){
							if(operand[1].equals("X")) 
								nixbpe |= BIT_X;
							else
								throw new CustomException(CustomException.OPERAND_ERR);
						}
						
					}
					else{
						nixbpe |= BIT_N|BIT_I;
					}
					
					code = (opcode<<24) | (nixbpe<<20) | address;
					result += String.format("%08X", code);
					
				}
				else{
					// format 3
					
					if(param>0){
						// operand number check
						if(getOperandNum()==0)
							throw new CustomException(CustomException.OPERAND_SHORTAGE);
						
						// addressing mode check
						if(operand[0].charAt(0)=='@') nixbpe |= BIT_N;
						else if(operand[0].charAt(0)=='#') nixbpe |= BIT_I;
						else nixbpe |= BIT_N|BIT_I;
						
						// X bit check
						if(operand[1]!=null){
							if(operand[1].equals("X")) 
								nixbpe |= BIT_X;
							else
								throw new CustomException(CustomException.OPERAND_ERR);
						}
						
						// operand check
						Symbol s = SYMTAB.search_localSymbol(operand[0], section);
						if(operand[0].charAt(0)=='#' && s==null){
							
							// immediate
							if(Pattern.matches("^#[0-9]+$", operand[0])){
								String str = operand[0].substring(1);
								address = Integer.parseInt(str);
							}
							else
								throw new CustomException(CustomException.WRONG_FORMAT);
							
						}
						else if(operand[0].charAt(0)=='='){
							
							// literal
							Literal l = LITTAB.search(operand[0], section);
							if(l==null)
								throw new CustomException(CustomException.SYMBOL_UNDEFINED);
							
							address = l.getAddr() - addr - 3;
							
							if(address<4096 && address>-4096) 
								nixbpe |= BIT_P;
							else
								throw new CustomException(CustomException.WRONG_FORMAT);
							
						}
						else if(s.isEXIST()){
							
							// pc-relative 
							address = s.getAddr() - addr - 3;
							
							if(address<4096 && address>-4096) 
								nixbpe |= BIT_P;
							else
								throw new CustomException(CustomException.WRONG_FORMAT);
							
						}
						else
							throw new CustomException(CustomException.SYMBOL_UNDEFINED);
						
						
					}
					else{
						nixbpe |= BIT_N|BIT_I;
					}
					
					address &= 0x000FFF;
					code = (opcode<<16) | (nixbpe<<12) | address;
					result += String.format("%06X", code);
				}
				
			}
			
			
		}
		else if(optype==TYPE_LITERAL){
			
			// get real data from =C'__' ( =C'__' -> __ )
			String substr = operator.substring(3, operator.length()-1);
			
			if(operator.charAt(1)=='C'){
				// =C'__'
				char[] arr = substr.toCharArray();
				for(char c : arr){
					result += String.format("%02X", (int)c);
				}
			}
			else{
				// =X'__'
				result += String.format("%s", substr);
			}
			
		}
		else if(operator.equals("BYTE")){
			
			// get real data from C'__' ( C'__' -> __ )
			String substr = operand[0].substring(2, operand[0].length()-1);

			if(operand[0].charAt(1)=='C'){
				// C'__'
				char[] arr = substr.toCharArray();
				for(char c : arr){
					result += String.format("%02X", (int)c);
				}
			}
			else{
				// X'__'
				result += String.format("%s", substr);
			}
			
		}
		else if(operator.equals("WORD")){
			
			if(isNumeric(operand[0])){
				code = Integer.parseInt(operand[0]);
				result += String.format("%06X", code);
			}
			else if(isExpression(operand[0])){
				
				int count = -1;
				char op = operand[0].charAt(0);
				String substr = operand[0];
				
				if(op=='+'||op=='-') substr = operand[0].substring(1);
				else op ='+';

				String[] arr = substr.split("[+|-]");


				for(String str : arr){
					
					Symbol s = SYMTAB.search_localSymbol(str, section);
					
					if(s!=null){
						if(op=='+') code += s.getAddr();
						else code -= s.getAddr();
					}
					else{
						
						s = SYMTAB.search_globalSymbol(str);
						if(s==null)
							throw new CustomException(CustomException.SYMBOL_UNDEFINED);
						else if(!s.isReference(section))
							throw new CustomException(CustomException.SYMBOL_NO_REFERENCE);
						
						Modification m = new Modification(section, 6, (op=='+')?1:0, str, this);
						MODTAB.addModifyTable(m);
						
					}
					
					count += str.length() + 1;
					if(count<operand[0].length())
						op = operand[0].charAt(count);
				}
				
				result += String.format("%06X", code);
				
			}
			else{
				Symbol s = SYMTAB.search_localSymbol(operand[0], section);
				if(s!=null && s.isEXIST()){
					code += s.getAddr();
				}
				else if((s=SYMTAB.search_globalSymbol(operand[0]))!=null){

					Modification m = new Modification(section, 6, 1, operand[0], this);
					MODTAB.addModifyTable(m);
				}
				else{
					throw new CustomException(CustomException.SYMBOL_UNDEFINED);
				}
				
				result += String.format("%06X", code);
				
			}
			
		}
		
		value = result;
		
	}
	
	public String toString(){
		
		String result = "";
		
		if(optype==TYPE_COMMENT){
			result += comment;
			return result;
		}
		
		// address
		if(optype==TYPE_INSTRUCTION || optype==TYPE_LITERAL)
			result += String.format("%04X\t", addr);
		else{
			int dir_index = DIRTAB.getIndex(operator);
			switch(dir_index){
				case 1:
				case 2:
				case 7:
				case 12:
					result += String.format("%4c\t", ' ');
					break;
				default :
					result += String.format("%04X\t", addr);
			}
		}
		
		// label
		if(label != null) result += String.format("%-10s", label);
		else result += String.format("%10c", ' ');
		
		// operator
		result += String.format("%-10s", operator);
		
		// operand
		String sum_operand="";
		if(operand[0] != null){
			
			sum_operand += operand[0];
		
			for(int i=1; i<operand.length; i++){
				if(operand[i]==null) break;
				sum_operand += ","+operand[i];
			}
		}
		result += String.format("%-20s", sum_operand);
		
		// machine code
		try {
			makeMachineCode();
			result += value;
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public int getAddr() {
		return addr;
	}

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public String getOperator() {
		return operator;
	}

	public int getOperandNum(){
		int count=0;
		for(String s : operand) {
			if(s==null) break;
			count++;
		}
		return count;
	}
	
	public String getOperand(int index) {
		return operand[index];
	}

	public String getLabel() {
		return label;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setOptype(int optype) {
		this.optype = optype;
	}

	public String getValue() {
		return value;
	}

	public int getOptype() {
		return optype;
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



	public static final int TYPE_INSTRUCTION = 1;
	public static final int TYPE_DIRECTIVE = 2;
	public static final int TYPE_COMMENT = 3;
	public static final int TYPE_LITERAL = 4;
	
	public static final int MAX_OPERAND = 3;

	public static final int BIT_N = 1<<5;
	public static final int BIT_I = 1<<4;
	public static final int BIT_X = 1<<3;
	public static final int BIT_B = 1<<2;
	public static final int BIT_P = 1<<1;
	public static final int BIT_E = 1;
	
}
