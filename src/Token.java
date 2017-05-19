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
	

	public Token(){
		label = null;
		operator = null;
		comment = null;
		value = null;
		
		addr = 0;
		section = -1;
		optype = -1;
		nixbpe = 0;

		operand = new String[3];
		operand[0] = null;
		operand[1] = null;
		operand[2] = null;
	
		INSTTAB = InstructionTable.getInstance();
		DIRTAB = DirectiveTable.getInstance();
		SYMTAB = SymbolTable.getInstance();
		LITTAB = LiteralTable.getInstance();
		SECTAB = SectionTable.getInstance();
	}
	
	public int makeToken(String line) throws CustomException{
		
		addr = SECTAB.getLocctr();
		section = SECTAB.getSectionNum();
		
		// 1. comment line check
		if(line.charAt(0)=='.' || line.equals("")){
			// comment
			comment = line;
			optype = TYPE_COMMENT;
			return 0;
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
				throw new CustomException(CustomException.NO_OPERAND);
			
			token_next = tokenizer.nextToken();
			
			int i=0;
			StringTokenizer st = new StringTokenizer(token_next, ",");
			while(st.hasMoreTokens()){
				operand[i] = st.nextToken();
				
				// literal check
				if(operand[i].charAt(0)=='='){
					if(i>0) throw new CustomException(CustomException.OPERAND_TOO_MUCH);
					LITTAB.addLiteral(operand[i], section);
				}
				
				i++;
				// maximum operand = 3
				if(i==3 && st.hasMoreTokens())
					throw new CustomException(CustomException.OPERAND_TOO_MUCH);
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
					throw new CustomException(CustomException.SYMBOL_NAME_DUPLICATION);
			}
			else if(SYMTAB.search_localSymbol(label, section) == null){
				SYMTAB.addSymbol(label, this);
			}
			else
				throw new CustomException(CustomException.SYMBOL_NAME_DUPLICATION);
			
		}
		
		
		// location counter calculation
		if(optype==TYPE_INSTRUCTION){
			if(operator.charAt(0)=='+') SECTAB.addLocctr(4);
			else SECTAB.addLocctr(INSTTAB.getFormat(operator));
		}
		
		return 0;
	}
	
	
	public void print(){
		
		if(optype==TYPE_COMMENT){
			System.out.println(comment);
		}
		else{
			if(label==null) System.out.format("%-6s ", " ");
			else System.out.format("%-6s ", label);
			
			System.out.format("%-6s ", operator);
			
			if(operand[0]!=null) System.out.format("%s", operand[0]);
			for(int i=1; i<operand.length; i++){
				if(operand[i]==null) break;
				System.out.format(",%s", operand[i]);
			}
			
//			if(comment!=null) System.out.format("\t%s", comment);
			
			if(optype==TYPE_INSTRUCTION){
				System.out.format("\t\t%X", INSTTAB.getInstCode(operator));
			}

			System.out.println();
			
		}
	}
	
	
	
	public int getAddr() {
		return addr;
	}

	public int getSection() {
		return section;
	}

	public String getOperator() {
		return operator;
	}

	public String getOperand(int index) {
		return operand[index];
	}

	public String getLabel() {
		return label;
	}





	public static int TYPE_INSTRUCTION = 1;
	public static int TYPE_DIRECTIVE = 2;
	public static int TYPE_COMMENT = 3;
	public static int TYPE_LITERAL = 4;
	
}
